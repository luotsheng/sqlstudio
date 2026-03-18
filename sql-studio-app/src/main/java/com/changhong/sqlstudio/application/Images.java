package com.changhong.sqlstudio.application;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Luo Tiansheng
 * @since 2026-03-01
 */
public class Images {

    public static Image CONNECT = getScaled("connect.png");
    public static Image QUERY = getScaled("query.png");
    public static Image CHAIN = getScaled("chain.png");
    public static Image DATABASE_0 = getScaled("database0.png");
    public static Image DATABASE_1 = getScaled("database1.png");
    public static Image TABLE = getScaled("table.png");
    public static Image SQL = getScaled("sql.png");

    private static final int ICON_SIZE = 16;
    private static Map<String, Image> iconsMap = null;

    private static Image getScaled(String name) {
        if (iconsMap == null)
            initializeImageLibrary();
        return iconsMap.get(name);
    }

    private static Image scaleImage(Image src)
    {
        Image current = src;

        while (true) {
            Rectangle b = current.getBounds();

            if (b.width / 2 < Images.ICON_SIZE)
                break;

            int w = b.width / 2;
            int h = b.height / 2;

            Image tmp = new Image(Launcher.display, w, h);
            GC gc = new GC(tmp);
            gc.setAntialias(SWT.ON);
            gc.setInterpolation(SWT.HIGH);

            gc.drawImage(
                    current,
                    0, 0, b.width, b.height,
                    0, 0, w, h
            );

            gc.dispose();

            if (current != src)
                current.dispose();

            current = tmp;
        }

        Rectangle b = current.getBounds();

        Image result = new Image(Launcher.display, Images.ICON_SIZE, Images.ICON_SIZE);
        GC gc = new GC(result);
        gc.setAntialias(SWT.ON);
        gc.setInterpolation(SWT.HIGH);

        gc.drawImage(
                current,
                0, 0, b.width, b.height,
                0, 0, Images.ICON_SIZE, Images.ICON_SIZE
        );

        gc.dispose();

        if (current != src)
            current.dispose();

        return result;
    }

    private static void initializeImageLibrary() {
        iconsMap = new HashMap<>();
        File iconsDir = new File("sql-studio-assets/icons");

        for (File file : Objects.requireNonNull(iconsDir.listFiles())) {
            Image src = new Image(Launcher.display, file.getAbsolutePath());
            Image scaled = scaleImage(src);
            src.dispose();
            iconsMap.put(file.getName(), scaled);
        }
    }

}
