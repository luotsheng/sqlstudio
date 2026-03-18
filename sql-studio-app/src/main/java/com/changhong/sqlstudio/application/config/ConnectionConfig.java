package com.changhong.sqlstudio.application.config;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 连接配置
 *
 * @author Luo Tiansheng
 * @since 2026/3/17
 */
public class ConnectionConfig {

    private String host;
    private int port;
    private String jdbcType;
    private String username;
    private String password;
    private boolean useSSL;
    private String timezone;

    @JSONField(serialize = false)
    private boolean savePassword;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getJdbcType() {
        return jdbcType;
    }

    public void setJdbcType(String jdbcType) {
        this.jdbcType = jdbcType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isUseSSL() {
        return useSSL;
    }

    public void setUseSSL(boolean useSSL) {
        this.useSSL = useSSL;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public boolean isSavePassword() {
        return savePassword;
    }

    public void setSavePassword(boolean savePassword) {
        this.savePassword = savePassword;
    }

    public String buildJdbcUrl() {
        return "jdbc:" + jdbcType + "://" + host + ":" + port
                + "?useSSL=" + useSSL + "&serverTimezone=" + timezone;
    }

}
