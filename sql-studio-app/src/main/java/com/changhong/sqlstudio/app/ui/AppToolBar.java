package com.changhong.sqlstudio.app.ui;

import com.changhong.sqlstudio.app.Images;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.*;

import static org.eclipse.swt.SWT.*;

/**
 * @author Luo Tiansheng
 * @since 2026-03-01
 */
public class AppToolBar {

    public AppToolBar(Shell shell) {
        ToolBar toolBar = new ToolBar(shell, FLAT | WRAP | NO_FOCUS);
        toolBar.setLayoutData(new GridData(FILL, CENTER, true, false));

        ToolItem connect = new ToolItem(toolBar, PUSH);
        connect.setImage(Images.CONNECT);
        connect.setToolTipText("连接");

        ToolItem query = new ToolItem(toolBar, PUSH);
        query.setImage(Images.QUERY);
        query.setToolTipText("查询");
    }

}
