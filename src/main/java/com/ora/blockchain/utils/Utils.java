/**
 * Copyright 2011 Google Inc.
 * Copyright 2013-2016 Ronald W Hoffman
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ora.blockchain.utils;


import java.math.BigInteger;

/**
 * Static utility methods
 */
public abstract class Utils {

    /** Constant -1 */
    public static final BigInteger NEGATIVE_ONE = BigInteger.valueOf(-1);

    /** Constant 1,000 */
    private static final BigInteger DISPLAY_1K = new BigInteger("1000");

    /** Constant 1,000,000 */
    private static final BigInteger DISPLAY_1M = new BigInteger("1000000");

    /** Constant 1,000,000,000 */
    private static final BigInteger DISPLAY_1G = new BigInteger("1000000000");

    /** Constant 1,000,000,000,000 */
    private static final BigInteger DISPLAY_1T = new BigInteger("1000000000000");

    /** Constant 1,000,000,000,000,000 */
    private static final BigInteger DISPLAY_1P = new BigInteger("1000000000000000");

    /** Bit masks (Low-order bit is bit 0 and high-order bit is bit 7) */
    private static final int bitMask[] = {0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80};

    /**
     * How many "nanocoins" there are in a Bitcoin.
     *
     * A nanocoin is the smallest unit that can be transferred using Bitcoin.
     * The term nanocoin is very misleading, though, because there are only 100 million
     * of them in a coin (whereas one would expect 1 billion.
     */
    public static final BigInteger COIN = new BigInteger("100000000", 10);


    public static final BigInteger BCD_COIN = new BigInteger("10000000", 10);

    /**
     * How many "nanocoins" there are in 0.01 BitCoins.
     *
     * A nanocoin is the smallest unit that can be transferred using Bitcoin.
     * The term nanocoin is very misleading, though, because there are only 100 million
     * of them in a coin (whereas one would expect 1 billion).
     */
    public static final BigInteger CENT = new BigInteger("1000000", 10);
}