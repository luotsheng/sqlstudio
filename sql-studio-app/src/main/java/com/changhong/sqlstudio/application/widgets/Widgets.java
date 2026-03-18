package com.changhong.sqlstudio.application.widgets;

import com.changhong.sqlstudio.application.Launcher;
import com.changhong.sqlstudio.core.common.Cores;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

/**
 * 组件库
 *
 * @author Luo Tiansheng
 */
public class Widgets {

    /**
     * 显示保存确认对话框
     */
    public static int showQuestionDialog(String tips) {
        Display display = Launcher.display;
        Shell shell = display.getActiveShell();
        MessageBox dialog = new MessageBox(shell,
                SWT.YES | SWT.NO | SWT.CANCEL | SWT.ICON_QUESTION);

        dialog.setText(Cores.SQL_STUDIO_TITLE);
        dialog.setMessage(tips);

        return dialog.open();
    }

    /**
     * 显示错误对话框
     */
    public static int showErrorDialog(String title, String tips) {
        Display display = Launcher.display;
        Shell shell = display.getActiveShell();

        MessageBox dialog = new MessageBox(shell,
                SWT.YES | SWT.ICON_ERROR);

        dialog.setText(title);
        dialog.setMessage(tips);

        return dialog.open();
    }

}
