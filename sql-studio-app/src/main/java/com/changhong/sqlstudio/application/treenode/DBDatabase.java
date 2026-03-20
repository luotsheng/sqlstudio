package com.changhong.sqlstudio.application.treenode;

import com.changhong.sqlstudio.application.Images;
import com.changhong.sqlstudio.application.window.Window;
import com.changhong.sqlstudio.core.event.EventBus;
import com.changhong.sqlstudio.core.event.notify.OpenNewQueryScriptEvent;
import com.changhong.sqlstudio.core.event.notify.RuntimeErrorEvent;
import com.changhong.sqlstudio.driver.HikariDataSourceAdapter;
import com.changhong.sqlstudio.driver.QueryResultSet;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
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
public class DBDatabase implements DBTreeNode
{
        private final HikariDataSourceAdapter ds;
        private final TreeItem parent;
        private final String name;
        private final Map<String, DBTable> tables = new LinkedHashMap<>();
        private boolean openFlag;
        private final TreeItem item;
        private TreeItem tabelItem;
        private TreeItem queryItem;
        private MenuItem openDatabaseItem;
        private MenuItem closeDatabaseItem;
        private Menu menu;

        public DBDatabase(HikariDataSourceAdapter ds, TreeItem parent, String name)
        {
                this.ds = ds;
                this.parent = parent;
                this.name = name;

                item = new TreeItem(parent, SWT.NO_FOCUS);
                item.setText(name);
                item.setData(this);
                item.setImage(Images.DATABASE_1);

                configureMenu();
        }

        private void configureMenu()
        {
                menu = new Menu(Window.shell(), SWT.POP_UP);

                openDatabaseItem = new MenuItem(menu, SWT.PUSH);
                openDatabaseItem.setText("打开数据库");
                openDatabaseItem.addSelectionListener(new SelectionAdapter()
                {
                        @Override
                        public void widgetSelected(SelectionEvent e)
                        {
                                if (!isOpen())
                                        open();
                        }
                });

                closeDatabaseItem = new MenuItem(menu, SWT.PUSH);
                closeDatabaseItem.setText("关闭数据库");
                closeDatabaseItem.addSelectionListener(new SelectionAdapter()
                {
                        @Override
                        public void widgetSelected(SelectionEvent e)
                        {
                                if (isOpen())
                                        close();
                        }
                });

                new MenuItem(menu, SWT.SEPARATOR);

                MenuItem newQueryItem = new MenuItem(menu, SWT.PUSH);
                newQueryItem.setText("新建查询");
                newQueryItem.addSelectionListener(new SelectionAdapter()
                {
                        @Override
                        public void widgetSelected(SelectionEvent e)
                        {
                                open();
                                EventBus.publish(new OpenNewQueryScriptEvent());
                        }
                });
        }

        private void showTables()
        {
                try {
                        List<String> tableNames = ds.getTables(item.getText());
                        for (String tableName : tableNames)
                                tables.put(tableName, new DBTable(tableName, this, tabelItem));
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

                item.setExpanded(true);
                openFlag = true;
        }

        public void close()
        {
                if (openFlag) {
                        openFlag = false;
                        tables.values().forEach(DBTable::close);
                        tabelItem.dispose();
                        queryItem.dispose();
                }
        }

        public void dispose()
        {
                close();
                item.dispose();
                menu.dispose();
        }

        public boolean isOpen()
        {
                return openFlag;
        }

        public QueryResultSet queryResultSet(String tableName, int start, int count) throws SQLException
        {
                return ds.queryResultSet(name, tableName, start, count);
        }

        @Override
        public Menu menu()
        {
                if (openFlag) {
                        openDatabaseItem.setEnabled(false);
                        closeDatabaseItem.setEnabled(true);
                } else {
                        openDatabaseItem.setEnabled(true);
                        closeDatabaseItem.setEnabled(false);
                }
                return menu;
        }
}
