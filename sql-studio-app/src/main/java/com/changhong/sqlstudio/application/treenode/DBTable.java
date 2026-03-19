package com.changhong.sqlstudio.application.treenode;

import com.changhong.sqlstudio.application.Images;
import com.changhong.sqlstudio.core.event.EventBus;
import com.changhong.sqlstudio.core.event.notify.OpenDataTableTabEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.TreeItem;

/**
 * 数据表
 *
 * @author Luo Tiansheng
 * @since 2026/3/18
 */
@SuppressWarnings({
        "FieldCanBeLocal",
})
public class DBTable
{
        private final String tableName;
        private final TreeItem parent;
        private final TreeItem item;
        private final DBDatabase db;
        private CTabItem openTabItem;

        public DBTable(String tableName, DBDatabase db, TreeItem parent)
        {
                this.tableName = tableName;
                this.db = db;
                this.parent = parent;

                item = new TreeItem(parent, SWT.NONE);
                item.setText(tableName);
                item.setImage(Images.TABLE);
                item.setData(this);
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

        public DBDatabase db()
        {
                return db;
        }

        public void setOpenTabItem(CTabItem openTabItem)
        {
                this.openTabItem = openTabItem;
        }

        public CTabItem getOpenTabItem()
        {
                return openTabItem;
        }
}
