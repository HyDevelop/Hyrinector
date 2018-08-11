package cc.moecraft.osu.converter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;

/**
 * 此类由 Hykilpikonna 在 2018/07/21 创建!
 * Created by Hykilpikonna on 2018/07/21!
 * Github: https://github.com/hykilpikonna
 * Meow!
 *
 * @author Hykilpikonna
 */
public class ImageReader
{
    public static boolean isAllTransparent(File imageFile) throws IOException, InterruptedException
    {
        Image image = ImageIO.read(imageFile);

        int width = image.getWidth(null);
        int height = image.getHeight(null);
        int[] pixels = new int[width * height];

        PixelGrabber pg = new PixelGrabber(image, 0, 0, width, height, pixels, 0, width);
        pg.grabPixels();

        for (int pixel : pixels)
        {
            Color color = new Color(pixel);
            if (color.getAlpha() != 0) return false;
        }

        return true;
    }
}
