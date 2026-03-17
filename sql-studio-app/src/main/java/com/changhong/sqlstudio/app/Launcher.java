package com.changhong.sqlstudio.app;

import com.changhong.sqlstudio.app.window.Window;
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
        while (!display.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
    }

}
