package com.changhong.sqlstudio.application.ui;


import com.changhong.sqlstudio.application.widgets.Widgets;
import com.changhong.sqlstudio.core.event.Event;
import com.changhong.sqlstudio.core.event.EventBus;
import com.changhong.sqlstudio.core.event.EventListener;
import com.changhong.sqlstudio.core.event.notify.ThrowExceptionEvent;

/**
 * @author Luo Tiansheng
 * @since 2026/3/18
 */
public class AppThrowable extends EventListener {

    public AppThrowable() {
        /* do nothing... */
    }

    public void subscribe() {
        EventBus.subscribe(ThrowExceptionEvent.class, this);
    }

    @Override
    public void eventTigger(Event event) {
        if (event instanceof ThrowExceptionEvent throwEvent) {
            Widgets.showErrorDialog(throwEvent.getMessage());
        }
    }

}
