package com.changhong.sqlstudio.common.security.key;

/* -------------------------------------------------------------------------------- *\
|*                                                                                  *|
|*    Copyright (C) 2023 Luo Tiansheng                                                   *|
|*                                                                                  *|
|*    This program is free software: you can redistribute it and/or modify          *|
|*    it under the terms of the GNU General Public License as published by          *|
|*    the Free Software Foundation, either version 3 of the License, or             *|
|*    (at your option) any later version.                                           *|
|*                                                                                  *|
|*    This program is distributed in the hope that it will be useful,               *|
|*    but WITHOUT ANY WARRANTY; without even the implied warranty of                *|
|*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                 *|
|*    GNU General Public License for more details.                                  *|
|*                                                                                  *|
|*    You should have received a copy of the GNU General Public License             *|
|*    along with this program.  If not, see <https://www.gnu.org/licenses/>.        *|
|*                                                                                  *|
|*    This program comes with ABSOLUTELY NO WARRANTY; for details type `show w'.    *|
|*    This is free software, and you are welcome to redistribute it                 *|
|*    under certain conditions; type `show c' for details.                          *|
|*                                                                                  *|
\* -------------------------------------------------------------------------------- */

/* Creates on 2025/2/20. */

import com.changhong.sqlstudio.common.security.Codec;
import com.changhong.sqlstudio.common.string.StringUtils;

import java.security.Key;

import static com.changhong.sqlstudio.common.utils.TypeCvt.atos;

/**
 * @author Luo Tiansheng
 */
public abstract class AbstractKey {

    private final byte[] encoded;

    public AbstractKey(Key key) {
        this(key.getEncoded());
    }

    public AbstractKey(byte[] encoded) {
        this.encoded = encoded;
    }

    public byte[] getEncoded() {
        return encoded;
    }

    public abstract String toPEMFormat();

    public static byte[] decodePEMFormat(String pem) {
        String[] lines = StringUtils.strtok(pem, "\n");
        StringBuilder remakeBuilder = new StringBuilder();
        for (int i = 1; i < (lines.length - 1); i++) {
            remakeBuilder.append(StringUtils.strcut(lines[i], 0, 0));
        }
        return Codec.BASE64.decodeBytes(atos(remakeBuilder));
    }

    protected static String toPEMFormat0(String keyType, byte[] encoded) {
        StringBuilder secretBuilder = new StringBuilder();

        String base64Encode = Codec.BASE64.encode(encoded);
        int encodeLength = StringUtils.strlen(base64Encode);
        int len = 64;
        int loopCount = encodeLength / len;
        int copyLength = 0;

        for (int i = 0; i < loopCount; i++) {
            int off = i * len;
            if (off + len > encodeLength)
                len = Math.abs((off + len) - encodeLength);
            secretBuilder.append(StringUtils.strcut(base64Encode, off, len)).append("\n");
            copyLength += len;
        }

        // 检查是否还有剩余内容
        if (copyLength < encodeLength)
            secretBuilder.append(StringUtils.strcut(base64Encode, copyLength, 0)).append("\n");

        secretBuilder.delete(secretBuilder.length() - 1, secretBuilder.length());
        return "-----BEGIN " + keyType + "-----\n" + atos(secretBuilder) + "\n-----END " + keyType + "-----";
    }

    public String toZipKeyFormat() {
        return Codec.BASE64.encode(encoded);
    }

    @Override
    public String toString() {
        return toZipKeyFormat();
    }
}
