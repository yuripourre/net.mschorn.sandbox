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


package net.mschorn.sandbox.lwjgl.tools.screenshot;

import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_VIEWPORT;
import static org.lwjgl.opengl.GL11.glGetInteger;
import static org.lwjgl.opengl.GL11.glReadPixels;
import static org.lwjgl.opengl.GL12.GL_UNSIGNED_INT_8_8_8_8;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;


public final class Screenshot {

    private ByteBuffer buffer;
    private int[] pixels;
    private int width;
    private int height;


    public BufferedImage take() {

        checkViewport();

        glReadPixels(0, 0, width, height, GL_RGBA, GL_UNSIGNED_INT_8_8_8_8, buffer);

        buffer.asIntBuffer().get(pixels);

        rgbaToRgb();
        vFlip();

        final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        image.setRGB(0, 0, width, height, pixels, 0, width);

        return image;

    }


    private void checkViewport() {

        final IntBuffer buf = BufferUtils.createIntBuffer(16);

        glGetInteger(GL_VIEWPORT, buf);

        if (buffer == null || pixels == null || width != buf.get(2) || height != buf.get(3)) {

            width = buf.get(2);
            height = buf.get(3);
            buffer = BufferUtils.createByteBuffer(width * height * 4);
            pixels = new int[width * height];

        }

    }


    private void rgbaToRgb() {

        for (int i = 0; i < pixels.length; i++)
            pixels[i] = (pixels[i] >> Byte.SIZE);

    }


    private void vFlip() {

        final int[] temp = new int[width];

        for (int i = 0; i < pixels.length / 2; i += width) {

            System.arraycopy(pixels, i, temp, 0, width);
            System.arraycopy(pixels, pixels.length - i - width, pixels, i, width);
            System.arraycopy(temp, 0, pixels, pixels.length - i - width, width);

        }

    }


}
