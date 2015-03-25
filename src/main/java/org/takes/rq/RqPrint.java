/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Yegor Bugayenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.takes.rq;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Iterator;
import lombok.EqualsAndHashCode;
import org.takes.Request;

/**
 * Request decorator, to print it all.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.1
 */
@EqualsAndHashCode(callSuper = true)
public final class RqPrint extends RqWrap {

    /**
     * Ctor.
     * @param req Original request
     */
    public RqPrint(final Request req) {
        super(req);
    }

    /**
     * Print it all.
     * @return Text form of request
     * @throws IOException If fails
     */
    public String print() throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        this.print(baos);
        return new String(baos.toByteArray());
    }

    /**
     * Print it all.
     * @param output Output stream
     * @throws IOException If fails
     */
    public void print(final OutputStream output) throws IOException {
        this.printHead(output);
        this.printBody(output);
    }

    /**
     * Print it all.
     * @return Text form of request
     * @throws IOException If fails
     */
    public String printHead() throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        this.printHead(baos);
        return new String(baos.toByteArray());
    }

    /**
     * Print it all.
     * @param output Output stream
     * @throws IOException If fails
     */
    public void printHead(final OutputStream output) throws IOException {
        final String eol = "\r\n";
        final Writer writer = new OutputStreamWriter(output);
        for (final String line : this.head()) {
            writer.append(line);
            writer.append(eol);
        }
        writer.append(eol);
        writer.flush();
    }

    /**
     * Print body.
     * @return Text form of request
     * @throws IOException If fails
     */
    public String printBody() throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        this.printBody(baos);
        return new String(baos.toByteArray());
    }

    /**
     * Print body.
     * @param output Output stream to print to
     * @throws IOException If fails
     */
    public void printBody(final OutputStream output) throws IOException {
        final Iterator<String> hdr = new RqHeaders(this)
            .header("Content-Length").iterator();
        final boolean endless;
        int more = Integer.MAX_VALUE;
        if (hdr.hasNext()) {
            endless = false;
            more = Integer.parseInt(hdr.next());
        } else {
            endless = true;
        }
        final InputStream input = this.body();
        while (more > 0) {
            if (endless && input.available() == 0) {
                break;
            }
            final int data = input.read();
            if (data < 0) {
                break;
            }
            output.write(data);
            --more;
        }
    }

}