package com.changhong.sqlstudio.driver;

import java.sql.SQLException;
import java.util.Properties;

/**
 * @author Luo Tiansheng
 * @since 2026/3/17
 */
public class Main
{
        public static void main(String[] args) throws SQLException
        {
                Properties props = new Properties();

                props.setProperty("jdbcUrl", "jdbc:mysql://127.0.0.1:3306?useSSL=false&serverTimezone=UTC");
                props.setProperty("username", "root");
                props.setProperty("password", "roo1t");

                props.setProperty("maximumPoolSize", "10");
                props.setProperty("minimumIdle", "5");

                MySqlDataSource ds = null;

                try {
                        ds = new MySqlDataSource(props);
                        System.out.println("连接池初始化成功");
                } catch (Exception e) {
                        System.err.println("连接池初始化失败: " + e.getCause().getMessage());
                } finally {
                        if (ds != null) {
                                ds.close();
                        }
                }
        }
}
