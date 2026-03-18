package com.changhong.sqlstudio.application;

import com.alibaba.fastjson.JSONObject;
import com.changhong.sqlstudio.application.config.ConnectionConfig;
import com.changhong.sqlstudio.common.io.IOUtils;
import com.changhong.sqlstudio.common.io.SystemResource;
import com.changhong.sqlstudio.common.utils.Assert;
import com.changhong.sqlstudio.common.utils.Captor;
import com.changhong.sqlstudio.core.event.EventBus;
import com.changhong.sqlstudio.core.event.notify.RefreshConnectionListEvent;
import com.changhong.sqlstudio.core.event.notify.ThrowExceptionEvent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
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

    private static File getConnectionFile(String name) {
        return new File(connectionSavePath + "/" + name);
    }

    public static void initialize() {
        File file = new File(connectionSavePath);
        if (!file.exists())
            file.mkdirs();
    }

    public static boolean saveConnection(String name, ConnectionConfig config) {
        File connectionFile = getConnectionFile(name);

        if (config.isSavePassword())
            config.setPassword(null);

        if (connectionFile.exists()) {
            EventBus.publish(new ThrowExceptionEvent(new FileAlreadyExistsException(name + " - 名称已存在")));
            return false;
        }

        Captor.call(connectionFile::createNewFile);

        try (FileOutputStream fileOutputStream = new FileOutputStream(connectionFile)) {
            String serializeString = JSONObject.toJSONString(config);
            fileOutputStream.write(serializeString.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return true;
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

    public static boolean checkConnectionExists(String name) {
        return getConnectionFile(name).exists();
    }

}
