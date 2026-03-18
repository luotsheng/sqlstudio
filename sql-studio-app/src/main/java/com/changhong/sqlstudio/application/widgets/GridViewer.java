package com.changhong.sqlstudio.application.widgets;

import com.changhong.sqlstudio.application.Launcher;
import com.changhong.sqlstudio.application.treenode.DBDatabase;
import com.changhong.sqlstudio.application.treenode.DBTable;
import com.changhong.sqlstudio.core.event.EventBus;
import com.changhong.sqlstudio.core.event.notify.RuntimeErrorEvent;
import com.changhong.sqlstudio.driver.QueryResultSet;
import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridEditor;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import java.sql.SQLException;
import java.util.*;

/**
 * 数据页
 *
 * @author Luo Tiansheng
 * @since 2026-03-01
 */
public class GridViewer extends Composite
{
        private final Grid grid;
        private final DBTable tableNode;
        private final GridEditor gridEditor;

        private int start = 0;
        private int count = 50;
        private QueryResultSet queryResultSet;

        public GridViewer(Composite parent, DBTable tableNode)
        {
                super(parent, SWT.NONE);
                setLayout(new FillLayout());

                this.tableNode = tableNode;

                grid = new Grid(this, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
                grid.setHeaderVisible(true);
                grid.setLinesVisible(true);

                grid.setAutoHeight(true);
                grid.setRowHeaderVisible(false);
                grid.setCellSelectionEnabled(true);

                gridEditor = new GridEditor(grid);

                addKeyListener();
                enableEditing();

                render();
        }

        private void addKeyListener()
        {
                grid.addKeyListener(new KeyAdapter()
                {
                        @Override
                        public void keyPressed(KeyEvent e)
                        {
                                // Ctrl+C 复制
                                if ((e.stateMask & SWT.CTRL) != 0 && e.keyCode == 'c') {
                                        copySelection();
                                }
                                // Ctrl+A 全选
                                if ((e.stateMask & SWT.CTRL) != 0 && e.keyCode == 'a') {
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
                }

                // 复制到剪贴板
                TextTransfer textTransfer = TextTransfer.getInstance();
                Launcher.clipboard.setContents(new Object[]{sb.toString()}, new Transfer[]{textTransfer});
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

        private void render()
        {
                grid.setRedraw(false);

                DBDatabase db = tableNode.db();

                try {
                        queryResultSet = db.queryResultSet(tableNode.name(), start, count);

                        queryResultSet.getColumns().forEach(col -> {
                                GridColumn column = new GridColumn(grid, SWT.NONE);
                                column.setText(col);

                                column.setResizeable(true);
                                column.setMoveable(true);

                                column.setWidth(150);
                        });

                        queryResultSet.getRows().forEach(row -> {
                                GridItem item = new GridItem(grid, SWT.NONE);

                                for (int i = 0; i < row.size(); i++) {
                                        String value = row.get(i) != null ? row.get(i) : "";
                                        item.setText(i, value);
                                }

                                item.setHeight(25);
                        });

                        for (int i = 0; i < grid.getColumnCount(); i++) {
                                GridColumn column = grid.getColumn(i);
                                int maxWidth = 100;

                                for (GridItem item : grid.getItems()) {
                                        String text = item.getText(i);
                                        int width = text.length() * 8;
                                        maxWidth = Math.max(maxWidth, width);
                                }

                                column.setWidth(Math.min(maxWidth, 300));
                        }
                } catch (SQLException e) {
                        EventBus.publish(new RuntimeErrorEvent(e));
                }

                grid.setRedraw(true);
        }
}
