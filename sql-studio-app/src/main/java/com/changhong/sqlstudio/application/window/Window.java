package com.changhong.sqlstudio.application.window;

import com.changhong.sqlstudio.application.ui.AppMenuBar;
import com.changhong.sqlstudio.application.ui.AppNavigator;
import com.changhong.sqlstudio.application.ui.AppToolBar;
import com.changhong.sqlstudio.application.ui.AppWorkbench;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Luo Tiansheng
 * @since 2026-03-01
 */
@SuppressWarnings({
        "FieldCanBeLocal",
        "unused"
})
public class Window
{

        private static Window sWindow = null;
        private final Display display;
        private Shell shell;
        private SashForm sashForm;
        private AppMenuBar menuBar;
        private AppToolBar toolBar;
        private AppNavigator navigator;
        private AppWorkbench workbench;

        private Window(Display display)
        {
                this.display = display;
        }

        public static Window initialize(Display display)
        {
                if (sWindow != null)
                        return sWindow;

                sWindow = new Window(display);

                return sWindow;
        }

        public static synchronized Shell shell()
        {
                return sWindow.shell;
        }

        public void open()
        {
                shell = new Shell(display);

                GridLayout gridLayout = new GridLayout();
                gridLayout.marginWidth = 0;
                gridLayout.marginHeight = 0;
                gridLayout.verticalSpacing = 0;

                shell.setLayout(gridLayout);
                shell.setSize(1200, 800);

                menuBar = new AppMenuBar(shell);
                toolBar = new AppToolBar(shell);

                sashForm = new SashForm(shell, SWT.HORIZONTAL | SWT.BORDER);
                sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

                navigator = new AppNavigator(sashForm);
                workbench = new AppWorkbench(shell, sashForm);

                sashForm.setWeights(20, 80);

                shell.open();
        }

}
