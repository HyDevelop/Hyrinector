package cc.moecraft.osu.builder;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;

/**
 * 此类由 Hykilpikonna 在 2018/08/12 创建!
 * Created by Hykilpikonna on 2018/08/12!
 * Github: https://github.com/hykilpikonna
 * Meow!
 *
 * @author Hykilpikonna
 */
@Data @AllArgsConstructor @lombok.Builder
public class BuilderProfile
{
    private String profileName;
    private Builder.Format format;
    private String fileName;
    private ArrayList<BuilderProfileEdit> edits;
}
