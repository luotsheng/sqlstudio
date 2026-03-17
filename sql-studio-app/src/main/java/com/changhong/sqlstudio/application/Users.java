package com.changhong.sqlstudio.application;

import com.alibaba.fastjson.JSONObject;
import com.changhong.sqlstudio.application.config.ConnectionConfig;
import com.changhong.sqlstudio.common.io.IOUtils;
import com.changhong.sqlstudio.common.io.SystemResource;
import com.changhong.sqlstudio.common.utils.Assert;
import com.changhong.sqlstudio.common.utils.Captor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
    private static final File connectionDir = new File(connectionSavePath);

    public static void initialize() {
        File file = new File(connectionSavePath);
        if (!file.exists())
            file.mkdirs();
    }

    public static void saveOrUpdateConnection(String name, ConnectionConfig config) {
        String savePath = connectionSavePath + "/" + name + ".sqlc";
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

    public static Map<String, ConnectionConfig> getConnectionList() {
        Map<String, ConnectionConfig> confs = new LinkedHashMap<>();
        File[] files = connectionDir.listFiles();

        if (files != null) {
            for (File file : files) {
                SystemResource systemResource = new SystemResource(file);
                JSONObject obj = systemResource.toJSONObject();
                confs.put(systemResource.getCleanName(), obj.toJavaObject(ConnectionConfig.class));
            }
        }

        return confs;
    }

}
