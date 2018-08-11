package cc.moecraft.osu.generator.sounds;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 此类由 Hykilpikonna 在 2018/07/20 创建!
 * Created by Hykilpikonna on 2018/07/20!
 * Github: https://github.com/hykilpikonna
 * Meow!
 *
 * @author Hykilpikonna
 */
public class SoundMdReader
{
    public static ArrayList<SoundEntry> readEntries(BufferedReader reader) throws IOException
    {
        ArrayList<SoundEntry> entries = new ArrayList<>();

        String currentH1 = null;
        String currentH2 = null;
        SoundEntry currentEntry = null;
        String line;
        while ((line = reader.readLine()) != null)
        {
            // 如果这一行是H1
            if (line.startsWith("# "))
            {
                currentH1 = line.replace("# ", "");
                currentH2 = null;
                continue;
            }

            // 如果这一行是H2
            if (line.startsWith("## "))
            {
                if (currentEntry != null) // H2有可能是上一个的结束
                {
                    entries.add(currentEntry);
                    currentEntry = null;
                }

                currentH2 = line.replace("## ", "");
                continue;
            }

            // 如果这一行是FileName
            if (line.matches("`.*?\\..*?`"))
            {
                currentEntry = getEntry(removePreSuf(line), currentH1, currentH2);
            }

            // 如果这一行是版本信息
            if (line.matches("\\| .*? \\|"))
            {
                assert currentEntry != null;
                String[] info = line.split("\\|");
                currentEntry.setBeatmapSkinnable(Boolean.valueOf(removePreSuf(info[1])));
            }

            // 如果这一行是分隔符
            if (line.startsWith("---"))
            {
                if (currentEntry != null)
                {
                    entries.add(currentEntry);
                    currentEntry = null;
                }
                else System.out.println("Entry ends but it is null at: " + (entries.size() == 0 ? "first entry" : entries.get(entries.size() - 1)));
            }
        }

        return entries;
    }

    public static String removeCharAt(String original, int index)
    {
        return original.substring(0, index) + original.substring(index + 1);
    }

    public static String removePreSuf(String original)
    {
        String result = removeCharAt(original, 0);
        return removeCharAt(result, result.length() - 1);
    }

    private static SoundEntry getEntry(String filename, String h1, String h2)
    {
        SoundEntry currentEntry = new SoundEntry();
        currentEntry.setFileName(filename);
        currentEntry.setH1(h1);
        currentEntry.setH2(h2);

        return currentEntry;
    }
}
