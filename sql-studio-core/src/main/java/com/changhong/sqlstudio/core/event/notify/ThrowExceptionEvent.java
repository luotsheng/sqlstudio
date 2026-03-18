package com.changhong.sqlstudio.core.event.notify;


import com.changhong.sqlstudio.core.event.Event;

/**
 * 异常事件
 *
 * @author Luo Tiansheng
 * @since 2026/3/18
 */
public class ThrowExceptionEvent implements Event {

    private final Throwable e;

    public ThrowExceptionEvent(Throwable e) {
        this.e = e;
    }

    public Throwable getThrowable() {
        return e;
    }

    public String getMessage() {
        return e.getMessage();
    }

}
