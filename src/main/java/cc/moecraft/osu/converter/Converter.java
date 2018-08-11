package cc.moecraft.osu.converter;

import cc.moecraft.osu.generator.shared.EntryBase;
import cc.moecraft.osu.generator.shared.EntryResolver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * 此类由 Hykilpikonna 在 2018/07/21 创建!
 * Created by Hykilpikonna on 2018/07/21!
 * Github: https://github.com/hykilpikonna
 * Meow!
 *
 * @author Hykilpikonna
 */
public class Converter
{
    public static void convert(EntryResolver entryResolver, String originalSkinDir, String toDir)
    {
        File dir = new File(originalSkinDir);
        toDir = new File(toDir).getAbsolutePath() + File.separator;

        for (File file : Objects.requireNonNull(dir.listFiles()))
        {
            try
            {
                processOneFile(entryResolver, toDir, file);
            }
            catch (IOException e)
            {
                System.out.println("IOException during file: " + file.getPath());
                e.printStackTrace();
            }
        }
    }

    private static void processOneFile(EntryResolver entryResolver, String toDir, File file) throws IOException
    {
        String fileName = file.getName();

        if (fileName.endsWith(".ini"))
        {
            copyTo(file, toDir, "Config");
            return;
        }

        try
        {
            EntryBase entry = entryResolver.getEntry(fileName);

            if (fileName.endsWith(".png") || fileName.endsWith(".jpg"))
            {
                boolean allTransparent = safeIsAllTransparent(file);

                if (allTransparent)
                {
                    createDisabled(file, toDir, entry.getDir()); // TODO: 带着图片内容创建
                    return;
                }
            }

            copyTo(file, toDir, entry.getDir());
        }
        catch (EntryResolver.EntryNotFoundException e)
        {
            System.out.println(String.format("File %s can't be found in the file list, moving it to the Unknown directory.", file.getPath()));
            copyTo(file, toDir, "Unknown");
        }
    }

    private static boolean safeIsAllTransparent(File file)
    {
        try
        {
            return ImageReader.isAllTransparent(file);
        }
        catch (InterruptedException | IOException e)
        {
            System.out.println("Error trying to read pixels of image: " + file.getPath());
            System.out.println("Leaving it as provided.");
            return false;
        }
    }

    private static void createDisabled(File file, String toDir, String dir) throws IOException
    {
        File disabledFile = new File(toDir + dir + file.getName() + ".disabled");
        disabledFile.getParentFile().mkdirs();
        disabledFile.createNewFile();
    }

    private static void copyTo(File file, String toDir, String dir) throws IOException
    {
        File finalPath = new File(toDir + File.separator + dir + File.separator + file.getName());
        finalPath.getParentFile().mkdirs();
        Files.copy(file.toPath(), finalPath.toPath());
    }
}
