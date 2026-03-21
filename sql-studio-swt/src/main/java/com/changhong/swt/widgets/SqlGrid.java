package com.changhong.swt.widgets;

import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridEditor;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;

import java.util.*;
import java.util.List;

/**
 * 数据页
 *
 * @author Luo Tiansheng
 * @since 2026-03-01
 */
public class SqlGrid extends Composite
{
        private final Grid grid;
        private final GridEditor gridEditor;

        private static final Color NULL_COLOR = new Color(128, 128, 128);

        public SqlGrid(Composite parent)
        {
                super(parent, SWT.NONE);
                setLayout(new FillLayout());

                int gridFlags = SWT.BORDER | SWT.FULL_SELECTION | SWT.VIRTUAL |
                                SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL;

                grid = new org.eclipse.nebula.widgets.grid.Grid(this, gridFlags);
                grid.setHeaderVisible(true);
                grid.setLinesVisible(true);

                grid.setAutoHeight(false);
                grid.setRowHeaderVisible(false);
                grid.setCellSelectionEnabled(true);

                grid.addListener(SWT.EraseItem, e -> e.detail &= ~(SWT.HOT | SWT.SELECTED));

                gridEditor = new GridEditor(grid);

                addKeyListener();
                enableEditing();
        }

        public void drawData(List<String> columns, List<List<String>> rows)
        {
                grid.setRedraw(false);
                grid.setLayoutDeferred(true);

                // 计算列宽
                int[] columnWidths = new int[columns.size()];
                Arrays.fill(columnWidths, 100);

                for (List<String> row : rows) {
                        for (int i = 0; i < row.size(); i++) {
                                String value = row.get(i);
                                if (value != null) {
                                        int width = value.length() * 8; // 估算
                                        columnWidths[i] = Math.max(columnWidths[i], Math.min(width, 300));
                                }
                        }
                }

                for (int i = 0; i < columns.size(); i++) {
                        GridColumn column = new GridColumn(grid, SWT.NONE);
                        column.setText(columns.get(i));
                        column.setResizeable(true);
                        column.setMoveable(true);
                        column.setWidth(columnWidths[i]);
                }

                // 设置总行数, 绑定 SetData
                grid.setItemCount(rows.size());

                grid.addListener(SWT.SetData, event -> {
                        GridItem item = (GridItem) event.item;
                        int index = grid.indexOf(item);

                        List<String> rowData = rows.get(index);

                        for (int col = 0; col < rowData.size(); col++) {
                                String value = rowData.get(col);
                                if (value != null) {
                                        item.setText(col, value);
                                } else {
                                        item.setText(col, "(NULL)");
                                        item.setForeground(col, NULL_COLOR);
                                }
                        }
                        item.setHeight(25);
                });

                grid.setLayoutDeferred(false);
                grid.setRedraw(true);
        }

        private void addKeyListener()
        {
                grid.addKeyListener(new KeyAdapter()
                {
                        @Override
                        public void keyPressed(KeyEvent e)
                        {
                                // Ctrl+C 复制
                                if (e.keyCode == 'c' && ((e.stateMask & SWT.CTRL) != 0 || (e.stateMask & SWT.COMMAND) != 0)) {
                                        copySelection();
                                }
                                // Ctrl+A 全选
                                if (e.keyCode == 'a' && ((e.stateMask & SWT.CTRL) != 0 || (e.stateMask & SWT.COMMAND) != 0)) {
                                        selectAll();
                                }
                        }
                });
        }

