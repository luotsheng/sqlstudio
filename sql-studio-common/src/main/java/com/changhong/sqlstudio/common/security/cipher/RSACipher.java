package com.changhong.sqlstudio.common.security.cipher;

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
import com.changhong.sqlstudio.common.security.RSA;
import com.changhong.sqlstudio.common.security.key.RSAPrivateKey;
import com.changhong.sqlstudio.common.security.key.RSAPublicKey;
import com.changhong.sqlstudio.common.tuple.Pair;
import com.changhong.sqlstudio.common.utils.Captor;

import javax.crypto.Cipher;
import java.security.KeyPair;
import java.security.KeyPairGenerator;

import static com.changhong.sqlstudio.common.utils.TypeCvt.atob;
import static com.changhong.sqlstudio.common.utils.TypeCvt.atos;

/**
 * @author Luo Tiansheng
 */
@SuppressWarnings("DataFlowIssue")
public class RSACipher implements RSA
{

        @Override
        public Pair<RSAPublicKey, RSAPrivateKey> generateKeyPair()
        {
                return generateKeyPair(2048);
        }

        @Override
        public Pair<RSAPublicKey, RSAPrivateKey> generateKeyPair(int size)
        {
                KeyPairGenerator keyPairGenerator =
                        Captor.icall(() -> KeyPairGenerator.getInstance("RSA"));
                keyPairGenerator.initialize(size);
                KeyPair keyPair = keyPairGenerator.generateKeyPair();
                return Pair.of(new RSAPublicKey(keyPair.getPublic()), new RSAPrivateKey(keyPair.getPrivate()));
        }

        @Override
        public String encrypt(String message, RSAPublicKey publicKey)
        {
                return Captor.call(() -> {
                        Cipher cipher = Cipher.getInstance("RSA");
                        cipher.init(Cipher.ENCRYPT_MODE, publicKey.toPublicKey());
                        byte[] b = cipher.doFinal(atob(message));
                        return Codec.BASE64.encode(b);
                });
        }

        @Override
        public String decrypt(String encryptedMessage, RSAPrivateKey privateKey)
        {
                return Captor.call(() -> {
                        Cipher cipher = Cipher.getInstance("RSA");
                        cipher.init(Cipher.DECRYPT_MODE, privateKey.toPrivateKey());
                        byte[] b = cipher.doFinal(Codec.BASE64.decodeBytes(encryptedMessage));
                        return atos(b);
                });
        }

}
