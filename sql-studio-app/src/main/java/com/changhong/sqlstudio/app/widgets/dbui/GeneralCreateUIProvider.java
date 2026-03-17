package com.changhong.sqlstudio.app.widgets.dbui;

import com.changhong.sqlstudio.core.common.DBType;
import com.changhong.sqlstudio.core.event.Event;
import com.changhong.sqlstudio.core.event.EventBus;
import com.changhong.sqlstudio.core.event.EventListener;
import com.changhong.sqlstudio.core.event.notify.ConnectionConfigChangeEvent;
import com.changhong.sqlstudio.driver.DataSourceConfig;
import com.changhong.sqlstudio.driver.MySqlDataSource;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

/**
 * 通用 JDBC 创建连接 UI 窗口类
 *
 * @author Luo Tiansheng
 * @since 2026/3/17
 */
@SuppressWarnings({
        "FieldCanBeLocal",
        "unused",
})
public class GeneralCreateUIProvider extends EventListener {

    private static final int DIALOG_WIDTH = 600;
    private static final int DIALOG_HEIGHT = 500;
    private static final int BUTTON_WIDTH = 80;
    private static final int BUTTON_HEIGHT = 25;
    private static final int HORIZONTAL_INDENT = 10;
    private static final String TAB_ITEM_GENERAL_TITLE = "常规";
    private static final String TAB_ITEM_ADVANCED_TITLE = "高级";

    private static final String[] COMMON_TIMEZONES = new String[]{
            "UTC",
            "Asia/Shanghai",
            "Asia/Hong_Kong",
            "Asia/Singapore",
            "Asia/Seoul",
            "Asia/Bangkok",
            "Asia/Dubai",
            "Europe/London",
            "Europe/Berlin",
            "Europe/Paris",
            "America/New_York",
            "America/Los_Angeles",
            "America/Chicago",
            "Australia/Sydney",
            "Asia/Tokyo",
    };

    private final Shell parentShell;
    private final String dialogTitle;
    private final ConnectionConfig config;
    private Shell dialog;
    private Composite container;
    private CTabFolder tabFolder;
    private CLabel status;
    private Text jdbcUrl;
    private Text user;
    private Text passwd;

    static class ConnectionConfig {
        public String host = "127.0.0.1";
        public String port = "3306";
        public String jdbcType;
        public String username = "root";
        public String password;
        public boolean useSSL = false;
        public String timezone = "Asia/Shanghai";

        public String build() {
            return "jdbc:" + jdbcType + "://" + host + ":" + port
                    + "?useSSL=" + useSSL + "&serverTimezone=" + timezone;
        }

    }

    public GeneralCreateUIProvider(DBType dbType) {
        this.dialogTitle = "新建" + dbType.getName() + "连接";

        this.config = new ConnectionConfig();
        this.config.jdbcType = dbType.getJdbcType();

        parentShell = Display.getCurrent().getActiveShell();

        /* 初始化 */
        configureDialog();
        configureContainer();
        configureTabFolder();

        /* 创建布局 */
        createGeneralTab();
        createAdvancedTab();

        /* 订阅事件 */
        EventBus.subscribe(ConnectionConfigChangeEvent.class, this);
    }

    private void configureDialog() {
        dialog = new Shell(parentShell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
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

    @Override
    public void eventTigger(Event event) {
        if (event instanceof ConnectionConfigChangeEvent) {
            jdbcUrl.setText(config.build());
        }
    }

    /**
     * 测试数据库连接
     */
    public void testConnection() {
        DataSourceConfig conf = new DataSourceConfig();
        conf.setJdbcUrl(jdbcUrl.getText());
        conf.setUsername(user.getText());
        conf.setPassword(passwd.getText());

        try (MySqlDataSource mySqlDataSource = new MySqlDataSource(conf)) {
            status.setText("数据库连接成功");
            status.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_DARK_GREEN));
        } catch (Exception e) {
            status.setText(e.getCause().getMessage());
            status.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
        }
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

        Text host = createLabeledTextField(content, "主机地址", config.host);
        host.addModifyListener(modifyEvent -> {
            config.host = host.getText();
            EventBus.publish(new ConnectionConfigChangeEvent());
        });

        Text port = createLabeledTextField(content, "端口号", config.port);
        host.addModifyListener(modifyEvent -> {
            config.port = port.getText();
            EventBus.publish(new ConnectionConfigChangeEvent());
        });

        user = createLabeledTextField(content, "用户名", config.username);
        user.addModifyListener(modifyEvent -> {
            config.username = user.getText();
            EventBus.publish(new ConnectionConfigChangeEvent());
        });

        passwd = createLabeledTextField(content, "密码", config.password, SWT.PASSWORD);

        new Label(content, SWT.NONE);
        new Label(content, SWT.NONE);

        Button checkBoxBtn = new Button(content, SWT.CHECK);
        checkBoxBtn.setText("保存密码");
        GridData checkBoxBtnGridData = new GridData(SWT.LEFT, SWT.CENTER, false, false);
        checkBoxBtnGridData.horizontalSpan = 2;
        checkBoxBtnGridData.horizontalIndent = HORIZONTAL_INDENT;
        checkBoxBtn.setLayoutData(checkBoxBtnGridData);

        /* 提示状态 */
        Label filler1 = new Label(content, SWT.NONE);
        filler1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));

        status = new CLabel(content, SWT.NONE);
        GridData gd = new GridData(SWT.BEGINNING, SWT.END, false, false);
        gd.horizontalSpan = 2;
        gd.widthHint = 2048;
        status.setLayoutData(gd);

        /* 按钮区域 */
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
        testBtn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                testConnection();
            }
        });

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
        cancelBtn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                dialog.dispose();
            }
        });

    }

    private void createAdvancedTab() {
        CTabItem tabItem = new  CTabItem(tabFolder, SWT.NONE);
        tabItem.setText(TAB_ITEM_ADVANCED_TITLE);

        Composite content = new Composite(tabFolder, SWT.NONE);
        content.setLayout(new GridLayout(2, false));
        tabItem.setControl(content);

        jdbcUrl = createLabeledTextField(content, "JDBC URL", config.build());

        Label label = new Label(content, SWT.LEFT);
        label.setText("时区：");
        GridData labelGridData = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
        labelGridData.horizontalIndent = HORIZONTAL_INDENT;
        label.setLayoutData(labelGridData);

        Combo timezoneCombo = new Combo(content, SWT.NONE);
        timezoneCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        timezoneCombo.setItems(COMMON_TIMEZONES);
        timezoneCombo.select(1);
        timezoneCombo.addModifyListener(modifyEvent -> {
            config.timezone = timezoneCombo.getText();
            EventBus.publish(new ConnectionConfigChangeEvent());
        });

        Button useSSLBtn = new Button(content, SWT.CHECK);
        useSSLBtn.setText("使用 SSL");
        GridData checkBoxBtnGridData = new GridData(SWT.LEFT, SWT.CENTER, false, false);
        checkBoxBtnGridData.horizontalSpan = 2;
        checkBoxBtnGridData.horizontalIndent = HORIZONTAL_INDENT;
        useSSLBtn.setLayoutData(checkBoxBtnGridData);
        useSSLBtn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                config.useSSL = useSSLBtn.getSelection();
                EventBus.publish(new ConnectionConfigChangeEvent());
            }
        });
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
