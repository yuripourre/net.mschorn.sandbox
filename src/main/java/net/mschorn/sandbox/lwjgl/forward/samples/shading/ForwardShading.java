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


package net.mschorn.sandbox.lwjgl.forward.samples.shading;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import net.mschorn.sandbox.lwjgl.tools.buffer.IBO;
import net.mschorn.sandbox.lwjgl.tools.buffer.VBO;
import net.mschorn.sandbox.lwjgl.tools.geometry.OBJReader;
import net.mschorn.sandbox.lwjgl.tools.lifecycle.LWJGLLifecycle;
import net.mschorn.sandbox.lwjgl.tools.light.Light;
import net.mschorn.sandbox.lwjgl.tools.mvp.MVP;
import net.mschorn.sandbox.lwjgl.tools.shader.Program;
import net.mschorn.sandbox.lwjgl.tools.shader.Shader;
import net.mschorn.sandbox.lwjgl.tools.state.VAO;
import net.mschorn.sandbox.lwjgl.tools.texture.Sampler;
import net.mschorn.sandbox.lwjgl.tools.texture.Texture;
import net.mschorn.sandbox.lwjgl.tools.window.LWJGLWindow;

import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;


public final class ForwardShading implements LWJGLLifecycle {

    public static final String TITLE = "Forward shading";

    public static final int GL_MAJOR_VERSION = 4;
    public static final int GL_MINOR_VERSION = 2;

    private static final int WIDTH = 800;
    private static final int HEIGHT = 475;

    private static final String TEACUP_IMAGE_DIFFUSE = "net/mschorn/sandbox/resources/images/teacup.diffuse.png";
    private static final String TEACUP_IMAGE_SPECULAR = "net/mschorn/sandbox/resources/images/teacup.specular.png";
    private static final String TEACUP_GEOMETRY = "net/mschorn/sandbox/resources/geometry/teacup.obj";

    private static final String FORWARD_VS = "forward.vs.glsl";
    private static final String FORWARD_FS = "forward.fs.glsl";

    private static final int MVP_BINDING = 0;
    private static final int LIGHT_BINDING = 1;
    private static final int DIFFUSE_TEXTURE_BINDING = 0;
    private static final int SPECULAR_TEXTURE_BINDING = 1;

    private final Sampler sampler;
    private final Texture diffuseTexture;
    private final Texture specularTexture;

    private final Shader vs;
    private final Shader fs;
    private final Program p;

    private final IBO ibo;
    private final VBO vbo;
    private final VAO vao;

    private final MVP mvp;
    private final Light light;


    public static void main(final String[] args) {

        final ForwardShading lifecycle = new ForwardShading();
        final DisplayMode displayMode = new DisplayMode(WIDTH, HEIGHT);
        final PixelFormat pixelFormat = new PixelFormat().withSamples(4);
        final ContextAttribs contextAttribs = new ContextAttribs(GL_MAJOR_VERSION, GL_MINOR_VERSION).withProfileCore(true).withForwardCompatible(true);

        LWJGLWindow.display(lifecycle, displayMode, pixelFormat, contextAttribs, TITLE);

    }


    public ForwardShading() {

        sampler = createSampler();
        diffuseTexture = new Texture(DIFFUSE_TEXTURE_BINDING, getClass().getClassLoader().getResourceAsStream(TEACUP_IMAGE_DIFFUSE));
        specularTexture = new Texture(SPECULAR_TEXTURE_BINDING, getClass().getClassLoader().getResourceAsStream(TEACUP_IMAGE_SPECULAR));

        vs = new Shader(GL_VERTEX_SHADER, getClass().getResourceAsStream(FORWARD_VS));
        fs = new Shader(GL_FRAGMENT_SHADER, getClass().getResourceAsStream(FORWARD_FS));
        p = new Program(vs, fs);

        final OBJReader obj = new OBJReader(getClass().getClassLoader().getResourceAsStream(TEACUP_GEOMETRY));
        ibo = new IBO(obj.getIndices(), GL_STATIC_DRAW);
        vbo = new VBO(obj.getAttributes(), GL_STATIC_DRAW);
        vao = new VAO(GL_TRIANGLES, obj.getIndices().size(), ibo, vbo);

        vao.addVertexAttribute(0, 3, GL_FLOAT, false, 8 * 4, 0 * 4);
        vao.addVertexAttribute(1, 2, GL_FLOAT, false, 8 * 4, 3 * 4);
        vao.addVertexAttribute(2, 3, GL_FLOAT, false, 8 * 4, 5 * 4);

        mvp = new MVP(MVP_BINDING, WIDTH, HEIGHT);
        light = createLight();

    }


    private Sampler createSampler() {

        final Sampler sampler = new Sampler(DIFFUSE_TEXTURE_BINDING, SPECULAR_TEXTURE_BINDING);
        sampler.addParameter(GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        sampler.addParameter(GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        sampler.addParameter(GL_TEXTURE_WRAP_S, GL_REPEAT);
        sampler.addParameter(GL_TEXTURE_WRAP_T, GL_REPEAT);

        return sampler;

    }


    private Light createLight() {

        final Light light = new Light(LIGHT_BINDING);
        light.setAmbient(0.1f, 0.1f, 0.1f);
        light.setDiffuse(1.0f, 1.0f, 1.0f);
        light.setSpecular(0.4f, 0.3f, 0.3f);
        light.setPosition(10, 0, 5);

        return light;

    }


    @Override
    public void glInit(final int width, final int height) {

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);

        sampler.glInit();
        diffuseTexture.glInit();
        specularTexture.glInit();

        vs.glInit();
        fs.glInit();
        p.glInit();
        fs.glDispose();
        vs.glDispose();

        ibo.glInit();
        vbo.glInit();
        vao.glInit();

        mvp.glInit();
        light.glInit();

    }


    @Override
    public void glDisplay(final double timedelta) {

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        mvp.update(timedelta);

        p.glBind();
        mvp.glBind();
        light.glBind();
        sampler.glBind();
        diffuseTexture.glBind();
        specularTexture.glBind();
        vao.glDraw();

    }


    @Override
    public void glDispose() {

        sampler.glDispose();
        diffuseTexture.glDispose();
        specularTexture.glDispose();

        p.glDispose();

        ibo.glDispose();
        vbo.glDispose();
        vao.glDispose();

        mvp.glDispose();
        light.glDispose();

    }


}
