package com.changhong.sqlstudio.driver;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

public abstract class HikariDataSourceAdapter extends HikariDataSource {

    public HikariDataSourceAdapter(DataSourceConfig config) {
        this(config.getProperties());
    }

    public HikariDataSourceAdapter(Properties props) {
        super(new HikariConfig(props));
    }

    /**
     * 查询数据库列表
     */
    public abstract List<String> getDatabases() throws SQLException;

    /**
     * 查询数据表列表
     */
    public abstract List<String> getTables(String dbName) throws SQLException;

    /**
     * 查询表数据
     */
    public abstract TableData selectTableData(String dbName, String tableName, int start, int count) throws SQLException;

}
