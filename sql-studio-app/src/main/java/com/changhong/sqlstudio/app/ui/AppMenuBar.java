package com.changhong.sqlstudio.app.ui;

import com.changhong.sqlstudio.core.event.notify.NewQueryScriptEvent;
import com.changhong.sqlstudio.core.event.EventBus;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import static org.eclipse.swt.SWT.*;

/**
 * @author Luo Tiansheng
 * @since 2026-03-01
 */
public class AppMenuBar {

    private final Menu menuBar;

    private static final String MENU_FILE = "文件";
    private static final String MENU_EDIT = "编辑";
    private static final String MENU_VIEW = "视图";
    private static final String MENU_WINDOW = "窗口";
    private static final String MENU_HELP = "帮助";

    public AppMenuBar(Shell shell) {
        this.menuBar = new Menu(shell, BAR);
        shell.setMenuBar(menuBar);
        createMenus();
    }

    private void createMenus() {
        createFileMenu();
    }

    private void createFileMenu() {
        MenuItem fileItem = new MenuItem(menuBar, CASCADE);
        fileItem.setText(MENU_FILE);
        Menu fileMenu = new Menu(fileItem);
        fileItem.setMenu(fileMenu);
        createNewSubMenu(fileMenu);
    }

    private void createNewSubMenu(Menu parent) {
        MenuItem newItem = new MenuItem(parent, CASCADE);
        newItem.setText("新建");

        Menu newMenu = new Menu(parent);
        newItem.setMenu(newMenu);

        MenuItem newScriptItem = new MenuItem(newMenu, PUSH);
        newScriptItem.setText("新建脚本\tCtrl+N");
        newScriptItem.setAccelerator(MOD1 | 'N');

        newScriptItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                EventBus.publish(new NewQueryScriptEvent());
            }
        });
    }

}
