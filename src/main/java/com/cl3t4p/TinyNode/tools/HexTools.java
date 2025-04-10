package com.cl3t4p.TinyNode.tools;

/**
 * HexTools - A utility class for encoding and decoding byte arrays to and from hexadecimal strings.
 */
public class HexTools {

    private static final char[] HEX = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f' };


    public static String encode(byte[] bytes) {
        final int nBytes = bytes.length;
        char[] result = new char[2 * nBytes];         //  1 hex contains two chars

        int j = 0;
        for (byte aByte : bytes) {                    // loop byte by byte

            // 0xF0 = FFFF 0000
            result[j++] = HEX[(0xF0 & aByte) >>> 4];    // get the top 4 bits, first half hex char

            // 0x0F = 0000 FFFF
            result[j++] = HEX[(0x0F & aByte)];          // get the bottom 4 bits, second half hex char

            // combine first and second half, we get a complete hex
        }

        return String.valueOf(result);
    }

    public static byte[] decode(CharSequence s) {
        int nChars = s.length();

        if (nChars % 2 != 0) {
            throw new IllegalArgumentException(
                    "Hex-encoded string must have an even number of characters");
        }

        byte[] result = new byte[nChars / 2];                                  // 1 hex = 2 char

        for (int i = 0; i < nChars; i += 2) {                                  // step 2, 1 hex = 2 char
            int msb = Character.digit(s.charAt(i), 16);                         // char -> hex, base16
            int lsb = Character.digit(s.charAt(i + 1), 16);

            if (msb < 0 || lsb < 0) {
                throw new IllegalArgumentException(
                        "Detected a Non-hex character at " + (i + 1) + " or " + (i + 2) + " position");
            }
            result[i / 2] = (byte) ((msb << 4) | lsb);
        }
        return result;
    }
}
