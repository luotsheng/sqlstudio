package com.changhong.sqlstudio.application.obj;

import com.changhong.sqlstudio.application.Images;
import com.changhong.sqlstudio.core.event.EventBus;
import com.changhong.sqlstudio.core.event.notify.RuntimeErrorEvent;
import com.changhong.sqlstudio.driver.HikariDataSourceAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TreeItem;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据库
 *
 * @author Luo Tiansheng
 * @since 2026/3/18
 */
@SuppressWarnings({
        "FieldCanBeLocal",
})
public class Database
{
        private final HikariDataSourceAdapter ds;
        private final TreeItem parent;
        private final String name;
        private boolean openFlag;
        private TreeItem item;
        private TreeItem tabelItem;
        private TreeItem queryItem;
        private final Map<String, TreeItem> tables = new LinkedHashMap<>();

        public Database(HikariDataSourceAdapter ds, TreeItem parent, String name)
        {
                this.ds = ds;
                this.parent = parent;
                this.name = name;

                item = new TreeItem(parent, SWT.NO_FOCUS);
                item.setText(name);
                item.setData(this);
                item.setImage(Images.DATABASE_1);
        }

        private void showTables() {
                try {
                        List<String> tableNames = ds.getTables(item.getText());
                        for (String tableName : tableNames) {
                                TreeItem tableItem = new TreeItem(tabelItem, SWT.NONE);
                                tableItem.setText(tableName);
                                tableItem.setImage(Images.TABLE);
                                tables.put(tableName, tableItem);
                        }
                } catch (SQLException e) {
                        EventBus.publish(new RuntimeErrorEvent(e));
                }
        }

        public void open()
        {
                if (openFlag)
                        return;

                tabelItem = new TreeItem(item, SWT.NONE);
                tabelItem.setText("数据表");
                tabelItem.setImage(Images.TABLE);

                queryItem = new TreeItem(item, SWT.NONE);
                queryItem.setText("查询脚本");
                queryItem.setImage(Images.SQL);

                showTables();

                openFlag = true;
        }

        public void close()
        {
                item.dispose();

                if (!openFlag)
                        return;

                tables.values().forEach(TreeItem::dispose);
                tabelItem.dispose();
                queryItem.dispose();
        }

        public boolean isOpen()
        {
                return openFlag;
        }
}
