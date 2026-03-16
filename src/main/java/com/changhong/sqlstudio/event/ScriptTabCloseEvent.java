package com.changhong.sqlstudio.event;

import org.eclipse.swt.custom.CTabItem;

/**
 * @author luotiansheng
 */
public class ScriptTabCloseEvent implements Event {

    private final CTabItem cTabItem;

    public boolean doit = true;

    public ScriptTabCloseEvent(CTabItem cTabItem) {
        this.cTabItem = cTabItem;
    }

    public CTabItem getCTabItem() {
        return cTabItem;
    }
}
