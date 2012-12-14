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


package net.mschorn.sandbox.lwjgl.tools.glunit;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import net.mschorn.sandbox.lwjgl.tools.lifecycle.LWJGLLifecycle;
import net.mschorn.sandbox.lwjgl.tools.window.LWJGLWindow;

import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;


public class GLUnit {

    public static final double MAXIMUM_DIFFERENCE = 0.1;
    private static final double SCALE = 1 / 4.0;

    private final String filename = getClass().getSimpleName() + ".png";


    public final double glTest(final LWJGLLifecycle lifecycle, final DisplayMode displayMode, final PixelFormat pixelFormat, final ContextAttribs contextAttribs, final int steps) {

        final BufferedImage actual = LWJGLWindow.getImage(lifecycle, displayMode, pixelFormat, contextAttribs, steps);

        final Image reference = read();

        if (actual != null && reference == null)
            write(actual);

        final double difference = compare(actual, reference);

        return difference;

    }


    private Image read() {

        final InputStream input = getClass().getResourceAsStream(filename);

        if (input == null)
            return null;

        try {

            return ImageIO.read(input);

        } catch (final IOException e) {

            e.printStackTrace();

        }

        return null;

    }


    private void write(final BufferedImage image) {

        try {

            ImageIO.write(image, "png", new File(filename));

        } catch (final IOException e) {

            e.printStackTrace();

        }

    }


    private double compare(final Image i1, final Image i2) {

        if (i1 == null || i2 == null)
            return Double.MAX_VALUE;

        final int[] data1 = getData(resize(i1));
        final int[] data2 = getData(resize(i2));

        if (data1.length != data2.length)
            return Double.MAX_VALUE;

        double difference = 0;
        for (int i = 0; i < data1.length; i++) {

            final int red = Math.abs(((data1[i] >>> 16) & 0xFF) - ((data2[i] >>> 16) & 0xFF));
            final int green = Math.abs(((data1[i] >>> 8) & 0xFF) - ((data2[i] >>> 8) & 0xFF));
            final int blue = Math.abs(((data1[i] >>> 0) & 0xFF) - ((data2[i] >>> 0) & 0xFF));

            difference += (red + green + blue) / 3.0;

        }

        difference /= data1.length;

        return difference;

    }


    private int[] getData(final BufferedImage image) {

        final int width = image.getWidth();
        final int height = image.getHeight();

        return image.getRGB(0, 0, width, height, null, 0, width);

    }


    private BufferedImage resize(final Image image) {

        final int width = (int) Math.round(image.getWidth(null) * SCALE);
        final int height = (int) Math.round(image.getHeight(null) * SCALE);

        final BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        final Graphics2D g = result.createGraphics();
        g.drawImage(image.getScaledInstance(width, height, 0), 0, 0, null);
        g.dispose();

        return result;

    }


}
