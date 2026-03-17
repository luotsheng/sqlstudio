package com.changhong.sqlstudio.app.ui;

import com.changhong.sqlstudio.app.event.OpenDBCreateUIEvent;
import com.changhong.sqlstudio.app.widgets.dbui.GeneralCreateUIProvider;
import com.changhong.sqlstudio.core.common.DBType;
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

        EventBus.subscribe(OpenDBCreateUIEvent.class, this);
    }

    @Override
    public void eventTigger(Event event) {
        if (event instanceof OpenDBCreateUIEvent openDBCreateUIEvent)
            new GeneralCreateUIProvider(openDBCreateUIEvent.dbType().getName()).open();
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

        MenuItem newConnectionItem = new MenuItem(menu, CASCADE);
        newConnectionItem.setText("创建连接");

        Menu newConnectionSubMenu = new Menu(newConnectionItem);
        newConnectionItem.setMenu(newConnectionSubMenu);

        for (DBType type : DBType.values()) {
            MenuItem newDBConnectionItem = new MenuItem(newConnectionSubMenu, PUSH);
            newDBConnectionItem.setText(type.getName());

            switch (type) {
                case PostgreSQL:
                case Oracle:
                case SQL_SERVER:
                case DM:
                case MySQL:
                    newDBConnectionItem.addSelectionListener(new SelectionAdapter() {
                        @Override
                        public void widgetSelected(SelectionEvent e) {
                            EventBus.publish(new OpenDBCreateUIEvent(type, e));
                        }
                    });
                    break;
                case SQLite: break;
            }

        }

        MenuItem closeAllConnectionItem = new MenuItem(menu, PUSH);
        closeAllConnectionItem.setText("关闭所有连接");
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
