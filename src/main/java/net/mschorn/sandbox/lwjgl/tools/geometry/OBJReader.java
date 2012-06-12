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


package net.mschorn.sandbox.lwjgl.tools.geometry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class OBJReader {

    private final List<Integer> indices    = new ArrayList<Integer>();
    private final List<Float>   attributes = new ArrayList<Float>();

    private int                 index      = 0;


    public OBJReader(final InputStream is) throws IOException {

        parse(is);

    }


    public final List<Integer> getIndices() {

        return Collections.unmodifiableList(indices);

    }


    public final List<Float> getAttributes() {

        return Collections.unmodifiableList(attributes);

    }


    private final void parse(final InputStream is) throws IOException {

        final List<Float> v = new ArrayList<Float>();
        final List<Float> vt = new ArrayList<Float>();
        final List<Float> vn = new ArrayList<Float>();
        final Map<String, Integer> f = new HashMap<String, Integer>();

        final BufferedReader br = new BufferedReader(new InputStreamReader(is));

        String line;
        while ((line = br.readLine()) != null) {

            if (line.contains("#"))
                line = line.substring(0, line.indexOf("#"));

            line = line.trim();

            if (line.length() > 0) {

                final String[] values = line.split(" ");

                switch (values[0]) {

                case "v":
                    parseV(v, values);
                    break;

                case "vt":
                    parseVT(vt, values);
                    break;

                case "vn":
                    parseVN(vn, values);
                    break;

                case "f":
                    parseF(v, vt, vn, f, values);
                    break;

                default:
                    break;

                }

            }

        }

    }


    private final void parseV(final List<Float> v, final String[] values) throws IOException {

        if (3 != values.length - 1)
            throw new IOException("unsupported file format (v dimension)");

        for (int i = 1; i <= 3; i++)
            v.add(Float.valueOf(values[i]));

    }


    private final void parseVT(final List<Float> vt, final String[] values) throws IOException {

        if (2 != values.length - 1)
            throw new IOException("unsupported file format (vt dimension)");

        for (int i = 1; i <= 2; i++)
            vt.add(Float.valueOf(values[i]));

    }


    private final void parseVN(final List<Float> vn, final String[] values) throws IOException {

        if (3 != values.length - 1)
            throw new IOException("unsupported file format (vn dimension)");

        for (int i = 1; i <= 3; i++)
            vn.add(Float.valueOf(values[i]));

    }


    private final void parseF(final List<Float> v, final List<Float> vt, final List<Float> vn, final Map<String, Integer> f, final String[] values) throws IOException {

        if (3 != values.length - 1)
            throw new IOException("unsupported file format (only triangle faces supported)");

        for (int i = 1; i <= 3; i++) {

            if (!f.containsKey(values[i]))
                parseF(v, vt, vn, f, values[i]);

            indices.add(f.get(values[i]));

        }

    }


    private final void parseF(final List<Float> v, final List<Float> vt, final List<Float> vn, final Map<String, Integer> f, final String value) throws IOException {

        final String[] values = value.split("/");

        if (3 != values.length)
            throw new IOException("unsupported file format (face components)");

        final int vID = Integer.valueOf(values[0]) - 1;
        for (int i = 0; i < 3; i++)
            attributes.add(v.get(vID * 3 + i));


        final int vtID = Integer.valueOf(values[1]) - 1;
        for (int i = 0; i < 2; i++)
            attributes.add(vt.get(vtID * 2 + i));


        final int vnID = Integer.valueOf(values[2]) - 1;
        for (int i = 0; i < 3; i++)
            attributes.add(vn.get(vnID * 3 + i));

        f.put(value, index++);

    }

}
