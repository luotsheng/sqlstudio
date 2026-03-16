package com.changhong.sqlstudio.ui;

import com.changhong.sqlstudio.event.Event;
import com.changhong.sqlstudio.event.EventBus;
import com.changhong.sqlstudio.event.EventListener;
import com.changhong.sqlstudio.event.NewQueryEvent;
import com.changhong.sqlstudio.ui.area.Navigator;
import com.changhong.sqlstudio.ui.area.QueryEditor;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.*;

import static org.eclipse.swt.SWT.*;

/**
 * @author luotiansheng
 */
@SuppressWarnings({
        "FieldCanBeLocal",
        "unused"
})
public class MainWorkbench extends EventListener {

    private static final int[] RATIO = new int[] { 15, 85 };

    private final Shell shell;

    private Navigator navigator;
    private QueryEditor queryEditor;

    public MainWorkbench(Shell shell) {
        this.shell = shell;
        createContents();
        EventBus.subscribe(NewQueryEvent.class, this);
    }

    @Override
    public void eventTigger(Event event) {
        queryEditor.newQueryScriptTab();
    }

    private void createContents() {
        SashForm sashForm = new SashForm(shell, HORIZONTAL | BORDER);
        sashForm.setLayoutData(new GridData(FILL, FILL, true, true));

        navigator = new Navigator(sashForm);
        queryEditor = new QueryEditor(shell, sashForm);

        sashForm.setWeights(RATIO);
    }

}
