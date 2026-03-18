package com.changhong.sqlstudio.driver;

/**
 * 数据源工具类
 *
 * @author Luo Tiansheng
 * @since 2026/3/17
 */
@SuppressWarnings("unused")
public class DataSourceUtils {

    public static Throwable testConnect(DataSourceConfig config) {
        try (MySqlDataSource ds = new MySqlDataSource(config)) {
            return null;
        } catch (Exception e) {
            return e.getCause();
        }
    }

}
