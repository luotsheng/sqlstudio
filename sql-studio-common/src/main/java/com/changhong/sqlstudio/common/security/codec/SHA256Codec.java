package com.changhong.sqlstudio.common.security.codec;

import com.changhong.sqlstudio.common.exception.SystemRuntimeException;
import com.changhong.sqlstudio.common.io.IOUtils;
import com.changhong.sqlstudio.common.io.SystemResource;
import com.changhong.sqlstudio.common.security.Codec;
import com.changhong.sqlstudio.common.security.SHA256;
import com.changhong.sqlstudio.common.utils.Captor;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * @author Luo Tiansheng
 */
public class SHA256Codec implements SHA256 {

    @Override
    public String encode(String source) {
        return encode(source.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String encode(File f0) {
        return Captor.icall(() -> {
            SystemResource systemResource = new SystemResource(f0);
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            systemResource.openInputStream(reader -> {
                int len = 0;
                byte[] buffer = new byte[IOUtils.MB];
                while ((len = reader.read(buffer)) != IOUtils.EOF)
                    messageDigest.update(buffer, 0, len);
            });
            return Codec.toByteHex(messageDigest.digest());
        });
    }

    @Override
    public String encode(byte[] source) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(source);
            return Codec.toByteHex(messageDigest.digest());
        } catch (Exception e) {
            throw new SystemRuntimeException(e);
        }
    }

}
