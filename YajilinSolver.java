import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class YajilinSolver {
    public static void main(String[] args) {
        System.out.println("solve start");
        Field field = new Field(6, 6);
        field.Initialize();
        field.ShowMasu();
        field.FillFixedValue();
        YajilinSolver solver = new YajilinSolver();
        List<Field> result = solver.Solve2(field);
        System.out.println("show result");
        for (Field testField : result) {
            System.out.println(testField);
        }
        System.out.println("Result : " + result.size());
        for (int i = 0; i < result.size(); i++) {
            Field f = result.get(i);
            f.ShowMasu();
            if (i == 7) {
                f.ShowTate();
                f.ShowYoko();

            }
        }
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
        // return;
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
