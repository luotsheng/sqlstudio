package com.changhong.sqlstudio.application.node;

import com.changhong.sqlstudio.core.event.EventBus;
import com.changhong.sqlstudio.core.event.notify.OpenDataTableTabEvent;
import com.changhong.sqlstudio.driver.QueryResultSet;
import com.changhong.swt.Images;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.TreeItem;

import java.sql.SQLException;

/**
 * 数据表
 *
 * @author Luo Tiansheng
 * @since 2026/3/18
 */
@SuppressWarnings({
        "FieldCanBeLocal",
})
public class DBNTable
{
        private final String tableName;
        private final TreeItem parent;
        private final TreeItem item;
        private final DBNDatabase db;
        private CTabItem openTabItem;

        public DBNTable(String tableName, DBNDatabase db, TreeItem parent)
        {
                this.tableName = tableName;
                this.db = db;
                this.parent = parent;

                item = new TreeItem(parent, SWT.NONE);
                item.setText(tableName);
                item.setImage(Images.TABLE);
                item.setData(this);
        }

        public QueryResultSet selectByPage(int start, int count) throws SQLException
        {
                return db().selectByPage(tableName, start, count);
        }

        public void openDataTabelTab()
        {
                if (openTabItem != null && !openTabItem.isDisposed()) {
                        openTabItem.getParent().setSelection(openTabItem);
                } else {
                        EventBus.publish(new OpenDataTableTabEvent(this));
                }
        }

        public String name()
        {
                return tableName;
        }

        public void close()
        {
                item.dispose();
        }

        public DBNDatabase db()
        {
                return db;
        }

        public void setOpenTabItem(CTabItem openTabItem)
        {
                this.openTabItem = openTabItem;
        }
}
