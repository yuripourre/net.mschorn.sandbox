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


package net.mschorn.sandbox.lwjgl.deferred.samples.shading;

import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_RGBA8;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;
import net.mschorn.sandbox.lwjgl.tools.lifecycle.LWJGLLifecycle;
import net.mschorn.sandbox.lwjgl.tools.mvp.MVP;
import net.mschorn.sandbox.lwjgl.tools.texture.Texture;
import net.mschorn.sandbox.lwjgl.tools.window.LWJGLWindow;

import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;


public final class DeferredShading implements LWJGLLifecycle {

    public static final String TITLE = "Deferred shading";

    public static final int GL_MAJOR_VERSION = 4;
    public static final int GL_MINOR_VERSION = 2;

    private static final int WIDTH = 800;
    private static final int HEIGHT = 475;

    private static final int MVP_BINDING = 0;
    private static final int V_BUFFER_BINDING = 0;
    private static final int VN_BUFFER_BINDING = 1;
    private static final int DIFFUSE_BUFFER_BINDING = 2;
    private static final int SPECULAR_BUFFER_BINDING = 3;

    private Texture vBuffer;
    private Texture vnBuffer;
    private Texture diffuseBuffer;
    private Texture specularBuffer;

    private GeometryPass geometryPass;
    private LightPass lightPass;

    private MVP mvp;


    public static void main(final String[] args) {

        final DeferredShading lifecycle = new DeferredShading();
        final DisplayMode displayMode = new DisplayMode(WIDTH, HEIGHT);
        final PixelFormat pixelFormat = new PixelFormat();
        final ContextAttribs contextAttribs = new ContextAttribs(GL_MAJOR_VERSION, GL_MINOR_VERSION).withProfileCore(true).withForwardCompatible(true);

        LWJGLWindow.display(lifecycle, displayMode, pixelFormat, contextAttribs, TITLE);

    }


    @Override
    public void glInit(final int width, final int height) {

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);

        vBuffer = new Texture(V_BUFFER_BINDING, width, height, GL_RGBA16F);
        vnBuffer = new Texture(VN_BUFFER_BINDING, width, height, GL_RGBA16F);
        diffuseBuffer = new Texture(DIFFUSE_BUFFER_BINDING, width, height, GL_RGBA8);
        specularBuffer = new Texture(SPECULAR_BUFFER_BINDING, width, height, GL_RGBA8);

        geometryPass = new GeometryPass(vBuffer, vnBuffer, diffuseBuffer, specularBuffer);
        lightPass = new LightPass(vBuffer, vnBuffer, diffuseBuffer, specularBuffer);

        mvp = new MVP(MVP_BINDING, width, height);

        vBuffer.glInit();
        vnBuffer.glInit();
        diffuseBuffer.glInit();
        specularBuffer.glInit();

        geometryPass.glInit();
        lightPass.glInit();

        mvp.glInit();

    }


    @Override
    public void glDisplay(final double timedelta) {

        mvp.update(timedelta);
        mvp.glBind();

        geometryPass.glDisplay();
        lightPass.glDisplay();

    }


    @Override
    public void glDispose() {

        vBuffer.glDispose();
        vnBuffer.glDispose();
        diffuseBuffer.glDispose();
        specularBuffer.glDispose();

        geometryPass.glDispose();
        lightPass.glDispose();

        mvp.glDispose();

    }


}
