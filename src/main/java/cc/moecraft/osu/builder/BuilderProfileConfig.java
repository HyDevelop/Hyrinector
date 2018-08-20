package cc.moecraft.osu.builder;

import cc.moecraft.yaml.Config;
import lombok.Getter;
import sun.swing.BakedArrayList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 此类由 Hykilpikonna 在 2018/08/12 创建!
 * Created by Hykilpikonna on 2018/08/12!
 * Github: https://github.com/hykilpikonna
 * Meow!
 *
 * @author Hykilpikonna
 */
public class BuilderProfileConfig extends Config
{
    @Getter
    private Map<String, BuilderProfile> builderProfiles;

    public BuilderProfileConfig(String dir)
    {
        super(dir, "builder-profile", "yml", false, true, false);
    }

    @Override
    public void readConfig()
    {
        this.builderProfiles = new HashMap<>();

        for (String key : getKeys(""))
        {
            BuilderProfile profile = new BuilderProfile(key,
                    Builder.Format.valueOf(getString(key + ".Format")),
                    getString(key + ".FileName"), new ArrayList<>());

            for (String edit : getStringList(key + ".Edits"))
                profile.getEdits().add(BuilderProfileEdit.parse(edit));

            builderProfiles.put(key, profile);
        }
    }

    @Override
    public void writeDefaultConfig() {}

    /**
     * 递归获取包含继承的所有edit
     * @param profileName 配置名
     * @return 所有edit
     */
    public ArrayList<BuilderProfileEdit> getAllEdits(String profileName)
    {
        ArrayList<BuilderProfileEdit> edits = new ArrayList<>(builderProfiles.get(profileName).getEdits());

        for (BuilderProfileEdit edit : builderProfiles.get(profileName).getEdits())
        {
            if (edit.getOperation() == BuilderProfileEdit.Operation.INHERIT)
            {
                if (builderProfiles.containsKey(edit.getValue())) edits.addAll(getAllEdits(edit.getValue()));
                else throw new RuntimeException("INHERIT tag '" + edit.getValue() + "' from '" + profileName + "' does not exist.");
            }
        }

        return edits;
    }
}
