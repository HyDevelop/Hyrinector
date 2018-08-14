package cc.moecraft.osu.builder;

import cc.moecraft.osu.utils.ResourceUtils;
import cc.moecraft.osu.utils.SkinIniReader;
import cc.moecraft.utils.TimeUtils;
import lombok.Getter;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.rauschig.jarchivelib.ArchiveFormat;
import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

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
    public static void build(File fromDir, File toDir, BuilderProfileConfig profileConfig) throws IOException
    {
        ResourceUtils.initCache();
        File cacheDir = new File(ResourceUtils.getCacheDir(), "build-cache-" + System.currentTimeMillis() + "/");
        cacheDir.mkdirs();

        build(fromDir, cacheDir);
        System.out.println("== Done building, start checking. ==");
        Builder.check(cacheDir);
        System.out.println("== Done checking. ==");

        for (Map.Entry<String, BuilderProfile> entry : profileConfig.getBuilderProfiles().entrySet())
        {
            BuilderProfile profile = entry.getValue();

            System.out.println("== Start packaging for profile " + profile.getProfileName() + ". ==");
            File profileCacheDir = new File(ResourceUtils.getCacheDir(), "profile-cache-" + profile.getProfileName() + "-" + System.currentTimeMillis() + "/");

            profileCacheDir.mkdirs();
            FileUtils.copyDirectory(cacheDir, profileCacheDir);

            edit(profileCacheDir, profileConfig, profile.getProfileName());
            pack(profileCacheDir, toDir, profile);
        }
    }

    public static void build(File fromDir, File buildDir)
    {
        ResourceUtils.initCache();
        buildDir.mkdirs();

        File[] files = fromDir.listFiles();
        if (files == null) return;
        for (File file : files)
        {
            if (file.isDirectory())
            {
                System.out.println("------------ [ DIR ] ------------");
                System.out.println("- " + file.getAbsolutePath());
                System.out.println("------------ [ END ] ------------");
                build(file, buildDir);
            }
            else
            {
                String message = processFile(file, buildDir);
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

    public static String edit(File buildDir, BuilderProfileConfig profileConfig, String profileName)
    {
        ArrayList<BuilderProfileEdit> edits = profileConfig.getAllEdits(profileName);
        File emptyPixelFile = ResourceUtils.getCachedResourceFile("empty-pixel.png");

        for (BuilderProfileEdit edit : edits)
        {
            Pattern pattern = Pattern.compile(edit.getValue());
            File[] files = buildDir.listFiles();

            for (File file : Objects.requireNonNull(files))
                System.out.println(editFile(pattern, file, edit, emptyPixelFile));
        }

        return "Edit Success.";
    }

    public static String pack(File buildDir, File archiveDir)
    {
        return pack(buildDir, archiveDir, new BuilderProfile(
                "Default", Format.OSK, "%{name} - %{first-author}.osk", null));
    }

    public static String pack(File buildDir, File archiveDir, BuilderProfile profile)
    {
        File skinIni = new File(buildDir, "skin.ini");
        if (!skinIni.exists()) return "Failed to pack, skin.ini doesn't exist.";
        try
        {
            SkinIniReader.SkinIni ini = SkinIniReader.parse(skinIni);

            String fileName = profile.getFileName()
                    .replace("%{name}", ini.getName())
                    .replace("%{first-author}", ini.getAuthor());

            File zipFile = new File(archiveDir, fileName + ".zip");
            File oskFile = new File(archiveDir, fileName);

            Archiver archiver = ArchiverFactory.createArchiver(ArchiveFormat.ZIP);
            archiver.create(zipFile.getName(), buildDir.getParentFile(), buildDir.listFiles());
            FileUtils.moveFile(zipFile, oskFile);
            
            return "Pack success, OSK file: " + oskFile;
        }
        catch (IOException e)
        {
            return "Error reading the skin.ini file: " + e.getMessage();
        }
    }

    private static String editFile(Pattern pattern, File file, BuilderProfileEdit edit, File emptyPixelFile)
    {
        if (pattern.matcher(file.getName()).matches())
        {
            if (edit.getOperation() == BuilderProfileEdit.Operation.DISABLE)
            {
                try
                {
                    FileUtils.copyFile(Objects.requireNonNull(emptyPixelFile), file);
                    return "Profile edit: Disabled file " + file.getName();
                }
                catch (IOException e)
                {
                    return "Failed to disable " + file.getName() + " : " + e.getMessage();
                }
            }
            else if (edit.getOperation() == BuilderProfileEdit.Operation.IGNORE)
            {
                if (file.delete()) return "Profile edit: Ignored file " + file.getName();
                else return "Failed to ignore file " + file.getName();
            }
            else throw new RuntimeException("Error: Program Error 0xb0172 Please report to me@hydev.org");
        }
        else return null;
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
                File smallImg = new File(file.getParentFile(), rawName.replace("@2x", "") + "." + extension.toLowerCase());
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
            boolean parent = false;
            if (operationType == OperationType.COPY)
            {
                operationType = OperationType.parse(file.getParentFile().getName());
                parent = true;
            }
            switch (operationType)
            {
                case USE_DEFAULT: case DO_NOTHING:
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
                    if (!parent) FileUtils.copyFile(Objects.requireNonNull(emptyPixelFile),
                            new File(toDir, FilenameUtils.removeExtension(file.getName())));
                    else FileUtils.copyFile(Objects.requireNonNull(emptyPixelFile),
                            new File(toDir, file.getName()));
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
        DO_NOTHING("psd", "pxr"),
        COPY,
        USE_DEFAULT("default", "usedefault"),
        DISABLED("disabled", "disable");

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

    enum Format
    {
        OSK
    }
}
