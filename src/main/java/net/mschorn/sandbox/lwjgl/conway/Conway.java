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

package net.mschorn.sandbox.lwjgl.conway;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_INFO_LOG_LENGTH;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glDetachShader;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glGetProgram;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetShader;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteFramebuffers;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glFramebufferTexture2D;
import static org.lwjgl.opengl.GL30.glGenFramebuffers;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL33.glBindSampler;
import static org.lwjgl.opengl.GL33.glDeleteSamplers;
import static org.lwjgl.opengl.GL33.glGenSamplers;
import static org.lwjgl.opengl.GL33.glSamplerParameteri;

import java.io.UnsupportedEncodingException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Random;

import javax.swing.JOptionPane;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.PixelFormat;


public final class Conway {


    private static final String VERTEX_SHADER  = "" +
            "#version 420 core                                                                       \n" +
            "                                                                                        \n" +
            "layout(location = 0) in vec4 position;                                                  \n" +
            "                                                                                        \n" +
            "void main() {                                                                           \n" +
            "                                                                                        \n" +
            "    gl_Position = position;                                                             \n" +
            "                                                                                        \n" +
            "}                                                                                       \n";

    private static final String DISPLAY_SHADER = "" +
            "#version 420 core                                                                       \n" +
            "                                                                                        \n" +
            "layout(binding = 0) uniform sampler2D texture;                                          \n" +
            "layout(location = 0) out vec4 color;                                                    \n" +
            "                                                                                        \n" +
            "void main() {                                                                           \n" +
            "                                                                                        \n" +
            "   color = texelFetch(texture, ivec2(gl_FragCoord.x, gl_FragCoord.y), 0);               \n" +
            "                                                                                        \n" +
            "}                                                                                       \n";

    private static final String CONWAY_SHADER  = "" +
            "#version 420 core                                                                       \n" +
            "                                                                                        \n" +
            "layout(binding = 0) uniform sampler2D texture;                                          \n" +
            "layout(location = 0) out vec4 color;                                                    \n" +
            "                                                                                        \n" +
            "void main() {                                                                           \n" +
            "                                                                                        \n" +
            "    float n = texelFetch(texture, ivec2(gl_FragCoord.x  - 1, gl_FragCoord.y - 1), 0).r; \n" +
            "    n += texelFetch(texture, ivec2(gl_FragCoord.x - 0, gl_FragCoord.y - 1), 0).r;       \n" +
            "    n += texelFetch(texture, ivec2(gl_FragCoord.x + 1, gl_FragCoord.y - 1), 0).r;       \n" +
            "    n += texelFetch(texture, ivec2(gl_FragCoord.x - 1, gl_FragCoord.y - 0), 0).r;       \n" +
            "    n += texelFetch(texture, ivec2(gl_FragCoord.x + 1, gl_FragCoord.y - 0), 0).r;       \n" +
            "    n += texelFetch(texture, ivec2(gl_FragCoord.x - 1, gl_FragCoord.y + 1), 0).r;       \n" +
            "    n += texelFetch(texture, ivec2(gl_FragCoord.x - 0, gl_FragCoord.y + 1), 0).r;       \n" +
            "    n += texelFetch(texture, ivec2(gl_FragCoord.x + 1, gl_FragCoord.y + 1), 0).r;       \n" +
            "                                                                                        \n" +
            "    int i = int(n);                                                                     \n" +
            "    if(texelFetch(texture, ivec2(gl_FragCoord.x, gl_FragCoord.y), 0).r > 0) {           \n" +
            "        if(i == 2 || i == 3) {                                                          \n" +
            "            color = vec4(1,1,1,1);                                                      \n" +
            "        }                                                                               \n" +
            "    } else {                                                                            \n" +
            "        if(i == 3) {                                                                    \n" +
            "            color = vec4(1,1,1,1);                                                      \n" +
            "        }                                                                               \n" +
            "     }                                                                                  \n" +
            "}                                                                                       \n";

    private static final int    COLOR          = 0xFFFFFF;
    private static final int    WIDTH          = 800;
    private static final int    HEIGHT         = 475;

    private final Program       display        = new Program(new Program.Shader(VERTEX_SHADER, GL_VERTEX_SHADER), new Program.Shader(DISPLAY_SHADER, GL_FRAGMENT_SHADER));
    private final Program       conway         = new Program(new Program.Shader(VERTEX_SHADER, GL_VERTEX_SHADER), new Program.Shader(CONWAY_SHADER, GL_FRAGMENT_SHADER));
    private final Quad          quad           = new Quad();
    private final Texture       front          = new Texture();
    private final Texture       back           = new Texture();
    private final FBO           fbo            = new FBO();

    private int                 frame          = 0;
    private long                time           = 0;


    public void init() throws UnsupportedEncodingException {

        try {

            Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
            Display.create(new PixelFormat(), new ContextAttribs(4, 2).withProfileCore(true).withForwardCompatible(true));
            Display.setTitle("Conway's Game of Life");

        } catch (final LWJGLException e) {

            JOptionPane.showMessageDialog(null, "Your Graphics Card does not support all necessary functions.", "OpenGL 4.2 Context Creation", JOptionPane.ERROR_MESSAGE);

            System.exit(1);

        }

        display.init();
        conway.init();
        quad.init();
        front.init();
        back.init();
        fbo.init();

    }


