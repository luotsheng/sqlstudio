package com.changhong.sqlstudio.widgets;

import com.changhong.sqlstudio.StudioApplication;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Composite;

import java.util.ArrayList;

import static org.eclipse.swt.SWT.*;

/**
 * @author luotiansheng
 */
public class CodeEditor extends StyledText {

    private static final int EDITOR_FONT_SIZE = 18;

    private static final String[] keywords = {
            "SELECT", "FROM", "WHERE", "INSERT", "UPDATE", "DELETE", "JOIN", "ON", "INNER", "LEFT", "RIGHT", "FULL", "OUTER",
            "AND", "OR", "NOT", "AS", "BY", "GROUP", "HAVING", "ORDER", "ASC", "DESC", "LIMIT", "OFFSET",
            "CREATE", "TABLE", "DROP", "ALTER", "ADD", "COLUMN", "PRIMARY", "KEY", "FOREIGN", "REFERENCES",
            "VALUES", "INTO", "SET", "DISTINCT", "ALL", "UNION", "INTERSECT", "EXCEPT", "CASE", "WHEN", "THEN", "ELSE", "END",
            "NULL", "IS", "LIKE", "BETWEEN", "EXISTS", "IN"
    };

    private CTabItem tabItem;
    private boolean dirty = false;

    public CodeEditor(Composite parent) {
        super(parent, MULTI | BORDER | V_SCROLL | H_SCROLL);

        setBackground(StudioApplication.DISPLAY.getSystemColor(COLOR_WHITE));
        setForeground(StudioApplication.DISPLAY.getSystemColor(COLOR_BLACK));

        setLineSpacing(8);

        setLeftMargin(10);
        setRightMargin(10);
        setTopMargin(10);
        setBottomMargin(10);

        Font font = new Font(StudioApplication.DISPLAY, new FontData[] {
                new FontData("Monaco", EDITOR_FONT_SIZE, NORMAL),
        });

        setFont(font);

        addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent modifyEvent) {
                if (!dirty) {
                    dirty = true;
                    if (tabItem != null) {
                        tabItem.setText("*" + tabItem.getText());
                    }
                }
            }
        });

        addLineStyleListener(new LineStyleListener() {
            private final Color keywordColor = StudioApplication.DISPLAY.getSystemColor(COLOR_DARK_BLUE);
            private final Color stringColor   = StudioApplication.DISPLAY.getSystemColor(COLOR_DARK_GREEN);

            @Override
            public void lineGetStyle(LineStyleEvent event) {
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
                            sr.fontStyle = BOLD;
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

    public void setTabItem(CTabItem tabItem) {
        this.tabItem = tabItem;
    }

    public CTabItem getTabItem() {
        return tabItem;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public boolean isDirty() {
        return dirty;
    }

}