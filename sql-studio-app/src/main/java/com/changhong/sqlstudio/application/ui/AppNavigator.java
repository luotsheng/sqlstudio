package com.changhong.sqlstudio.application.ui;

import com.changhong.sqlstudio.application.Images;
import com.changhong.sqlstudio.application.Users;
import com.changhong.sqlstudio.application.config.ConnectionConfig;
import com.changhong.sqlstudio.application.treenode.DBConnection;
import com.changhong.sqlstudio.application.treenode.DBDatabase;
import com.changhong.sqlstudio.application.treenode.DBTable;
import com.changhong.sqlstudio.application.widgets.Widgets;
import com.changhong.sqlstudio.application.widgets.dbui.GeneralConnectionCreateUI;
import com.changhong.sqlstudio.core.common.DBType;
import com.changhong.sqlstudio.core.event.Event;
import com.changhong.sqlstudio.core.event.EventBus;
import com.changhong.sqlstudio.core.event.EventListener;
import com.changhong.sqlstudio.core.event.notify.ApplicationReadyEvent;
import com.changhong.sqlstudio.core.event.notify.OpenDBCreateUIEvent;
import com.changhong.sqlstudio.core.event.notify.RefreshConnectionListEvent;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;

import java.util.LinkedHashMap;
import java.util.List;
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
        private final Map<String, DBConnection> connectionItems
                = new LinkedHashMap<>();
        private Tree tree;
        private TreeItem connectionListChild;

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
                connectionItems.put(name, new DBConnection(name, connectionListChild, config));
        }

        private void doubleClickEvent(TreeItem item)
        {
                if (item == null)
                        return;

                if (item.getData() instanceof DBConnection) {
                        DBConnection DBConnection = connectionItems.get(item.getText());

                        if (DBConnection == null)
                                return;

                        DBConnection.open();
                }

                if (item.getData() instanceof DBDatabase db)
                        db.open();

                if (item.getData() instanceof DBTable table)
                        table.openDataTabelTab();
        }

        private void createNavigatorTabItem(CTabFolder tabFolder)
        {
                CTabItem navigatorItem = new CTabItem(tabFolder, NONE);
                navigatorItem.setText("连接管理");

                tree = new Tree(tabFolder, NONE);
                navigatorItem.setControl(tree);

                connectionListChild = new TreeItem(tree, NONE);
                connectionListChild.setText("我的连接");
                connectionListChild.setImage(Images.CHAIN);

                Menu menu = new Menu(tree);
                tree.setMenu(menu);

                /* 不允许其他子节点调用菜单 */
                tree.addMenuDetectListener(event -> {
                        Point point = tree.toControl(event.x, event.y);
                        TreeItem item = tree.getItem(point);

                        if (item == null)
                                return;

                        if (item == connectionListChild) {
                                tree.setMenu(menu);
                        } else if (item.getData() instanceof DBConnection conn) {
                                tree.setMenu(conn.getMenu());
                        } else {
                                tree.setMenu(null);
                        }
                });

                tree.addListener(MouseDoubleClick, event -> {
                        TreeItem item = tree.getItem(new Point(event.x, event.y));

                        if (item == null || item == connectionListChild)
                                return;

                        doubleClickEvent(item);
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
                                        newDBConnectionItem.addSelectionListener(new SelectionAdapter()
                                        {
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
                closeAllConnectionItem.addSelectionListener(new SelectionAdapter()
                {
                        @Override
                        public void widgetSelected(SelectionEvent e)
                        {
                                List<DBConnection> values = connectionItems.values().stream()
                                        .filter(DBConnection::isOpen)
                                        .toList();

                                if (values.isEmpty())
                                        return;

                                int r = Widgets.showQuestionDialog("是否关闭所有连接？");
                                if (r == YES)
                                        connectionItems.values().forEach(DBConnection::close);
                        }
                });

                new MenuItem(menu, SEPARATOR);

                MenuItem refreshConnectionsItem = new MenuItem(menu, PUSH);
                refreshConnectionsItem.setText("刷新连接");
                refreshConnectionsItem.addSelectionListener(new SelectionAdapter()
                {
                        @Override
                        public void widgetSelected(SelectionEvent e)
                        {
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
