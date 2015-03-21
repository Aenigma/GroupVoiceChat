/*
 * Copyright 2015 Kevin Raoofi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.frostburg.groupvoicechat.networking.events;

import edu.frostburg.groupvoicechat.commons.Pair;
import java.util.EnumMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * This class manages the dispatching of events as added.
 *
 * @author Kevin Raoofi
 * @param <S> type used to store event router state. The state is not managed by
 * this class and thus has no bearing on this class. However, event handlers
 * generated may be interested to be able to store and read state, and thus this
 * is provided.
 */
public class EventRouter<S> {

    private final EnumMap<EventType, EventHandler> em;

    private final ExecutorService eventProcessor;
    private final ScheduledExecutorService ses;

    /** Whilst our reference to state is immutable, the state object should be
     * mutable so that it can represent changes */
    private final S state;

    /**
     * Creates instance using the given state type.
     *
     * @param state object used to keep track of state for the event handlers
     */
    public EventRouter(S state) {
        this.em = new EnumMap<>(EventType.class);
        this.eventProcessor = Executors.newSingleThreadExecutor();
        this.ses = Executors.newSingleThreadScheduledExecutor();
        this.state = state;
    }

    /**
     * Instantiates state object with just {@link Object}.
     */
    public EventRouter() {
        this((S) new Object());
    }

    public <T> Future<?> addEvent(EventWrapper<T> e) {
        return eventProcessor.submit(new EventDispatcher(e));
    }

    public <T> Future<?> addScheduledEvent(EventWrapper<T> e, final long delay,
            final TimeUnit t) {
        return ses.schedule(new AddToEventQueue(e), delay, t);
    }

    /**
     *
     * @param <T> type held by event wrapper
     * @param e The event wrapper to embed
     * @param delay time to delay for
     * @param t TimeUnits to delay for
     * @return a Pair with the left being a future for the original event and
     * the right being the future for the follow-up event
     */
    public <T> Pair<Future<?>, Future<?>> addWithFollowUp(
            final EventWrapper<T> e, long delay,
            TimeUnit t) {
        Future<?> left = addEvent(e);
        final EventWrapper<EventWrapper<T>> followUpEvent = new EventWrapper<>(
                EventType.FOLLOWUP);
        Future<?> right = addScheduledEvent(followUpEvent, delay, t);

        return new Pair<Future<?>, Future<?>>(left, right);
    }

    public void register(EventHandler eh, EventType et) {
        em.put(et, eh);
    }

    public S getState() {
        return state;
    }

    public void shutdown() {
        ses.shutdown();
        eventProcessor.shutdown();
    }

    class AddToEventQueue implements Runnable {

        private final EventWrapper e;

        public AddToEventQueue(EventWrapper e) {
            this.e = e;
        }

        @Override
        public void run() {
            eventProcessor.submit(new EventDispatcher(e));
        }
    }

    class EventDispatcher implements Runnable {

        private final EventWrapper e;

        public EventDispatcher(EventWrapper e) {
            this.e = e;
        }

        @Override
        public void run() {
            EventHandler eh = em.get(e.getType());
            eh.onEvent(e);
        }
    }
}
