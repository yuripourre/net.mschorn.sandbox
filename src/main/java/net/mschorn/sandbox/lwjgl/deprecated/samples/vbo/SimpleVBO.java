/*
 * Copyright 2012, Michael Schorn (me@mschorn.net). All rights reserved.
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


package net.mschorn.sandbox.lwjgl.deprecated.samples.vbo;

import static org.lwjgl.opengl.GL11.GL_COLOR_ARRAY;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_VERTEX_ARRAY;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glColorPointer;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnableClientState;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glVertexPointer;
import net.mschorn.sandbox.lwjgl.deprecated.tools.buffer.IBO;
import net.mschorn.sandbox.lwjgl.deprecated.tools.buffer.VBO;
import net.mschorn.sandbox.lwjgl.tools.lifecycle.LWJGLLifecycle;
import net.mschorn.sandbox.lwjgl.tools.window.LWJGLWindow;

import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;


public final class SimpleVBO implements LWJGLLifecycle {

    public static final String TITLE = "Fixed Function VBO";

    public static final int GL_MAJOR_VERSION = 1;
    public static final int GL_MINOR_VERSION = 5;

    private static final int[] TRIANGLE_IBO = new int[] {

        0, 1, 2

    };

    // x, y, z, r, g, b
    private static final float[] TRIANGLE_VBO = new float[] {

        -1.0f, -1.0f, 0.0f, 1.0f, 0.0f, 0.0f,
        +1.0f, -1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
        +0.0f, +1.0f, 0.0f, 0.0f, 0.0f, 1.0f,

    };

    private static final int FLOAT_SIZE = Float.SIZE / Byte.SIZE;

    private static final int WIDTH = 800;
    private static final int HEIGHT = 475;

    private final IBO ibo;
    private final VBO vbo;


    public static void main(final String[] args) {

        final SimpleVBO lifecycle = new SimpleVBO();
        final DisplayMode displayMode = new DisplayMode(WIDTH, HEIGHT);
        final PixelFormat pixelFormat = new PixelFormat();
        final ContextAttribs contextAttribs = new ContextAttribs(GL_MAJOR_VERSION, GL_MINOR_VERSION);

        LWJGLWindow.display(lifecycle, displayMode, pixelFormat, contextAttribs, TITLE);

    }


    public SimpleVBO() {

        ibo = new IBO(TRIANGLE_IBO);
        vbo = new VBO(TRIANGLE_VBO);

    }


    @Override
    public void glInit(final int width, final int height) {

        glEnable(GL_DEPTH_TEST);

        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_COLOR_ARRAY);

        ibo.glInit();
        vbo.glInit();

    }


    @Override
    public void glDisplay(final double timedelta) {

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glLoadIdentity();

        vbo.glBind();
        glVertexPointer(3, GL_FLOAT, 6 * FLOAT_SIZE, 0 * FLOAT_SIZE);
        glColorPointer(3, GL_FLOAT, 6 * FLOAT_SIZE, 3 * FLOAT_SIZE);

        ibo.glDraw(GL_TRIANGLES);

    }


    @Override
    public void glDispose() {

        ibo.glDispose();
        vbo.glDispose();

    }


}
