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


package net.mschorn.sandbox.lwjgl.tools.geometry;

import static org.lwjgl.opengl.GL11.GL_FLOAT;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Quad implements Geometry {

    private static final Integer[] QUAD_IBO = new Integer[] {

        1, 2, 0, 3

    };

    // x, y, z, s, t, nx, ny, nz
    private static final Float[] QUAD_VBO = new Float[] {

        -1.0f, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -1.0f,
        +1.0f, -1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, -1.0f,
        +1.0f, +1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, -1.0f,
        -1.0f, +1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, -1.0f

    };

    private final Map<Attribute, Descriptor> descriptors = new HashMap<>();


    public Quad() {

        descriptors.put(Attribute.V, new Descriptor(3, GL_FLOAT, false, 8 * 4, 0 * 4));
        descriptors.put(Attribute.VT, new Descriptor(2, GL_FLOAT, false, 8 * 4, 3 * 4));
        descriptors.put(Attribute.VN, new Descriptor(3, GL_FLOAT, false, 8 * 4, 5 * 4));

    }


    @Override
    public final Mode getMode() {

        return Mode.TRIANGLE_STRIP;

    }


    @Override
    public final List<Integer> getIndices() {

        return Collections.unmodifiableList(Arrays.asList(QUAD_IBO));

    }


    @Override
    public final List<Float> getAttributes() {

        return Collections.unmodifiableList(Arrays.asList(QUAD_VBO));

    }


    @Override
    public final Descriptor getAttributeDescriptor(final Attribute attribute) {

        return descriptors.get(attribute);

    }


}
