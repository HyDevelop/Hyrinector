package cc.moecraft.osu.generator;

import cc.moecraft.osu.generator.images.ImageMdDownloader;
import cc.moecraft.osu.generator.shared.EntryResolver;
import cc.moecraft.osu.generator.sounds.SoundMdDownloader;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import java.io.*;

import static cc.moecraft.osu.generator.EntriesJsonGenerator.*;

/**
 * 此类由 Hykilpikonna 在 2018/07/21 创建!
 * Created by Hykilpikonna on 2018/07/21!
 * Github: https://github.com/hykilpikonna
 * Meow!
 *
 * @author Hykilpikonna
 */
public class EntriesJsonWriter
{
    public static String writeJsonFile(JsonElement element, String file) throws IOException
    {
        File fileObject = new File(file);
        fileObject.getParentFile().mkdirs();

        PrintWriter writer = new PrintWriter(file);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String result = gson.toJson(element);
        writer.println(result);
        writer.close();
        return result;
    }

    public static void writeAll(String dir) throws IOException
    {
        writeJsonFile(entriesToJson(new EntryResolver(ImageMdDownloader.getCombinedEntries(), SoundMdDownloader.getCombinedEntries())), dir + "assets.json");
    }
}
