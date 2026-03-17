package com.changhong.sqlstudio.application;

import com.changhong.sqlstudio.core.event.notify.ApplicationReadyEvent;
import com.changhong.sqlstudio.application.window.Window;
import com.changhong.sqlstudio.core.event.EventBus;
import org.eclipse.swt.widgets.Display;

/**
 * @author Luo Tiansheng
 * @since 2026-03-01
 */
public class Launcher  {

    public static final Display display = new Display();

    public static void main(String[] args)
    {
        try {

            Window window = new Window(display);
            window.open();

            eventLoop(display);
        } finally {
            display.dispose();
        }
    }

    public  static void eventLoop(Display display)
    {
        boolean isPublish = false;

        while (!display.isDisposed()) {
            if (!display.readAndDispatch()) {

                if (!isPublish) {
                    EventBus.publish(new ApplicationReadyEvent());
                    isPublish = true;
                }

                display.sleep();
            }
        }
    }

}
