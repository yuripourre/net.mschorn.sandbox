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


package net.mschorn.sandbox.lwjgl.tools.window;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import net.mschorn.sandbox.lwjgl.tools.lifecycle.LWJGLLifecycle;
import net.mschorn.sandbox.lwjgl.tools.screenshot.Screenshot;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;


public final class LWJGLWindow {

    private static final int DEFAULT_MILLI_PER_FRAME = 16;
    private static final int NANO_PER_MILLI = 1000000;
    private static final double NANO_TO_MILLI = 1.0 / NANO_PER_MILLI;

    private static String title;


    private LWJGLWindow() {

    }


    public static void display(final LWJGLLifecycle lifecycle,
            final DisplayMode displayMode,
            final PixelFormat pixelFormat,
            final ContextAttribs contextAttribs,
            final String title) {

        LWJGLWindow.title = title;

        displayInit(lifecycle, displayMode, pixelFormat, contextAttribs);
        displayLoop(lifecycle);
        displayDestroy(lifecycle);

    }


    public static BufferedImage getImage(final LWJGLLifecycle lifecycle,
            final DisplayMode displayMode,
            final PixelFormat pixelFormat,
            final ContextAttribs contextAttribs,
            final int steps) {

        displayInit(lifecycle, displayMode, pixelFormat, contextAttribs);
        displayLoop(lifecycle, steps);

        final Screenshot screenshot = new Screenshot();
        final BufferedImage image = screenshot.take();

        displayDestroy(lifecycle);

        return image;

    }


    public static void takeImageSequence(final LWJGLLifecycle lifecycle,
            final DisplayMode displayMode,
            final PixelFormat pixelFormat,
            final ContextAttribs contextAttribs,
            final String filePrefix,
            final double timedelta,
            final int steps) {

        displayInit(lifecycle, displayMode, pixelFormat, contextAttribs);
        displayLoop(lifecycle, filePrefix, timedelta, steps);
        displayDestroy(lifecycle);

    }


    public static void setTitle(final String title) {

        LWJGLWindow.title = title;

    }


    private static void displayInit(final LWJGLLifecycle lifecycle,
            final DisplayMode displayMode,
            final PixelFormat pixelFormat,
            final ContextAttribs contextAttribs) {

        try {

            Display.setDisplayMode(displayMode);
            Display.create(pixelFormat, contextAttribs);
            Display.setTitle(title);

            lifecycle.glInit(displayMode.getWidth(), displayMode.getHeight());

        } catch (final LWJGLException e) {

            JOptionPane.showMessageDialog(null, "Your Graphics Card does not support all necessary functions.", "OpenGL Context Creation", JOptionPane.ERROR_MESSAGE);

            System.exit(1);

        }

    }


    private static void displayLoop(final LWJGLLifecycle lifecycle) {

        long time = System.nanoTime();

        long fpsFrames = 0;
        double fpsTime = 0;

        while (!Display.isCloseRequested()) {

            final long current = System.nanoTime();
            final double timedelta = (current - time) * NANO_TO_MILLI;
            time = current;

            lifecycle.glDisplay(timedelta);

            Display.update();

            fpsFrames += 1;
            fpsTime += timedelta;

            if (fpsTime > 1000) {

                final long fps = Math.round(fpsFrames / fpsTime * 1000);

                Display.setTitle(title + ": " + fps + "fps");

                fpsFrames = 0;
                fpsTime = 0;

            }

        }


    }


    private static void displayLoop(final LWJGLLifecycle lifecycle, final int steps) {

        for (int i = 0; i <= steps; i++) {

            lifecycle.glDisplay(DEFAULT_MILLI_PER_FRAME);

            Display.update();

        }

    }


    private static void displayLoop(final LWJGLLifecycle lifecycle, final String filePrefix, final double timedelta, final int steps) {

        final Screenshot screenshot = new Screenshot();

        for (int i = 0; i < steps; i++) {

            lifecycle.glDisplay(timedelta);

            Display.update();

            final BufferedImage image = screenshot.take();

            try {

                ImageIO.write(image, "PNG", new File(filePrefix + i + ".png"));

            } catch (final IOException e) {

                e.printStackTrace();

            }

        }

    }


    private static void displayDestroy(final LWJGLLifecycle lifecycle) {

        lifecycle.glDispose();

        Display.destroy();

    }


}
