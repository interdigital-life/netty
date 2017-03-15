/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import static io.netty.util.CharsetUtil.UTF_8;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;

/** {@link AbstractMemoryHttpData} test cases. */
public class AbstractMemoryHttpDataTest {
    /**
     * Provide content into HTTP data with input stream.
     *
     * @throws Exception In case of any exception.
     */
    @Test
    public void testSetContentFromStream() throws Exception {
        Random random = new SecureRandom();

        for (int i = 0; i < 20; i++) {
            // Generate input data bytes.
            int size = random.nextInt(Short.MAX_VALUE);
            byte[] bytes = new byte[size];

            random.nextBytes(bytes);

            // Generate parsed HTTP data block.
            TestHttpData data = new TestHttpData("name", UTF_8, 0);

            data.setContent(new ByteArrayInputStream(bytes));

            // Validate stored data.
            ByteBuf buffer = data.getByteBuf();

            assertEquals(0, buffer.readerIndex());
            assertEquals(bytes.length, buffer.writerIndex());
            assertArrayEquals(bytes, Arrays.copyOf(buffer.array(), bytes.length));
        }
    }

    /** Memory-based HTTP data implementation for test purposes. */
    private static final class TestHttpData extends AbstractMemoryHttpData {
        /**
         * Constructs HTTP data for tests.
         *
         * @param name Name of parsed data block.
         * @param charset Used charset for data decoding.
         * @param size Expected data block size.
         */
        private TestHttpData(String name, Charset charset, long size) {
            super(name, charset, size);
        }

        private static UnsupportedOperationException reject() {
            throw new UnsupportedOperationException("Should never be called.");
        }

        @Override
        public InterfaceHttpData.HttpDataType getHttpDataType() {
            throw reject();
        }

        @Override
        public HttpData copy() {
            throw reject();
        }

        @Override
        public HttpData duplicate() {
            throw reject();
        }

        @Override
        public HttpData retainedDuplicate() {
            throw reject();
        }

        @Override
        public HttpData replace(ByteBuf content) {
            return null;
        }

        @Override
        public int compareTo(InterfaceHttpData o) {
            throw reject();
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return super.equals(obj);
        }
    }
}