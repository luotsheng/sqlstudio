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

/**
 * 连接属性
 *
 * @author Luo Tiansheng
 * @since 2026/3/18
 */
public class Connection
{
        private final ConnectionConfig config;
        private final TreeItem item;
        private boolean isOpen;
        private HikariDataSourceAdapter ds;
        private final Map<String, Database> databases = new LinkedHashMap<>();

        public Connection(TreeItem item, ConnectionConfig config)
        {
                this.item = item;
                this.config = config;
                this.isOpen = false;
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

                isOpen = false;
        }
}
