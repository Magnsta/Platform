// Copyright 2016, S&K Software Development Ltd.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.

// Package crc implements generic CRC calculations up to 64 bits wide.
// It aims to be fairly complete, allowing users to match pretty much
// any CRC algorithm used in the wild by choosing appropriate Parameters.
// And it's also fairly fast for everyday use.
//
// This package has been largely inspired by Ross Williams' 1993 paper "A Painless Guide to CRC Error Detection Algorithms".
// A good list of parameter sets for various CRC algorithms can be found at http://reveng.sourceforge.net/crc-catalogue/.

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class CRC16 {

  /**
   * calculates the crc 16 value of a packet.
   * this method is heavily inspired by:
   * <p>
   * https://github.com/snksoft/java-crc/blob/master/src/main/java/com/github/snksoft/crc/CRC.java?fbclid=IwAR3JZjjAJOjlaU3k9zZT94Mzz0UCtziwOkknQ6wn95Md4hS1ohkcrZxu2Ac
   * <p>
   * calculateCRC method, but reconfigured to work with our program that only needs CRC 16
   * this Method is used under the Lisence provided by the S&K company
   * see the CRCLisence File for more info
   *
   * @param packet the data for us to calculate the crc to
   * @return crc long value;
   */
  public static long calcCrc16(byte[] packet) {
    long crc = 0x0000;
    long topBit = 1L << 15;
    long mask = (topBit << 1) - 1;

    for (int i = 0; i < packet.length; i++) {
      long curByte = reflect((long) packet[i] & 0x00FFL, 8);
      for (int j = 0x80; j != 0; j >>= 1) {
        long bit = crc & topBit;
        crc <<= 1;
        if ((curByte & j) != 0) {
          bit ^= topBit;
        }
        if (bit != 0) {
          crc ^= 0x8005;
        }
      }
    }
    crc = reflect(crc, 16) & mask;
    return crc;
  }

  public static boolean checkCrc(byte[] packet, byte[] crcToTest) {
    return (calcCrc16(packet) == ByteBuffer.wrap(crcToTest).getLong());
  }


  /**
   * borrowed from : https://github.com/snksoft/java-crc/blob/master/src/main/java/com/github/snksoft/crc/CRC.java?fbclid=IwAR3JZjjAJOjlaU3k9zZT94Mzz0UCtziwOkknQ6wn95Md4hS1ohkcrZxu2Ac
   * Reverses order of last count bits.
   *
   * @param in value which bits need to be reversed
   * @return the value with specified bits order reversed
   */
  private static long reflect(long in, int count) {
    long ret = in;
    for (int idx = 0; idx < count; idx++) {
      long srcbit = 1L << idx;
      long dstbit = 1L << (count - idx - 1);
      if ((in & srcbit) != 0) {
        ret |= dstbit;
      } else {
        ret = ret & (~dstbit);
      }
    }
    return ret;
  }

  public static long crcForCommand(ArrayList<byte[]> bb) {
    long crc = 0;
    for (byte[] bs : bb) {
      crc = (crc << bs.length * 8) ^ calcCrc16(bs);
    }
    return crc;
  }

  private static long bytesToLong(byte[] bb) {
    long l = 0;
    for (int i : bb) {
      l <<= 8;
      l ^= bb[i] & 0xff;
    }
    return l;
  }
}
