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


package net.mschorn.sandbox.lwjgl.tools.geometry;

import java.util.List;


public class Point implements Comparable<Point> {

    private final float x;
    private final float y;
    private final float z;


    public Point(final Geometry geometry, final int index) {

        final Descriptor descriptor = geometry.getAttributeDescriptor(Attribute.V);
        final int offset = index * descriptor.getStride() / 4 + descriptor.getPointer() / 4;

        final List<Float> attributes = geometry.getAttributes();

        this.x = attributes.get(offset + 0);
        this.y = attributes.get(offset + 1);
        this.z = attributes.get(offset + 2);

    }


    @Override
    public final int hashCode() {

        final int prime = 31;
        int result = 1;
        result = prime * result + Float.floatToIntBits(x);
        result = prime * result + Float.floatToIntBits(y);
        result = prime * result + Float.floatToIntBits(z);
        return result;

    }


    @Override
    public final boolean equals(final Object obj) {

        if (this == obj)
            return true;

        if (obj == null)
            return false;

        if (getClass() != obj.getClass())
            return false;

        final Point other = (Point) obj;
        if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x))
            return false;

        if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y))
            return false;

        if (Float.floatToIntBits(z) != Float.floatToIntBits(other.z))
            return false;

        return true;

    }


    @Override
    public final int compareTo(final Point other) {

        if (this.x < other.x)
            return -1;

        if (this.x > other.x)
            return +1;

        if (this.y < other.y)
            return -1;

        if (this.y > other.y)
            return +1;

        if (this.z < other.z)
            return -1;

        if (this.z > other.z)
            return +1;

        return 0;

    }


}
