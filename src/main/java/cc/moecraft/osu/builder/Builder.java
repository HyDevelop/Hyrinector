package cc.moecraft.osu.builder;

import cc.moecraft.osu.utils.ResourceUtils;
import lombok.Getter;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 此类由 Hykilpikonna 在 2018/07/21 创建!
 * Created by Hykilpikonna on 2018/07/21!
 * Github: https://github.com/hykilpikonna
 * Meow!
 *
 * @author Hykilpikonna
 */
public class Builder
{
    public static void build(File fromDir, File toDir)
    {
        ResourceUtils.initCache();
        if (toDir.exists()) toDir.delete();
        toDir.mkdirs();

        File[] files = fromDir.listFiles();
        if (files == null) return;
        for (File file : files)
        {
            if (file.isDirectory())
            {
                System.out.println("------------ [ DIR ] ------------");
                System.out.println("- " + file.getAbsolutePath());
                System.out.println("------------ [ END ] ------------");
                build(file, toDir);
            }
            else
            {
                String message = processFile(file, toDir);
                if (!message.isEmpty()) System.out.println(message);
            }
        }
    }

    public static void check(File buildDir)
    {
        File[] files = buildDir.listFiles();
        if (files == null) return;
        for (File file : files)
        {
            if (file.isDirectory())
            {
                System.out.println("-------------- [ ERROR ] --------------");
                System.out.println("- Build dir should not have sub dirs  -");
                System.out.println("-------------- [  END  ] --------------");
                // check(file);
            }
            else
            {
                String message = checkFile(file);
                if (message != null && !message.isEmpty()) System.out.println(message);
            }
        }
    }

    private static String checkFile(File file)
    {
        try
        {
            String rawName = FilenameUtils.removeExtension(file.getName());
            String extension = FilenameUtils.getExtension(file.getName());
            if (rawName.endsWith("@2x"))
            {
                // Check if 1x exists, if not, generate it
                // 检查1x图是否存在, 如果不存在, 反向生成
                File smallImg = new File(file.getParentFile(), rawName.replace("@2x", "") + extension);
                if (!smallImg.exists()) return generate1x(file, smallImg);
            }
            return null;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return "文件 " + file + " 处理失败: " + e.getMessage();
        }
    }

    private static String processFile(File file, File toDir)
    {
        try
        {
            OperationType operationType = OperationType.parse(FilenameUtils.getExtension(file.getName()));
            if (operationType == OperationType.COPY) operationType = OperationType.parse(file.getParentFile().getName());
            switch (operationType)
            {
                case USE_DEFAULT:
                {
                    // Do nothing, so that osu would use the default resource.
                    // 不复制文件, 使用默认资源
                    return "已忽略: " + file.getName();
                }
                case DISABLED:
                {
                    // Generate a 1x1 transparent pixel, so that the resource won't show up.
                    // 生成1x1的透明图, 让资源不显示
                    File emptyPixelFile = ResourceUtils.getCachedResourceFile("empty-pixel.png");
                    FileUtils.copyFile(Objects.requireNonNull(emptyPixelFile),
                            new File(toDir, FilenameUtils.removeExtension(file.getName())));
                    return "已改为透明: " + file.getName();
                }
                case COPY:
                {
                    // Directly copy the resource without processing.
                    // 其他所有资源, 直接复制
                    FileUtils.copyFile(file, new File(toDir, file.getName()));
                    return "已复制: " + file.getName();
                }
                default: return null;
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return "文件 " + file + " 处理失败: " + e.getMessage();
        }
    }

    private static String generate1x(File largeImg, File smallImg) throws IOException
    {
        if (!largeImg.exists()) return "尝试从@2x版本生成" + smallImg + "失败: @2x文件" + largeImg + "不存在";
        Thumbnails.of(largeImg).scale(0.5f).toFile(smallImg);
        return "已生成@1x: " + smallImg.getName();
    }

    private enum OperationType
    {
        COPY,
        USE_DEFAULT("default", "usedefault"),
        DISABLED("disabled", "disable"),
        SMALLER("1x", "smaller");

        @Getter
        private final String[] names;

        OperationType(String ... names)
        {
            this.names = names;
            for (String name : names) Constants.nameIndex.put(name, this);
        }

        public static OperationType parse(String name)
        {
            return Constants.nameIndex.getOrDefault(name, COPY);
        }

        private static class Constants
        {
            private static final Map<String, OperationType> nameIndex = new HashMap<>();
        }
    }
}
