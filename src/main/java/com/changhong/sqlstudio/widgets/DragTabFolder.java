package com.changhong.sqlstudio.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

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

    // 拖拽状态
    private CTabItem dragSourceItem;
    private boolean isDragging;
    private Point dragStartPoint;

    // 插入位置
    private int insertIndex = -1;
    private boolean insertAfter;

    // 拖拽阈值
    private static final int DRAG_THRESHOLD = 5;

    // 插入框的尺寸
    private static final int INSERT_MARKER_WIDTH = 4;
    private static final int INSERT_MARKER_COLOR = SWT.COLOR_LIST_SELECTION;

    public DragTabFolder(Composite parent) {
        this.tabFolder = new CTabFolder(parent, SWT.BORDER | SWT.CLOSE);
        configureTabFolder();
        setupDragListeners();
        setupPaintListener(); // 关键：添加绘制监听器
    }

    private void configureTabFolder() {
        tabFolder.setBorderVisible(true);
        tabFolder.setTabHeight(25);
        tabFolder.setMinimumCharacters(15);
        tabFolder.setUnselectedCloseVisible(true);
    }

    /**
     * 设置绘制监听器 - 用于绘制自定义插入框
     */
    private void setupPaintListener() {
        tabFolder.addPaintListener(new PaintListener() {
            @Override
            public void paintControl(PaintEvent e) {
                // 只有在拖拽状态且有有效插入索引时才绘制
                if (!isDragging || insertIndex < 0) return;

                // 获取标签区域
                Rectangle tabArea = tabFolder.getClientArea();
                int itemCount = tabFolder.getItemCount();

                // 如果没有标签，不绘制
                if (itemCount == 0) return;

                // 计算插入框的位置
                Rectangle markerRect = calculateInsertMarkerRect();
                if (markerRect == null) return;

                // 设置颜色和透明度
                GC gc = e.gc;
                gc.setAdvanced(true);
                gc.setAlpha(180); // 半透明效果

                // 绘制插入框
                Color markerColor = Display.getCurrent().getSystemColor(INSERT_MARKER_COLOR);
                gc.setBackground(markerColor);
                gc.fillRectangle(markerRect);

                // 绘制边框（可选，更突出）
                gc.setAlpha(255);
                gc.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_DARK_SHADOW));
                gc.drawRectangle(markerRect);
            }
        });
    }

    /**
     * 计算插入框的位置
     */
    private Rectangle calculateInsertMarkerRect() {
        int itemCount = tabFolder.getItemCount();
        if (itemCount == 0) return null;

        // 如果插入索引超出范围，调整
        int targetIndex = insertIndex;
        if (targetIndex > itemCount) targetIndex = itemCount;
        if (targetIndex < 0) targetIndex = 0;

        // 获取目标标签的位置
        Rectangle bounds;
        if (targetIndex >= itemCount) {
            // 插入到最后，使用最后一个标签的位置
            CTabItem lastItem = tabFolder.getItem(itemCount - 1);
            bounds = lastItem.getBounds();
            // 放在最后一个标签的右边
            return new Rectangle(
                    bounds.x + bounds.width + 2,
                    bounds.y,
                    INSERT_MARKER_WIDTH,
                    bounds.height
            );
        } else {
            CTabItem targetItem = tabFolder.getItem(targetIndex);
            bounds = targetItem.getBounds();

            if (insertAfter) {
                // 放在目标标签后面
                return new Rectangle(
                        bounds.x + bounds.width + 2,
                        bounds.y,
                        INSERT_MARKER_WIDTH,
                        bounds.height
                );
            } else {
                // 放在目标标签前面
                return new Rectangle(
                        bounds.x - INSERT_MARKER_WIDTH - 2,
                        bounds.y,
                        INSERT_MARKER_WIDTH,
                        bounds.height
                );
            }
        }
    }

    /**
     * 设置拖拽监听器
     */
    private void setupDragListeners() {
        // 鼠标按下
        tabFolder.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(MouseEvent e) {
                if (e.button != 1) return;

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
                if (isDragging && insertIndex >= 0) {
                    // 执行移动
                    performMove();
                }

                // 清理状态
                isDragging = false;
                dragSourceItem = null;
                dragStartPoint = null;
                insertIndex = -1;
                tabFolder.setCursor(null);
                tabFolder.redraw(); // 触发重绘，清除插入框
            }
        });

        // 鼠标移动
        tabFolder.addMouseMoveListener(new MouseMoveListener() {
            @Override
            public void mouseMove(MouseEvent e) {
                if (dragSourceItem == null || dragSourceItem.isDisposed()) return;

                Point currentPoint = new Point(e.x, e.y);

                // 检查是否开始拖拽
                if (!isDragging) {
                    if (dragStartPoint == null) return;

                    int distance = Math.abs(currentPoint.x - dragStartPoint.x)
                            + Math.abs(currentPoint.y - dragStartPoint.y);

                    if (distance > DRAG_THRESHOLD) {
                        isDragging = true;
                        tabFolder.setCursor(Display.getCurrent().getSystemCursor(SWT.CURSOR_SIZEALL));
                    } else {
                        return;
                    }
                }

                // 更新插入位置
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
            // 鼠标不在任何标签上，根据鼠标位置判断
            if (itemCount > 0) {
                CTabItem firstItem = tabFolder.getItem(0);
                CTabItem lastItem = tabFolder.getItem(itemCount - 1);

                if (mousePos.x < firstItem.getBounds().x) {
                    // 在第一个标签左边
                    insertIndex = 0;
                    insertAfter = false;
                } else if (mousePos.x > lastItem.getBounds().x + lastItem.getBounds().width) {
                    // 在最后一个标签右边
                    insertIndex = itemCount;
                    insertAfter = true;
                } else {
                    insertIndex = -1;
                }
            } else {
                insertIndex = -1;
            }
        } else {
            // 鼠标在某个标签上
            if (targetItem == dragSourceItem) {
                // 在自己上面，不显示插入框
                insertIndex = -1;
            } else {
                Rectangle bounds = targetItem.getBounds();
                insertAfter = mousePos.x > (bounds.x + bounds.width / 2);
                insertIndex = tabFolder.indexOf(targetItem);
            }
        }

        // 如果插入位置发生变化，重绘
        if (oldInsertIndex != insertIndex || oldInsertAfter != insertAfter) {
            tabFolder.redraw();
        }
    }

    /**
     * 执行移动操作
     */
    private void performMove() {
        if (dragSourceItem == null || dragSourceItem.isDisposed()) return;
        if (insertIndex < 0) return;

        int sourceIndex = tabFolder.indexOf(dragSourceItem);
        int targetIndex = insertIndex;

        // 根据 after 调整目标索引
        if (insertAfter && targetIndex < tabFolder.getItemCount()) {
            targetIndex++;
        }

        // 如果源索引小于目标索引，需要调整
        if (sourceIndex < targetIndex) {
            targetIndex--;
        }

        // 如果索引相同，不需要移动
        if (sourceIndex == targetIndex) {
            return;
        }

        // 执行移动
        moveItem(sourceIndex, targetIndex);
    }

    /**
     * 移动标签页
     */
    private void moveItem(int fromIndex, int toIndex) {
        // 保存原标签页的信息
        CTabItem fromItem = tabFolder.getItem(fromIndex);
        String text = fromItem.getText();
        String toolTip = fromItem.getToolTipText();
        Image image = fromItem.getImage();
        Object data = fromItem.getData();
        Control control = itemContentMap.get(fromItem);
        boolean isSelected = (tabFolder.getSelection() == fromItem);

        // 如果控件不存在，尝试从 item 获取
        if (control == null || control.isDisposed()) {
            control = fromItem.getControl();
        }

        // 从映射中移除旧项
        itemContentMap.remove(fromItem);

        // 必须先移除控件再 dispose item
        fromItem.setControl(null);
        fromItem.dispose();

        // 创建新标签页（在指定索引）
        CTabItem newItem = new CTabItem(tabFolder, SWT.CLOSE, toIndex);
        newItem.setText(text);
        if (toolTip != null) newItem.setToolTipText(toolTip);
        if (image != null) newItem.setImage(image);
        if (data != null) newItem.setData(data);
        newItem.setControl(control);
        itemContentMap.put(newItem, control);

        // 恢复选中状态
        if (isSelected) {
            tabFolder.setSelection(newItem);
        }

        // 重绘
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
}