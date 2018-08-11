package cc.moecraft.osu.generator;

import cc.moecraft.osu.generator.shared.EntryResolver;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

/**
 * 此类由 Hykilpikonna 在 2018/07/21 创建!
 * Created by Hykilpikonna on 2018/07/21!
 * Github: https://github.com/hykilpikonna
 * Meow!
 *
 * @author Hykilpikonna
 */
public class EntriesJsonGenerator
{
    public static JsonElement entriesToJson(EntryResolver entries)
    {
        return new Gson().toJsonTree(entries);
    }
}
