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


package net.mschorn.sandbox.lwjgl.tools.buffer;

import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.BufferUtils;


public class VBO {

    private final static class VertexAttribute {

        private final int     size;
        private final int     type;
        private final boolean normalized;
        private final int     stride;
        private final int     pointer;


        private VertexAttribute(final int size, final int type, final boolean normalized, final int stride, final int pointer) {

            this.size = size;
            this.type = type;
            this.normalized = normalized;
            this.stride = stride;
            this.pointer = pointer;

        }

    }


    private final Map<Integer, VertexAttribute> attributes = new HashMap<Integer, VertexAttribute>();

    private final FloatBuffer                   buffer;
    private final int                           usage;

    private int                                 handle     = -1;


    public VBO(final List<Float> values, final int usage) {

        this.buffer = BufferUtils.createFloatBuffer(values.size());
        this.usage = usage;

        for (final float f : values)
            buffer.put(f);

        buffer.flip();

    }


    public final void addVertexAttribute(final int index, final int size, final int type, final boolean normalized, final int stride, final int pointer) {

        attributes.put(index, new VertexAttribute(size, type, normalized, stride, pointer));

    }


    public final void glInit() {

        handle = glGenBuffers();

        glBindBuffer(GL_ARRAY_BUFFER, handle);
        glBufferData(GL_ARRAY_BUFFER, buffer, usage);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

    }


    public final void glBind() {

        glBindBuffer(GL_ARRAY_BUFFER, handle);

        for (final int i : attributes.keySet()) {

            final VertexAttribute attribute = attributes.get(i);

            glEnableVertexAttribArray(i);
            glVertexAttribPointer(i, attribute.size, attribute.type, attribute.normalized, attribute.stride, attribute.pointer);

        }

    }


    public final void glDispose() {

        glDeleteBuffers(handle);

    }

}
