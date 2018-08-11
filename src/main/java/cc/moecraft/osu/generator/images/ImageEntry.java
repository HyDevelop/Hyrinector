package cc.moecraft.osu.generator.images;

import cc.moecraft.osu.generator.shared.EntryBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 此类由 Hykilpikonna 在 2018/07/20 创建!
 * Created by Hykilpikonna on 2018/07/20!
 * Github: https://github.com/hykilpikonna
 * Meow!
 *
 * @author Hykilpikonna
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ImageEntry extends EntryBase
{
    private String link;
    private String version;
    private Boolean animatable;
    private String blendMode;
    private String origin;
    private String suggestedSDSize;
}
