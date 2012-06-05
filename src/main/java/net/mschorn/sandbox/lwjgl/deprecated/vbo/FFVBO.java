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


package net.mschorn.sandbox.lwjgl.deprecated.vbo;

import static org.lwjgl.opengl.GL11.GL_C3F_V3F;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glInterleavedArrays;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.swing.JOptionPane;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;


public final class FFVBO {

    // r, g, b, x, y, z
    private static final float[] TRIANGLE_VBO = new float[] {

        1.0f, 0.0f, 0.0f, -1.0f, -1.0f, 0.0f,
        0.0f, 1.0f, 0.0f, +1.0f, -1.0f, 0.0f,
        0.0f, 0.0f, 1.0f, +0.0f, +1.0f, 0.0f

    };


    private static final int[]   TRIANGLE_IBO = new int[] {

        0, 1, 2

    };


    private static final int     WIDTH        = 800;
    private static final int     HEIGHT       = 475;

    private int                  ibo;
    private int                  vbo;


    private FFVBO() {

    }


    private void glInit() {

        glEnable(GL11.GL_DEPTH_TEST);

        final IntBuffer iboBuffer = BufferUtils.createIntBuffer(TRIANGLE_IBO.length);
        iboBuffer.put(TRIANGLE_IBO);
        iboBuffer.flip();

        ibo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, iboBuffer, GL_STATIC_DRAW);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

        final FloatBuffer vboBuffer = BufferUtils.createFloatBuffer(TRIANGLE_VBO.length);
        vboBuffer.put(TRIANGLE_VBO);
        vboBuffer.flip();

        vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vboBuffer, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

    }


    private void glDisplay() {

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glLoadIdentity();

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);

        glInterleavedArrays(GL_C3F_V3F, 0, 0);
        glDrawElements(GL_TRIANGLES, 3, GL_UNSIGNED_INT, 0);

    }


    private void glDispose() {

    }


    private void init() {

        try {

            Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
            Display.create(new PixelFormat(), new ContextAttribs(1, 5));
            Display.setTitle("Fixed Function VBO");

        } catch (final LWJGLException e) {

            JOptionPane.showMessageDialog(null, "Your Graphics Card does not support all necessary functions.", "OpenGL Context Creation", JOptionPane.ERROR_MESSAGE);

            System.exit(1);

        }


    }


    private void loop() {

        glInit();

        while (!Display.isCloseRequested()) {

            glDisplay();

            Display.update();
        }

        glDispose();

        Display.destroy();

    }


    public static void main(final String[] args) {

        final FFVBO main = new FFVBO();

        main.init();
        main.loop();

    }

}
