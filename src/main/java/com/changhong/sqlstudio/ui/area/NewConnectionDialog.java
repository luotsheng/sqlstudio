package com.changhong.sqlstudio.ui.area;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

/**
 * @author luotiansheng
 */
@SuppressWarnings("FieldCanBeLocal")
public class NewConnectionDialog {

    private final Shell parent;
    private final Shell dialog;

    private CTabFolder tabFolder;
    private CTabItem advancedTab;
    private CTabItem generalTab;

    public NewConnectionDialog() {
        parent = Display.getCurrent().getActiveShell();

        dialog = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE);
        dialog.setText("新建连接");
        dialog.setSize(600, 500);
        dialog.setLayout(new FormLayout());

        Monitor primary = Display.getCurrent().getPrimaryMonitor();
        Rectangle bounds = primary.getBounds();
        Rectangle rect = dialog.getBounds();

        int x = bounds.x + (bounds.width - rect.width) / 2;
        int y = bounds.y + (bounds.height - rect.height) / 2;

        dialog.setLocation(x, y);

        createTabFolder();
        createGeneralTab();
        createButtonComp();
        createAdvancedTab();

        tabFolder.setSelection(generalTab);
    }

    public void open() {
        dialog.open();

        Display display = parent.getDisplay();
        while (!dialog.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }

    private void createAdvancedTab() {
        /* 高级标签页 */
        advancedTab = new CTabItem(tabFolder, SWT.NONE);
        advancedTab.setText("高级");

        Composite advancedComp = new Composite(tabFolder, SWT.NONE);
        advancedComp.setLayout(new GridLayout(2, false));
        advancedTab.setControl(advancedComp);

        // 编码
        Label encodingLabel = new Label(advancedComp, SWT.NONE);
        encodingLabel.setText("编码：");
        encodingLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));

        Combo encodingCombo = new Combo(advancedComp, SWT.DROP_DOWN | SWT.READ_ONLY);
        encodingCombo.setItems(new String[]{"UTF-8", "GBK", "Latin1"});
        encodingCombo.select(0);
        encodingCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        // 自动连接
        Button autoConnectBtn = new Button(advancedComp, SWT.CHECK);
        autoConnectBtn.setText("启动时自动连接");
        autoConnectBtn.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));

        // 连接超时
        Label timeoutLabel = new Label(advancedComp, SWT.NONE);
        timeoutLabel.setText("连接超时(秒)：");
        timeoutLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));

        Spinner timeoutSpinner = new Spinner(advancedComp, SWT.BORDER);
        timeoutSpinner.setMinimum(1);
        timeoutSpinner.setMaximum(600);
        timeoutSpinner.setSelection(30);
        timeoutSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    }

    private void createTabFolder() {
        tabFolder = new CTabFolder(dialog, SWT.TOP);
        tabFolder.setBorderVisible(true);
        tabFolder.setTabHeight(25);

        FormData tabData = new FormData();
        tabData.top = new FormAttachment(0, 5);
        tabData.left = new FormAttachment(0, 5);
        tabData.right = new FormAttachment(100, -5);
        tabData.bottom = new FormAttachment(100, -45);
        tabFolder.setLayoutData(tabData);
    }

    private void createGeneralTab() {
        /* 常规标签页 */
        generalTab = new CTabItem(tabFolder, SWT.NONE);
        generalTab.setText("常规");

        Composite generalComp = new Composite(tabFolder, SWT.NONE);
        generalComp.setLayout(new GridLayout(2, false));
        generalTab.setControl(generalComp);

        // 空行
        new Label(generalComp, SWT.NONE);
        new Label(generalComp, SWT.NONE);

        Label nameLabel = new Label(generalComp, SWT.NONE);
        nameLabel.setText("连接名称：");
        nameLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));

        Text nameText = new Text(generalComp, SWT.BORDER);
        nameText.setText("本地数据库");
        nameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        // 主机
        Label hostLabel = new Label(generalComp, SWT.NONE);
        hostLabel.setText("主机：");
        hostLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));

        Text hostText = new Text(generalComp, SWT.BORDER);
        hostText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        hostText.setText("localhost");

        // 端口
        Label portLabel = new Label(generalComp, SWT.NONE);
        portLabel.setText("端口：");
        portLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));

        Text portText = new Text(generalComp, SWT.BORDER);
        portText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        portText.setText("3306");

        // 用户名
        Label userLabel = new Label(generalComp, SWT.NONE);
        userLabel.setText("用户：");
        userLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));

        Text userText = new Text(generalComp, SWT.BORDER);
        userText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        userText.setText("root");

        // 密码
        Label passLabel = new Label(generalComp, SWT.NONE);
        passLabel.setText("密码：");
        passLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));

        Text passText = new Text(generalComp, SWT.BORDER | SWT.PASSWORD);
        passText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        // 保存密码复选框
        Button savePassBtn = new Button(generalComp, SWT.CHECK);
        savePassBtn.setText("保存密码");
        savePassBtn.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
    }

    private void createButtonComp() {
        /* 按钮区域 */
        Composite buttonComp = new Composite(dialog, SWT.NONE);
        FormData buttonData = new FormData();
        buttonData.bottom = new FormAttachment(100, -10);
        buttonData.right = new FormAttachment(100, -10);
        buttonComp.setLayoutData(buttonData);
        buttonComp.setLayout(new GridLayout(3, false));

        // 测试连接按钮
        Button testBtn = new Button(buttonComp, SWT.PUSH);
        testBtn.setText("测试连接");
        testBtn.setLayoutData(new GridData(80, 25));

        // 确定按钮
        Button okButton = new Button(buttonComp, SWT.PUSH);
        okButton.setText("确定");
        okButton.setLayoutData(new GridData(80, 25));

        // 取消按钮
        Button cancelButton = new Button(buttonComp, SWT.PUSH);
        cancelButton.setText("取消");
        cancelButton.setLayoutData(new GridData(80, 25));


        // 按钮事件
        okButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                dialog.close();
            }
        });

        cancelButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                dialog.close();
            }
        });

        testBtn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                MessageBox msg = new MessageBox(dialog, SWT.ICON_INFORMATION | SWT.OK);
                msg.setText("连接测试");
                msg.setMessage("连接成功！");
                msg.open();
            }
        });

    }

}
