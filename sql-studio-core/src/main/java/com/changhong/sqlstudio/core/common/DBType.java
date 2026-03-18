package com.changhong.sqlstudio.core.common;

/**
 * 数据库名称与 JDBC 映射
 *
 * @author Luo Tiansheng
 * @since 2026-03-17
 */
public enum DBType
{
        MySQL("MySQL", "mysql"),
        PostgreSQL("PostgreSQL", "postgresql"),
        Oracle("Oracle", "oracle"),
        SQL_SERVER("SQL Server", "sqlserver"),
        SQLite("SQLite", "sqlite"),
        DM("达梦数据库", "dm"),
        ;

        private final String name;
        private final String jdbcType;

        DBType(String name, String jdbcType)
        {
                this.name = name;
                this.jdbcType = jdbcType;
        }

        public String getName()
        {
                return name;
        }

        public String getJdbcType()
        {
                return jdbcType;
        }
}
