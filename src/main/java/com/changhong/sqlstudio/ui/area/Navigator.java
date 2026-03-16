package com.changhong.sqlstudio.ui.area;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import static org.eclipse.swt.SWT.*;

/**
 * @author luotiansheng
 */
@SuppressWarnings("FieldCanBeLocal")
public class Navigator {

    private final Composite container;

    public Navigator(SashForm sashForm) {
        container = new Composite(sashForm, BORDER);

        container.setLayout(new FillLayout());
        CTabFolder tabFolder = new CTabFolder(container, HORIZONTAL);
        tabFolder.setSimple(false);

        createNavigatorTabItem(tabFolder);
        createProjectTabItem(tabFolder);
    }

    private void createNavigatorTabItem(CTabFolder tabFolder) {
        CTabItem navigatorItem = new CTabItem(tabFolder, NONE);
        navigatorItem.setText("连接管理");

        Tree projectTree = new Tree(tabFolder, NONE);
        navigatorItem.setControl(projectTree);

        TreeItem projectTreeRoot = new TreeItem(projectTree, NONE);
        projectTreeRoot.setText("根目录");

        TreeItem projectTreeSample = new TreeItem(projectTreeRoot, NONE);
        projectTreeSample.setText("连接样例");
    }

    private void createProjectTabItem(CTabFolder tabFolder) {
        CTabItem projectItem = new CTabItem(tabFolder, NONE);
        projectItem.setText("项目管理");

        Tree projectTree = new Tree(tabFolder, NONE);
        projectItem.setControl(projectTree);

        TreeItem projectTreeRoot = new TreeItem(projectTree, NONE);
        projectTreeRoot.setText("根项目");

        TreeItem projectTreeSample = new TreeItem(projectTreeRoot, NONE);
        projectTreeSample.setText("项目样例");
    }

}
