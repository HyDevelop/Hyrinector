package cc.moecraft.osu.generator.images;

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
public class ImageMdReader
{
    public static ArrayList<ImageEntry> readEntries(BufferedReader reader) throws IOException
    {
        ArrayList<ImageEntry> entries = new ArrayList<>();

        String currentH1 = null;
        String currentH2 = null;
        ImageEntry currentEntry = null;
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

            // 如果这一行是图链接 TODO: 获取绝对链接
            if (line.startsWith("![]("))
            {
                // 这要让写wiki的人背锅, noteSH居然没写文件名...
                if (line.contains("mania-noteSH.png")) currentEntry = getEntry("mania-noteSH.png", currentH1, currentH2);

                assert currentEntry != null;
                currentEntry.setLink(line.replace("![](", "").replace(")", ""));
            }

            // 如果这一行是版本信息
            if (line.matches("\\| .*? \\| .*? \\| .*? \\| .*? \\| .*? \\| .*? \\|"))
            {
                assert currentEntry != null;
                String[] info = line.split("\\|");

                currentEntry.setVersion(removePreSuf(info[1])); // 不是我写错了, 是它真的是从1开始的哇www
                currentEntry.setAnimatable(Boolean.valueOf(removePreSuf(info[2])));
                currentEntry.setBeatmapSkinnable(Boolean.valueOf(removePreSuf(info[3])));
                currentEntry.setBlendMode(removePreSuf(info[4]));
                currentEntry.setOrigin(removePreSuf(info[5]));
                currentEntry.setSuggestedSDSize(removePreSuf(info[6]));
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

    private static ImageEntry getEntry(String filename, String h1, String h2)
    {
        ImageEntry currentEntry = new ImageEntry();
        currentEntry.setFileName(filename);
        currentEntry.setH1(h1);
        currentEntry.setH2(h2);

        return currentEntry;
    }
}
