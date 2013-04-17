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


package net.mschorn.sandbox.lwjgl.deprecated.samples.cube;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_LIGHT0;
import static org.lwjgl.opengl.GL11.GL_LIGHTING;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_NORMAL_ARRAY;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_COORD_ARRAY;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;
import static org.lwjgl.opengl.GL11.GL_VERTEX_ARRAY;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnableClientState;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glNormalPointer;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glTexCoordPointer;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertexPointer;
import static org.lwjgl.opengl.GL31.GL_PRIMITIVE_RESTART;
import static org.lwjgl.opengl.GL31.glPrimitiveRestartIndex;
import net.mschorn.sandbox.lwjgl.deprecated.tools.buffer.IBO;
import net.mschorn.sandbox.lwjgl.deprecated.tools.buffer.VBO;
import net.mschorn.sandbox.lwjgl.deprecated.tools.texture.Texture;
import net.mschorn.sandbox.lwjgl.tools.lifecycle.LWJGLLifecycle;
import net.mschorn.sandbox.lwjgl.tools.window.LWJGLWindow;

import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.glu.GLU;


public final class CubeVBO implements LWJGLLifecycle {

    public static final String TITLE = "Fixed Function Cube VBO";

    public static final int GL_MAJOR_VERSION = 3;
    public static final int GL_MINOR_VERSION = 2;

    private static final int RESTART = 0xFFFFFFFF;

    private static final int[] CUBE_IBO = new int[] {

        0, 1, 2, 3, RESTART,
        4, 5, 6, 7, RESTART,
        8, 9, 10, 11, RESTART,
        12, 13, 14, 15, RESTART,
        16, 17, 18, 19, RESTART,
        20, 21, 22, 23

    };

    // x, y, z, s, t, nx, ny, nz
    private static final float[] CUBE_VBO = new float[] {

        +1.0f, -1.0f, -1.0f, 1.0f, 0.0f, +0.0f, -1.0f, +0.0f,
        +1.0f, -1.0f, +1.0f, 1.0f, 1.0f, +0.0f, -1.0f, +0.0f,
        -1.0f, -1.0f, -1.0f, 0.0f, 0.0f, +0.0f, -1.0f, +0.0f,
        -1.0f, -1.0f, +1.0f, 0.0f, 1.0f, +0.0f, -1.0f, +0.0f,

        -1.0f, +1.0f, +1.0f, 0.0f, 1.0f, +0.0f, +1.0f, +0.0f,
        +1.0f, +1.0f, +1.0f, 1.0f, 1.0f, +0.0f, +1.0f, +0.0f,
        -1.0f, +1.0f, -1.0f, 0.0f, 0.0f, +0.0f, +1.0f, +0.0f,
        +1.0f, +1.0f, -1.0f, 1.0f, 0.0f, +0.0f, +1.0f, +0.0f,

        +1.0f, -1.0f, +1.0f, 1.0f, 0.0f, +0.0f, +0.0f, +1.0f,
        +1.0f, +1.0f, +1.0f, 1.0f, 1.0f, +0.0f, +0.0f, +1.0f,
        -1.0f, -1.0f, +1.0f, 0.0f, 0.0f, +0.0f, +0.0f, +1.0f,
        -1.0f, +1.0f, +1.0f, 0.0f, 1.0f, +0.0f, +0.0f, +1.0f,

        -1.0f, +1.0f, -1.0f, 0.0f, 1.0f, +0.0f, +0.0f, -1.0f,
        +1.0f, +1.0f, -1.0f, 1.0f, 1.0f, +0.0f, +0.0f, -1.0f,
        -1.0f, -1.0f, -1.0f, 0.0f, 0.0f, +0.0f, +0.0f, -1.0f,
        +1.0f, -1.0f, -1.0f, 1.0f, 0.0f, +0.0f, +0.0f, -1.0f,

        -1.0f, -1.0f, +1.0f, 0.0f, 1.0f, -1.0f, +0.0f, +0.0f,
        -1.0f, +1.0f, +1.0f, 1.0f, 1.0f, -1.0f, +0.0f, +0.0f,
        -1.0f, -1.0f, -1.0f, 0.0f, 0.0f, -1.0f, +0.0f, +0.0f,
        -1.0f, +1.0f, -1.0f, 1.0f, 0.0f, -1.0f, +0.0f, +0.0f,

        +1.0f, +1.0f, -1.0f, 1.0f, 0.0f, +1.0f, +0.0f, +0.0f,
        +1.0f, +1.0f, +1.0f, 1.0f, 1.0f, +1.0f, +0.0f, +0.0f,
        +1.0f, -1.0f, -1.0f, 0.0f, 0.0f, +1.0f, +0.0f, +0.0f,
        +1.0f, -1.0f, +1.0f, 0.0f, 1.0f, +1.0f, +0.0f, +0.0f,

    };

