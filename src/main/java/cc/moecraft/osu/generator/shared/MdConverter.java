package cc.moecraft.osu.generator.shared;

/**
 * 此类由 Hykilpikonna 在 2018/07/21 创建!
 * Created by Hykilpikonna on 2018/07/21!
 * Github: https://github.com/hykilpikonna
 * Meow!
 *
 * @author Hykilpikonna
 */
public class MdConverter
{
    public static String convert(String original)
    {
        original += "\n\n----";

        original = original
                .replace("[true]: /wiki/shared/true.png", "")
                .replace("[false]: /wiki/shared/false.png", "")
                .replace("| Versions | Animatable | Beatmap Skinnable | Blend Mode | Origin | Suggested SD Size |", "")
                .replace("|:-:|:-:|:-:|:-:|:-:|:-:|", "")
                .replace("| Beatmap Skinnable |", "")
                .replace("|:-----------------:|", "")
                .replace("_`", "`") // 这两个是sounds下的taiko-drum-hitclap.wav
                .replace("`_", "`")
                .replace("![No][false]     ", "false")
                .replace("![No][false]", "false")
                .replace("false (see notes)", "false")
                .replace("![Yes][true]     ", "true")
                .replace("![Yes][true]", "true")
                .replace("true (see notes)", "true");

        original = original.replaceAll("\"Notes:.*?\\n\\n.*?\\n\\n\"", "");

        return original;
    }
}
