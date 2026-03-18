package com.changhong.sqlstudio.application.ui;

import com.changhong.sqlstudio.application.Users;
import com.changhong.sqlstudio.application.config.ConnectionConfig;
import com.changhong.sqlstudio.application.obj.Connection;
import com.changhong.sqlstudio.core.event.notify.ApplicationReadyEvent;
import com.changhong.sqlstudio.core.event.notify.OpenDBCreateUIEvent;
import com.changhong.sqlstudio.application.widgets.dbui.GeneralConnectionCreateUI;
import com.changhong.sqlstudio.core.common.DBType;
import com.changhong.sqlstudio.core.event.Event;
import com.changhong.sqlstudio.core.event.EventBus;
import com.changhong.sqlstudio.core.event.EventListener;
import com.changhong.sqlstudio.core.event.notify.RefreshConnectionListEvent;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.eclipse.swt.SWT.*;

/**
 * @author Luo Tiansheng
 * @since 2026-03-01
 */
@SuppressWarnings("FieldCanBeLocal")
public class AppNavigator extends EventListener
{

        private final Composite container;
        private Tree connectionTree;
        private TreeItem connections;

        private final Map<String, Connection> connectionItems
                = new LinkedHashMap<>();

        public AppNavigator(SashForm sashForm)
        {
                container = new Composite(sashForm, BORDER);

                container.setLayout(new FillLayout());
                CTabFolder tabFolder = new CTabFolder(container, HORIZONTAL);
                tabFolder.setSimple(false);

                createNavigatorTabItem(tabFolder);
                createProjectTabItem(tabFolder);

                EventBus.subscribe(OpenDBCreateUIEvent.class, this);
                EventBus.subscribe(ApplicationReadyEvent.class, this);
                EventBus.subscribe(RefreshConnectionListEvent.class, this);
        }

        @Override
        public void eventTigger(Event event)
        {
                if (event instanceof OpenDBCreateUIEvent openDBCreateUIEvent) {
                        DBType dbType = openDBCreateUIEvent.dbType();
                        new GeneralConnectionCreateUI(dbType, false).open();
                }

                if (event instanceof ApplicationReadyEvent || event instanceof RefreshConnectionListEvent) {
                        Map<String, ConnectionConfig> connectionList = Users.getConnectionList();
                        connectionList.forEach(this::addConnectionItem);
                }
        }

        private void addConnectionItem(String name, ConnectionConfig config)
        {
                if (connectionItems.containsKey(name))
                        return;

                TreeItem childItem = new TreeItem(connections, NONE);
                childItem.setText(name);
                connectionItems.put(name, new Connection(childItem, config));

                childItem.addListener(MouseDoubleClick, event -> {
                        TreeItem item = connectionTree.getItem(new Point(event.x, event.y));
                        System.out.println("双击事件，" + item.getText());
                });
        }

        private void createNavigatorTabItem(CTabFolder tabFolder)
        {
                CTabItem navigatorItem = new CTabItem(tabFolder, NONE);
                navigatorItem.setText("连接管理");

                connectionTree = new Tree(tabFolder, NONE);
                navigatorItem.setControl(connectionTree);

                connections = new TreeItem(connectionTree, NONE);
                connections.setText("我的连接");

                Menu menu = new Menu(connectionTree);
                connectionTree.setMenu(menu);

                /* 不允许其他子节点调用菜单 */
                connectionTree.addMenuDetectListener(event -> {
                        Point point = connectionTree.toControl(event.x, event.y);
                        TreeItem item = connectionTree.getItem(point);

                        if (item == connections) {
                                connectionTree.setMenu(menu);
                        } else {
                                connectionTree.setMenu(null);
                        }
                });

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
                                                public void widgetSelected(SelectionEvent e)
                                                {
                                                        EventBus.publish(new OpenDBCreateUIEvent(type, e));
                                                }
                                        });
                                        break;
                                case SQLite:
                                        break;
                        }

                }

                MenuItem closeAllConnectionItem = new MenuItem(menu, PUSH);
                closeAllConnectionItem.setText("关闭所有连接");

                new MenuItem(menu, SEPARATOR);

                MenuItem refreshConnectionsItem = new MenuItem(menu, PUSH);
                refreshConnectionsItem.setText("刷新连接");
                refreshConnectionsItem.addSelectionListener(new SelectionAdapter() {
                        @Override
                        public void widgetSelected(SelectionEvent e) {
                                EventBus.publish(new RefreshConnectionListEvent());
                        }
                });
        }

        private void createProjectTabItem(CTabFolder tabFolder)
        {
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
