package com.changhong.sqlstudio.driver;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 数据源配置
 *
 * @author Luo Tiansheng
 * @since 2026/3/17
 */
@SuppressWarnings({
        "SqlNoDataSourceInspection",
        "SqlDialectInspection",
})
public class MySqlDataSource extends HikariDataSourceAdapter {

    public MySqlDataSource(DataSourceConfig config) {
        super(config);
    }

    public MySqlDataSource(Properties props) {
        super(props);
    }

    @Override
    public List<String> getDatabases() throws SQLException {
        List<String> databases = new ArrayList<>();
        try (Connection connection = getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SHOW DATABASES")) {
            while (rs.next())
                databases.add(rs.getString(1));
        }
        return databases;
    }

}
