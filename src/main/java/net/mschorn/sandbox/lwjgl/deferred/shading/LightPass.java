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

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;

import java.io.IOException;

import net.mschorn.sandbox.lwjgl.tools.buffer.IBO;
import net.mschorn.sandbox.lwjgl.tools.buffer.VBO;
import net.mschorn.sandbox.lwjgl.tools.geometry.Quad;
import net.mschorn.sandbox.lwjgl.tools.light.Light;
import net.mschorn.sandbox.lwjgl.tools.shader.Program;
import net.mschorn.sandbox.lwjgl.tools.shader.Shader;
import net.mschorn.sandbox.lwjgl.tools.state.VAO;
import net.mschorn.sandbox.lwjgl.tools.texture.Sampler;
import net.mschorn.sandbox.lwjgl.tools.texture.Texture;


public final class LightPass {

    private static final String LIGHT_VS      = "light.vs";
    private static final String LIGHT_FS      = "light.fs";

    private static final int    LIGHT_BINDING = 1;

    private final Shader        vs;
    private final Shader        fs;
    private final Program       p;

    private final Light[]       lights;

    private final Sampler       sampler;
    private final Texture       vBuffer;
    private final Texture       vnBuffer;
    private final Texture       diffuseBuffer;
    private final Texture       specularBuffer;

    private final IBO           ibo;
    private final VBO           vbo;
    private final VAO           vao;


    public LightPass(final Texture vBuffer, final Texture vnBuffer, final Texture diffuseBuffer, final Texture specularBuffer) throws IOException {

        this.vBuffer = vBuffer;
        this.vnBuffer = vnBuffer;
        this.diffuseBuffer = diffuseBuffer;
        this.specularBuffer = specularBuffer;

        vs = new Shader(GL_VERTEX_SHADER, getClass().getResourceAsStream(LIGHT_VS));
        fs = new Shader(GL_FRAGMENT_SHADER, getClass().getResourceAsStream(LIGHT_FS));
        p = new Program(vs, fs);

        lights = new Light[2];

        lights[0] = new Light(LIGHT_BINDING);
        lights[0].setAmbient(0.05f, 0.05f, 0.05f);
        lights[0].setDiffuse(0.6f, 0.5f, 0.5f);
        lights[0].setSpecular(0.6f, 0.3f, 0.3f);
        lights[0].setPosition(10, 0, 5);

        lights[1] = new Light(LIGHT_BINDING);
        lights[1].setAmbient(0.05f, 0.05f, 0.05f);
        lights[1].setDiffuse(0.5f, 0.5f, 0.6f);
        lights[1].setSpecular(0.3f, 0.3f, 0.6f);
        lights[1].setPosition(5, -20, 5);

        sampler = new Sampler(0, 1, 2, 3);
        sampler.addParameter(GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        sampler.addParameter(GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        sampler.addParameter(GL_TEXTURE_WRAP_S, GL_REPEAT);
        sampler.addParameter(GL_TEXTURE_WRAP_T, GL_REPEAT);

        final Quad quad = new Quad();

        ibo = new IBO(quad.getIndices(), GL_STATIC_DRAW);
        vbo = new VBO(quad.getAttributes(), GL_STATIC_DRAW);
        vao = new VAO(GL_TRIANGLE_STRIP, quad.getIndices().size(), ibo, vbo);

        vbo.addVertexAttribute(0, 3, GL_FLOAT, false, 8 * 4, 0 * 4);
        vbo.addVertexAttribute(1, 2, GL_FLOAT, false, 8 * 4, 3 * 4);
        vbo.addVertexAttribute(2, 3, GL_FLOAT, false, 8 * 4, 5 * 4);

    }


    public void glInit() {

        vs.glInit();
        fs.glInit();

        p.glInit();

        fs.glDispose();
        vs.glDispose();

        sampler.glInit();

        for (final Light light : lights)
            light.glInit();

        ibo.glInit();
        vbo.glInit();
        vao.glInit();

    }


    public void glRender() {

        p.glBind();
        sampler.glBind();
        vBuffer.glBind();
        vnBuffer.glBind();
        diffuseBuffer.glBind();
        specularBuffer.glBind();

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE);

        for (final Light light : lights) {

            light.glBind();
            vao.glDraw();
            glClear(GL_DEPTH_BUFFER_BIT);

        }

        glDisable(GL_BLEND);

    }


    public void glDispose() {

        p.glDispose();

        for (final Light light : lights)
            light.glDispose();

        sampler.glDispose();

        vao.glDispose();
        vbo.glDispose();
        ibo.glDispose();

    }

}
