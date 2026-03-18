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

import com.changhong.sqlstudio.common.io.SystemResource;
import com.changhong.sqlstudio.common.utils.Captor;

import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;

import static com.changhong.sqlstudio.common.io.IOUtils.strread;

/**
 * @author Luo Tiansheng
 */
public class RSAPrivateKey extends AbstractKey
{

        public RSAPrivateKey(Key key)
        {
                super(key);
        }

        public RSAPrivateKey(byte[] encoded)
        {
                super(encoded);
        }

        public static RSAPrivateKey fromKeyFile(String filepath)
        {
                SystemResource keyfile = new SystemResource(filepath);
                return fromPEMFormat(strread(keyfile));
        }

        public static RSAPrivateKey fromPEMFormat(String pem)
        {
                return new RSAPrivateKey(decodePEMFormat(pem));
        }

        public PrivateKey toPrivateKey()
        {
                return Captor.call(() -> {
                        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(getEncoded());
                        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                        return keyFactory.generatePrivate(keySpec);
                });
        }

        @Override
        public String toPEMFormat()
        {
                return toPEMFormat0("PRIVATE KEY", getEncoded());
        }

}
