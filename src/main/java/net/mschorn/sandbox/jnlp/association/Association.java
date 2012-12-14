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


package net.mschorn.sandbox.jnlp.association;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import javax.jnlp.ExtendedService;
import javax.jnlp.FileContents;
import javax.jnlp.ServiceManager;
import javax.jnlp.UnavailableServiceException;
import javax.swing.JOptionPane;


public final class Association {

    private static final Charset CHARSET = Charset.forName("US-ASCII");


    public static void main(final String[] args) throws UnavailableServiceException, IOException {

        if (args.length == 2 && "-open".equals(args[0]))
            showFile(new File(args[1]));

        else
            JOptionPane.showMessageDialog(null, "Hello, world!");

    }


    private Association() {

    }


    public static void showFile(final File file) throws UnavailableServiceException, IOException {

        final ExtendedService es = (ExtendedService) ServiceManager.lookup("javax.jnlp.ExtendedService");
        final FileContents fc = es.openFile(file);

        try (BufferedReader br = new BufferedReader(new InputStreamReader(fc.getInputStream(), CHARSET))) {

            final StringBuilder sb = new StringBuilder();

            String line = null;
            while ((line = br.readLine()) != null)
                sb.append(line).append('\n');

            JOptionPane.showMessageDialog(null, sb.toString());

        }

    }


}
