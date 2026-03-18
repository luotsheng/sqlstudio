package com.changhong.sqlstudio.core.event.notify;

import com.changhong.sqlstudio.core.event.Event;

/**
 * 创建 SQL 脚本事件
 *
 * @author Luo Tiansheng
 * @since 2026-03-01
 */
public class OpenNewQueryScriptEvent implements Event
{
        private String databaseName;
        private String tableName;

        public OpenNewQueryScriptEvent()
        {

        }

        public OpenNewQueryScriptEvent(String databaseName, String tableName)
        {
                this.databaseName = databaseName;
                this.tableName = tableName;
        }

        public String getDatabaseName()
        {
                return databaseName;
        }

        public void setDatabaseName(String databaseName)
        {
                this.databaseName = databaseName;
        }

        public String getTableName()
        {
                return tableName;
        }

        public void setTableName(String tableName)
        {
                this.tableName = tableName;
        }
}
