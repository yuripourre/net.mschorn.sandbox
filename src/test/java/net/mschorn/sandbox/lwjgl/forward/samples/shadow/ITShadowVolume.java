/*
 * Copyright 2013, Michael Schorn (me@mschorn.net). All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 *   1. Redistributions of source code must retain the above copyright notice, this list of
 *      conditions and the following disclaimer.
 *
 *   2. Redistributions in binary form must reproduce the above copyright notice, this list of
 *      conditions and the following disclaimer in the documentation and/or other materials
 *      provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */


package net.mschorn.sandbox.lwjgl.forward.samples.shadow;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import net.mschorn.sandbox.lwjgl.tools.glunit.GLUnit;

import org.junit.Test;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;


public final class ITShadowVolume extends GLUnit {

    private static final int WIDTH = 640;
    private static final int HEIGHT = 360;
    private static final int STEPS = 180;


    @Test
    public void test() throws IOException {

        final ShadowVolume lifecycle = new ShadowVolume();
        final DisplayMode displayMode = new DisplayMode(WIDTH, HEIGHT);
        final PixelFormat pixelFormat = new PixelFormat().withSamples(4);
        final ContextAttribs contextAttribs = new ContextAttribs(ShadowVolume.GL_MAJOR_VERSION, ShadowVolume.GL_MINOR_VERSION)
        .withProfileCore(true)
        .withForwardCompatible(true);

        final double difference = glTest(lifecycle, displayMode, pixelFormat, contextAttribs, STEPS);

        assertTrue("Image difference is " + difference, difference < MAXIMUM_DIFFERENCE);

    }


}
