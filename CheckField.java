
public class CheckField {
    // Trueはは通れない場所
    public boolean[][] map;
    public Position curret = new Position(0, 0);
    private int height;
    private int width;

    public CheckField(Field field, Position initialPosition) {

        this.height = field.height;
        this.width = field.width;
        this.curret = initialPosition;

        this.map = new boolean[height][];
        for (int i = 0; i < height; i++) {
            map[i] = new boolean[width];
            for (int j = 0; j < width; j++) {
                map[i][j] = (field.masu[i][j] != Masu.NotBlack);
            }
        }
        map[initialPosition.i][initialPosition.j] = true;

    }

    public CheckField Move(int di, int dj) {
        if (curret.i + di < 0)
            return null;
        if (curret.j + dj < 0)
            return null;
        if (curret.i + di >= height)
            return null;
        if (curret.j + dj >= width)
            return null;
        if (map[curret.i + di][curret.j + dj])
            return null;
        CheckField newField = Copy();
        newField.map[curret.i + di][curret.j + dj] = true;
        newField.curret.i = curret.i + di;
        newField.curret.j = curret.j + dj;
        return newField;
    }

    public void Show() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                char c = map[i][j] ? '●' : '?';
                System.out.print(c + " ");
            }
            System.out.println();
        }
    }

    public Position GetLast() {
        int cnt = 0;
        Position pos = new Position(0, 0);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (!map[i][j]) {
                    cnt++;
                    pos.i = i;
                    pos.j = j;
                    if (cnt >= 2)
                        return null;
                }
            }
        }
        return pos;
    }

    private CheckField() {
    }

    public CheckField Copy() {
        CheckField c = new CheckField();
        c.map = new boolean[height][];
        c.height = this.height;
        c.width = this.width;
        c.curret = new Position(this.curret.i, this.curret.j);
        for (int i = 0; i < height; i++) {
            c.map[i] = new boolean[width];
            for (int j = 0; j < width; j++) {
                c.map[i][j] = this.map[i][j];
            }
        }
        return c;
    }
}
