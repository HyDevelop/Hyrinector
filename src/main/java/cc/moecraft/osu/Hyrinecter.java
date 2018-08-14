package cc.moecraft.osu;

import cc.moecraft.osu.builder.Builder;
import cc.moecraft.osu.builder.BuilderProfile;
import cc.moecraft.osu.builder.BuilderProfileConfig;
import cc.moecraft.osu.converter.Converter;
import cc.moecraft.osu.generator.EntriesJsonWriter;
import cc.moecraft.osu.generator.shared.EntryResolver;
import com.xiaoleilu.hutool.http.HttpUtil;
import com.xiaoleilu.hutool.io.FileUtil;
import com.xiaoleilu.hutool.io.StreamProgress;
import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 此类由 Hykilpikonna 在 2018/07/20 创建!
 * Created by Hykilpikonna on 2018/07/20!
 * Github: https://github.com/hykilpikonna
 * Meow!
 *
 * @author Hykilpikonna
 */
public class Hyrinecter
{
    private static ArrayList<String> operations;
    private static Map<String, String> options;

    public static void main(String[] args) throws IOException
    {
        if (args.length == 0) args = new String[]{"help"};

        operations = getOperations(args);
        options = getOptions(args);

        if (operations.size() == 0)
        {
            printHelp();
            return;
        }

        switch (operations.get(0))
        {
            case "download":
            {
                if (operations.size() == 1)
                {
                    System.out.println("download [json|default]");
                    return;
                }

                switch (operations.get(1))
                {
                    case "json":
                    {
                        operationDownloadJson();
                        break;
                    }
                    case "default":
                    {
                        operationDownloadDefault();
                        break;
                    }
                    default: // Help
                    {
                        printHelp();
                        break;
                    }
                }
                break;
            }
            case "extract":
            {
                operationExtract();
                break;
            }
            case "convert":
            {
                operationConvert();
                break;
            }
            case "build":
            {
                System.out.println(operationBuild());
                break;
            }
            default: // Help
            {
                printHelp();
                break;
            }
        }
    }

    private static String operationBuild()
    {
        if (!options.containsKey("fp")) return "Error: Project folder not specified, specify it with -fp=<path> option.";
        File projectPath = new File(options.get("fp"));
        File toPath = new File(options.getOrDefault("cp", new File(projectPath.getParentFile(), "build/").getAbsolutePath()));

        File builderProfileFile = new File("builder-profile.yml");
        if (builderProfileFile.exists())
        {
            BuilderProfileConfig profileConfig = new BuilderProfileConfig("./");
            try
            {
                Builder.build(projectPath, toPath, profileConfig);
            }
            catch (IOException e)
            {
                e.printStackTrace();
                return "Error: Build failed: " + e.getMessage();
            }
        }
        else
        {
            Builder.build(projectPath, toPath);

            System.out.println("== Done building, start checking. ==");

            Builder.check(toPath);

            System.out.println("== Done checking, start packing.  ==");

            System.out.println(Builder.pack(toPath, toPath.getParentFile()));
        }

        return "Build Success. Target path: " + toPath;
    }

    private static void operationConvert() throws IOException
    {
        if (!options.containsKey("fp"))
        {
            System.out.println("Error: Original skin file/folder path is not specified.");
            System.out.println("Specify it with -fp=<path> parameter.");
            return;
        }

        String downloadPath = getDp();
        String originalPath = getValue("fp", null);
        String convertPath = getValue("cp", "./convert/");
        boolean fillMissing = Boolean.parseBoolean(getValue("fm", "true"));

        File downloadDir = new File(downloadPath);
        if (!downloadDir.exists()) downloadDir.mkdirs();

        if (new File(convertPath).exists())
        {
            System.out.println("Target folder " + convertPath + " exists, please delete the already existing one or choose another location by specifying -cp=<path> parameter.");
            return;
        }

        File jsonFile = new File(downloadDir.getPath() + File.separator + "assets.json");
        File resource = new File(downloadDir.getPath() + File.separator + "default-assets");

        if (!jsonFile.exists())
        {
            System.out.println("No assets.json is detected in cache folder, downloading to cache...");
            operationDownloadJson();
        }

        File[] resourceFiles = resource.listFiles();
        if (resourceFiles == null || resourceFiles.length < 480)
        {
            System.out.println("No default assets is detected in cache folder, extracting to cache...");
            operationExtract();
        }

        setTime();
        System.out.println("Starting to convert...");

        EntryResolver entryResolver = EntryResolver.initializeFromFile(jsonFile);
        Converter.convert(entryResolver, originalPath, convertPath);

        finishTime();
    }

