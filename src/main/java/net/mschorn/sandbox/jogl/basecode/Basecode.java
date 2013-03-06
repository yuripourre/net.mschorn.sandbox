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

package net.mschorn.sandbox.jogl.basecode;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;

import com.jogamp.opengl.util.Animator;


public final class Basecode implements GLEventListener {

    public static final String TITLE = "Basecode";

    private static final int WIDTH = 800;
    private static final int HEIGHT = 475;

    private GL2 gl;


    @Override
    public void init(final GLAutoDrawable drawable) {

        gl = drawable.getGL().getGL2();

        gl.glEnable(GL.GL_DEPTH_TEST);

    }


    @Override
    public void display(final GLAutoDrawable drawable) {

        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();

    }


    @Override
    public void reshape(final GLAutoDrawable drawable, final int x, final int y, final int width, final int height) {

        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();

        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();

    }


    @Override
    public void dispose(final GLAutoDrawable arg0) {

    }


    public static void main(final String[] args) throws IOException {

        final Basecode base = new Basecode();
        final Frame frame = new Frame(TITLE);
        final GLCanvas canvas = new GLCanvas();
        final Animator animator = new Animator(canvas);

        canvas.addGLEventListener(base);
        frame.add(canvas);
        frame.setSize(WIDTH, HEIGHT);
        frame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(final WindowEvent e) {

                new Thread(new Runnable() {

                    @Override
                    public void run() {

                        animator.stop();
                        frame.remove(canvas);
                        frame.dispose();

                    }

                }).start();
            }
        });

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        animator.start();

    }

}
