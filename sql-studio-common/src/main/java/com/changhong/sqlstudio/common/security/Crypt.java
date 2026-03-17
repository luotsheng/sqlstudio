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

/* Creates on 2023/5/16. */

import com.changhong.sqlstudio.common.security.cipher.AESCipher;
import com.changhong.sqlstudio.common.security.cipher.RSACipher;

/**
 * `Crypt` 是一个工具类，提供了多种加密和解密算法的实现，支持常见的加密需求。
 *
 * <p>该类包含静态方法，可以直接调用，无需实例化对象。支持的算法包括 AES、RSA 等，能够
 * 满足用户对数据加密和解密的需求，确保数据的安全性。
 *
 * <p>本类的主要特点包括：
 * <ul>
 *     <li>提供多种加密算法，支持不同类型的数据处理。</li>
 *     <li>所有方法均为静态，方便直接调用，简化使用流程。</li>
 *     <li>包含基本的异常处理，确保在加密解密过程中提供适当的错误信息。</li>
 * </ul>
 *
 * <h2>使用示例</h2>
 * <pre>
 *     // 使用 AES 加密
 *     String encrypted = Crypt.AES.encrypt("Hello World", "mysecretkey");
 * </pre>
 *
 * @author Luo Tiansheng
 * @since 1.0
 */
public final class Crypt {

    public static final AES AES = new AESCipher();    // AES
    public static final RSA RSA = new RSACipher();    // RSA

}
