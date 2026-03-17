package com.changhong.sqlstudio.app.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Listener;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 支持拖拽排序的 CTabFolder
 *
 * @author luotiansheng
 */
public class DragTabFolder {
    private final CTabFolder tabFolder;
    private final Map<CTabItem, Control> itemContentMap = new HashMap<>();

    /* 鼠标右键所在的标签 */
    private CTabItem contextMenuItem;

    /* 拖拽状态 */
    private CTabItem dragSourceItem;
    private boolean isDragging;
    private Point dragStartPoint;

    /* 插入位置 */
    private int insertIndex = -1;
    private boolean insertAfter;

    /* 拖拽阈值 */
    private static final int DRAG_THRESHOLD = 5;

    /* 插入框的尺寸 */
    private static final int INSERT_MARKER_WIDTH = 4;
    private static final int INSERT_MARKER_COLOR = SWT.COLOR_LIST_SELECTION;

    public DragTabFolder(Composite parent) {
        this.tabFolder = new CTabFolder(parent, SWT.BORDER | SWT.CLOSE);

        createContextMenu();
        configureTabFolder();
        setupDragListeners();
        setupPaintListener();
    }

    /**
     * 创建右键菜单
     */
    private void createContextMenu() {
        /* 右键菜单相关 */
        Menu contextMenu = new Menu(tabFolder);

        /* 关闭当前标签 */
        MenuItem closeCurrentItem = new MenuItem(contextMenu, SWT.PUSH);
        closeCurrentItem.setText("关闭当前标签");
        closeCurrentItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (contextMenuItem != null && !contextMenuItem.isDisposed())
                    closeTab(contextMenuItem);
            }
        });

        /* 关闭右侧所有标签 */
        MenuItem closeRightItem = new MenuItem(contextMenu, SWT.PUSH);
        closeRightItem.setText("关闭右侧标签");
        closeRightItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (contextMenuItem != null && !contextMenuItem.isDisposed())
                    closeTabsToRight(contextMenuItem);
            }
        });

        /* 分隔线 */
        new MenuItem(contextMenu, SWT.SEPARATOR);

        /* 关闭其他标签 */
        MenuItem closeOthersItem = new MenuItem(contextMenu, SWT.PUSH);
        closeOthersItem.setText("关闭其他标签");
        closeOthersItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (contextMenuItem != null && !contextMenuItem.isDisposed())
                    closeOtherTabs(contextMenuItem);
            }
        });

        /* 关闭所有标签 */
        MenuItem closeAllItem = new MenuItem(contextMenu, SWT.PUSH);
        closeAllItem.setText("关闭所有标签");
        closeAllItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                closeAllTabs();
            }
        });

        /* 菜单检测监听器 */
        tabFolder.addMenuDetectListener(new MenuDetectListener() {
            @Override
            public void menuDetected(MenuDetectEvent e) {
                Point point = tabFolder.toControl(e.x, e.y);
                CTabItem item = tabFolder.getItem(point);

                if (item == null) {
                    e.doit = false;
                    contextMenuItem = null;
                } else {
                    contextMenuItem = item;
                    e.doit = true;
                }
            }
        });

        tabFolder.setMenu(contextMenu);
    }

    /**
     * 关闭指定标签（触发事件）
     */
    @SuppressWarnings("ALL")
    public void closeTabItem(CTabItem item) {
        if (item == null || item.isDisposed())
            return;

        try {
            CTabFolder2Listener[] listeners = getCTabFolder2Listeners();

            Constructor<CTabFolderEvent> constructor =
                    CTabFolderEvent.class.getDeclaredConstructor(Widget.class);
            constructor.setAccessible(true);
            CTabFolderEvent event = constructor.newInstance(tabFolder);

            event.item = item;
            event.doit = true;

            for (CTabFolder2Listener listener : listeners) {
                listener.close(event);
                if (!event.doit)
                    return;
            }

            if (event.doit) {
                item.dispose();
                itemContentMap.remove(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private CTabFolder2Listener[] getCTabFolder2Listeners() {
        try {
            Field field = CTabFolder.class.getDeclaredField("folderListeners");
            field.setAccessible(true);

            CTabFolder2Listener[] listeners =
                    (CTabFolder2Listener[]) field.get(tabFolder);

            if (listeners == null)
                return new CTabFolder2Listener[0];

            return listeners;
        } catch (Exception e) {
            return new CTabFolder2Listener[0];
        }
    }

    /**
     * 关闭指定标签
     */
    private void closeTab(CTabItem item) {
        if (item == null || item.isDisposed())
            return;

        closeTabItem(item);
    }

    /**
     * 关闭指定标签右侧的所有标签
     */
    private void closeTabsToRight(CTabItem item) {
        if (item == null || item.isDisposed())
            return;

        int index = tabFolder.indexOf(item);
        CTabItem[] items = tabFolder.getItems();

        for (int i = items.length - 1; i > index; i--) {
            CTabItem rightItem = items[i];
            if (!rightItem.isDisposed()) {
                closeTabItem(rightItem);
            }
        }
    }

    /**
     * 关闭其他所有标签（除了指定的）
     */
    private void closeOtherTabs(CTabItem item) {
        if (item == null || item.isDisposed())
            return;

        CTabItem[] items = tabFolder.getItems();
        for (CTabItem otherItem : items) {
            if (otherItem != item && !otherItem.isDisposed())
                closeTabItem(otherItem);
        }
    }

    /**
     * 关闭所有标签
     */
    private void closeAllTabs() {
        CTabItem[] items = tabFolder.getItems();
        for (CTabItem item : items) {
            if (!item.isDisposed())
                closeTabItem(item);
        }
    }

    /**
     * 配置标签页外观
     */
    private void configureTabFolder() {
        tabFolder.setBorderVisible(true);
        tabFolder.setTabHeight(25);
        tabFolder.setMinimumCharacters(15);
        tabFolder.setUnselectedCloseVisible(true);
    }

    /**
     * 绘制插入标记
     */
    private void setupPaintListener() {
        tabFolder.addPaintListener(new PaintListener() {
            @Override
            public void paintControl(PaintEvent e) {
                if (!isDragging || insertIndex < 0)
                    return;

                int itemCount = tabFolder.getItemCount();
                if (itemCount == 0)
                    return;

                Rectangle markerRect = calculateInsertMarkerRect();
                if (markerRect == null)
                    return;

                GC gc = e.gc;
                gc.setAdvanced(true);
                gc.setAlpha(180);

                Color markerColor = Display.getCurrent()
                        .getSystemColor(INSERT_MARKER_COLOR);
                gc.setBackground(markerColor);
                gc.fillRectangle(markerRect);

                gc.setAlpha(255);
                gc.setForeground(Display.getCurrent()
                        .getSystemColor(SWT.COLOR_WIDGET_DARK_SHADOW));
                gc.drawRectangle(markerRect);
            }
        });
    }

    /**
     * 计算插入标记位置
     */
    private Rectangle calculateInsertMarkerRect() {
        int itemCount = tabFolder.getItemCount();
        if (itemCount == 0)
            return null;

        int targetIndex = insertIndex;
        if (targetIndex > itemCount)
            targetIndex = itemCount;
        if (targetIndex < 0)
            targetIndex = 0;

        Rectangle bounds;
        if (targetIndex >= itemCount) {
            CTabItem lastItem = tabFolder.getItem(itemCount - 1);
            bounds = lastItem.getBounds();
            return new Rectangle(bounds.x + bounds.width + 2, bounds.y,
                    INSERT_MARKER_WIDTH, bounds.height);
        } else {
            CTabItem targetItem = tabFolder.getItem(targetIndex);
            bounds = targetItem.getBounds();

            if (insertAfter) {
                return new Rectangle(bounds.x + bounds.width + 2, bounds.y,
                        INSERT_MARKER_WIDTH, bounds.height);
            } else {
                return new Rectangle(bounds.x - INSERT_MARKER_WIDTH - 2,
                        bounds.y, INSERT_MARKER_WIDTH, bounds.height);
            }
        }
    }

    /**
     * 设置拖拽监听器
     */
    private void setupDragListeners() {
        tabFolder.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(MouseEvent e) {
                if (e.button != 1)
                    return;

                Point pt = new Point(e.x, e.y);
                dragSourceItem = tabFolder.getItem(pt);

                if (dragSourceItem != null) {
                    dragStartPoint = pt;
                    isDragging = false;
                    insertIndex = -1;
                }
            }

            @Override
            public void mouseUp(MouseEvent e) {
                if (isDragging && insertIndex >= 0)
                    performMove();

                isDragging = false;
                dragSourceItem = null;
                dragStartPoint = null;
                insertIndex = -1;
                tabFolder.setCursor(null);
                tabFolder.redraw();
            }
        });

        tabFolder.addMouseMoveListener(new MouseMoveListener() {
            @Override
            public void mouseMove(MouseEvent e) {
                if (dragSourceItem == null || dragSourceItem.isDisposed())
                    return;

                Point currentPoint = new Point(e.x, e.y);

                if (!isDragging) {
                    if (dragStartPoint == null)
                        return;

                    int distance = Math.abs(currentPoint.x - dragStartPoint.x)
                            + Math.abs(currentPoint.y - dragStartPoint.y);

                    if (distance > DRAG_THRESHOLD) {
                        isDragging = true;
                        tabFolder.setCursor(Display.getCurrent()
                                .getSystemCursor(SWT.CURSOR_SIZEALL));
                    } else {
                        return;
                    }
                }

                updateInsertPosition(currentPoint);
            }
        });
    }

    /**
     * 更新插入位置
     */
    private void updateInsertPosition(Point mousePos) {
        int oldInsertIndex = insertIndex;
        boolean oldInsertAfter = insertAfter;

        CTabItem targetItem = tabFolder.getItem(mousePos);
        int itemCount = tabFolder.getItemCount();

        if (targetItem == null) {
            if (itemCount > 0) {
                CTabItem firstItem = tabFolder.getItem(0);
                CTabItem lastItem = tabFolder.getItem(itemCount - 1);

                if (mousePos.x < firstItem.getBounds().x) {
                    insertIndex = 0;
                    insertAfter = false;
                } else if (mousePos.x > lastItem.getBounds().x
                        + lastItem.getBounds().width) {
                    insertIndex = itemCount;
                    insertAfter = true;
                } else {
                    insertIndex = -1;
                }
            } else {
                insertIndex = -1;
            }
        } else {
            if (targetItem == dragSourceItem) {
                insertIndex = -1;
            } else {
                Rectangle bounds = targetItem.getBounds();
                insertAfter = mousePos.x > (bounds.x + bounds.width / 2);
                insertIndex = tabFolder.indexOf(targetItem);
            }
        }

        if (oldInsertIndex != insertIndex || oldInsertAfter != insertAfter)
            tabFolder.redraw();
    }

    /**
     * 执行移动操作
     */
    private void performMove() {
        if (dragSourceItem == null || dragSourceItem.isDisposed())
            return;
        if (insertIndex < 0)
            return;

        int sourceIndex = tabFolder.indexOf(dragSourceItem);
        int targetIndex = insertIndex;

        if (insertAfter && targetIndex < tabFolder.getItemCount())
            targetIndex++;

        if (sourceIndex < targetIndex)
            targetIndex--;

        if (sourceIndex == targetIndex)
            return;

        moveItem(sourceIndex, targetIndex);
    }

    /**
     * 移动标签页
     */
    private void moveItem(int fromIndex, int toIndex) {
        CTabItem fromItem = tabFolder.getItem(fromIndex);
        String text = fromItem.getText();
        String toolTip = fromItem.getToolTipText();
        Image image = fromItem.getImage();
        Object data = fromItem.getData();
        Control control = itemContentMap.get(fromItem);
        boolean isSelected = (tabFolder.getSelection() == fromItem);

        if (control == null || control.isDisposed())
            control = fromItem.getControl();

        itemContentMap.remove(fromItem);
        fromItem.setControl(null);
        fromItem.dispose();

        CTabItem newItem = new CTabItem(tabFolder, SWT.CLOSE, toIndex);
        newItem.setText(text);
        if (toolTip != null)
            newItem.setToolTipText(toolTip);
        if (image != null)
            newItem.setImage(image);
        if (data != null)
            newItem.setData(data);
        newItem.setControl(control);
        itemContentMap.put(newItem, control);

        if (isSelected)
            tabFolder.setSelection(newItem);

        tabFolder.redraw();
    }

    /**
     * 添加新标签页
     */
    @SuppressWarnings("UnusedReturnValue")
    public CTabItem addTab(String title, Control content) {
        CTabItem item = new CTabItem(tabFolder, SWT.CLOSE);
        item.setText(title);
        item.setControl(content);
        itemContentMap.put(item, content);
        tabFolder.setSelection(item);
        return item;
    }

    /**
     * 获取当前选中的标签页
     */
    public CTabItem getSelection() {
        return tabFolder.getSelection();
    }

    /**
     * 获取底层的 CTabFolder
     */
    public CTabFolder getTabFolder() {
        return tabFolder;
    }

    public void addCTabFolder2Listener(CTabFolder2Listener listener) {
        tabFolder.addCTabFolder2Listener(listener);
    }

}