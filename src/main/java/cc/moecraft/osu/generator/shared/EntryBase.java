package cc.moecraft.osu.generator.shared;

import lombok.Data;

import java.io.File;

/**
 * 此类由 Hykilpikonna 在 2018/07/21 创建!
 * Created by Hykilpikonna on 2018/07/21!
 * Github: https://github.com/hykilpikonna
 * Meow!
 *
 * @author Hykilpikonna
 */
@Data
public abstract class EntryBase
{
    private String h1;
    private String h2;
    private String fileName;
    private Boolean beatmapSkinnable;

    public String getDir()
    {
        return "." + File.separator + h1 + File.separator + h2 + File.separator;
    }
}
