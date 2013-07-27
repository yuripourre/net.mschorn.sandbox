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

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glDepthMask;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;
import net.mschorn.sandbox.lwjgl.tools.buffer.FBO;
import net.mschorn.sandbox.lwjgl.tools.buffer.RBO;
import net.mschorn.sandbox.lwjgl.tools.lifecycle.LWJGLLifecycle;
import net.mschorn.sandbox.lwjgl.tools.shader.Program;
import net.mschorn.sandbox.lwjgl.tools.shader.Shader;
import net.mschorn.sandbox.lwjgl.tools.state.VAO;
import net.mschorn.sandbox.lwjgl.tools.texture.Texture;


public class ShadowPass implements LWJGLLifecycle {

    private static final String PASSTHRU_VS = "passthru.vs.glsl";
    private static final String PASSTHRU_FS = "passthru.fs.glsl";

    private static final String SHADOW_VS = "shadow.vs.glsl";
    private static final String SHADOW_GS = "shadow.gs.glsl";
    private static final String SHADOW_FS = "shadow.fs.glsl";

    private final Shader passthruVS;
    private final Shader passthruFS;
    private final Program passthruP;

    private final Shader shadowVS;
    private final Shader shadowGS;
    private final Shader shadowFS;
    private final Program shadowP;

    private final Texture shadow;

    private final VAO vao;

    private final FBO fbo;
    private final RBO rbo;


    ShadowPass(final VAO vao, final Texture shadow) {

        this.vao = vao;
        this.shadow = shadow;

        final int width = shadow.getWidth();
        final int height = shadow.getHeight();

        passthruVS = new Shader(GL_VERTEX_SHADER, getClass().getResourceAsStream(PASSTHRU_VS));
        passthruFS = new Shader(GL_FRAGMENT_SHADER, getClass().getResourceAsStream(PASSTHRU_FS));
        passthruP = new Program(passthruVS, passthruFS);

        shadowVS = new Shader(GL_VERTEX_SHADER, getClass().getResourceAsStream(SHADOW_VS));
        shadowGS = new Shader(GL_GEOMETRY_SHADER, getClass().getResourceAsStream(SHADOW_GS));
        shadowFS = new Shader(GL_FRAGMENT_SHADER, getClass().getResourceAsStream(SHADOW_FS));
        shadowP = new Program(shadowVS, shadowGS, shadowFS);

        fbo = new FBO();
        rbo = new RBO(width, height);

    }


    @Override
    public void glInit(final int width, final int height) {

        passthruVS.glInit();
        passthruFS.glInit();
        passthruP.glInit();
        passthruFS.glDispose();
        passthruVS.glDispose();

        shadowVS.glInit();
        shadowGS.glInit();
        shadowFS.glInit();
        shadowP.glInit();
        shadowFS.glDispose();
        shadowGS.glDispose();
        shadowVS.glDispose();

        fbo.glInit();
        rbo.glInit();
        fbo.glBind();
        rbo.glBind();
        shadow.glAttachToFBO(GL_COLOR_ATTACHMENT0);
        fbo.glUnbind();

    }


    @Override
    public void glDisplay(final double timedelta) {

        fbo.glBind();

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        passthruP.glBind();
        vao.glDraw();

        glEnable(GL_BLEND);
        glDisable(GL_CULL_FACE);
        glDepthMask(false);

        shadowP.glBind();
        vao.glDraw();

        glDepthMask(true);
        glEnable(GL_CULL_FACE);
        glDisable(GL_BLEND);

        fbo.glUnbind();

    }


    @Override
    public void glDispose() {

        passthruP.glDispose();
        shadowP.glDispose();

        fbo.glDispose();
        rbo.glDispose();

    }


}
