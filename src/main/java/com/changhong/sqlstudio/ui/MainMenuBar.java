package com.changhong.sqlstudio.ui;

import com.changhong.sqlstudio.event.EventBus;
import com.changhong.sqlstudio.event.NewQueryEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import javax.management.Query;

import static org.eclipse.swt.SWT.*;
import static org.eclipse.swt.SWT.ALT;
import static org.eclipse.swt.SWT.CASCADE;
import static org.eclipse.swt.SWT.F4;
import static org.eclipse.swt.SWT.MOD1;
import static org.eclipse.swt.SWT.PUSH;
import static org.eclipse.swt.SWT.SEPARATOR;

/**
 * @author luotiansheng
 */
public class MainMenuBar {

    public MainMenuBar(Shell shell) {
        Menu menuBar = new Menu(shell, BAR);
        shell.setMenuBar(menuBar);

        Menu fileMenu = new Menu(menuBar);

        MenuItem fileItem = new MenuItem(menuBar, CASCADE);
        fileItem.setMenu(fileMenu);
        fileItem.setText("文件");

        Menu newMenu = new Menu(fileMenu);
        MenuItem newItem = new MenuItem(fileMenu, CASCADE);
        newItem.setText("&新建\tCtrl+N");
        newItem.setMenu(newMenu);

        MenuItem fileNewScriptItem = new MenuItem(newMenu, PUSH);
        fileNewScriptItem.setText("新建脚本\tCtrl+N");
        fileNewScriptItem.setAccelerator(MOD1 | 'N');

        fileNewScriptItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                EventBus.publish(new NewQueryEvent());
            }
        });

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

}
