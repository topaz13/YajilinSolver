import java.util.Stack;

public class CheckField {
    // Trueはは通れない場所
    public boolean[][] map;
    public Position curret = new Position(0, 0);
    private int height;
    private int width;
    private Field field;

    public CheckField(Field field, Position initialPosition) {
        this.field = field;

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
        System.out.println("-----");
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                char c = map[i][j] ? '●' : '?';
                System.out.print(c + " ");
            }
            System.out.println();
        }
    }

    int[] di = new int[] { -1, 0, 1, 0 };
    int[] dj = new int[] { 0, -1, 0, 1 };

    public Position GetLast() {
        int cnt = 0;
        Position pos = new Position(0, 0);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (!map[i][j]) {
                    cnt++;
                    pos.i = i;
                    pos.j = j;
                    boolean rinsetsu = false;
                    for (int k = 0; k < di.length; k++) {
                        if (pos.i == curret.i + di[k] && pos.j == curret.j + dj[k]) {
                            rinsetsu = true;
                        }
                    }
                    // 最後のマスにたどり着けなかった場合nullを返す。
                    if (!rinsetsu)
                        return null;
                    // 二つ以上埋まっていない場合nullを返す。
                    if (cnt >= 2)
                        return null;
                }
            }
        }
        return pos;
    }

    // 孤立島が二つあるかどうか
    public boolean IsDiscrete() {
        // 縦で見る
        boolean existUnrearchable = false;
        boolean kabeari = false;
        for (int i = 0; i < height - 1; i++) {
            int blackCount = 0;
            for (int j = 0; j < width; j++) {
                if (!map[i][j]) {
                    existUnrearchable = true;
                    if (kabeari) {
                        if (YajilinSolver.debug) {
                            System.out.println("縦で隔てられています");
                            Show();
                            System.out.println("縦で隔てられています");
                        }
                        return true;
                    }
                } else if (existUnrearchable) {
                    blackCount++;
                    if (blackCount == width) {
                        kabeari = true;
                        break;
                    }
                }
            }
        }
        // 横で見る
        kabeari = false;
        existUnrearchable = false;
        for (int j = 0; j < width - 1; j++) {
            int blackCount = 0;
            for (int i = 0; i < height; i++) {
                if (!map[i][j]) {
                    existUnrearchable = true;
                    if (kabeari) {
                        if (YajilinSolver.debug) {
                            System.out.println("横で隔てられています");
                            Show();
                            System.out.println("横で隔てられています");
                        }
                        return true;
                    }
                } else if (existUnrearchable) {
                    blackCount++;
                    if (blackCount == height) {
                        kabeari = true;
                        break;
                    }
                }
            }
        }

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                // 離れ島探し
                if (!map[i][j]) {
                    int cnt = 0;
                    for (int k = 0; k < di.length; k++) {
                        if (IsInArae(i, j, k)) {
                            if (map[i + di[k]][j + dj[k]]) {
                                cnt++;
                            }
                        } else {
                            cnt++;
                        }

                    }
                    if (cnt == 4) {
                        if (YajilinSolver.debug) {
                            System.out.println("周りが囲まれています");
                            Show();
                            System.out.println("周りが囲まれています");
                        }
                        // return true;
                    }
                }
            }
        }

        Stack<Position> passed = new Stack<>();
        boolean[][] newMap = new boolean[height][];
        boolean check = false;
        for (int i = 0; i < height; i++) {
            newMap[i] = new boolean[width];
        }
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (!map[i][j] && !newMap[i][j]) {

                    if (check) {
                        if (!newMap[i][j]) {
                            if (YajilinSolver.debug) {
                                System.out.println("変な形の離れ島があります");
                                Show();
                                System.out.println("変な形の離れ島があります");
                            }
                            return true;
                        } else {
                            continue;
                        }
                    }
                    check = true;

                    passed.add(new Position(i, j));
                    newMap[i][j] = true;
                    // System.out.println("開始します");
                    while (!passed.isEmpty()) {
                        Position pos = passed.pop();
                        for (int k = 0; k < di.length; k++) {
                            // nextposi
                            int npi = pos.i + di[k];
                            int npj = pos.j + dj[k];
                            try {
                                if (IsInArae(pos.i, pos.j, k) && !map[npi][npj] && !newMap[npi][npj]) {
                                    passed.add(new Position(npi, npj));
                                    newMap[npi][npj] = true;
                                }
                            } catch (Exception e) {
                                System.out.println(e.getMessage());
                                System.out.println(npi);
                                System.out.println(npj);
                                int aaaa = 0;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean IsInArae(int i, int j, int index) {
        if (i + di[index] < 0)
            return false;
        if (i + di[index] >= height)
            return false;
        if (j + dj[index] < 0)
            return false;
        if (j + dj[index] >= width)
            return false;
        return true;
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
