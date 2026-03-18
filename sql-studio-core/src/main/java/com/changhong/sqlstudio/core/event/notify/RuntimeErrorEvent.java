package com.changhong.sqlstudio.core.event.notify;


import com.changhong.sqlstudio.core.common.Cores;
import com.changhong.sqlstudio.core.event.Event;

/**
 * 异常事件
 *
 * @author Luo Tiansheng
 * @since 2026/3/18
 */
public class RuntimeErrorEvent implements Event {

    private final String title;
    private final Throwable e;

    public RuntimeErrorEvent(Throwable e) {
        this(Cores.SQL_STUDIO_TITLE, e);
    }

    public RuntimeErrorEvent(String title, Throwable e) {
        this.title = title;
        this.e = e;
    }

    public String getTitle() {
        return title;
    }

    public Throwable getThrowable() {
        return e;
    }

    public String getMessage() {
        return e.getMessage();
    }

}
