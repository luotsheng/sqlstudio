package com.changhong.sqlstudio;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.widgets.Composite;

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

        createMenuBar();
        createToolBar();
        createContents();
    }

    private void createMenuBar() {
        Menu menuBar = new Menu(shell, BAR);
        shell.setMenuBar(menuBar);

        Menu fileMenu = new Menu(menuBar);

        MenuItem fileItem = new MenuItem(menuBar, CASCADE);
        fileItem.setMenu(fileMenu);
        fileItem.setText("文件");

        MenuItem newItem = new MenuItem(fileMenu, PUSH);
        newItem.setText("&新建\tCtrl+N");
        newItem.setAccelerator(MOD1 | 'N');

        MenuItem openItem = new MenuItem(fileMenu, PUSH);
        openItem.setText("&打开...\tCtrl+O");
        openItem.setAccelerator(MOD1 | 'O');

        new MenuItem(fileMenu, SEPARATOR);

        MenuItem exitItem = new MenuItem(fileMenu, PUSH);
        exitItem.setText("退出\tAlt+F4");
        exitItem.setAccelerator(ALT | F4);

        MenuItem editItem = new MenuItem(menuBar, CASCADE);
        editItem.setText("编辑");

        MenuItem viewItem = new MenuItem(menuBar, CASCADE);
        viewItem.setText("视图");

        MenuItem windowItem = new MenuItem(menuBar, CASCADE);
        windowItem.setText("窗口");

        MenuItem helpItem = new MenuItem(menuBar, CASCADE);
        helpItem.setText("帮助");
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

        CTabFolder tabFolder = new CTabFolder(site, HORIZONTAL);

        createNavigatorTabItem(tabFolder);
        createProjectTabItem(tabFolder);
    }

    private void createNavigatorTabItem(CTabFolder tabFolder) {
        CTabItem navigatorItem = new CTabItem(tabFolder, NONE | CLOSE);
        navigatorItem.setText("连接管理");
    }

    private void createProjectTabItem(CTabFolder tabFolder) {
        CTabItem projectItem = new CTabItem(tabFolder, NONE | CLOSE);
        projectItem.setText("项目管理");

        Composite projectContent = new Composite(tabFolder, NONE);
        projectContent.setLayout(new FillLayout());

        projectItem.setControl(projectContent);

        Tree projectTree = new Tree(projectContent, NONE);

        TreeItem projectTreeRoot = new TreeItem(projectTree, NONE);
        projectTreeRoot.setText("根项目");

        TreeItem projectTreeSample = new TreeItem(projectTreeRoot, NONE);
        projectTreeSample.setText("项目样例");
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
