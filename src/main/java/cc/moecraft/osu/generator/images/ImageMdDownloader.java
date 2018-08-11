package cc.moecraft.osu.generator.images;

import cc.moecraft.osu.generator.shared.MdConverter;
import com.xiaoleilu.hutool.http.HttpUtil;

import javax.xml.ws.http.HTTPException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * 此类由 Hykilpikonna 在 2018/07/21 创建!
 * Created by Hykilpikonna on 2018/07/21!
 * Github: https://github.com/hykilpikonna
 * Meow!
 *
 * @author Hykilpikonna
 */
public class ImageMdDownloader
{
    private static final String prefix = "https://raw.githubusercontent.com/ppy/osu-wiki/master/wiki/Skinning/";
    private static final String suffix = "/en.md";
    private static final String[] elements = new String[]
            {
                    "Interface",
                    "osu!",
                    "osu!taiko",
                    "osu!catch",
                    "osu!mania",
            };

    public static ArrayList<ImageEntry> getCombinedEntries()
    {
        ArrayList<ImageEntry> combinedEntries = new ArrayList<>();

        for (String element : elements)
        {
            try
            {
                String raw = HttpUtil.downloadString(prefix + element + suffix, Charset.forName("utf-8"));
                String md = MdConverter.convert(raw);
                ArrayList<ImageEntry> currentEntries = ImageMdReader.readEntries(new BufferedReader(new StringReader(md)));
                combinedEntries.addAll(currentEntries);
            }
            catch (HTTPException | IOException e)
            {
                System.out.println("URL无效: " + prefix + element + suffix);
                e.printStackTrace();
            }
        }

        return combinedEntries;
    }
}
