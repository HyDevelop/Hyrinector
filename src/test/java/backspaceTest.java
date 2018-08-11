/**
 * 此类由 Hykilpikonna 在 2018/07/21 创建!
 * Created by Hykilpikonna on 2018/07/21!
 * Github: https://github.com/hykilpikonna
 * Meow!
 *
 * @author Hykilpikonna
 */
public class backspaceTest
{
    public static void main(String[] args) throws InterruptedException
    {
        System.out.println("Counting down ...");
        System.out.print("     5");
        Thread.sleep(500);
        System.out.print("\r    4");
        Thread.sleep(500);
        System.out.print("\r   3");
        Thread.sleep(500);
        System.out.print("\r  2");
        Thread.sleep(500);
        System.out.print("\r 1");
        Thread.sleep(500);
        System.out.println("\rDone!");
    }
}
