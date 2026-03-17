package com.changhong.sqlstudio.app.ui;

import com.changhong.sqlstudio.app.event.NewQueryScriptEvent;
import com.changhong.sqlstudio.app.event.ScriptTabCloseEvent;
import com.changhong.sqlstudio.app.widgets.DragTabFolder;
import com.changhong.sqlstudio.app.widgets.StyledTextEditor;
import com.changhong.sqlstudio.app.widgets.Widgets;
import com.changhong.sqlstudio.core.event.Event;
import com.changhong.sqlstudio.core.event.EventBus;
import com.changhong.sqlstudio.core.event.EventListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder2Adapter;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import static org.eclipse.swt.SWT.BORDER;

/**
 * @author Luo Tiansheng
 * @since 2026-03-01
 */
@SuppressWarnings("FieldCanBeLocal")
public class AppQueryEditor extends EventListener {

    private int count = 1;

    private final Shell shell;
    private final Composite container;
    private final DragTabFolder tabFolder;

    public AppQueryEditor(Shell shell, SashForm sashForm) {
        this.shell = shell;
        container = new Composite(sashForm, BORDER);
        container.setLayout(new FillLayout());

        tabFolder = new DragTabFolder(container);

        tabFolder.addCTabFolder2Listener(new CTabFolder2Adapter() {
            @Override
            public void close(CTabFolderEvent event) {
                EventBus.publish(new ScriptTabCloseEvent(event));
            }
        });

        EventBus.subscribe(NewQueryScriptEvent.class, this);
        EventBus.subscribe(ScriptTabCloseEvent.class, this);
    }

    @Override
    public void eventTigger(Event event) {
        if (event instanceof ScriptTabCloseEvent closeEvent) {
            CTabItem tabItem = closeEvent.getCTabItem();
            StyledTextEditor codeEditor = (StyledTextEditor) tabItem.getControl();

            if (codeEditor.isDirty()) {
                String tips = "文件 \"" + tabItem.getText() + "\" 已修改，是否保存？";
                switch (Widgets.showDialog(shell, tips)) {
                    case SWT.CANCEL -> closeEvent.setDoit(false);
                    case SWT.NO -> closeEvent.setDoit(true);
                }
            }

            return;
        }

        if (event instanceof NewQueryScriptEvent)
            newQueryScriptTab();
    }

    public void newQueryScriptTab() {
        StyledTextEditor codeEditor = new StyledTextEditor(tabFolder.getTabFolder());
        CTabItem cTabItem = tabFolder.addTab("新建查询" + "_" + (count++) + ".sql", codeEditor);
        codeEditor.setTabItem(cTabItem);
    }

}
