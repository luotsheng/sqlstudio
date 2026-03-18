package com.changhong.sqlstudio.application.obj;

import com.changhong.sqlstudio.application.config.ConnectionConfig;
import com.changhong.sqlstudio.core.event.EventBus;
import com.changhong.sqlstudio.core.event.notify.RuntimeErrorEvent;
import com.changhong.sqlstudio.driver.DataSourceConfig;
import com.changhong.sqlstudio.driver.DataSourceUtils;
import com.changhong.sqlstudio.driver.HikariDataSourceAdapter;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeItem;

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

        public Connection(TreeItem item, ConnectionConfig config)
        {
                this.item = item;
                this.config = config;
                this.isOpen = false;
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
                        }
                }).start();
        }

        public void close()
        {
                isOpen = false;
        }
}
