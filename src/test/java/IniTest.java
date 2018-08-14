import cc.moecraft.osu.utils.SkinIniReader;

import java.io.File;
import java.io.IOException;

/**
 * 此类由 Hykilpikonna 在 2018/08/12 创建!
 * Created by Hykilpikonna on 2018/08/12!
 * Github: https://github.com/hykilpikonna
 * Meow!
 *
 * @author Hykilpikonna
 */
public class IniTest
{
    public static void main(String[] args) throws IOException
    {
        SkinIniReader.SkinIni ini = SkinIniReader.parse(new File("C:\\Games\\osu!\\Skins\\Abyssal 2018-15-06\\skin.ini"));
        System.out.println(ini);
    }
}
