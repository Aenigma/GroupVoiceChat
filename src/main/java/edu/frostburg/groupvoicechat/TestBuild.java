/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.frostburg.groupvoicechat;

import org.jitsi.impl.neomedia.codec.audio.opus.Opus;

/**
 *
 * @author Kevin Raoofi
 */
public class TestBuild {

    public void test() {
        Opus.encoder_create(10, 100);
    }
}
