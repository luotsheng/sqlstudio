package com.changhong.sqlstudio.application.ui;

import com.changhong.sqlstudio.application.node.DBNTable;
import com.changhong.sqlstudio.application.widgets.Widgets;
import com.changhong.sqlstudio.core.event.Event;
import com.changhong.sqlstudio.core.event.EventBus;
import com.changhong.sqlstudio.core.event.EventListener;
import com.changhong.sqlstudio.core.event.notify.*;
import com.changhong.sqlstudio.driver.QueryResultSet;
import com.changhong.swt.widgets.CodeStyledText;
import com.changhong.swt.widgets.VTabFolder;
import com.changhong.swt.widgets.SqlGrid;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder2Adapter;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import java.sql.SQLException;

import static org.eclipse.swt.SWT.BORDER;

/**
 * @author Luo Tiansheng
 * @since 2026-03-01
 */
@SuppressWarnings("FieldCanBeLocal")
public class AppWorkbench extends EventListener
{

        private final Shell shell;
        private final Composite container;
        private final VTabFolder tabFolder;
        private int count = 1;

        public AppWorkbench(Shell shell, SashForm sashForm)
        {
                this.shell = shell;
                container = new Composite(sashForm, BORDER);
                container.setLayout(new FillLayout());

                tabFolder = new VTabFolder(container);
                tabFolder.setSimple(true);

                tabFolder.addCTabFolder2Listener(new CTabFolder2Adapter()
                {
                        @Override
                        public void close(CTabFolderEvent event)
                        {
                                EventBus.publish(new ScriptTabCloseEvent(event));
                        }
                });

                EventBus.subscribe(OpenNewQueryScriptEvent.class, this);
                EventBus.subscribe(OpenDataTableTabEvent.class, this);
                EventBus.subscribe(ScriptTabCloseEvent.class, this);
                EventBus.subscribe(ApplicationReadyEvent.class, this);
        }

        @Override
        public void eventTigger(Event event)
        {
                if (event instanceof ScriptTabCloseEvent closeEvent) {
                        CTabItem tabItem = closeEvent.getCTabItem();

                        if (tabItem.getControl() instanceof CodeStyledText editor) {
                                if (editor.isDirty()) {
                                        String tips = "文件 \"" + tabItem.getText() + "\" 已修改，是否保存？";
                                        switch (Widgets.showSaveDialog(tips)) {
                                                case SWT.CANCEL -> closeEvent.setDoit(false);
                                                case SWT.NO -> closeEvent.setDoit(true);
                                        }
                                }
                        }

                        return;
                }

                if (event instanceof OpenNewQueryScriptEvent)
                        newQueryScriptTab();

                if (event instanceof OpenDataTableTabEvent openDataTableTabEvent)
                        newDataTableTab(openDataTableTabEvent);
        }

        public void newQueryScriptTab()
        {
                CodeStyledText codeEditor = new CodeStyledText(tabFolder);
                CTabItem cTabItem = tabFolder.addTab("新建查询" + "_" + (count++) + ".sql", codeEditor);
                codeEditor.setTabItem(cTabItem);
                cTabItem.addDisposeListener(disposeEvent -> {
                        if (!codeEditor.isDisposed())
                                codeEditor.dispose();
                });
        }

        public void newDataTableTab(OpenDataTableTabEvent event)
        {
                DBNTable table = event.table();
                SqlGrid grid = new SqlGrid(tabFolder);
                CTabItem cTabItem = tabFolder.addTab(table.name(), grid);
                cTabItem.addDisposeListener(disposeEvent -> {
                        if (!grid.isDisposed())
                                grid.dispose();
                        table.setOpenTabItem(null);
                });

                try {
                        QueryResultSet rs = table.selectByPage(0, 50);
                        grid.drawData(rs.getColumns(), rs.getRows());
                } catch (SQLException e) {
                        EventBus.publish(new RuntimeErrorEvent(e));
                }

                table.setOpenTabItem(cTabItem);
        }

}
