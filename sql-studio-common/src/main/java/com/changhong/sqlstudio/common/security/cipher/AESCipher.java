package com.changhong.sqlstudio.common.security.cipher;

import com.changhong.sqlstudio.common.security.AES;
import com.changhong.sqlstudio.common.security.Codec;
import com.changhong.sqlstudio.common.utils.Captor;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import static com.changhong.sqlstudio.common.utils.TypeCvt.atos;

/**
 * @author Luo Tiansheng
 */
public class AESCipher implements AES
{

        @Override
        public String encrypt(String data, String secret)
        {
                return encrypt(data.getBytes(), secret);
        }

        @Override
        public String encrypt(byte[] bytes, String secret)
        {
                return Captor.call(() -> {
                        Cipher cipher = Cipher.getInstance("AES");
                        SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(), "AES");
                        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
                        return Codec.BASE64.encode(cipher.doFinal(bytes));
                });
        }

        @Override
        public String decrypt(String data, String secret)
        {
                return atos(decrypt(Codec.BASE64.decodeBytes(data), secret));
        }

        @Override
        public byte[] decrypt(byte[] bytes, String secret)
        {
                return Captor.call(() -> {
                        Cipher cipher = Cipher.getInstance("AES");
                        SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(), "AES");
                        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
                        return cipher.doFinal(bytes);
                });
        }

}
