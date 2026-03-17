package com.changhong.sqlstudio.app.ui;

import com.changhong.sqlstudio.app.event.StartReadyEvent;
import com.changhong.sqlstudio.app.widgets.NewConnectionDialog;
import com.changhong.sqlstudio.core.event.Event;
import com.changhong.sqlstudio.core.event.EventBus;
import com.changhong.sqlstudio.core.event.EventListener;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;

import static org.eclipse.swt.SWT.*;

/**
 * @author Luo Tiansheng
 * @since 2026-03-01
 */
@SuppressWarnings("FieldCanBeLocal")
public class AppNavigator extends EventListener {

    private final Composite container;

    public AppNavigator(SashForm sashForm) {
        container = new Composite(sashForm, BORDER);

        container.setLayout(new FillLayout());
        CTabFolder tabFolder = new CTabFolder(container, HORIZONTAL);
        tabFolder.setSimple(false);

        createNavigatorTabItem(tabFolder);
        createProjectTabItem(tabFolder);

        EventBus.subscribe(StartReadyEvent.class, this);
    }

    @Override
    public void eventTigger(Event event) {
        if (event instanceof StartReadyEvent)
            openNewConnectionDialog();
    }

    private void createNavigatorTabItem(CTabFolder tabFolder) {
        CTabItem navigatorItem = new CTabItem(tabFolder, NONE);
        navigatorItem.setText("连接管理");

        Tree connectionTree = new Tree(tabFolder, NONE);
        navigatorItem.setControl(connectionTree);

        TreeItem rootItem = new TreeItem(connectionTree, NONE);
        rootItem.setText("我的连接");

        Menu menu = new Menu(connectionTree);
        connectionTree.setMenu(menu);

        MenuItem newConnectionItem = new MenuItem(menu, PUSH);
        newConnectionItem.setText("创建连接");
        newConnectionItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                openNewConnectionDialog();
            }
        });

        MenuItem closeAllConnectionItem = new MenuItem(menu, PUSH);
        closeAllConnectionItem.setText("关闭所有连接");
        closeAllConnectionItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                System.out.println("关闭所有连接");
            }
        });
    }

    private void openNewConnectionDialog() {
        new NewConnectionDialog().open();
    }

    private void createProjectTabItem(CTabFolder tabFolder) {
        CTabItem projectItem = new CTabItem(tabFolder, NONE);
        projectItem.setText("项目管理");

        Tree projectTree = new Tree(tabFolder, NONE);
        projectItem.setControl(projectTree);

        TreeItem projectTreeRoot = new TreeItem(projectTree, NONE);
        projectTreeRoot.setText("我的项目");

        TreeItem projectTreeSample = new TreeItem(projectTreeRoot, NONE);
        projectTreeSample.setText("项目样例");
    }

}
