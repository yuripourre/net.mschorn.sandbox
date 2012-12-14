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


package net.mschorn.sandbox.lwjgl.tools.texture;

import static org.lwjgl.opengl.GL11.GL_RGBA8;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL12.GL_BGRA;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glFramebufferTexture2D;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;


public class Texture {

    private final int unit;
    private final int width;
    private final int height;
    private final int type;
    private final ByteBuffer buffer;

    private int handle = -1;


    public Texture(final int unit, final int width, final int height, final int type) {

        this(unit, width, height, type, null);

    }


    public Texture(final int unit, final int width, final int height, final int type, final ByteBuffer buffer) {

        this.unit = unit;
        this.width = width;
        this.height = height;
        this.type = type;
        this.buffer = buffer;

    }


    public Texture(final int unit, final InputStream is) {

        this.unit = unit;

        BufferedImage image = loadImage(is);

        width = image.getWidth();
        height = image.getHeight();
        type = GL_RGBA8;

        image = flipImage(image);

        final int[] pixels = image.getRGB(0, 0, width, height, null, 0, width);

        buffer = BufferUtils.createByteBuffer(pixels.length * Integer.SIZE / Byte.SIZE);
        buffer.asIntBuffer().put(pixels);
        buffer.rewind();

    }


    private BufferedImage loadImage(final InputStream is) {

        BufferedImage image;

        try {

            image = ImageIO.read(is);

            is.close();

        } catch (final IOException e) {

            throw new RuntimeException(e);

        }

        return image;

    }


    private BufferedImage flipImage(final BufferedImage image) {

        final AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
        tx.translate(0, -image.getHeight());

        final AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        final BufferedImage result = op.filter(image, null);

        return result;

    }


    public final void glInit() {

        handle = glGenTextures();

        glActiveTexture(GL_TEXTURE0 + unit);
        glBindTexture(GL_TEXTURE_2D, handle);
        glTexImage2D(GL_TEXTURE_2D, 0, type, width, height, 0, GL_BGRA, GL_UNSIGNED_BYTE, buffer);
        glBindTexture(GL_TEXTURE_2D, 0);

    }


    public final void glBind() {

        glActiveTexture(GL_TEXTURE0 + unit);
        glBindTexture(GL_TEXTURE_2D, handle);

    }


    public final void glAttachToFBO(final int attachment) {

        glFramebufferTexture2D(GL_FRAMEBUFFER, attachment, GL_TEXTURE_2D, handle, 0);

    }


    public final void glDispose() {

        glDeleteTextures(handle);

    }


    public final int getUnit() {

        return unit;

    }


    public final int getWidth() {

        return width;

    }


    public final int getHeight() {

        return height;

    }


}
