package com.changhong.sqlstudio.application.node;

import org.eclipse.swt.widgets.Menu;

/**
 * 树节点
 *
 * @author Luo Tiansheng
 * @since 2026/3/19
 */
public abstract class DBNode
{
        protected boolean openFlag;

        public boolean isClose()
        {
                return !openFlag;
        }

        public abstract Menu getMenu();
}
