package com.changhong.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.Display;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Luo Tiansheng
 * @since 2026-03-01
 */
@SuppressWarnings("ExtractMethodRecommender")
public class Images
{
        private static final int ICON_WIDTH = 20;
        private static final int ICON_HEIGHT = 20;
        private static Map<String, Image> iconsMap = null;
        public static Image CONNECT = getScaled("connect.png");
        public static Image QUERY = getScaled("query.png");
        public static Image CHAIN = getScaled("chain.png");
        public static Image DATABASE_0 = getScaled("database0.png");
        public static Image DATABASE_1 = getScaled("database1.png");
        public static Image TABLE = getScaled("table.png");
        public static Image SQL = getScaled("sql.png");
        public static Image RUN_0 = getScaled("run0.png");

        private static Image getScaled(String name)
        {
                if (iconsMap == null)
                        initializeImageLibrary();
                return iconsMap.get(name);
        }

        private static Image scaleImage(Image src)
        {
                Image current = src;

                while (true) {
                        Rectangle rectangle = current.getBounds();
                        if (rectangle.width / 2 < ICON_WIDTH)
                                break;

                        int w = rectangle.width / 2;
                        int h = rectangle.height / 2;

                        ImageData srcData = current.getImageData();

                        PaletteData palette = srcData.palette;
                        if (srcData.alphaData != null) {
                                for (int y = 0; y < srcData.height; y++) {
                                        for (int x = 0; x < srcData.width; x++) {
                                                int pixel = srcData.getPixel(x, y);
                                                int alpha = srcData.alphaData[y * srcData.width + x] & 0xFF;
                                                RGB rgb = palette.getRGB(pixel);
                                                int r = rgb.red * alpha / 255;
                                                int g = rgb.green * alpha / 255;
                                                int b = rgb.blue * alpha / 255;
                                                srcData.setPixel(x, y, palette.getPixel(new RGB(r, g, b)));
                                        }
                                }
                        }

                        // 使用原图的 ImageData，缩放后保留 alpha
                        Image tmp = new Image(Display.getCurrent(), w, h);

                        GC gc = new GC(tmp);
                        gc.setAntialias(SWT.ON);
                        gc.setInterpolation(SWT.HIGH);
                        gc.drawImage(current, 0, 0, rectangle.width, rectangle.height, 0, 0, w, h);
                        gc.dispose();

                        // 复制 alpha 通道
                        ImageData tmpData = tmp.getImageData();
                        if (srcData.alphaData != null) {
                                // 按比例缩放 alphaData
                                tmpData.alphaData = new byte[w * h];
                                for (int y = 0; y < h; y++) {
                                        for (int x = 0; x < w; x++) {
                                                int sx = x * srcData.width / w;
                                                int sy = y * srcData.height / h;
                                                tmpData.alphaData[y * w + x] = srcData.alphaData[sy * srcData.width + sx];
                                        }
                                }
                                tmp.dispose();
                                tmp = new Image(Display.getCurrent(), tmpData);
                        }

                        if (current != src)
                                current.dispose();

                        current = tmp;
                }

                ImageData currentData = current.getImageData();
                ImageData finalData = currentData.scaledTo(ICON_WIDTH, ICON_HEIGHT);

                Image result = new Image(Display.getCurrent(), finalData);

                if (current != src)
                        current.dispose();

                return result;
        }

        private static void initializeImageLibrary()
        {
                iconsMap = new HashMap<>();
                File iconsDir = new File("assets/icons");

                for (File file : Objects.requireNonNull(iconsDir.listFiles())) {
                        Image src = new Image(Display.getCurrent(), file.getAbsolutePath());
                        Image scaled = scaleImage(src);
                        src.dispose();
                        iconsMap.put(file.getName(), scaled);
                }
        }

}
