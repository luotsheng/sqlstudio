package com.changhong.sqlstudio.application;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;

import java.io.File;
import java.util.Arrays;
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
    public static Image RUN_0 = getScaled("run0.png");

    private static final int ICON_SIZE = 16;
    private static Map<String, Image> iconsMap = null;

    private static Image getScaled(String name) {
        if (iconsMap == null)
            initializeImageLibrary();
        return iconsMap.get(name);
    }

    private static Image scaleImage(Image src)
    {
        ImageData data = src.getImageData();

        ImageData scaled = data.scaledTo(
                Images.ICON_SIZE,
                Images.ICON_SIZE
        );

        return new Image(Launcher.display, scaled);
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
