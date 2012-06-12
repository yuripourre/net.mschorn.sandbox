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


package net.mschorn.sandbox.lwjgl.deferred.shading;

import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_LEQUAL;
import static org.lwjgl.opengl.GL11.glDepthFunc;
import static org.lwjgl.opengl.GL11.glEnable;

import java.io.IOException;

import javax.swing.JOptionPane;

import net.mschorn.sandbox.lwjgl.tools.mvp.MVP;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;


public final class DeferredShading {

    private static final double NANO_TO_MILLI = 1 / 1000000.0;

    private static final int    WIDTH         = 800;
    private static final int    HEIGHT        = 475;

    private static final int    MVP_BINDING   = 0;

    private final GeometryPass  geometryPass;
    private final LightPass     lightPass;

    private final MVP           mvp;

    private long                time;


    private DeferredShading() throws IOException {


        geometryPass = new GeometryPass(WIDTH, HEIGHT);
        lightPass = new LightPass(geometryPass.getVertexTexture(), geometryPass.getNormalTexture(), geometryPass.getDiffuseBuffer(), geometryPass.getSpecularBuffer());

        mvp = new MVP(MVP_BINDING);

    }


    private void glInit() {

        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);
        glEnable(GL_CULL_FACE);

        geometryPass.glInit();
        lightPass.glInit();

        mvp.glInit();
        mvp.update(0, 0, WIDTH, HEIGHT);

    }


    private void glDisplay() {

        mvp.update(-(time - (time = System.nanoTime())) * NANO_TO_MILLI);
        mvp.glBind();

        geometryPass.glRender();
        lightPass.glRender();

    }


    private void glDispose() {

        geometryPass.glDispose();
        lightPass.glDispose();

    }


    private void init() {

        try {

            Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
            Display.create(new PixelFormat(), new ContextAttribs(4, 2).withProfileCore(true).withForwardCompatible(true));
            Display.setTitle("Deferred shading");

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

        final DeferredShading main = new DeferredShading();

        main.init();
        main.loop();

    }

}
