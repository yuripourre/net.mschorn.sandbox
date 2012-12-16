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


package net.mschorn.sandbox.lwjgl.gpgpu.samples.conway;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_RGBA8;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Random;

import net.mschorn.sandbox.lwjgl.tools.buffer.FBO;
import net.mschorn.sandbox.lwjgl.tools.buffer.IBO;
import net.mschorn.sandbox.lwjgl.tools.buffer.VBO;
import net.mschorn.sandbox.lwjgl.tools.geometry.Quad;
import net.mschorn.sandbox.lwjgl.tools.lifecycle.LWJGLLifecycle;
import net.mschorn.sandbox.lwjgl.tools.shader.Program;
import net.mschorn.sandbox.lwjgl.tools.shader.Shader;
import net.mschorn.sandbox.lwjgl.tools.state.VAO;
import net.mschorn.sandbox.lwjgl.tools.texture.Sampler;
import net.mschorn.sandbox.lwjgl.tools.texture.Texture;
import net.mschorn.sandbox.lwjgl.tools.window.LWJGLWindow;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;


public final class Conway implements LWJGLLifecycle {

    public static final String TITLE = "Conway's Game of Life";

    public static final int GL_MAJOR_VERSION = 4;
    public static final int GL_MINOR_VERSION = 2;

    private static final String VERTEX_SHADER = "vs.glsl";
    private static final String CONWAY_SHADER = "conway.glsl";
    private static final String DISPLAY_SHADER = "display.glsl";

    private static final int SAMPLER_BINDING = 0;

    private static final int COLOR = 0xFFFFFF;
    private static final int WIDTH = 800;
    private static final int HEIGHT = 475;

    private final Sampler sampler;
    private Texture front;
    private Texture back;

    private final Shader vsCommon;
    private final Shader fsConway;
    private final Shader fsDisplay;
    private final Program pConway;
    private final Program pDisplay;

    private final IBO ibo;
    private final VBO vbo;
    private final VAO vao;

    private final FBO fbo;

    private boolean toggle;


    public static void main(final String[] args) {

        final Conway lifecycle = new Conway();
        final DisplayMode displayMode = new DisplayMode(WIDTH, HEIGHT);
        final PixelFormat pixelFormat = new PixelFormat();
        final ContextAttribs contextAttribs = new ContextAttribs(GL_MAJOR_VERSION, GL_MINOR_VERSION).withProfileCore(true).withForwardCompatible(true);

        LWJGLWindow.display(lifecycle, displayMode, pixelFormat, contextAttribs, TITLE);

    }


    public Conway() {

        sampler = createSampler();

        vsCommon = new Shader(GL_VERTEX_SHADER, getClass().getResourceAsStream(VERTEX_SHADER));
        fsConway = new Shader(GL_FRAGMENT_SHADER, getClass().getResourceAsStream(CONWAY_SHADER));
        fsDisplay = new Shader(GL_FRAGMENT_SHADER, getClass().getResourceAsStream(DISPLAY_SHADER));
        pConway = new Program(vsCommon, fsConway);
        pDisplay = new Program(vsCommon, fsDisplay);

        final Quad quad = new Quad();
        ibo = new IBO(quad.getIndices(), GL_STATIC_DRAW);
        vbo = new VBO(quad.getAttributes(), GL_STATIC_DRAW);
        vao = new VAO(GL_TRIANGLE_STRIP, quad.getIndices().size(), ibo, vbo);

        vao.addVertexAttribute(0, 3, GL_FLOAT, false, 8 * 4, 0 * 4);
        vao.addVertexAttribute(1, 2, GL_FLOAT, false, 8 * 4, 3 * 4);
        vao.addVertexAttribute(2, 3, GL_FLOAT, false, 8 * 4, 5 * 4);

        fbo = new FBO();

    }


    private Sampler createSampler() {

        final Sampler sampler = new Sampler(SAMPLER_BINDING);

        sampler.addParameter(GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        sampler.addParameter(GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        sampler.addParameter(GL_TEXTURE_WRAP_S, GL_REPEAT);
        sampler.addParameter(GL_TEXTURE_WRAP_T, GL_REPEAT);

        return sampler;

    }


    @Override
    public void glInit(final int width, final int height) {

        final ByteBuffer randomField = createRandomField(width, height);
        front = new Texture(0, width, height, GL_RGBA8);
        back = new Texture(0, width, height, GL_RGBA8, randomField);

        sampler.glInit();
        front.glInit();
        back.glInit();

        vsCommon.glInit();
        fsConway.glInit();
        fsDisplay.glInit();
        pConway.glInit();
        pDisplay.glInit();
        vsCommon.glDispose();
        fsConway.glDispose();
        fsDisplay.glDispose();

        ibo.glInit();
        vbo.glInit();
        vao.glInit();

        fbo.glInit();

    }


    private ByteBuffer createRandomField(final int width, final int height) {

        final Random random = new Random(0);
        final ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * Integer.SIZE / Byte.SIZE);
        final IntBuffer data = buffer.asIntBuffer();
        for (int i = 0; i < data.capacity(); i++)
            if (random.nextInt(100) > 90)
                data.put(i, COLOR);

        return buffer;

    }


    @Override
    public void glDisplay(final double timedelta) {

        if (toggle)
            step(front, back, timedelta);

        else
            step(back, front, timedelta);

        toggle = !toggle;

    }


    private void step(final Texture front, final Texture back, final double timedelta) {

        sampler.glBind();

        pConway.glBind();
        fbo.glBind();
        back.glAttachToFBO(GL_COLOR_ATTACHMENT0);
        front.glBind();
        glClear(GL_COLOR_BUFFER_BIT);
        vao.glDraw();
        fbo.glUnbind();

        pDisplay.glBind();
        back.glBind();
        vao.glDraw();

    }


    @Override
    public void glDispose() {

        sampler.glDispose();
        front.glDispose();
        back.glDispose();

        pConway.glDispose();
        pDisplay.glDispose();

        ibo.glDispose();
        vbo.glDispose();
        vao.glDispose();

        fbo.glDispose();

    }


}
