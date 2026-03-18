package com.changhong.sqlstudio.application.widgets;

import com.changhong.sqlstudio.application.Images;
import com.changhong.sqlstudio.application.Launcher;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import java.util.ArrayList;

/**
 * 查询工作页
 *
 * @author Luo Tiansheng
 * @since 2026-03-01
 */
@SuppressWarnings({
        "FieldMayBeFinal",
        "FieldCanBeLocal",
})
public class QueryEditor extends Composite
{
        private static final int EDITOR_FONT_SIZE = 18;

        private static final String[] keywords = {
                "SELECT", "FROM", "WHERE", "INSERT", "UPDATE", "DELETE", "JOIN", "ON", "INNER", "LEFT", "RIGHT", "FULL", "OUTER",
                "AND", "OR", "NOT", "AS", "BY", "GROUP", "HAVING", "ORDER", "ASC", "DESC", "LIMIT", "OFFSET",
                "CREATE", "TABLE", "DROP", "ALTER", "ADD", "COLUMN", "PRIMARY", "KEY", "FOREIGN", "REFERENCES",
                "VALUES", "INTO", "SET", "DISTINCT", "ALL", "UNION", "INTERSECT", "EXCEPT", "CASE", "WHEN", "THEN", "ELSE", "END",
                "NULL", "IS", "LIKE", "BETWEEN", "EXISTS", "IN"
        };

        private StyledText styledText;
        private ToolBar toolBar;
        private CTabItem tabItem;
        private boolean dirty = false;

        public QueryEditor(Composite parent)
        {
                super(parent, SWT.NONE);

                setLayout(new GridLayout(2, false));

                createToolBar();
                createStyledText();
        }

        private void createToolBar()
        {
                toolBar = new ToolBar(this, SWT.FLAT | SWT.VERTICAL);

                GridData toolbarData = new GridData(SWT.LEFT, SWT.FILL, false, true);
                toolbarData.verticalAlignment = SWT.BEGINNING;
                toolBar.setLayoutData(toolbarData);

                ToolItem runItem = new ToolItem(toolBar, SWT.PUSH);
                runItem.setImage(Images.RUN_0);
                runItem.setToolTipText("运行已选择");
        }

        private void createStyledText()
        {
                styledText = new StyledText(this, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
                styledText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

                setBackground(Launcher.display.getSystemColor(SWT.COLOR_WHITE));
                setForeground(Launcher.display.getSystemColor(SWT.COLOR_BLACK));

                styledText.setLeftMargin(10);
                styledText.setRightMargin(10);
                styledText.setTopMargin(10);
                styledText.setBottomMargin(10);

                Font font = new Font(Launcher.display, new FontData[]{
                        new FontData("Monaco", EDITOR_FONT_SIZE, SWT.NORMAL),
                });

                styledText.setFont(font);

                styledText.addModifyListener(modifyEvent -> {
                        try {
                                if (tabItem == null || tabItem.isDisposed())
                                        return;

                                if (!dirty) {
                                        dirty = true;
                                        if (tabItem != null && !tabItem.isDisposed())
                                                tabItem.setText("*" + tabItem.getText());
                                }
                        } catch (SWTException e) {
                                if (e.code != SWT.ERROR_WIDGET_DISPOSED) {
                                        throw e;
                                }
                        }
                });

                styledText.addLineStyleListener(new LineStyleListener()
                {
                        private final Color keywordColor = Launcher.display.getSystemColor(SWT.COLOR_DARK_BLUE);
                        private final Color stringColor = Launcher.display.getSystemColor(SWT.COLOR_DARK_GREEN);

                        @Override
                        public void lineGetStyle(LineStyleEvent event)
                        {
                                String lineUpper = event.lineText.toUpperCase();
                                java.util.List<StyleRange> styles = new ArrayList<>();

                                for (String kw : keywords) {
                                        int pos = 0;
                                        while ((pos = lineUpper.indexOf(kw, pos)) >= 0) {
                                                boolean isWordStart = pos == 0 || !Character.isJavaIdentifierPart(event.lineText.charAt(pos - 1));
                                                boolean isWordEnd = pos + kw.length() == event.lineText.length() ||
                                                        !Character.isJavaIdentifierPart(event.lineText.charAt(pos + kw.length()));

                                                if (isWordStart && isWordEnd) {
                                                        StyleRange sr = new StyleRange();
                                                        sr.start = pos + event.lineOffset;
                                                        sr.length = kw.length();
                                                        sr.foreground = keywordColor;
                                                        sr.fontStyle = SWT.BOLD;
                                                        styles.add(sr);
                                                }
                                                pos += kw.length();
                                        }
                                }

                                int start = -1;
                                for (int i = 0; i < event.lineText.length(); i++) {
                                        char c = event.lineText.charAt(i);
                                        if (c == '\'') {
                                                if (start == -1) {
                                                        start = i;
                                                } else {
                                                        StyleRange sr = new StyleRange();
                                                        sr.start = start + event.lineOffset;
                                                        sr.length = i - start + 1;
                                                        sr.foreground = stringColor;
                                                        styles.add(sr);
                                                        start = -1;
                                                }
                                        }
                                }

                                if (!styles.isEmpty()) {
                                        event.styles = styles.toArray(new StyleRange[0]);
                                }
                        }
                });
        }

        public CTabItem getTabItem()
        {
                return tabItem;
        }

        public void setTabItem(CTabItem tabItem)
        {
                this.tabItem = tabItem;
        }

        public boolean isDirty()
        {
                return dirty;
        }

        public void setDirty(boolean dirty)
        {
                this.dirty = dirty;
        }
}