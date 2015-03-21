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

/**
 *
 * @author Kevin Raoofi
 * @param <T> type used for the event context
 */
public class EventWrapper<T> {

    /**
     * This event type can be used to differentiate different handlers for the
     * {@link EventRouter}. The type of {@code T} can should be inferable based
     * on this value and should be able to be safely casted.
     */
    private final EventType type;

    /**
     * This object is what gives context to the event handler. It should contain
     * absolutely everything an event handler may need to process the event
     */
    private T context;

    public EventWrapper(EventType type) {
        this.type = type;
    }

    public T getContext() {
        return context;
    }

    public void setContext(T context) {
        this.context = context;
    }

    public EventType getType() {
        return type;
    }
}
