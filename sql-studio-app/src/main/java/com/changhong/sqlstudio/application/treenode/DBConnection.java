package com.changhong.sqlstudio.application.treenode;

import com.changhong.sqlstudio.application.config.ConnectionConfig;
import com.changhong.sqlstudio.application.window.Window;
import com.changhong.sqlstudio.core.event.EventBus;
import com.changhong.sqlstudio.core.event.notify.RuntimeErrorEvent;
import com.changhong.sqlstudio.driver.DataSourceConfig;
import com.changhong.sqlstudio.driver.DataSourceUtils;
import com.changhong.sqlstudio.driver.HikariDataSourceAdapter;
import com.changhong.sqlstudio.driver.MySqlDataSource;
import com.changhong.swt.Images;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TreeItem;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.eclipse.swt.SWT.NONE;

/**
 * 连接属性
 *
 * @author Luo Tiansheng
 * @since 2026/3/18
 */
@SuppressWarnings({
        "FieldCanBeLocal",
        "unused",
})
public class DBConnection extends DBTreeNode
{
        private final ConnectionConfig config;
        private final String name;
        private final TreeItem item;
        private final Map<String, DBDatabase> databases = new LinkedHashMap<>();
        private Menu menu;
        private MenuItem editConnection;
        private MenuItem openConnection;
        private MenuItem closeConnection;
        private HikariDataSourceAdapter ds;

        public DBConnection(String name, TreeItem parent, ConnectionConfig config)
        {
                this.name = name;
                this.config = config;
                this.openFlag = false;

                item = new TreeItem(parent, NONE);
                item.setText(name);
                item.setData(this);
                item.setImage(Images.DATABASE_0);

                configureMenu();
        }

        private void configureMenu()
        {
                menu = new Menu(Window.shell(), SWT.POP_UP);

                editConnection = new MenuItem(menu, SWT.PUSH);
                editConnection.setText("编辑连接");

                new MenuItem(menu, SWT.SEPARATOR);

                openConnection = new MenuItem(menu, SWT.PUSH);
                openConnection.setText("打开连接");
                openConnection.addSelectionListener(new SelectionAdapter()
                {
                        @Override
                        public void widgetSelected(SelectionEvent e)
                        {
                                open();
                        }
                });

                closeConnection = new MenuItem(menu, SWT.PUSH);
                closeConnection.setText("关闭连接");
                closeConnection.addSelectionListener(new SelectionAdapter()
                {
                        @Override
                        public void widgetSelected(SelectionEvent e)
                        {
                                close();
                        }
                });

        }

        private void asyncOpen(DataSourceConfig cnf)
        {
                ds = new MySqlDataSource(cnf);

                try {
                        List<String> databaseNames = ds.getDatabases();

                        for (String databaseName : databaseNames) {
                                if (databases.containsKey(databaseName))
                                        continue;
                                databases.put(databaseName, new DBDatabase(ds, item, databaseName));
                        }

                        item.setExpanded(true);
                } catch (SQLException e) {
                        EventBus.publish(new RuntimeErrorEvent(item.getText(), e));
                }
        }

        public void open()
        {
                if (openFlag)
                        return;

                new Thread(() -> {
                        openFlag = true;
                        DataSourceConfig cnf = config.getDataSourceConfig();

                        Throwable throwable = DataSourceUtils.testConnect(cnf);
                        if (throwable != null) {
                                Display.getDefault().asyncExec(() -> {
                                        EventBus.publish(new RuntimeErrorEvent(item.getText(), throwable));
                                });
                                return;
                        }

                        Display.getDefault().asyncExec(() -> asyncOpen(cnf));
                }).start();
        }

        public void close()
        {
                if (!openFlag)
                        return;

                if (ds != null)
                        ds.close();

                databases.values().forEach(DBDatabase::dispose);
                databases.clear();

                openFlag = false;
        }

        @Override
        public Menu getMenu()
        {
                if (openFlag) {
                        openConnection.setEnabled(false);
                        closeConnection.setEnabled(true);
                } else {
                        openConnection.setEnabled(true);
                        closeConnection.setEnabled(false);
                }
                return menu;
        }
}
