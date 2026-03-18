package com.changhong.sqlstudio.driver;

import java.util.List;

/**
 * 表格数据
 *
 * @author Luo Tiansheng
 * @since 2026/3/17
 */
public class QueryResultSet
{
        private final List<String> columns;
        private final List<List<String>> rows;

        public QueryResultSet(List<String> columns, List<List<String>> rows)
        {
                this.columns = columns;
                this.rows = rows;
        }

        public List<String> getColumns()
        {
                return columns;
        }

        public List<List<String>> getRows()
        {
                return rows;
        }

        public static QueryResultSet of(List<String> columns, List<List<String>> rows)
        {
                return new QueryResultSet(columns, rows);
        }
}
