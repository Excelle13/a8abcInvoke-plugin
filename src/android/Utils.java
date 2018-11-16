package com.ttebd.a8abcResInvoke;

import java.util.Locale;

public class Utils {
  public static String bcd2Ascii(final byte[] bcd) {
    if (bcd == null)
      return "";
    StringBuilder sb = new StringBuilder(bcd.length << 1);
    for (byte ch : bcd) {
      byte half = (byte) (ch >> 4);
      sb.append((char)(half + ((half > 9) ? ('A' - 10) : '0')));
      half = (byte) (ch & 0x0f);
      sb.append((char)(half + ((half > 9) ? ('A' - 10) : '0')));
    }
    return sb.toString();
  }

  public static String bytes2HexString(byte[] data) {
    if (data == null)
      return "";
    StringBuilder buffer = new StringBuilder();
    for (byte b : data) {
      String hex = Integer.toHexString(b & 0xff);
      if (hex.length() == 1) {
        buffer.append('0');
      }
      buffer.append(hex);
    }
    return buffer.toString().toUpperCase(Locale.US);
  }

  public static byte[] hexString2Bytes(String data) {
    if (data == null)
      return null;
    byte[] result = new byte[(data.length()+1)/2];
    if ((data.length() & 1) == 1) {
      data += "0";
    }
    for (int i = 0; i < result.length; i++) {
      result[i] = (byte) (hex2byte(data.charAt(i*2+1)) | (hex2byte(data.charAt(i*2))<<4));
    }
    return result;
  }

  public static byte[] ascii2Bcd(String ascii) {
    if (ascii == null)
      return null;
    if ((ascii.length() & 0x01) == 1)
      ascii = "0" + ascii;
    byte[] asc = ascii.getBytes();
    byte[] bcd = new byte[ascii.length() >> 1];
    for (int i = 0; i < bcd.length; i++) {
      bcd[i] = (byte)(hex2byte((char)asc[2 * i]) << 4 | hex2byte((char)asc[2 * i + 1]));
    }
    return bcd;
  }

  public static byte hex2byte(char hex) {
    if (hex <= 'f' && hex >= 'a') {
      return (byte) (hex - 'a' + 10);
    }

    if (hex <= 'F' && hex >= 'A') {
      return (byte) (hex - 'A' + 10);
    }

    if (hex <= '9' && hex >= '0') {
      return (byte) (hex - '0');
    }

    return 0;
  }
}
