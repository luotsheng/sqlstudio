package com.changhong.sqlstudio.driver;

import java.sql.SQLException;
import java.util.List;

public interface DataSourceAdapter {

    /**
     * 查询数据库列表
     */
    List<String> getTableNames() throws SQLException;

}
