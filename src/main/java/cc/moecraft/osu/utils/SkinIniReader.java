package cc.moecraft.osu.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * 此类由 Hykilpikonna 在 2018/08/12 创建!
 * Created by Hykilpikonna on 2018/08/12!
 * Github: https://github.com/hykilpikonna
 * Meow!
 *
 * @author Hykilpikonna
 */
public class SkinIniReader
{
    public static SkinIni parse(File iniFile) throws IOException
    {
        BufferedReader reader = new BufferedReader(new FileReader(iniFile));
        String name = null;
        String author = null;

        String line;
        while ((line = reader.readLine()) != null)
        {
            if (line.startsWith("Name: ")) name = line.replaceFirst("Name: ", "");
            if (line.startsWith("Author: ")) author = line.replaceFirst("Author: ", "");
        }

        name = name == null ? "Untitled" : name;
        author = author == null ? System.getProperty("user.name") : author;

        if (author.contains(", "))
            author = author.split(", ")[0];
        if (author.contains(" & "))
            author = author.split(" & ")[0];

        return new SkinIni(name, author);
    }

    @AllArgsConstructor @Data
    public static class SkinIni
    {
        private final String name;
        private final String author;
    }
}
