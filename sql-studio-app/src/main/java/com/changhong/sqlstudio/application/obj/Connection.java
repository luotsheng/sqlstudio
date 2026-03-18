package com.changhong.sqlstudio.application.obj;

import com.changhong.sqlstudio.application.config.ConnectionConfig;
import com.changhong.sqlstudio.core.event.EventBus;
import com.changhong.sqlstudio.core.event.notify.RuntimeErrorEvent;
import com.changhong.sqlstudio.driver.DataSourceConfig;
import com.changhong.sqlstudio.driver.DataSourceUtils;
import com.changhong.sqlstudio.driver.HikariDataSourceAdapter;
import com.changhong.sqlstudio.driver.MySqlDataSource;
import org.eclipse.swt.widgets.Display;
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
@SuppressWarnings("FieldCanBeLocal")
public class Connection
{
        private final ConnectionConfig config;
        private final String name;
        private final TreeItem parent;
        private final TreeItem item;
        private boolean isOpen;
        private HikariDataSourceAdapter ds;
        private final Map<String, Database> databases = new LinkedHashMap<>();

        public Connection(String name, TreeItem parent, ConnectionConfig config)
        {
                this.name = name;
                this.config = config;
                this.parent = parent;
                this.isOpen = false;

                item = new TreeItem(parent, NONE);
                item.setText(name);
        }

        private void asyncOpen(DataSourceConfig cnf)
        {
                ds = new MySqlDataSource(cnf);

                try {
                        List<String> databaseNames = ds.getDatabases();

                        for (String databaseName : databaseNames) {
                                if (databases.containsKey(databaseName))
                                        continue;
                                databases.put(databaseName, new Database(item, databaseName));
                        }
                } catch (SQLException e) {
                        EventBus.publish(new RuntimeErrorEvent(item.getText(), e));
                }
        }

        public void open()
        {
                new Thread(() -> {
                        isOpen = true;
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
                if (!isOpen)
                        return;

                if (ds != null)
                        ds.close();

                isOpen = false;
        }
}
