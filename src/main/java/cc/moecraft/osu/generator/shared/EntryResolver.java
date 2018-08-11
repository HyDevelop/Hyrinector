package cc.moecraft.osu.generator.shared;

import cc.moecraft.osu.generator.images.ImageEntry;
import cc.moecraft.osu.generator.images.ImageMdDownloader;
import cc.moecraft.osu.generator.shared.EntryBase;
import cc.moecraft.osu.generator.sounds.SoundEntry;
import cc.moecraft.osu.generator.sounds.SoundMdDownloader;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * 此类由 Hykilpikonna 在 2018/07/21 创建!
 * Created by Hykilpikonna on 2018/07/21!
 * Github: https://github.com/hykilpikonna
 * Meow!
 *
 * @author Hykilpikonna
 */
@AllArgsConstructor
public class EntryResolver
{
    private ArrayList<ImageEntry> imageEntries;
    private ArrayList<SoundEntry> soundEntries;

    public static EntryResolver initializeFromFile(File jsonFile) throws FileNotFoundException
    {
        return new Gson().fromJson(new JsonReader(new FileReader(jsonFile)), EntryResolver.class);
    }

    public static EntryResolver initializeFromInternet()
    {
        return new EntryResolver(ImageMdDownloader.getCombinedEntries(), SoundMdDownloader.getCombinedEntries());
    }

    public EntryBase getEntry(String fileName) throws EntryNotFoundException
    {
        fileName = fileName.replace("@2x", "");

        for (ImageEntry imageEntry : imageEntries)
            if (imageEntry.getFileName().equals(fileName)) return imageEntry;

        for (SoundEntry soundEntry : soundEntries)
            if (soundEntry.getFileName().equals(fileName)) return soundEntry;

        throw new EntryNotFoundException(fileName);
    }

    @EqualsAndHashCode(callSuper = true)
    @Data @AllArgsConstructor
    public static class EntryNotFoundException extends Exception
    {
        private String fileName;
    }
}
