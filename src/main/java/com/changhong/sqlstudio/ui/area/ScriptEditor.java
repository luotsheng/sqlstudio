package com.changhong.sqlstudio.ui.area;

import com.changhong.sqlstudio.StudioApplication;
import com.changhong.sqlstudio.widgets.DragTabFolder;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import java.util.ArrayList;

import static org.eclipse.swt.SWT.*;
import static org.eclipse.swt.SWT.BOLD;
import static org.eclipse.swt.SWT.COLOR_BLACK;
import static org.eclipse.swt.SWT.COLOR_DARK_BLUE;
import static org.eclipse.swt.SWT.COLOR_DARK_GREEN;
import static org.eclipse.swt.SWT.COLOR_WHITE;
import static org.eclipse.swt.SWT.H_SCROLL;
import static org.eclipse.swt.SWT.NORMAL;
import static org.eclipse.swt.SWT.V_SCROLL;

/**
 * @author luotiansheng
 */
@SuppressWarnings("FieldCanBeLocal")
public class ScriptEditor {

    private static final int EDITOR_FONT_SIZE = 16;

    private final Composite container;
    private final DragTabFolder tabFolder;

    public ScriptEditor(SashForm sashForm) {
        container = new Composite(sashForm, BORDER);
        container.setLayout(new FillLayout());

        tabFolder = new DragTabFolder(container);
    }

    private int count = 1;

    public void newQueryScriptTab() {
        CodeEditor codeEditor = new CodeEditor(tabFolder.getTabFolder());
        tabFolder.addTab("*新建查询" + "_" + (count++), codeEditor);
    }

    /**
     * @author luotiansheng
     */
    static class CodeEditor extends StyledText {
        public CodeEditor(Composite parent) {
            super(parent, MULTI | BORDER | V_SCROLL | H_SCROLL);

            setBackground(StudioApplication.DISPLAY.getSystemColor(COLOR_WHITE));
            setForeground(StudioApplication.DISPLAY.getSystemColor(COLOR_BLACK));

            Font font = new Font(StudioApplication.DISPLAY, new FontData[] {
                    new FontData("Consolas", EDITOR_FONT_SIZE, NORMAL),
                    new FontData("Monaco", EDITOR_FONT_SIZE, NORMAL),
            });

            setFont(font);

            addLineStyleListener(new LineStyleListener() {
                private final String[] keywords = {
                        "SELECT", "FROM", "WHERE", "INSERT", "UPDATE", "DELETE", "JOIN", "ON", "INNER", "LEFT", "RIGHT", "FULL", "OUTER",
                        "AND", "OR", "NOT", "AS", "BY", "GROUP", "HAVING", "ORDER", "ASC", "DESC", "LIMIT", "OFFSET",
                        "CREATE", "TABLE", "DROP", "ALTER", "ADD", "COLUMN", "PRIMARY", "KEY", "FOREIGN", "REFERENCES",
                        "VALUES", "INTO", "SET", "DISTINCT", "ALL", "UNION", "INTERSECT", "EXCEPT", "CASE", "WHEN", "THEN", "ELSE", "END",
                        "NULL", "IS", "LIKE", "BETWEEN", "EXISTS", "IN"
                };
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
    }

}
