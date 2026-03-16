package com.changhong.sqlstudio.event;

import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;

/**
 * @author luotiansheng
 */
public class ScriptTabCloseEvent implements Event {

    private final CTabFolderEvent event;

    public ScriptTabCloseEvent(CTabFolderEvent event) {
        this.event = event;
    }

    public CTabItem getCTabItem() {
        return (CTabItem) event.item;
    }

    public void setDoit(boolean value) {
        event.doit = value;
    }

}
