package com.changhong.sqlstudio.app.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

/**
 * 组件库
 *
 * @author luotiansheng
 */
public class Widgets {

    /**
     * 显示保存确认对话框
     */
    public static int showDialog(Shell shell, String tips) {
        MessageBox dialog = new MessageBox(shell,
                SWT.YES | SWT.NO | SWT.CANCEL | SWT.ICON_QUESTION);

        dialog.setText("数据库管理工具");
        dialog.setMessage(tips);

        return dialog.open();
    }

}