    /**
     * 检查一个路径下是否存在一个文件
     * @param dir 路径
     * @param fileName 文件
     * @return 是否存在
     */
    private static boolean containsFileInDir(File dir, String fileName)
    {
        File[] files = dir.listFiles();

        if (files == null || files.length == 0) return false;
        for (File file : files)
        {
            if (file.getName().equals(fileName)) return true;
        }
        return false;
    }

    private static void operationExtract() throws IOException
    {
        setTime();
        System.out.println("Starting to copy file...");

        String path = getDp();
        File zip = new File(path + "defaults.zip");
        zip.getParentFile().mkdirs();
        Files.copy(Hyrinecter.class.getResourceAsStream("/osu-default-skin.tar.gz"),
                zip.toPath(), StandardCopyOption.REPLACE_EXISTING);

        finishTime();
        setTime();
        System.out.println("Starting to extract copied file...");

        File destDir = new File(path + "/default-assets");
        Archiver archiver = ArchiverFactory.createArchiver("tar", "gz");
        archiver.extract(zip, destDir);

        finishTime();
        setTime();
        System.out.println("Deleting unnecessary files...");

        if (!zip.delete())
        {
            System.out.println("Failed to delete file, setting it to delete on next launch.");
            zip.deleteOnExit();
        }

        finishTime();
    }

    private static void operationDownloadJson() throws IOException
    {
        setTime();
        System.out.println("Starting download...");

        String path = getDp();
        EntriesJsonWriter.writeAll(path);

        finishTime();
    }

    private static String getDp()
    {
        return getValue("dp", "./.cache/");
    }

    private static String getValue(String key, String defaultValue)
    {
        return options.getOrDefault(key, defaultValue);
    }

    @Deprecated
    private static void operationDownloadDefault()
    {
        System.out.println("Note: This operation is deprecated since the osu default skin pack is already packed in to the jar file.");

        File path = new File(options.getOrDefault("dp", "./.cache/") + "osu-default-skin.zip");
        path.getParentFile().mkdirs();

        HttpUtil.downloadFile("https://raw.githubusercontent.com/HyDEV-Plugins/File-Sharing/master/Osu/osu-default-skin.zip", path,
                new StreamProgress()
                {
                    @Override
                    public void start()
                    {
                        setTime();
                        System.out.println("Starting download...");
                        System.out.print("... Waiting ...");
                    }

                    @Override
                    public void progress(long progressSize)
                    {
                        String message =
                                "\r... Progress: " +
                                        FileUtil.readableFileSize(progressSize) +
                                        " ... Speed: " +
                                        FileUtil.readableFileSize(progressSize / (System.currentTimeMillis() - startingTime)) +
                                        "/s.";
                        System.out.print(message);
                    }

                    @Override
                    public void finish()
                    {
                        System.out.println();
                        finishTime();
                    }
                });
    }

    private static long startingTime;

    private static void setTime()
    {
        startingTime = System.currentTimeMillis();
    }

    private static void finishTime()
    {
        System.out.println("Finished! It took " +                     // 转换到秒
                (Math.round((System.currentTimeMillis() - startingTime) / 1000d * 100d) / 100d) + " seconds.");
    }

    private static void printHelp()
    {
        printResource("help.txt", "jarfile.name", getJarfileName());
    }

    private static ArrayList<String> getOperations(String[] args)
    {
        ArrayList<String> result = new ArrayList<>();

        for (String arg : args)
        {
            if (arg.startsWith("-")) break;

            result.add(arg);
        }

        return result;
    }

    private static final Pattern patternToFindOptions = Pattern.compile("(?<=-).*?(?==)");

    private static Map<String, String> getOptions(String[] args)
    {
        Map<String, String> result = new HashMap<>();

        for (String arg : args)
        {
            if (arg.startsWith("-"))
            {
                Matcher matcher = patternToFindOptions.matcher(arg);
                matcher.find();
                result.put(matcher.group(), arg.replaceFirst("-.*?=", ""));
            }
        }

        return result;
    }

    private static void printResource(String name, String... variablesAndReplacements)
    {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(Hyrinecter.class.getClassLoader().getResourceAsStream(name))))
        {
            String line;
            while ((line = br.readLine()) != null)
            {
                for (int i = 0; i < variablesAndReplacements.length; i += 2)
                {
                    line = line.replace("%{" + variablesAndReplacements[i] + "}", variablesAndReplacements[i + 1]);
                }

                System.out.println(line);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static String getJarfileName()
    {
        return new java.io.File(Hyrinecter.class.getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .getPath())
                .getName();
    }
}
