package com.changhong.sqlstudio.app.widgets;


import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Luo Tiansheng
 * @since 2026/3/17
 */
@SuppressWarnings({
        "FieldCanBeLocal",
        "unused"
})
public class NewConnectionDialog {

    private static final String DIALOG_TITLE = "新建连接";
    private static final int DIALOG_WIDTH = 600;
    private static final int DIALOG_HEIGHT = 500;
    private static final int BUTTON_WIDTH = 80;
    private static final int BUTTON_HEIGHT = 25;
    private static final String TAB_ITEM_GENERAL_TITLE = "常规";
    private static final String TAB_ITEM_ADVANCED_TITLE = "高级";

    private final Shell parentShell;
    private final Shell dialog;
    private final Composite container;
    private final CTabFolder tabFolder;

    public NewConnectionDialog() {
        parentShell = Display.getCurrent().getActiveShell();

        dialog = new Shell(parentShell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE);
        dialog.setText(DIALOG_TITLE);
        dialog.setSize(DIALOG_WIDTH, DIALOG_HEIGHT);

        container = new Composite(dialog, SWT.NONE);
        container.setLayout(new FillLayout());

        tabFolder = new  CTabFolder(container, SWT.BORDER);

        createGeneralTab();
        createAdvancedTab();
    }

    private void createGeneralTab() {
        CTabItem cTabItem = new  CTabItem(tabFolder, SWT.NONE);
        cTabItem.setText(TAB_ITEM_GENERAL_TITLE);
    }

    private void createAdvancedTab() {
        CTabItem cTabItem = new  CTabItem(tabFolder, SWT.NONE);
        cTabItem.setText(TAB_ITEM_ADVANCED_TITLE);
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

}
