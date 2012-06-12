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


package net.mschorn.sandbox.lwjgl.tools.light;

import net.mschorn.sandbox.lwjgl.tools.buffer.UBO;


public class Light extends UBO {

    private static final int AMBIENT_RED    = 0;
    private static final int AMBIENT_GREEN  = 4;
    private static final int AMBIENT_BLUE   = 8;
    private static final int DIFFUSE_RED    = 16;
    private static final int DIFFUSE_GREEN  = 20;
    private static final int DIFFUSE_BLUE   = 24;
    private static final int SPECULAR_RED   = 32;
    private static final int SPECULAR_GREEN = 36;
    private static final int SPECULAR_BLUE  = 40;
    private static final int POSITION_X     = 48;
    private static final int POSITION_Y     = 52;
    private static final int POSITION_Z     = 56;
    private static final int POSITION_W     = 60;


    public Light(final int binding) {

        super(binding, 16 * Float.SIZE / Byte.SIZE);

    }


    public final void setAmbient(final float r, final float g, final float b) {

        buffer.putFloat(AMBIENT_RED, r);
        buffer.putFloat(AMBIENT_GREEN, g);
        buffer.putFloat(AMBIENT_BLUE, b);

        modified = true;

    }


    public final void setDiffuse(final float r, final float g, final float b) {

        buffer.putFloat(DIFFUSE_RED, r);
        buffer.putFloat(DIFFUSE_GREEN, g);
        buffer.putFloat(DIFFUSE_BLUE, b);

        modified = true;

    }


    public final void setSpecular(final float r, final float g, final float b) {

        buffer.putFloat(SPECULAR_RED, r);
        buffer.putFloat(SPECULAR_GREEN, g);
        buffer.putFloat(SPECULAR_BLUE, b);

        modified = true;

    }


    public final void setPosition(final float x, final float y, final float z) {


        buffer.putFloat(POSITION_X, x);
        buffer.putFloat(POSITION_Y, y);
        buffer.putFloat(POSITION_Z, z);
        buffer.putFloat(POSITION_W, 1);

        modified = true;

    }

}
