package com.changhong.sqlstudio.application.widgets;

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
 * @author Luo Tiansheng
 */
public class DragTabFolder extends CTabFolder
{
        /* 拖拽阈值 */
        private static final int DRAG_THRESHOLD = 5;
        /* 插入框的尺寸 */
        private static final int INSERT_MARKER_WIDTH = 4;
        private static final int INSERT_MARKER_COLOR = SWT.COLOR_LIST_SELECTION;
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

        public DragTabFolder(Composite parent)
        {
                super(parent, SWT.CLOSE);

                createContextMenu();
                configureTabFolder();
                setupDragListeners();
                setupPaintListener();
        }

        /**
         * 创建右键菜单
         */
        private void createContextMenu()
        {
                /* 右键菜单相关 */
                Menu contextMenu = new Menu(this);

                /* 关闭当前标签 */
                MenuItem closeCurrentItem = new MenuItem(contextMenu, SWT.PUSH);
                closeCurrentItem.setText("关闭当前标签");
                closeCurrentItem.addSelectionListener(new SelectionAdapter()
                {
                        @Override
                        public void widgetSelected(SelectionEvent e)
                        {
                                if (contextMenuItem != null && !contextMenuItem.isDisposed())
                                        closeTab(contextMenuItem);
                        }
                });

                /* 关闭右侧所有标签 */
                MenuItem closeRightItem = new MenuItem(contextMenu, SWT.PUSH);
                closeRightItem.setText("关闭右侧标签");
                closeRightItem.addSelectionListener(new SelectionAdapter()
                {
                        @Override
                        public void widgetSelected(SelectionEvent e)
                        {
                                if (contextMenuItem != null && !contextMenuItem.isDisposed())
                                        closeTabsToRight(contextMenuItem);
                        }
                });

                /* 分隔线 */
                new MenuItem(contextMenu, SWT.SEPARATOR);

                /* 关闭其他标签 */
                MenuItem closeOthersItem = new MenuItem(contextMenu, SWT.PUSH);
                closeOthersItem.setText("关闭其他标签");
                closeOthersItem.addSelectionListener(new SelectionAdapter()
                {
                        @Override
                        public void widgetSelected(SelectionEvent e)
                        {
                                if (contextMenuItem != null && !contextMenuItem.isDisposed())
                                        closeOtherTabs(contextMenuItem);
                        }
                });

                /* 关闭所有标签 */
                MenuItem closeAllItem = new MenuItem(contextMenu, SWT.PUSH);
                closeAllItem.setText("关闭所有标签");
                closeAllItem.addSelectionListener(new SelectionAdapter()
                {
                        @Override
                        public void widgetSelected(SelectionEvent e)
                        {
                                closeAllTabs();
                        }
                });

                /* 菜单检测监听器 */
                this.addMenuDetectListener(new MenuDetectListener()
                {
                        @Override
                        public void menuDetected(MenuDetectEvent e)
                        {
                                Point point = toControl(e.x, e.y);
                                CTabItem item = getItem(point);

                                if (item == null) {
                                        e.doit = false;
                                        contextMenuItem = null;
                                } else {
                                        contextMenuItem = item;
                                        e.doit = true;
                                }
                        }
                });

                this.setMenu(contextMenu);
        }

