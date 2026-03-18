package com.changhong.sqlstudio.driver;

import java.util.List;

/**
 * 表格数据
 *
 * @author Luo Tiansheng
 * @since 2026/3/17
 */
public class TableData {
    private List<String> column;
    private List<List<String>> data;

    public List<String> getColumn() {
        return column;
    }

    public void setColumn(List<String> column) {
        this.column = column;
    }

    public List<List<String>> getData() {
        return data;
    }

    public void setData(List<List<String>> data) {
        this.data = data;
    }
}
