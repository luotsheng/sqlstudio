package com.changhong.sqlstudio.common.security;

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


import com.changhong.sqlstudio.common.security.codec.Base64Codec;
import com.changhong.sqlstudio.common.security.codec.MD5Codec;
import com.changhong.sqlstudio.common.security.codec.SHA256Codec;
import com.changhong.sqlstudio.common.security.codec.URLCodec;
import com.changhong.sqlstudio.common.string.StringInterface;
import com.changhong.sqlstudio.common.string.StringUtils;
import com.changhong.sqlstudio.common.utils.Captor;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import static com.changhong.sqlstudio.common.utils.TypeCvt.atos;

/**
 * `Codec` 类用于数据的编码和解码操作。它提供了将数据从一种格式转换为另一种格式的功能，
 * 通常用于压缩、加密或其他数据转换需求。
 *
 * <p>本类的实现可能支持多种编码和解码格式，允许用户在不同的数据表示之间进行转换。
 *
 * <h2>注意事项</h2>
 * <ul>
 *     <li>本类的实现需要保证编码与解码的一致性，以确保数据能够被正确还原。</li>
 *     <li>根据使用的编码/解码算法，可能存在性能差异或安全性考虑。</li>
 * </ul>
 *
 * @see Base64Codec
 * @see MD5Codec
 * @see SHA256Codec
 * @see URLCodec
 * @author Luo Tiansheng
 */
public class Codec {

    public static final Base64 BASE64 = new Base64Codec(); // BASE64
    public static final MD5    MD5    = new MD5Codec();    // MD5
    public static final SHA256 SHA256 = new SHA256Codec(); // SHA-256
    public static final URL    URL    = new URLCodec();    // URL

    /**
     * 生成一个版本号
     *
     * @param major
     *        主版本号
     *
     * @param minor
     *        副版本号
     *
     * @param patch
     *        补丁版本
     */
    public static int makeVersion(int major, int minor, int patch) {
        return major << 22 | minor << 12 | patch;
    }

    /**
     * 解析 {@link #makeVersion(int, int, int)} 生成的版本号，获取其中
     * 的主版本号内容并返回。
     *
     * @param version
     *        版本号
     *
     * @return 主版本号
     */
    public static int versionMajor(int version) {
        return version >> 22;
    }

    /**
     * 解析 {@link #makeVersion(int, int, int)} 生成的版本号，获取其中
     * 的次版本号内容并返回。
     *
     * @param version
     *        版本号
     *
     * @return 次版本号
     */
    public static int versionMinor(int version) {
        return version >> 12 & 0x3ff;
    }

    /**
     * 解析 {@link #makeVersion(int, int, int)} 生成的版本号，获取其中
     * 的补丁版本内容并返回。
     *
     * @param version
     *        版本号
     *
     * @return 补丁版本
     */
    public static int versionPatch(int version) {
        return version & 0xfff;
    }

    /**
     * 字节码转 16 进制
     */
    public static String toByteHex(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte b : bytes) {
            String tmp = Integer.toHexString(b & 0xFF);
            if (StringUtils.strlen(tmp) == 1)
                builder.append("0");
            builder.append(tmp);
        }
        return atos(builder, StringInterface.STRING_IFACE_UPPER_CASE_EXT);
    }

    /**
     * #brief: 生成并返回下一个随机的 AES 密钥，使用 Base64 编码格式表示
     *
     * <p>该方法利用 `KeyGenerator` 类生成一个 128 位的 AES 密钥，并将其编码为 Base64
     * 字符串以便于存储或传输。返回的密钥可用于加密或解密数据，确保数据的安全性。
     *
     * @return 生成的随机 AES 密钥的 Base64 编码字符串
     */
    public static String randomNextSecret() {
        return Captor.call(() -> {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128);
            SecretKey secretKey = keyGen.generateKey();
            return BASE64.encode(secretKey.getEncoded());
        });
    }

}
