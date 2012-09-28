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


package net.mschorn.sandbox.lwjgl.deprecated.texture;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_COORD_ARRAY;
import static org.lwjgl.opengl.GL11.GL_VERTEX_ARRAY;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnableClientState;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glTexCoordPointer;
import static org.lwjgl.opengl.GL11.glVertexPointer;

import java.io.IOException;

import javax.swing.JOptionPane;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;


public final class FFTexturedVBO {

    private static final String  TEXTURE      = "net/mschorn/sandbox/resources/images/eye.london.png";

    private static final int[]   TRIANGLE_IBO = new int[] {

        0, 1, 2

    };


    // x, y, z, s, t
    private static final float[] TRIANGLE_VBO = new float[] {

        -1.0f, -1.0f, +0.0f, 0.0f, 0.0f,
        +1.0f, -1.0f, +0.0f, 1.0f, 0.0f,
        +0.0f, +1.0f, +0.0f, 0.5f, 1.0f,

    };

    private static final int     FLOAT_SIZE   = Float.SIZE / Byte.SIZE;

    private static final int     WIDTH        = 800;
    private static final int     HEIGHT       = 475;


    private final Texture        texture;
    private final IBO            ibo;
    private final VBO            vbo;


    private FFTexturedVBO() throws IOException {

        texture = new Texture(0, getClass().getClassLoader().getResourceAsStream(TEXTURE));
        ibo = new IBO(TRIANGLE_IBO);
        vbo = new VBO(TRIANGLE_VBO);

    }


    private void glInit() {

        glEnable(GL11.GL_DEPTH_TEST);

        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_TEXTURE_COORD_ARRAY);

        texture.glInit();
        ibo.glInit();
        vbo.glInit();

    }


    private void glDisplay() {

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glLoadIdentity();

        texture.glBind();

        vbo.glBind();
        glVertexPointer(3, GL_FLOAT, 5 * FLOAT_SIZE, 0 * FLOAT_SIZE);
        glTexCoordPointer(2, GL_FLOAT, 5 * FLOAT_SIZE, 3 * FLOAT_SIZE);

        ibo.glDraw();

    }


    private void glDispose() {

        texture.glDispose();
        ibo.glDispose();
        vbo.glDispose();

    }


    private void init() {

        try {

            Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
            Display.create(new PixelFormat(), new ContextAttribs(1, 5));
            Display.setTitle("Textured Fixed Function VBO");

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


    public static void main(final String[] args) throws IOException {

        final FFTexturedVBO main = new FFTexturedVBO();

        main.init();
        main.loop();

    }


}
