package com.changhong.sqlstudio.app;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;

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

    private static final int ICON_SIZE = 16;
    private static Map<String, Image> iconsMap = null;

    private static Image getScaled(String name) {
        if (iconsMap == null)
            initializeImageLibrary();
        return iconsMap.get(name);
    }

    private static void initializeImageLibrary() {
        iconsMap = new HashMap<>();
        File iconsDir = new File("sql-studio-assets/icons");

        for (File file : Objects.requireNonNull(iconsDir.listFiles())) {
            ImageData data = new ImageData(file.getAbsolutePath());
            Image src = new Image(Launcher.display, data);

            ImageData scaledData = data.scaledTo(ICON_SIZE, ICON_SIZE);
            Image scaled = new Image(Launcher.display, scaledData);

            GC gc = new GC(scaled);
            gc.setAntialias(SWT.ON);
            gc.setInterpolation(SWT.HIGH);

            gc.drawImage(
                    src,
                    0, 0, src.getBounds().width, src.getBounds().height,
                    0, 0, ICON_SIZE, ICON_SIZE
            );

            src.dispose();
            gc.dispose();

            iconsMap.put(file.getName(), scaled);
        }
    }

}
