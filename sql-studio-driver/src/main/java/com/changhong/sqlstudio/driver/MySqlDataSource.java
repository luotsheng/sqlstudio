package com.changhong.sqlstudio.driver;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据源配置
 *
 * @author Luo Tiansheng
 * @since 2026/3/17
 */
public class MySqlDataSource implements DataSourceAdapter {

    private final DataSource dataSource;

    public MySqlDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<String> getTableNames() throws SQLException {
        List<String> tableNames = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SHOW DATABASES")) {
            while (rs.next())
                tableNames.add(rs.getString(1));
        }
        return tableNames;
    }

}
