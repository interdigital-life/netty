/*
 * Copyright 2015 The Netty Project
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

package io.netty.util.internal;

import static io.netty.util.internal.PlatformDependent.hashCodeAscii;
import static io.netty.util.internal.PlatformDependent.hashCodeAsciiSafe;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import java.security.Permission;
import java.util.Random;

public class PlatformDependentTest {
    private static final Random r = new Random();

    private static char randomCharInByteRange() {
        return (char) r.nextInt(255 + 1);
    }

    @Test
    public void testEqualsConsistentTime() {
        testEquals(new EqualityChecker() {
            @Override
            public boolean equals(byte[] bytes1, int startPos1, byte[] bytes2, int startPos2, int length) {
                return PlatformDependent.equalsConstantTime(bytes1, startPos1, bytes2, startPos2, length) != 0;
            }
        });
    }

    @Test
    public void testEquals() {
        testEquals(new EqualityChecker() {
            @Override
            public boolean equals(byte[] bytes1, int startPos1, byte[] bytes2, int startPos2, int length) {
                return PlatformDependent.equals(bytes1, startPos1, bytes2, startPos2, length);
            }
        });
    }

    private void testEquals(EqualityChecker equalsChecker) {
        byte[] bytes1 = {'H', 'e', 'l', 'l', 'o', ' ', 'W', 'o', 'r', 'l', 'd'};
        byte[] bytes2 = {'H', 'e', 'l', 'l', 'o', ' ', 'W', 'o', 'r', 'l', 'd'};
        assertNotSame(bytes1, bytes2);
        assertTrue(equalsChecker.equals(bytes1, 0, bytes2, 0, bytes1.length));
        assertTrue(equalsChecker.equals(bytes1, 2, bytes2, 2, bytes1.length - 2));

        bytes1 = new byte[] {1, 2, 3, 4, 5, 6};
        bytes2 = new byte[] {1, 2, 3, 4, 5, 6, 7};
        assertNotSame(bytes1, bytes2);
        assertFalse(equalsChecker.equals(bytes1, 0, bytes2, 1, bytes1.length));
        assertTrue(equalsChecker.equals(bytes2, 0, bytes1, 0, bytes1.length));

        bytes1 = new byte[] {1, 2, 3, 4};
        bytes2 = new byte[] {1, 2, 3, 5};
        assertFalse(equalsChecker.equals(bytes1, 0, bytes2, 0, bytes1.length));
        assertTrue(equalsChecker.equals(bytes1, 0, bytes2, 0, 3));

        bytes1 = new byte[] {1, 2, 3, 4};
        bytes2 = new byte[] {1, 3, 3, 4};
        assertFalse(equalsChecker.equals(bytes1, 0, bytes2, 0, bytes1.length));
        assertTrue(equalsChecker.equals(bytes1, 2, bytes2, 2, bytes1.length - 2));

        bytes1 = new byte[0];
        bytes2 = new byte[0];
        assertNotSame(bytes1, bytes2);
        assertTrue(equalsChecker.equals(bytes1, 0, bytes2, 0, 0));

        bytes1 = new byte[100];
        bytes2 = new byte[100];
        for (int i = 0; i < 100; i++) {
            bytes1[i] = (byte) i;
            bytes2[i] = (byte) i;
        }
        assertTrue(equalsChecker.equals(bytes1, 0, bytes2, 0, bytes1.length));
        bytes1[50] = 0;
        assertFalse(equalsChecker.equals(bytes1, 0, bytes2, 0, bytes1.length));
        assertTrue(equalsChecker.equals(bytes1, 51, bytes2, 51, bytes1.length - 51));
        assertTrue(equalsChecker.equals(bytes1, 0, bytes2, 0, 50));

        bytes1 = new byte[] {1, 2, 3, 4, 5};
        bytes2 = new byte[] {3, 4, 5};
        assertFalse(equalsChecker.equals(bytes1, 0, bytes2, 0, bytes2.length));
        assertTrue(equalsChecker.equals(bytes1, 2, bytes2, 0, bytes2.length));
        assertTrue(equalsChecker.equals(bytes2, 0, bytes1, 2, bytes2.length));

        for (int i = 0; i < 1000; ++i) {
            bytes1 = new byte[i];
            r.nextBytes(bytes1);
            bytes2 = bytes1.clone();
            assertTrue(equalsChecker.equals(bytes1, 0, bytes2, 0, bytes1.length));
        }
    }

    @Test
    public void testHashCodeAscii() {
        for (int i = 0; i < 1000; ++i) {
            // byte[] and char[] need to be initialized such that there values are within valid "ascii" range
            byte[] bytes = new byte[i];
            char[] bytesChar = new char[i];
            for (int j = 0; j < bytesChar.length; ++j) {
                bytesChar[j] = randomCharInByteRange();
                bytes[j] = (byte) (bytesChar[j] & 0xff);
            }
            String string = new String(bytesChar);
            assertEquals("length=" + i,
                    hashCodeAsciiSafe(bytes, 0, bytes.length),
                    hashCodeAscii(bytes, 0, bytes.length));
            assertEquals("length=" + i,
                    hashCodeAscii(bytes, 0, bytes.length),
                    hashCodeAscii(string));
        }
    }

    @Test
    public void testMajorVersionFromJavaSpecificationVersion() {
        final SecurityManager current = System.getSecurityManager();

        try {
            System.setSecurityManager(new SecurityManager() {
                // so we can restore the security manager
                @Override
                public void checkPermission(Permission perm) {
                }

                @Override
                public void checkPropertyAccess(String key) {
                    if (key.equals("java.specification.version")) {
                        // deny
                        throw new SecurityException(key);
                    }
                }
            });

            assertEquals(6, PlatformDependent.majorVersionFromJavaSpecificationVersion());
        } finally {
            System.setSecurityManager(current);
        }
    }

    @Test
    public void testMajorVersion() {
        assertEquals(6, PlatformDependent.majorVersion("1.6"));
        assertEquals(7, PlatformDependent.majorVersion("1.7"));
        assertEquals(8, PlatformDependent.majorVersion("1.8"));
        assertEquals(8, PlatformDependent.majorVersion("8"));
        assertEquals(9, PlatformDependent.majorVersion("1.9")); // early version of JDK 9 before Project Verona
        assertEquals(9, PlatformDependent.majorVersion("9"));
    }

    private interface EqualityChecker {
        boolean equals(byte[] bytes1, int startPos1, byte[] bytes2, int startPos2, int length);
    }
}
