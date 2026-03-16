package com.changhong.sqlstudio;

import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import static org.eclipse.swt.SWT.*;

public class SqlStudio {

    private static final String SQL_STUDIO_TITLE = "数据库可视化管理工具";
    private static final int[] SQL_STUDIO_RATIO = new int[] {30, 70};

    public static final Display DISPLAY = new Display();
    private final Shell shell;

    private SqlStudio() {
        shell = new Shell(DISPLAY);

        GridLayout gridLayout = new GridLayout();
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        gridLayout.verticalSpacing = 0;

        shell.setLayout(gridLayout);
        shell.setText(SQL_STUDIO_TITLE);
        shell.setSize(800, 600);

        createToolBar();
        createContents();
    }

    private void createToolBar() {
        ToolBar toolBar = new ToolBar(shell, FLAT | WRAP | NO_FOCUS);
        toolBar.setLayoutData(new GridData(FILL, CENTER, true, false));

        ToolItem connect = new ToolItem(toolBar, PUSH);
        connect.setImage(Images.CONNECT);
        connect.setToolTipText("连接");

        ToolItem query = new ToolItem(toolBar, PUSH);
        query.setImage(Images.QUERY);
        query.setToolTipText("查询");
    }

    private void createContents() {
        SashForm sash = new SashForm(shell, HORIZONTAL | BORDER);
        sash.setLayoutData(new GridData(FILL, FILL, true, true));

        createLeftComposite(sash);
        createRightComposite(sash);

        sash.setWeights(SQL_STUDIO_RATIO);
    }

    private void createLeftComposite(SashForm sashForm) {
        Composite site = new Composite(sashForm, BORDER);
        site.setLayout(new FillLayout());
        new Table(site, BORDER | FULL_SELECTION);
    }

    private void createRightComposite(SashForm sashForm) {
        Composite site = new Composite(sashForm, NONE);
        site.setLayout(new FillLayout());
        new Table(site, FULL_SELECTION);
    }

    private void run0(String[] args) {
        shell.open();

        while (!shell.isDisposed()) {
            if (!DISPLAY.readAndDispatch())
                DISPLAY.sleep();
        }

        shell.dispose();
    }

    public static void run(String[] args) {
        new SqlStudio().run0(args);
    }

}
