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


package net.mschorn.sandbox.lwjgl.forward.samples.shadow;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import net.mschorn.sandbox.lwjgl.tools.lifecycle.LWJGLLifecycle;
import net.mschorn.sandbox.lwjgl.tools.shader.Program;
import net.mschorn.sandbox.lwjgl.tools.shader.Shader;
import net.mschorn.sandbox.lwjgl.tools.state.VAO;
import net.mschorn.sandbox.lwjgl.tools.texture.Sampler;
import net.mschorn.sandbox.lwjgl.tools.texture.Texture;


public class RenderPass implements LWJGLLifecycle {

    private static final String TEACUP_IMAGE_DIFFUSE = "net/mschorn/sandbox/resources/images/teacup.diffuse.png";
    private static final String TEACUP_IMAGE_SPECULAR = "net/mschorn/sandbox/resources/images/teacup.specular.png";

    private static final String FORWARD_VS = "render.vs.glsl";
    private static final String FORWARD_FS = "render.fs.glsl";

    private static final int DIFFUSE_TEXTURE_BINDING = 1;
    private static final int SPECULAR_TEXTURE_BINDING = 2;

    private final Sampler sampler;
    private final Texture diffuse;
    private final Texture specular;
    private final Texture shadow;

    private final Shader vs;
    private final Shader fs;
    private final Program p;

    private final VAO vao;


    RenderPass(final VAO vao, final Texture shadow) {

        this.vao = vao;
        this.shadow = shadow;

        diffuse = new Texture(DIFFUSE_TEXTURE_BINDING, getClass().getClassLoader().getResourceAsStream(TEACUP_IMAGE_DIFFUSE));
        specular = new Texture(SPECULAR_TEXTURE_BINDING, getClass().getClassLoader().getResourceAsStream(TEACUP_IMAGE_SPECULAR));

        sampler = createSampler();

        vs = new Shader(GL_VERTEX_SHADER, getClass().getResourceAsStream(FORWARD_VS));
        fs = new Shader(GL_FRAGMENT_SHADER, getClass().getResourceAsStream(FORWARD_FS));
        p = new Program(vs, fs);

    }


    private Sampler createSampler() {

        final Sampler sampler = new Sampler(diffuse.getUnit(), specular.getUnit(), shadow.getUnit());
        sampler.addParameter(GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        sampler.addParameter(GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        sampler.addParameter(GL_TEXTURE_WRAP_S, GL_REPEAT);
        sampler.addParameter(GL_TEXTURE_WRAP_T, GL_REPEAT);

        return sampler;

    }


    @Override
    public void glInit(final int width, final int height) {

        sampler.glInit();
        diffuse.glInit();
        specular.glInit();

        vs.glInit();
        fs.glInit();
        p.glInit();
        fs.glDispose();
        vs.glDispose();

    }


    @Override
    public void glDisplay(final double timedelta) {

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        p.glBind();
        sampler.glBind();
        diffuse.glBind();
        specular.glBind();
        shadow.glBind();
        vao.glDraw();

    }


    @Override
    public void glDispose() {

        sampler.glDispose();
        diffuse.glDispose();
        specular.glDispose();

        p.glDispose();

    }


}
