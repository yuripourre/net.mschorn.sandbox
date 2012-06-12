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

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_RGBA8;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glDrawBuffers;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT1;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT2;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT3;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;

import java.io.IOException;
import java.nio.IntBuffer;

import net.mschorn.sandbox.lwjgl.tools.buffer.FBO;
import net.mschorn.sandbox.lwjgl.tools.buffer.IBO;
import net.mschorn.sandbox.lwjgl.tools.buffer.RBO;
import net.mschorn.sandbox.lwjgl.tools.buffer.VBO;
import net.mschorn.sandbox.lwjgl.tools.geometry.OBJReader;
import net.mschorn.sandbox.lwjgl.tools.shader.Program;
import net.mschorn.sandbox.lwjgl.tools.shader.Shader;
import net.mschorn.sandbox.lwjgl.tools.state.VAO;
import net.mschorn.sandbox.lwjgl.tools.texture.Sampler;
import net.mschorn.sandbox.lwjgl.tools.texture.Texture;

import org.lwjgl.BufferUtils;


public final class GeometryPass {

    private static final String TEACUP_IMAGE_DIFFUSE     = "net/mschorn/sandbox/resources/images/teacup.diffuse.png";
    private static final String TEACUP_IMAGE_SPECULAR    = "net/mschorn/sandbox/resources/images/teacup.specular.png";
    private static final String TEACUP_GEOMETRY          = "net/mschorn/sandbox/resources/geometry/teacup.obj";

    private static final String GEOMETRY_VS              = "geometry.vs";
    private static final String GEOMETRY_FS              = "geometry.fs";

    private static final int    V_BUFFER_BINDING         = 0;
    private static final int    VN_BUFFER_BINDING        = 1;
    private static final int    DIFFUSE_BUFFER_BINDING   = 2;
    private static final int    SPECULAR_BUFFER_BINDING  = 3;
    private static final int    DIFFUSE_TEXTURE_BINDING  = 4;
    private static final int    SPECULAR_TEXTURE_BINDING = 5;

    private final Shader        vs;
    private final Shader        fs;
    private final Program       p;

    private final FBO           fbo;
    private final RBO           rbo;

    private final Sampler       sampler;
    private final Texture       vBuffer;
    private final Texture       vnBuffer;
    private final Texture       diffuseBuffer;
    private final Texture       specularBuffer;
    private final Texture       diffuseTexture;
    private final Texture       specularTexture;

    private final IBO           ibo;
    private final VBO           vbo;
    private final VAO           vao;


    public GeometryPass(final int width, final int height) throws IOException {

        vs = new Shader(GL_VERTEX_SHADER, getClass().getResourceAsStream(GEOMETRY_VS));
        fs = new Shader(GL_FRAGMENT_SHADER, getClass().getResourceAsStream(GEOMETRY_FS));
        p = new Program(vs, fs);

        sampler = new Sampler(DIFFUSE_TEXTURE_BINDING, SPECULAR_TEXTURE_BINDING);
        sampler.addParameter(GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        sampler.addParameter(GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        sampler.addParameter(GL_TEXTURE_WRAP_S, GL_REPEAT);
        sampler.addParameter(GL_TEXTURE_WRAP_T, GL_REPEAT);

        vBuffer = new Texture(V_BUFFER_BINDING, width, height, GL_RGBA16F);
        vnBuffer = new Texture(VN_BUFFER_BINDING, width, height, GL_RGBA16F);
        diffuseBuffer = new Texture(DIFFUSE_BUFFER_BINDING, width, height, GL_RGBA8);
        specularBuffer = new Texture(SPECULAR_BUFFER_BINDING, width, height, GL_RGBA8);
        diffuseTexture = new Texture(DIFFUSE_TEXTURE_BINDING, getClass().getClassLoader().getResourceAsStream(TEACUP_IMAGE_DIFFUSE));
        specularTexture = new Texture(SPECULAR_TEXTURE_BINDING, getClass().getClassLoader().getResourceAsStream(TEACUP_IMAGE_SPECULAR));

        fbo = new FBO();
        rbo = new RBO(width, height);

        final OBJReader obj = new OBJReader(getClass().getClassLoader().getResourceAsStream(TEACUP_GEOMETRY));

        ibo = new IBO(obj.getIndices(), GL_STATIC_DRAW);
        vbo = new VBO(obj.getAttributes(), GL_STATIC_DRAW);
        vao = new VAO(GL_TRIANGLES, obj.getIndices().size(), ibo, vbo);

        vbo.addVertexAttribute(0, 3, GL_FLOAT, false, 8 * 4, 0 * 4);
        vbo.addVertexAttribute(1, 2, GL_FLOAT, false, 8 * 4, 3 * 4);
        vbo.addVertexAttribute(2, 3, GL_FLOAT, false, 8 * 4, 5 * 4);

    }


    public Texture getVertexTexture() {

        return vBuffer;

    }


    public Texture getNormalTexture() {

        return vnBuffer;

    }


    public Texture getDiffuseBuffer() {

        return diffuseBuffer;

    }


    public Texture getSpecularBuffer() {

        return specularBuffer;

    }


    public void glInit() {

        vs.glInit();
        fs.glInit();

        p.glInit();

        fs.glDispose();
        vs.glDispose();

        sampler.glInit();
        vBuffer.glInit();
        vnBuffer.glInit();
        diffuseBuffer.glInit();
        specularBuffer.glInit();
        diffuseTexture.glInit();
        specularTexture.glInit();

        fbo.glInit();
        rbo.glInit();
        fbo.glBind();
        rbo.glBind();
        vBuffer.glAttachToFBO(GL_COLOR_ATTACHMENT0);
        vnBuffer.glAttachToFBO(GL_COLOR_ATTACHMENT1);
        diffuseBuffer.glAttachToFBO(GL_COLOR_ATTACHMENT2);
        specularBuffer.glAttachToFBO(GL_COLOR_ATTACHMENT3);

        final IntBuffer buffer = BufferUtils.createIntBuffer(4);
        buffer.put(V_BUFFER_BINDING, GL_COLOR_ATTACHMENT0);
        buffer.put(VN_BUFFER_BINDING, GL_COLOR_ATTACHMENT1);
        buffer.put(DIFFUSE_BUFFER_BINDING, GL_COLOR_ATTACHMENT2);
        buffer.put(SPECULAR_BUFFER_BINDING, GL_COLOR_ATTACHMENT3);
        buffer.rewind();
        glDrawBuffers(buffer);

        fbo.glUnbind();

        ibo.glInit();
        vbo.glInit();
        vao.glInit();

    }


    public void glRender() {

        fbo.glBind();

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        p.glBind();
        sampler.glBind();
        diffuseTexture.glBind();
        specularTexture.glBind();
        vao.glDraw();

        fbo.glUnbind();

    }


    public void glDispose() {

        p.glDispose();

        sampler.glDispose();
        vBuffer.glDispose();
        vnBuffer.glDispose();
        diffuseBuffer.glDispose();
        specularBuffer.glDispose();
        diffuseTexture.glDispose();
        specularTexture.glDispose();

        vao.glDispose();
        vbo.glDispose();
        ibo.glDispose();

        rbo.glDispose();
        fbo.glDispose();

    }

}
