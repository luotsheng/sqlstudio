package com.changhong.sqlstudio.app.widgets.dbui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

/**
 * @author Luo Tiansheng
 * @since 2026/3/17
 */
@SuppressWarnings({
        "FieldCanBeLocal",
        "unused"
})
public class GeneralCreateUIProvider {

    private static final int DIALOG_WIDTH = 600;
    private static final int DIALOG_HEIGHT = 500;
    private static final int BUTTON_WIDTH = 80;
    private static final int BUTTON_HEIGHT = 25;
    private static final int HORIZONTAL_INDENT = 10;
    private static final String TAB_ITEM_GENERAL_TITLE = "常规";
    private static final String TAB_ITEM_ADVANCED_TITLE = "高级";

    private final Shell parentShell;
    private final String dialogTitle;
    private Shell dialog;
    private Composite container;
    private CTabFolder tabFolder;

    public GeneralCreateUIProvider(String dbname) {
        this.dialogTitle = "新建" + dbname + "连接";

        parentShell = Display.getCurrent().getActiveShell();

        /* 初始化 */
        configureDialog();
        configureContainer();
        configureTabFolder();

        /* 创建布局 */
        createGeneralTab();
        createAdvancedTab();
    }

    private void configureDialog() {
        dialog = new Shell(parentShell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE);
        dialog.setText(dialogTitle);
        dialog.setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
        dialog.setLayout(new GridLayout(1, false));
    }

    private void configureContainer() {
        container = new Composite(dialog, SWT.NONE);
        container.setLayout(new FillLayout());
        GridData containerGridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        containerGridData.grabExcessVerticalSpace = true;
        container.setLayoutData(containerGridData);
    }

    private void configureTabFolder() {
        tabFolder = new  CTabFolder(container, SWT.BORDER);
        tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    }

    private void createGeneralTab() {
        CTabItem tabItem = new  CTabItem(tabFolder, SWT.NONE);
        tabItem.setText(TAB_ITEM_GENERAL_TITLE);

        Composite content = new Composite(tabFolder, SWT.NONE);
        content.setLayout(new GridLayout(2, false));
        tabItem.setControl(content);

        Text connectionName = createLabeledTextField(content, "连接名称", "本地数据库");

        new Label(content, SWT.NONE);
        new Label(content, SWT.NONE);

        Text host = createLabeledTextField(content, "主机地址", "127.0.0.1");
        Text port = createLabeledTextField(content, "端口号", "3306");
        Text user = createLabeledTextField(content, "用户名", "root");
        Text passwd = createLabeledTextField(content, "密码", null, SWT.PASSWORD);

        new Label(content, SWT.NONE);
        new Label(content, SWT.NONE);

        Button checkBoxBtn = new Button(content, SWT.CHECK);
        checkBoxBtn.setText("保存密码");
        GridData checkBoxBtnGridData = new GridData(SWT.LEFT, SWT.CENTER, false, false);
        checkBoxBtnGridData.horizontalSpan = 2;
        checkBoxBtnGridData.horizontalIndent = HORIZONTAL_INDENT;
        checkBoxBtn.setLayoutData(checkBoxBtnGridData);

        Composite buttonBar = new Composite(dialog, SWT.NONE);
        buttonBar.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false));
        GridLayout buttonBarGridLayout = new GridLayout(4, false);
        buttonBarGridLayout.marginWidth = 0;
        buttonBarGridLayout.marginHeight = 0;
        buttonBarGridLayout.horizontalSpacing = 8;
        buttonBar.setLayout(buttonBarGridLayout);

        Button testBtn = new Button(buttonBar, SWT.PUSH);
        testBtn.setText("测试连接");
        GridData testBtnGridData = new GridData(SWT.LEFT, SWT.CENTER, true, false);
        testBtnGridData.widthHint = 90;
        testBtn.setLayoutData(testBtnGridData);

        Label spacerLabel1 = new Label(buttonBar, SWT.NONE);
        spacerLabel1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        Button saveBtn = new Button(buttonBar, SWT.PUSH);
        saveBtn.setText("保存");
        GridData saveBtnGridData = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
        saveBtnGridData.widthHint = 80;
        saveBtn.setLayoutData(saveBtnGridData);

        Button cancelBtn = new Button(buttonBar, SWT.PUSH);
        cancelBtn.setText("取消");
        GridData cancelBtnGridData = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
        cancelBtnGridData.widthHint = 80;
        cancelBtn.setLayoutData(cancelBtnGridData);

    }

    private void createAdvancedTab() {
        CTabItem tabItem = new  CTabItem(tabFolder, SWT.NONE);
        tabItem.setText(TAB_ITEM_ADVANCED_TITLE);

        Composite content = new Composite(tabFolder, SWT.NONE);
        content.setLayout(new GridLayout(2, false));
        tabItem.setControl(content);

        Text jdbcUrl = createLabeledTextField(content, "JDBC URL", "jdbc:mysql://127.0.0.1:3306/testdb?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8");
    }

    public void open() {
        dialog.open();

        Display display = parentShell.getDisplay();
        while (!dialog.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }

    private static Text createLabeledTextField(Composite content, String title, String defValue, int... flags) {
        int style = 0;

        if (flags.length != 0)
            style |= flags[0];

        Label label = new Label(content, SWT.LEFT);
        label.setText(title + "：");
        GridData labelGridData = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
        labelGridData.horizontalIndent = HORIZONTAL_INDENT;
        label.setLayoutData(labelGridData);

        Text text = new Text(content, SWT.BORDER | style);
        if (defValue != null)
            text.setText(defValue);
        text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        return text;
    }

}
