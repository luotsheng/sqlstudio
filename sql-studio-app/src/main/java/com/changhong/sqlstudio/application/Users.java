package com.changhong.sqlstudio.application;

import com.alibaba.fastjson.JSONObject;
import com.changhong.sqlstudio.application.config.ConnectionConfig;
import com.changhong.sqlstudio.common.utils.Assert;
import com.changhong.sqlstudio.common.utils.Captor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 用户相关信息
 *
 * @author Luo Tiansheng
 * @since 2026/3/17
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
public class Users {

    private static final String userHome = System.getProperty("user.home")
            .replaceAll("\\\\", "/");
    private static final String connectionSavePath = userHome + "/.sqlstudio/connections";

    public static void initialize() {
        File file = new File(connectionSavePath);
        if (!file.exists())
            file.mkdirs();
    }

    public static void saveOrUpdateConnection(String name, ConnectionConfig config) {
        String savePath = connectionSavePath + "/" + name + ".inf";
        File connectionFile = new File(savePath);

        if (!connectionFile.exists())
            Captor.call(connectionFile::createNewFile);

        try (FileOutputStream fileOutputStream = new FileOutputStream(connectionFile)) {
            String serializeString = JSONObject.toJSONString(config);
            fileOutputStream.write(serializeString.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
