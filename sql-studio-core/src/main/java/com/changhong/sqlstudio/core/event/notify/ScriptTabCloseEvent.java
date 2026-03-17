package com.changhong.sqlstudio.core.event.notify;

import com.changhong.sqlstudio.core.event.Event;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;

/**
 * 脚本标签关闭事件
 *
 * @author Luo Tiansheng
 * @since 2026-03-01
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
