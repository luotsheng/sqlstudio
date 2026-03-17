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
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import static com.changhong.sqlstudio.common.io.IOUtils.strread;

/**
 * @author Luo Tiansheng
 */
public class RSAPublicKey extends AbstractKey {

    public RSAPublicKey(Key key) {
        super(key);
    }

    public RSAPublicKey(byte[] encoded) {
        super(encoded);
    }

    public PublicKey toPublicKey() {
        return Captor.call(() -> {
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(getEncoded());
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(keySpec);
        });
    }

    public static RSAPublicKey fromKeyFile(String filepath) {
        SystemResource keyfile = new SystemResource(filepath);
        return fromPEMFormat(strread(keyfile));
    }

    public static RSAPublicKey fromPEMFormat(String pem) {
        return new RSAPublicKey(decodePEMFormat(pem));
    }

    @Override
    public String toPEMFormat() {
        return toPEMFormat0("PUBLIC KEY", getEncoded());
    }

}