    private void step(final Texture back, final Texture front) {

        if (frame++ > 1000) {

            Display.setTitle("Conway's Game of Life: " + Math.round(frame / ((-(time - (time = System.nanoTime()))) / 1000000000.0)) + "fps");

            frame = 0;

        }

        conway.use();
        fbo.bind();
        back.attachToFBO();
        front.bind();
        glClear(GL_COLOR_BUFFER_BIT);
        quad.draw();
        fbo.unbind();

        display.use();
        back.bind();
        quad.draw();

        Display.update();

    }


    public void display() {

        while (!Display.isCloseRequested()) {

            step(back, front);
            step(front, back);

        }

        display.delete();
        conway.delete();
        quad.delete();
        front.delete();
        back.delete();
        fbo.delete();

        Display.destroy();

    }


    private static class Program {

        private static class Shader {

            private final String code;
            private final int    type;

            private int          handle;


            public Shader(final String code, final int type) {

                this.code = code;
                this.type = type;

            }


            private void init() {

                handle = glCreateShader(type);
                glShaderSource(handle, code);
                glCompileShader(handle);

                if (glGetShader(handle, GL_COMPILE_STATUS) == GL_FALSE) {

                    final int length = glGetShader(handle, GL_INFO_LOG_LENGTH);
                    System.err.println(glGetShaderInfoLog(handle, length));

                    handle = -1;

                }

            }


            private void attach(final int program) {

                glAttachShader(program, handle);

            }


            private void detach(final int program) {

                glDetachShader(program, handle);

            }


            private void delete() {

                glDeleteShader(handle);

            }

        }


        private final Shader[] shaders;

        private int            handle;


        public Program(final Shader... shaders) {

            this.shaders = shaders;

        }


        public void init() {

            handle = glCreateProgram();

            for (final Shader shader : shaders) {

                shader.init();
                shader.attach(handle);

            }

            glLinkProgram(handle);

            if (glGetProgram(handle, GL_LINK_STATUS) == GL_FALSE) {

                final int length = glGetProgram(handle, GL_INFO_LOG_LENGTH);
                System.err.println(glGetProgramInfoLog(handle, length));

                handle = -1;

            }

            for (final Shader shader : shaders) {

                shader.detach(handle);
                shader.delete();

            }

        }


        public void use() {

            glUseProgram(handle);

        }


        public void delete() {

            glDeleteProgram(handle);

        }

    }


    private static class Quad {

        private static final float[] QUAD = new float[] { -1, -1, +1, +1, -1, +1, -1, -1, +1, -1, +1, +1 };

        private int                  vao;
        private int                  vbo;


        public void init() {

            final FloatBuffer quad = BufferUtils.createFloatBuffer(QUAD.length);
            quad.put(QUAD);
            quad.rewind();

            vao = glGenVertexArrays();
            glBindVertexArray(vao);

            vbo = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBufferData(GL_ARRAY_BUFFER, quad, GL_STATIC_DRAW);
            glEnableVertexAttribArray(0);
            glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);

        }


        public void draw() {

            glBindVertexArray(vao);
            glDrawArrays(GL_TRIANGLES, 0, 6);

        }


        public void delete() {

            glDeleteBuffers(vbo);
            glDeleteVertexArrays(vao);

        }

    }


    private static class Texture {

        private int sampler;
        private int handle;


        public void init() {

            sampler = glGenSamplers();
            glSamplerParameteri(sampler, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glSamplerParameteri(sampler, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glSamplerParameteri(sampler, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glSamplerParameteri(sampler, GL_TEXTURE_WRAP_T, GL_REPEAT);
            glBindSampler(0, sampler);

            final Random random = new Random(0);
            final IntBuffer data = BufferUtils.createIntBuffer(WIDTH * HEIGHT);
            for (int i = 0; i < data.capacity(); i++)
                if (random.nextInt(100) > 90)
                    data.put(i, COLOR);

            handle = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, handle);
            glTexImage2D(GL_TEXTURE_2D, 0, GL11.GL_RGBA8, Display.getDisplayMode().getWidth(), Display.getDisplayMode().getHeight(), 0, GL12.GL_BGRA, GL_UNSIGNED_BYTE, data);

        }


        public void bind() {

            glBindSampler(0, sampler);
            glBindTexture(GL_TEXTURE_2D, handle);

        }


        public void attachToFBO() {

            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, handle, 0);

        }


        public void delete() {

            glDeleteSamplers(sampler);
            glDeleteTextures(handle);

        }

    }

    private static class FBO {

        private int handle;


        public void init() {

            handle = glGenFramebuffers();

        }


        public void bind() {

            glBindFramebuffer(GL_FRAMEBUFFER, handle);

        }


        public void unbind() {

            glBindFramebuffer(GL_FRAMEBUFFER, 0);

        }


        public void delete() {

            glDeleteFramebuffers(handle);

        }

    }


    public static void main(final String[] args) throws UnsupportedEncodingException {

        final Conway main = new Conway();

        main.init();
        main.display();

    }

}
