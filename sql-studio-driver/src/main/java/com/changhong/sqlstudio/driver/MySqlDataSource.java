package com.changhong.sqlstudio.driver;

import java.sql.*;
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
        "SqlSourceToSinkFlow",
})
public class MySqlDataSource extends HikariDataSourceAdapter
{
        public MySqlDataSource(DataSourceConfig config)
        {
                super(config);
        }

        public MySqlDataSource(Properties props)
        {
                super(props);
        }

        private void use(Statement statement, String dbName) throws SQLException
        {
                statement.execute("USE " + dbName + ";");
        }

        @Override
        public List<String> getDatabases() throws SQLException
        {
                List<String> databases = new ArrayList<>();
                try (Connection connection = getConnection();
                     Statement stmt = connection.createStatement();
                     ResultSet rs = stmt.executeQuery("SHOW DATABASES;")) {
                        while (rs.next())
                                databases.add(rs.getString(1));
                }
                return databases;
        }

        @Override
        public List<String> getTables(String dbName) throws SQLException
        {
                ResultSet rs = null;
                List<String> tables = new ArrayList<>();

                try (Connection connection = getConnection();
                     Statement stmt = connection.createStatement()) {
                        rs = stmt.executeQuery("SHOW TABLES FROM " + dbName);
                        while (rs.next())
                                tables.add(rs.getString(1));
                } finally {
                        if (rs != null)
                                rs.close();
                }

                return tables;
        }

        @Override
        public QueryResultSet selectTableData(String dbName, String tableName, int start, int count) throws SQLException
        {
                List<String> columns = new ArrayList<>();
                List<List<String>> rows = new ArrayList<>();

                String sql = "SELECT * FROM " + tableName + " LIMIT ? OFFSET ?;";

                try (Connection conn = getConnection();
                     PreparedStatement ps = conn.prepareStatement(sql)) {

                        use(ps, dbName);

                        ps.setInt(1, count);
                        ps.setInt(2, start);

                        try (ResultSet rs = ps.executeQuery()) {
                                ResultSetMetaData meta = rs.getMetaData();
                                int colCount = meta.getColumnCount();

                                for (int i = 1; i <= colCount; i++)
                                        columns.add(meta.getColumnLabel(i));

                                while (rs.next()) {
                                        List<String> row = new ArrayList<>();
                                        for (int i = 1; i <= colCount; i++) {
                                                Object val = rs.getObject(i);
                                                row.add(val != null ? val.toString() : null);
                                        }
                                        rows.add(row);
                                }
                        }

                }

                return QueryResultSet.of(columns, rows);
        }
}
