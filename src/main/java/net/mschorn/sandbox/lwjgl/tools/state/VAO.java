/*
 * Copyright 2012 - 2013, Michael Schorn (me@mschorn.net). All rights reserved.
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


package net.mschorn.sandbox.lwjgl.tools.state;

import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.util.HashMap;
import java.util.Map;

import net.mschorn.sandbox.lwjgl.tools.buffer.IBO;
import net.mschorn.sandbox.lwjgl.tools.buffer.VBO;
import net.mschorn.sandbox.lwjgl.tools.geometry.Descriptor;


public class VAO {

    private final Map<Integer, Descriptor> descriptors = new HashMap<Integer, Descriptor>();

    private final int mode;
    private final int size;
    private final IBO ibo;
    private final VBO vbo;

    private int handle = -1;


    public VAO(final int mode, final int size, final IBO ibo, final VBO vbo) {

        this.mode = mode;
        this.size = size;
        this.ibo = ibo;
        this.vbo = vbo;

    }


    public final void addVertexAttribute(final int index, final Descriptor descriptor) {

        descriptors.put(index, descriptor);

    }


    public final void glInit() {

        handle = glGenVertexArrays();

        glBindVertexArray(handle);

        vbo.glBind();

        for (final int i : descriptors.keySet()) {

            final Descriptor descriptor = descriptors.get(i);

            glEnableVertexAttribArray(i);
            glVertexAttribPointer(i, descriptor.getSize(), descriptor.getType(), descriptor.isNormalized(), descriptor.getStride(), descriptor.getPointer());

        }

        ibo.glBind();

        glBindVertexArray(0);

    }


    public final void glDraw() {

        glBindVertexArray(handle);
        glDrawElements(mode, size, GL_UNSIGNED_INT, 0);

    }


    public final void glDispose() {

        glDeleteVertexArrays(handle);

    }


}
