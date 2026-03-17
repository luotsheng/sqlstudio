package com.changhong.sqlstudio.driver;

import java.util.Properties;

/**
 * 数据源配置
 *
 * @author Luo Tiansheng
 * @since 2026/3/17
 */
public class DataSourceConfig {

    private String jdbcUrl;
    private String username;
    private String password;

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
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

    public Properties getProperties() {
        Properties props = new Properties();

        props.setProperty("jdbcUrl", jdbcUrl);
        props.setProperty("username", username);
        props.setProperty("password", password);

        props.setProperty("maximumPoolSize", "10");
        props.setProperty("minimumIdle", "5");

        return props;
    }

}
