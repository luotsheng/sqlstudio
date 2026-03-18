package com.changhong.sqlstudio.application.obj;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TreeItem;

/**
 * 数据库
 *
 * @author Luo Tiansheng
 * @since 2026/3/18
 */
public class Database
{

    private final TreeItem parent;
    private final String name;
    private TreeItem item;

    public Database(TreeItem parent, String name)
    {
        this.parent = parent;
        this.name = name;
        bound();
    }

    public void bound()
    {
        item = new TreeItem(parent, SWT.NONE);
        item.setText(name);
    }

    public void destroy() {
        item.dispose();
    }

}
