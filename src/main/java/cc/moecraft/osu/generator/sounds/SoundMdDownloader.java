package cc.moecraft.osu.generator.sounds;

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
public class SoundMdDownloader
{
    private static final String url = "https://raw.githubusercontent.com/ppy/osu-wiki/master/wiki/Skinning/Sounds/en.md";

    public static ArrayList<SoundEntry> getCombinedEntries()
    {
        try
        {
            String raw = HttpUtil.downloadString(url, Charset.forName("utf-8"));
            String md = MdConverter.convert(raw);
            return SoundMdReader.readEntries(new BufferedReader(new StringReader(md)));
        }
        catch (HTTPException | IOException e)
        {
            System.out.println("URL无效: " + url);
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
