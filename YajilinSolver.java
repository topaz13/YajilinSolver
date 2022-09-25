import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class YajilinSolver {

    public static boolean debug = false;

    public static void main(String[] args) {
        System.out.println("solve start");

        String url = "https://puzz.link/p?yajilin/10/10/b2241u1222e3131zf2031e1231u4011b";

        // OK
        url = "https://puzz.link/p?yajilin/6/6/c40c30f30f40f30a40e";
        url = "https://puzz.link/p?yajilin/6/6/c40h10b41i40a10h";
        url = "https://puzz.link/p?yajilin/6/6/g30f313131c101010c303030g";
        url = "https://puzz.link/p?yajilin/6/6/21m41a31f20g1111c";

        // TLE
        url = "https://puzz.link/p?yajilin/10/10/s11i11b12f1111e23b11e13c11c22e11i1121h11i11";
        url = "https://puzz.link/p?yajilin/10/10/40h21l43a23a41d11l40b23c12l40b22c13l20b21b41i";
        url = "https://puzz.link/p?yajilin/10/10/20f25zzp11a10n15f"; // TLE
        // 1815875248667 nanotime;
        url = "https://puzz.link/p?yajilin/10/10/j11f32k10p41h33q41p14c14a14b31";
        url = "https://puzz.link/p?yajilin/10/10/s11i11b12f1111e23b11e13c11c22e11i1121h11i11";

        URLPerser parser = new URLPerser();
        Field field = parser.CreateFieldFromURL(url);
        field.ShowMasu();
        YajilinSolver solver = new YajilinSolver();
        long t = System.nanoTime();
        List<Field> result = solver.Solve2(field);
        t = System.nanoTime() - t;
        System.out.println("Result : " + result.size());
        for (int i = 0; i < result.size(); i++) {
            Field f = result.get(i);
            f.ShowMasu();
        }
        System.out.println(t);
    }

    public List<Field> results = new ArrayList<>();

    public void Solve(Field field, List<Field> result) {
        Field black = field.Copy();
        black.Fill(Masu.Black);
        black.ShowMasu();
        black.FillFixedValue();
        black.ShowMasu();
        black.ShowTate();
        if (black.IsClear()) {
            result.add(black);
            results.add(black);
        } else if (black.HasAnswer()) {
            System.out.println("Has answer");
            Solve(black, result);
        } else {
            System.out.println("Not Has answer");
        }

        System.out.println("White");
        Field white = field.Copy();
        white.Fill(Masu.NotBlack);
        white.FillFixedValue();
        if (white.IsClear()) {
            result.add(white);
            results.add(white);
        } else if (white.HasAnswer()) {
            Solve(white, result);
        } else {
            return;
        }
    }

    private List<Field> Solve2(Field field) {
        System.out.println("Solve2");
        Field f = field.Copy();
        List<Field> results = new ArrayList<>();
        Stack<Field> stack = new Stack<>();
        stack.add(f);
        while (!stack.isEmpty()) {
            Field currentField = stack.pop();
            Field kuro = currentField.Copy();
            System.out.println(stack.size());
            kuro.Fill(Masu.Black);
            kuro.FillFixedValue();
            if (kuro.IsClear()) {
                results.add(kuro);
            } else if (kuro.HasAnswer()) {
                stack.push(kuro);
            }

            Field shiro = currentField.Copy();
            shiro.Fill(Masu.NotBlack);
            shiro.FillFixedValue();
            if (shiro.IsClear()) {
                results.add(shiro);
            } else if (shiro.HasAnswer()) {
                stack.push(shiro);
            }
        }
        return results;
    }
}
