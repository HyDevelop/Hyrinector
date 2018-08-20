package cc.moecraft.osu.builder;

import lombok.AllArgsConstructor;
import lombok.Data;

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
@Data @AllArgsConstructor
public class BuilderProfileEdit
{
    private Operation operation;
    private String value;

    public static BuilderProfileEdit parse(String edit)
    {
        String operationName = edit.split(" ")[0];
        Operation operation = Operation.parse(operationName);
        return new BuilderProfileEdit(operation, edit.replaceFirst(operationName + " ", ""));
    }

    public enum Operation
    {
        IGNORE, INHERIT, DISABLE, COPY, MOVE;

        Operation()
        {
            Constants.nameIndex.put(name().toLowerCase(), this);
        }

        public static Operation parse(String name)
        {
            return Constants.nameIndex.get(name.toLowerCase());
        }

        static class Constants
        {
            private static Map<String, Operation> nameIndex = new HashMap<>();
        }
    }
}
