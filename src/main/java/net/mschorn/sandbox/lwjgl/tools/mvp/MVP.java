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


package net.mschorn.sandbox.lwjgl.tools.mvp;

import net.mschorn.sandbox.lwjgl.tools.buffer.UBO;


public class MVP extends UBO {

    private static final int    P_OFFSET   = 0;
    private static final int    MV_OFFSET  = 16;
    private static final int    MVP_OFFSET = 32;
    private static final int    NM_OFFSET  = 48;

    private static final double FOVY       = 45.0f;
    private static final double NEAR       = 0.1;
    private static final double FAR        = 100.0;

    private final float[]       p          = new float[16];
    private final float[]       mv         = new float[16];
    private final float[]       mvp        = new float[16];
    private final float[]       nm         = new float[16];

    private double              rotX, rotY, rotZ;
    private double              posX, posY, posZ;

    private double              angle;


    public MVP(final int binding) {

        super(binding, 64 * Float.SIZE / Byte.SIZE);

        for (int i = 0; i < 4; i++)
            mv[i * 5] = 1.0f;

        for (int i = 0; i < 3; i++)
            nm[i * 5] = 1.0f;

    }


    public final void update(final double timedelta) {

        angle += 0.001 * timedelta;

        posZ = Math.cos(angle) * 5;
        posX = Math.sin(angle) * 5;

        rotY = Math.PI - angle;

        final double sinX = Math.sin(rotX);
        final double sinY = Math.sin(rotY);
        final double sinZ = Math.sin(rotZ);

        final double cosX = Math.cos(rotX);
        final double cosY = Math.cos(rotY);
        final double cosZ = Math.cos(rotZ);

        mv[0] = nm[0] = (float) (cosY * cosZ + sinY * sinX * sinZ);
        mv[1] = nm[1] = (float) (cosX * sinZ);
        mv[2] = nm[2] = (float) (-sinY * cosZ + cosY * sinX * sinZ);
        mv[4] = nm[4] = (float) (-cosY * sinZ + sinY * sinX * cosZ);
        mv[5] = nm[5] = (float) (cosX * cosZ);
        mv[6] = nm[6] = (float) (sinY * sinZ + cosY * sinX * cosZ);
        mv[8] = nm[8] = (float) (sinY * cosX);
        mv[9] = nm[9] = (float) (-sinX);
        mv[10] = nm[10] = (float) (cosY * cosX);

        mv[12] = (float) (mv[0] * posX + mv[4] * posY + mv[8] * posZ);
        mv[13] = (float) (mv[1] * posX + mv[5] * posY + mv[9] * posZ);
        mv[14] = (float) (mv[2] * posX + mv[6] * posY + mv[10] * posZ);

        mvp[0] = mv[0] * p[0];
        mvp[1] = mv[1] * p[5];
        mvp[2] = mv[2] * p[10];
        mvp[3] = mv[2] * -1;
        mvp[4] = mv[4] * p[0];
        mvp[5] = mv[5] * p[5];
        mvp[6] = mv[6] * p[10];
        mvp[7] = mv[6] * -1;
        mvp[8] = mv[8] * p[0];
        mvp[9] = mv[9] * p[5];
        mvp[10] = mv[10] * p[10];
        mvp[11] = mv[10] * -1;
        mvp[12] = mv[12] * p[0];
        mvp[13] = mv[13] * p[5];
        mvp[14] = mv[14] * p[10] + p[14];
        mvp[15] = mv[14] * -1;

        for (int i = 0; i < mv.length; i++)
            buffer.putFloat((MV_OFFSET + i) * 4, mv[i]);

        for (int i = 0; i < mvp.length; i++)
            buffer.putFloat((MVP_OFFSET + i) * 4, mvp[i]);

        for (int i = 0; i < nm.length; i++)
            buffer.putFloat((NM_OFFSET + i) * 4, nm[i]);

        modified = true;

    }


    public final void update(final int x, final int y, final int width, final int height) {

        final double f = (1.0 / Math.tan(Math.toRadians(FOVY / 2.0)));

        p[0] = (float) (f / ((double) width / (double) height));
        p[5] = (float) (f);
        p[10] = (float) ((FAR + NEAR) / (NEAR - FAR));
        p[14] = (float) ((2 * FAR * NEAR) / (NEAR - FAR));
        p[11] = (-1);

        for (int i = 0; i < p.length; i++)
            buffer.putFloat((P_OFFSET + i) * 4, p[i]);

        modified = true;

    }

}
