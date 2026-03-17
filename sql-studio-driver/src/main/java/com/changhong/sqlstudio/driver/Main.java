package com.changhong.sqlstudio.driver;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

/**
 * @author Luo Tiansheng
 * @since 2026/3/17
 */
public class Main {
    public static void main(String[] args) throws SQLException {
        Properties props = new Properties();

        props.setProperty("jdbcUrl", "jdbc:mysql://127.0.0.1:3306?useSSL=false&serverTimezone=UTC");
        props.setProperty("username", "root");
        props.setProperty("password", "root");

        props.setProperty("maximumPoolSize", "10");
        props.setProperty("minimumIdle", "2");

        HikariDataSource ds = new HikariDataSource(new HikariConfig(props));
        MySqlDataSource dataSource = new MySqlDataSource(ds);
    }
}