        /**
         * 关闭指定标签（触发事件）
         */
        @SuppressWarnings("ALL")
        public void closeTabItem(CTabItem item)
        {
                if (item == null || item.isDisposed())
                        return;

                try {
                        CTabFolder2Listener[] listeners = getCTabFolder2Listeners();

                        Constructor<CTabFolderEvent> constructor =
                                CTabFolderEvent.class.getDeclaredConstructor(Widget.class);
                        constructor.setAccessible(true);
                        CTabFolderEvent event = constructor.newInstance(this);

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

        private CTabFolder2Listener[] getCTabFolder2Listeners()
        {
                try {
                        Field field = CTabFolder.class.getDeclaredField("folderListeners");
                        field.setAccessible(true);

                        CTabFolder2Listener[] listeners =
                                (CTabFolder2Listener[]) field.get(this);

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
        private void closeTab(CTabItem item)
        {
                if (item == null || item.isDisposed())
                        return;

                closeTabItem(item);
        }

        /**
         * 关闭指定标签右侧的所有标签
         */
        private void closeTabsToRight(CTabItem item)
        {
                if (item == null || item.isDisposed())
                        return;

                int index = this.indexOf(item);
                CTabItem[] items = this.getItems();

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
        private void closeOtherTabs(CTabItem item)
        {
                if (item == null || item.isDisposed())
                        return;

                CTabItem[] items = this.getItems();
                for (CTabItem otherItem : items) {
                        if (otherItem != item && !otherItem.isDisposed())
                                closeTabItem(otherItem);
                }
        }

        /**
         * 关闭所有标签
         */
        private void closeAllTabs()
        {
                CTabItem[] items = this.getItems();
                for (CTabItem item : items) {
                        if (!item.isDisposed())
                                closeTabItem(item);
                }
        }

        /**
         * 配置标签页外观
         */
        private void configureTabFolder()
        {
                this.setBorderVisible(true);
                this.setTabHeight(25);
                this.setMinimumCharacters(15);
                this.setUnselectedCloseVisible(true);
        }

        /**
         * 绘制插入标记
         */
        private void setupPaintListener()
        {
                this.addPaintListener(new PaintListener()
                {
                        @Override
                        public void paintControl(PaintEvent e)
                        {
                                if (!isDragging || insertIndex < 0)
                                        return;

                                int itemCount = getItemCount();
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
        private Rectangle calculateInsertMarkerRect()
        {
                int itemCount = this.getItemCount();
                if (itemCount == 0)
                        return null;

                int targetIndex = insertIndex;
                if (targetIndex > itemCount)
                        targetIndex = itemCount;
                if (targetIndex < 0)
                        targetIndex = 0;

                Rectangle bounds;
                if (targetIndex >= itemCount) {
                        CTabItem lastItem = this.getItem(itemCount - 1);
                        bounds = lastItem.getBounds();
                        return new Rectangle(bounds.x + bounds.width + 2, bounds.y,
                                INSERT_MARKER_WIDTH, bounds.height);
                } else {
                        CTabItem targetItem = this.getItem(targetIndex);
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
        private void setupDragListeners()
        {
                this.addMouseListener(new MouseAdapter()
                {
                        @Override
                        public void mouseDown(MouseEvent e)
                        {
                                if (e.button != 1)
                                        return;

                                Point pt = new Point(e.x, e.y);
                                dragSourceItem = getItem(pt);

                                if (dragSourceItem != null) {
                                        dragStartPoint = pt;
                                        isDragging = false;
                                        insertIndex = -1;
                                }
                        }

                        @Override
                        public void mouseUp(MouseEvent e)
                        {
                                if (isDragging && insertIndex >= 0)
                                        performMove();

                                isDragging = false;
                                dragSourceItem = null;
                                dragStartPoint = null;
                                insertIndex = -1;
                                setCursor(null);
                                redraw();
                        }
                });

                this.addMouseMoveListener(new MouseMoveListener()
                {
                        @Override
                        public void mouseMove(MouseEvent e)
                        {
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
                                                setCursor(Display.getCurrent()
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
        private void updateInsertPosition(Point mousePos)
        {
                int oldInsertIndex = insertIndex;
                boolean oldInsertAfter = insertAfter;

                CTabItem targetItem = this.getItem(mousePos);
                int itemCount = this.getItemCount();

                if (targetItem == null) {
                        if (itemCount > 0) {
                                CTabItem firstItem = this.getItem(0);
                                CTabItem lastItem = this.getItem(itemCount - 1);

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
                                insertIndex = this.indexOf(targetItem);
                        }
                }

                if (oldInsertIndex != insertIndex || oldInsertAfter != insertAfter)
                        this.redraw();
        }

        /**
         * 执行移动操作
         */
        private void performMove()
        {
                if (dragSourceItem == null || dragSourceItem.isDisposed())
                        return;
                if (insertIndex < 0)
                        return;

                int sourceIndex = this.indexOf(dragSourceItem);
                int targetIndex = insertIndex;

                if (insertAfter && targetIndex < this.getItemCount())
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
        private void moveItem(int fromIndex, int toIndex)
        {
                CTabItem fromItem = this.getItem(fromIndex);
                String text = fromItem.getText();
                String toolTip = fromItem.getToolTipText();
                Image image = fromItem.getImage();
                Object data = fromItem.getData();
                Control control = itemContentMap.get(fromItem);
                boolean isSelected = (this.getSelection() == fromItem);

                if (control == null || control.isDisposed())
                        control = fromItem.getControl();

                itemContentMap.remove(fromItem);
                fromItem.setControl(null);
                fromItem.dispose();

                CTabItem newItem = new CTabItem(this, SWT.CLOSE, toIndex);
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
                        this.setSelection(newItem);

                this.redraw();
        }

        /**
         * 添加新标签页
         */
        @SuppressWarnings("UnusedReturnValue")
        public CTabItem addTab(String title, Control content)
        {
                CTabItem item = new CTabItem(this, SWT.CLOSE);
                item.setText(title);
                item.setControl(content);
                itemContentMap.put(item, content);
                this.setSelection(item);
                return item;
        }

}