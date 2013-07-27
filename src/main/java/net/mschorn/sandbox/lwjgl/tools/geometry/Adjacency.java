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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Adjacency implements Geometry {

    private final List<Integer> indices = new ArrayList<>();
    private final Geometry geometry;

    private final Map<Edge, List<Integer>> edgeToIndices = new HashMap<>();


    public Adjacency(final Geometry geometry) {

        this.geometry = geometry;

        final List<Integer> origin = geometry.getIndices();
        for (int i = 0; i < origin.size(); i += 3) {

            final int i1 = origin.get(i + 0);
            final int i2 = origin.get(i + 1);
            final int i3 = origin.get(i + 2);

            final Point p1 = new Point(geometry, i1);
            final Point p2 = new Point(geometry, i2);
            final Point p3 = new Point(geometry, i3);

            final Edge e1 = new Edge(p2, p3);
            final Edge e2 = new Edge(p1, p3);
            final Edge e3 = new Edge(p1, p2);

            if (!edgeToIndices.containsKey(e1))
                edgeToIndices.put(e1, new ArrayList<Integer>());

            if (!edgeToIndices.containsKey(e2))
                edgeToIndices.put(e2, new ArrayList<Integer>());

            if (!edgeToIndices.containsKey(e3))
                edgeToIndices.put(e3, new ArrayList<Integer>());

            edgeToIndices.get(e1).add(i1);
            edgeToIndices.get(e2).add(i2);
            edgeToIndices.get(e3).add(i3);

        }


        for (int i = 0; i < origin.size(); i += 3) {

            final int i1 = origin.get(i + 0);
            final int i2 = origin.get(i + 1);
            final int i3 = origin.get(i + 2);

            indices.add(i1);
            indices.add(getAdj(i1, i2, i3));
            indices.add(i2);
            indices.add(getAdj(i2, i3, i1));
            indices.add(i3);
            indices.add(getAdj(i3, i1, i2));
        }


    }


    private int getAdj(final int i1, final int i2, final int not) {

        final Point p1 = new Point(geometry, i1);
        final Point p2 = new Point(geometry, i2);
        final Edge e = new Edge(p1, p2);

        final List<Integer> results = edgeToIndices.get(e);
        for (final int result : results)
            if (result != not)
                return result;

        throw new IllegalArgumentException();

    }


    @Override
    public Mode getMode() {

        return Mode.TRIANGLES_ADJACENCY;

    }


    @Override
    public List<Integer> getIndices() {

        return Collections.unmodifiableList(indices);

    }


    @Override
    public List<Float> getAttributes() {

        return geometry.getAttributes();

    }


    @Override
    public Descriptor getAttributeDescriptor(final Attribute attribute) {

        return geometry.getAttributeDescriptor(attribute);

    }


}
