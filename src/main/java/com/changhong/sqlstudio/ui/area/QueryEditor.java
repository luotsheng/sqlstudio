package com.changhong.sqlstudio.ui.area;

import com.changhong.sqlstudio.event.Event;
import com.changhong.sqlstudio.event.EventBus;
import com.changhong.sqlstudio.event.EventListener;
import com.changhong.sqlstudio.event.ScriptTabCloseEvent;
import com.changhong.sqlstudio.widgets.CodeEditor;
import com.changhong.sqlstudio.widgets.DragTabFolder;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder2Adapter;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import static org.eclipse.swt.SWT.BORDER;

/**
 * @author luotiansheng
 */
@SuppressWarnings("FieldCanBeLocal")
public class QueryEditor extends EventListener {

    private int count = 1;

    private final Shell shell;
    private final Composite container;
    private final DragTabFolder tabFolder;

    public QueryEditor(Shell shell, SashForm sashForm) {
        this.shell = shell;
        container = new Composite(sashForm, BORDER);
        container.setLayout(new FillLayout());

        tabFolder = new DragTabFolder(container);

        tabFolder.getTabFolder().addCTabFolder2Listener(new CTabFolder2Adapter() {
            @Override
            public void close(CTabFolderEvent event) {
                EventBus.publish(new ScriptTabCloseEvent(event));
            }
        });

        EventBus.subscribe(ScriptTabCloseEvent.class, this);
    }

    @Override
    public void eventTigger(Event event) {
        ScriptTabCloseEvent closeEvent = (ScriptTabCloseEvent) event;
        CTabItem tabItem = closeEvent.getCTabItem();
        CodeEditor codeEditor = (CodeEditor) tabItem.getControl();

        if (codeEditor.isDirty()) {
            switch (showSaveDialog(tabItem.getText())) {
                case SWT.CANCEL -> closeEvent.setDoit(false);
                case SWT.NO -> closeEvent.setDoit(true);
            }
        }

    }

    /**
     * 显示保存确认对话框
     */
    private int showSaveDialog(String tabName) {
        MessageBox dialog = new MessageBox(shell,
                SWT.YES | SWT.NO | SWT.CANCEL | SWT.ICON_QUESTION);

        dialog.setText("数据库管理工具");
        dialog.setMessage("文件 \"" + tabName + "\" 已修改，是否保存？");

        return dialog.open();
    }

    public void newQueryScriptTab() {
        CodeEditor codeEditor = new CodeEditor(tabFolder.getTabFolder());
        CTabItem cTabItem = tabFolder.addTab("新建查询" + "_" + (count++) + ".sql", codeEditor);
        codeEditor.setTabItem(cTabItem);
    }

}
