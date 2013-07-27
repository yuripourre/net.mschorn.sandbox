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

import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL30.GL_RG16;
import net.mschorn.sandbox.lwjgl.tools.buffer.IBO;
import net.mschorn.sandbox.lwjgl.tools.buffer.VBO;
import net.mschorn.sandbox.lwjgl.tools.geometry.Adjacency;
import net.mschorn.sandbox.lwjgl.tools.geometry.Attribute;
import net.mschorn.sandbox.lwjgl.tools.geometry.Geometry;
import net.mschorn.sandbox.lwjgl.tools.geometry.OBJReader;
import net.mschorn.sandbox.lwjgl.tools.lifecycle.LWJGLLifecycle;
import net.mschorn.sandbox.lwjgl.tools.light.Light;
import net.mschorn.sandbox.lwjgl.tools.mvp.MVP;
import net.mschorn.sandbox.lwjgl.tools.state.VAO;
import net.mschorn.sandbox.lwjgl.tools.texture.Texture;
import net.mschorn.sandbox.lwjgl.tools.window.LWJGLWindow;

import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;


public final class ShadowVolume implements LWJGLLifecycle {

    public static final String TITLE = "Shadow volume";

    public static final int GL_MAJOR_VERSION = 4;
    public static final int GL_MINOR_VERSION = 2;

    private static final String TEACUP_GEOMETRY = "net/mschorn/sandbox/resources/geometry/teacup.obj";

    private static final int WIDTH = 800;
    private static final int HEIGHT = 475;

    private static final int MVP_BINDING = 0;
    private static final int LIGHT_BINDING = 1;
    private static final int SHADOW_BINDING = 0;

    private final IBO geometryIBO;
    private final VBO geometryVBO;
    private final VAO geometryVAO;

    private final IBO adjacencyIBO;
    private final VAO adjacencyVAO;

    private final MVP mvp;
    private final Light light;

    private Texture shadow;

    private LWJGLLifecycle shadowPass;
    private LWJGLLifecycle renderPass;


    public static void main(final String[] args) {

        final ShadowVolume lifecycle = new ShadowVolume();
        final DisplayMode displayMode = new DisplayMode(WIDTH, HEIGHT);
        final PixelFormat pixelFormat = new PixelFormat().withSamples(4);
        final ContextAttribs contextAttribs = new ContextAttribs(GL_MAJOR_VERSION, GL_MINOR_VERSION).withProfileCore(true).withForwardCompatible(true);

        LWJGLWindow.display(lifecycle, displayMode, pixelFormat, contextAttribs, TITLE);

    }


    public ShadowVolume() {

        final Geometry geometry = new OBJReader(getClass().getClassLoader().getResourceAsStream(TEACUP_GEOMETRY));
        geometryIBO = new IBO(geometry.getIndices(), GL_STATIC_DRAW);
        geometryVBO = new VBO(geometry.getAttributes(), GL_STATIC_DRAW);
        geometryVAO = new VAO(geometry.getMode().getGlMode(), geometry.getIndices().size(), geometryIBO, geometryVBO);

        geometryVAO.addVertexAttribute(0, geometry.getAttributeDescriptor(Attribute.V));
        geometryVAO.addVertexAttribute(1, geometry.getAttributeDescriptor(Attribute.VT));
        geometryVAO.addVertexAttribute(2, geometry.getAttributeDescriptor(Attribute.VN));

        final Geometry adjacency = new Adjacency(geometry);
        adjacencyIBO = new IBO(adjacency.getIndices(), GL_STATIC_DRAW);
        adjacencyVAO = new VAO(adjacency.getMode().getGlMode(), adjacency.getIndices().size(), adjacencyIBO, geometryVBO);

        adjacencyVAO.addVertexAttribute(0, geometry.getAttributeDescriptor(Attribute.V));

        mvp = new MVP(MVP_BINDING, WIDTH, HEIGHT);
        light = createLight();

    }


    private Light createLight() {

        final Light light = new Light(LIGHT_BINDING);
        light.setAmbient(0.1f, 0.1f, 0.1f);
        light.setDiffuse(1.0f, 1.0f, 1.0f);
        light.setSpecular(0.4f, 0.3f, 0.3f);
        light.setPosition(20, 1, 10);

        return light;

    }


    @Override
    public void glInit(final int width, final int height) {

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glBlendFunc(GL_ONE, GL_ONE);

        geometryIBO.glInit();
        geometryVBO.glInit();
        geometryVAO.glInit();

        adjacencyIBO.glInit();
        adjacencyVAO.glInit();

        mvp.glInit();
        light.glInit();

        shadow = new Texture(SHADOW_BINDING, width, height, GL_RG16);

        shadowPass = new ShadowPass(adjacencyVAO, shadow);
        renderPass = new RenderPass(geometryVAO, shadow);

        shadow.glInit();
        shadowPass.glInit(width, height);
        renderPass.glInit(width, height);

    }


    @Override
    public void glDisplay(final double timedelta) {

        mvp.update(timedelta);

        mvp.glBind();
        light.glBind();

        shadowPass.glDisplay(timedelta);
        renderPass.glDisplay(timedelta);

    }


    @Override
    public void glDispose() {

        shadowPass.glDispose();
        renderPass.glDispose();

        adjacencyIBO.glDispose();
        adjacencyVAO.glDispose();

        geometryIBO.glDispose();
        geometryVBO.glDispose();
        geometryVAO.glDispose();

        mvp.glDispose();
        light.glDispose();

    }


}
