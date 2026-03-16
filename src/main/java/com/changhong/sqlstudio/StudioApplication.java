package com.changhong.sqlstudio;

import com.changhong.sqlstudio.ui.MainWorkbench;
import com.changhong.sqlstudio.ui.MainMenuBar;
import com.changhong.sqlstudio.ui.MainToolBar;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

/**
 * @author luotiansheng
 */
@SuppressWarnings("FieldCanBeLocal")
public class StudioApplication {

    public static final String TITLE = "数据库可视化管理工具";
    public static final Display DISPLAY = new Display();

    private final Shell shell;
    private final MainMenuBar menuBar;
    private final MainToolBar toolBar;
    private final MainWorkbench mainWorkbench;

    private StudioApplication() {
        shell = new Shell(DISPLAY);

        GridLayout gridLayout = new GridLayout();
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        gridLayout.verticalSpacing = 0;

        shell.setLayout(gridLayout);
        shell.setText(TITLE);
        shell.setSize(800, 600);
        shell.setMaximized(true);

        menuBar = new MainMenuBar(shell);
        toolBar = new MainToolBar(shell);
        mainWorkbench = new MainWorkbench(shell);
    }

    public static void startEventLoop(String[] args) {
        new StudioApplication().startEventLoop0(args);
    }

    private void startEventLoop0(String[] args) {
        shell.open();

        while (!shell.isDisposed()) {
            if (!DISPLAY.readAndDispatch())
                DISPLAY.sleep();
        }

        shell.dispose();
    }

}