    // r, g, b
    private static final byte[] TEXTURE = {
        (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xF0, (byte) 0x00, (byte) 0x00, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xF0, (byte) 0x00, (byte) 0x00,
        (byte) 0xF0, (byte) 0x00, (byte) 0x00, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xF0, (byte) 0x00, (byte) 0x00, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
        (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xF0, (byte) 0x00, (byte) 0x00, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xF0, (byte) 0x00, (byte) 0x00,
        (byte) 0xF0, (byte) 0x00, (byte) 0x00, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xF0, (byte) 0x00, (byte) 0x00, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF };


    private static final int FLOAT_SIZE = Float.SIZE / Byte.SIZE;

    private static final int WIDTH = 800;
    private static final int HEIGHT = 475;

    private final Texture texture;
    private final IBO ibo;
    private final VBO vbo;

    private double angle = 0.0f;


    public static void main(final String[] args) {

        final CubeVBO lifecycle = new CubeVBO();
        final DisplayMode displayMode = new DisplayMode(WIDTH, HEIGHT);
        final PixelFormat pixelFormat = new PixelFormat().withSamples(4);
        final ContextAttribs contextAttribs = new ContextAttribs(GL_MAJOR_VERSION, GL_MINOR_VERSION).withProfileCompatibility(true);

        LWJGLWindow.display(lifecycle, displayMode, pixelFormat, contextAttribs, TITLE);

    }


    public CubeVBO() {

        texture = new Texture(GL_RGB, 4, 4, TEXTURE, GL_NEAREST, GL_NEAREST);

        ibo = new IBO(CUBE_IBO);
        vbo = new VBO(CUBE_VBO);

    }


    @Override
    public void glInit(final int width, final int height) {

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glEnable(GL_LIGHTING);
        glEnable(GL_LIGHT0);
        glEnable(GL_TEXTURE_2D);

        glEnable(GL_PRIMITIVE_RESTART);
        glPrimitiveRestartIndex(0xFFFFFFFF & RESTART);

        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_TEXTURE_COORD_ARRAY);
        glEnableClientState(GL_NORMAL_ARRAY);

        glMatrixMode(GL_PROJECTION);
        GLU.gluPerspective(45.0f, (float) Display.getWidth() / (float) Display.getHeight(), 1.0f, 500.0f);

        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();

        texture.glInit();

        ibo.glInit();
        vbo.glInit();

    }


    @Override
    public void glDisplay(final double timedelta) {

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glLoadIdentity();

        angle += timedelta * 0.05;
        glTranslatef(0, 0, -5);
        glRotatef((float) angle, 0, 1, 1);

        texture.glBind();

        vbo.glBind();
        glVertexPointer(3, GL_FLOAT, 8 * FLOAT_SIZE, 0 * FLOAT_SIZE);
        glTexCoordPointer(2, GL_FLOAT, 8 * FLOAT_SIZE, 3 * FLOAT_SIZE);
        glNormalPointer(GL_FLOAT, 8 * FLOAT_SIZE, 5 * FLOAT_SIZE);

        ibo.glDraw(GL_TRIANGLE_STRIP);

    }


    @Override
    public void glDispose() {

        texture.glDispose();

        ibo.glDispose();
        vbo.glDispose();

    }


}
