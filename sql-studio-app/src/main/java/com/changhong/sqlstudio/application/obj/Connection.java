package com.changhong.sqlstudio.application.obj;

import com.changhong.sqlstudio.application.config.ConnectionConfig;
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

        public Connection(TreeItem item, ConnectionConfig config) {
                this.item = item;
                this.config = config;
                this.isOpen = false;
        }

        public void open() {
                isOpen = true;
        }

        public void close()
        {
                isOpen = false;
        }
}
