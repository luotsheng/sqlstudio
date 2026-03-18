package com.changhong.sqlstudio.application.widgets;

import com.changhong.sqlstudio.application.treenode.NNDatabase;
import com.changhong.sqlstudio.application.treenode.NNTable;
import com.changhong.sqlstudio.core.event.EventBus;
import com.changhong.sqlstudio.core.event.notify.RuntimeErrorEvent;
import com.changhong.sqlstudio.driver.QueryResultSet;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import java.sql.SQLException;
import java.util.stream.IntStream;

/**
 * 数据页
 *
 * @author Luo Tiansheng
 * @since 2026-03-01
 */
public class DataTable extends Composite {

    private final Table table;
    private final NNTable tableNode;

    private int start = 0;
    private int count = 50;

    public DataTable(Composite parent, NNTable tableNode) {
        super(parent, SWT.NONE);
        setLayout(new FillLayout());

        this.tableNode = tableNode;

        table = new Table(this, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        render();
    }

    private void render()
    {
        NNDatabase db = tableNode.db();

        try {
            QueryResultSet data = db.selectTableData(tableNode.name(), start, count);

            data.getColumns().forEach(col -> {
                TableColumn column = new TableColumn(table, SWT.NONE);
                column.setText(col);
            });

            data.getRows().forEach(row -> {
                TableItem item = new TableItem(table, SWT.NONE);
                IntStream.range(0, row.size()).forEach(i -> item.setText(i, row.get(i) != null ? row.get(i) : ""));
            });

            for (TableColumn col : table.getColumns())
                col.pack();
        } catch (SQLException e) {
            EventBus.publish(new RuntimeErrorEvent(e));
        }

    }

}