        private void copySelection()
        {
                // 获取选中的单元格（Point[]，x=列索引，y=行索引）
                Point[] selectedCells = grid.getCellSelection();
                if (selectedCells == null || selectedCells.length == 0) {
                        return;
                }

                // 分析选中的区域
                Set<Integer> selectedRows = new TreeSet<>();
                Set<Integer> selectedCols = new TreeSet<>();

                for (Point cell : selectedCells) {
                        selectedRows.add(cell.y);
                        selectedCols.add(cell.x);
                }

                // 转换为有序列表
                List<Integer> rows = new ArrayList<>(selectedRows);
                List<Integer> cols = new ArrayList<>(selectedCols);
                Collections.sort(rows);
                Collections.sort(cols);

                // 获取列顺序（考虑用户调整列顺序）
                int[] columnOrder = grid.getColumnOrder();

                StringBuilder sb = new StringBuilder();

                // 按行、列顺序输出选中的单元格
                for (int row : rows) {
                        GridItem item = grid.getItem(row);
                        if (item == null || item.isDisposed()) continue;

                        for (int i = 0; i < cols.size(); i++) {
                                if (i > 0) sb.append("\t");

                                int col = cols.get(i);
                                // 找到实际显示顺序的列索引
                                int actualColIndex = -1;
                                for (int j = 0; j < columnOrder.length; j++) {
                                        if (columnOrder[j] == col) {
                                                actualColIndex = j;
                                                break;
                                        }
                                }

                                if (actualColIndex != -1) {
                                        String text = item.getText(col);
                                        sb.append(text != null ? text : "");
                                }
                        }

                        sb.append(System.lineSeparator());
                }

                sb.delete(sb.length() - 1, sb.length());

                TextTransfer textTransfer = TextTransfer.getInstance();
                Clipboard clipboard = new Clipboard(Display.getCurrent());
                clipboard.setContents(new Object[]{sb.toString()}, new Transfer[]{textTransfer});
                clipboard.dispose();
        }


        private void selectAll()
        {
                grid.selectAll();
        }

        private void enableEditing()
        {
                grid.addListener(SWT.MouseDoubleClick, event -> {
                        Point point = new Point(event.x, event.y);
                        GridItem item = grid.getItem(point);
                        GridColumn column = grid.getColumn(point);

                        if (item != null && column != null) {
                                int columnIndex = grid.indexOf(column);
                                startEditing(item, columnIndex);
                        }
                });

                grid.addListener(SWT.KeyDown, event -> {
                        if (event.keyCode == SWT.CR || event.keyCode == SWT.KEYPAD_CR) {
                                GridItem[] selection = grid.getSelection();
                                if (selection.length > 0) {
                                        startEditing(selection[0], 0);
                                }
                        }
                });
        }

        /**
         * 开始编辑单元格
         */
        private void startEditing(GridItem item, int columnIndex)
        {
                // 创建文本输入框
                Text text = new Text(grid, SWT.BORDER);
                text.setText(item.getText(columnIndex));
                text.selectAll();

                // 设置编辑器
                gridEditor.horizontalAlignment = SWT.LEFT;
                gridEditor.grabHorizontal = true;
                gridEditor.minimumWidth = 50;
                gridEditor.setEditor(text, item, columnIndex);

                // 监听焦点丢失
                text.addListener(SWT.FocusOut, e -> {
                        saveEditing(item, columnIndex, text.getText());
                        text.dispose();
                        gridEditor.setEditor(null, null, -1);  // 清除编辑器
                });

                // 监听键盘事件
                text.addListener(SWT.KeyDown, e -> {
                        switch (e.keyCode) {
                                case SWT.CR:
                                case SWT.KEYPAD_CR:
                                        // 回车保存
                                        saveEditing(item, columnIndex, text.getText());
                                        text.dispose();
                                        gridEditor.setEditor(null, null, -1);
                                        break;
                                case SWT.ESC:
                                        // ESC 取消
                                        text.dispose();
                                        gridEditor.setEditor(null, null, -1);
                                        break;
                                case SWT.ARROW_UP:
                                case SWT.ARROW_DOWN:
                                case SWT.ARROW_LEFT:
                                case SWT.ARROW_RIGHT:
                                        e.doit = false;
                                        break;
                        }
                });

                text.setFocus();
        }

        /**
         * 保存编辑结果
         */
        private void saveEditing(GridItem item, int columnIndex, String newValue)
        {
                String oldValue = item.getText(columnIndex);
                if (oldValue.equals(newValue)) {
                        return;
                }

                item.setText(columnIndex, newValue);
        }

        @Override
        public void dispose()
        {
                super.dispose();
                gridEditor.dispose();
                grid.dispose();
        }
}
