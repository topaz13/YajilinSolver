import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

public class Field {
    public Masu[][] masu;
    public Arrow[][] arrows;

    // 縦向きの壁
    public Wall[][] tateWalls;
    // 横向きの壁
    public Wall[][] yokoWalls;

    public int height;
    public int width;

    private boolean IsUnsolved = false;

    // ここでデータを受け取る。
    public Field(int height, int width) {
        this.height = height;
        this.width = width;
        masu = new Masu[height][];
        arrows = new Arrow[height][];
        for (int i = 0; i < height; i++) {
            masu[i] = new Masu[width];
            arrows[i] = new Arrow[width];
            for (int j = 0; j < width; j++) {
                masu[i][j] = Masu.UnDefined;
            }
        }
        tateWalls = new Wall[height][];
        for (int i = 0; i < height; i++) {
            tateWalls[i] = new Wall[width + 1];
            for (int j = 0; j < width + 1; j++) {
                tateWalls[i][j] = Wall.UnDefined;
                if (j == 0 || j == width) {
                    tateWalls[i][j] = Wall.Exist;
                }
            }
        }

        yokoWalls = new Wall[height + 1][];
        for (int i = 0; i < height + 1; i++) {
            yokoWalls[i] = new Wall[width];
            for (int j = 0; j < width; j++) {
                yokoWalls[i][j] = Wall.UnDefined;
                if (i == 0 || i == height) {
                    yokoWalls[i][j] = Wall.Exist;
                }
            }
        }

    }

    public void Initialize() {
        // テスト
        // https://seesaawiki.jp/pencilpuzzle/d/%B2%F2%A4%AD%CA%FD%28%A5%E4%A5%B8%A5%EA%A5%F3%29
        FillMasu(Masu.Arrow, 0, 1);
        FillMasu(Masu.Arrow, 2, 2);
        FillMasu(Masu.Arrow, 4, 4);
        FillMasu(Masu.Arrow, 5, 2);
        arrows[0][1] = new Arrow(Direction.Right, 1);
        arrows[2][2] = new Arrow(Direction.Right, 2);
        arrows[4][4] = new Arrow(Direction.Left, 1);
        arrows[5][2] = new Arrow(Direction.Left, 0);
    }

    // ======================
    // public method
    // ======================

    /**
     * 自身の情報をコピーした新しいインスタンスを返す
     * 
     * @return
     */
    public Field Copy() {
        Field field = new Field(height, width);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                field.masu[i][j] = this.masu[i][j];
                if (field.masu[i][j] == Masu.Arrow)
                    field.arrows[i][j] = this.arrows[i][j];
            }
        }
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width + 1; j++) {
                field.tateWalls[i][j] = this.tateWalls[i][j];
            }
        }
        for (int i = 0; i < height + 1; i++) {
            for (int j = 0; j < width; j++) {
                field.yokoWalls[i][j] = this.yokoWalls[i][j];
            }
        }
        field.IsUnsolved = this.IsUnsolved;
        return field;
    }

    /**
     * 引数を空いているマスに埋める
     * 
     * @param masuType
     */
    public void Fill(Masu masuType) {
        if (!(masuType == Masu.Black || masuType == Masu.NotBlack)) {
            System.err.println("不正な入力です");
            return;
        }
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (masu[i][j] == Masu.UnDefined) {
                    FillMasu(masuType, i, j);
                    // System.out.println("(" + i + "," + j + ") : " + masuType);
                    return;
                }
            }
        }
    }

    /**
     * 現在の状態から確定させられる場所を埋める
     */
    public void FillFixedValue() {
        // TODO: 決まる場所を置く
        FillByArrows();
        FillByWall();
    }

    /**
     * クリアしてる探索
     * 
     * @return
     */
    public boolean IsClear() {
        // TODO: クリア判定
        // 全て埋まっている
        FillByWall();

        // 未確定マスが存在したらクリアではない。
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (masu[i][j] == Masu.UnDefined)
                    return false;
            }
        }
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {

                // 矢印の方向の数が合っているかどうか確認
                if (masu[i][j] == Masu.Arrow) {
                    if (!CheckCorrectArrow(i, j)) {
                        return false;
                    }
                }

                // 線のマスの場合は周りに壁が二つ
                if (masu[i][j] == Masu.NotBlack) {
                    // 線画繋がるマスの場合
                    MasuInfo info = GetMasuInfo(i, j);
                    if (info.wallCount > 2) {
                        return false;
                    }
                }
            }
        }
        // 最終チェック一筆書きできるかどうか。。
        // 重いからあまり呼びたくないかも
        return CanDrawOnePath();
        // return true;
    }

    /**
     * 答えとして完成させられるかを返す。
     * 
     * @return
     */
    public boolean HasAnswer() {
        int cnt = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (masu[i][j] == Masu.UnDefined)
                    cnt++;
            }
        }
        if (cnt == 0) {
            return false;
        }

        // 適当な場所から一筆書きできるか試す。
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (masu[i][j] == Masu.Arrow) {
                    if (!CheckCorrectArrow(i, j)) {
                        return false;
                    }
                }
            }
        }
        return !IsUnsolved;
    }

    public void ShowMasu() {
        System.out.println("SHOW MASU======-");
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                char c = 'a';
                switch (masu[i][j]) {
                    case UnDefined:
                        c = 'U';
                        break;
                    case Arrow:
                        c = 'A';
                        break;
                    case Black:
                        c = 'B';
                        break;
                    case NotBlack:
                        c = 'N';
                        break;
                }
                System.out.print(c + " ");
            }
            System.out.println();
        }
        System.out.println("SHOW MASU======-");
    }

    public void ShowTate() {
        System.out.println("===SHOW TATE===");
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width + 1; j++) {
                if (tateWalls[i][j] == Wall.Exist) {
                    System.out.print("| ");
                } else if (tateWalls[i][j] == Wall.NotExist) {
                    System.out.print("- ");
                } else {
                    System.out.print("* ");
                }
            }
            System.out.println();
        }
        System.out.println("===SHOW TATE===");
    }

    public void ShowYoko() {
        System.out.println("===SHOW YOKO===");
        for (int i = 0; i < height + 1; i++) {
            for (int j = 0; j < width; j++) {
                if (yokoWalls[i][j] == Wall.Exist) {
                    // 横棒
                    System.out.print("- ");
                } else if (yokoWalls[i][j] == Wall.NotExist) {
                    // 棒無し
                    System.out.print("| ");
                } else {
                    System.out.print("* ");
                }
            }
            System.out.println();
        }
        System.out.println("===SHOW TATE===");
    }

    // ==============================
    // PRIVATE METHOD
    // ==============================

    private void FillByArrows() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (masu[i][j] != Masu.Arrow)
                    continue;
                FillByArrow(i, j);
            }
        }
    }

    private void FillByArrow(int i, int j) {
        // System.out.println("Fill By Arrow (" + i + "," + j + ")");
        Arrow arrow = arrows[i][j];
        if (arrow.direction == Direction.Down) {
            for (int k = i + 1; k < height; k++) {
                // TODO:処理を行う。
                // Masu cell = masu[k][j];
            }
        }

        if (arrow.direction == Direction.Up) {
            for (int k = i - 1; k >= 0; k--) {
                // TODO:処理を行う。
                // Masu cell = masu[k][j];
            }
        }

        if (arrow.direction == Direction.Right) {
            // 処理を行う。
            List<Masu[]> cands = GetCandidate(arrow, i, j);
            if (cands.size() == 0)
                return;
            Iterator<Masu[]> iter = cands.iterator();
            while (iter.hasNext()) {
                Masu[] data = iter.next();
                boolean isValid = true;
                for (int k = 0; k < data.length; k++) {
                    Masu target = masu[i][j + k + 1];
                    if (target == Masu.Black && data[k] != Masu.Black) {
                        isValid = false;
                        break;
                    }
                }
                if (!isValid)
                    iter.remove();
            }
            if (cands.size() == 0)
                return;
            Masu[] fixed = cands.get(0);
            for (int k = 0; k < cands.size(); k++) {
                Masu[] data = cands.get(k);
                for (int l = 0; l < data.length; l++) {
                    if (fixed[l] != data[l])
                        fixed[l] = Masu.UnDefined;
                }
            }
            for (int l = 0; l < fixed.length; l++) {
                if (fixed[l] == Masu.Black) {
                    FillMasu(Masu.Black, i, j + l + 1);
                } else if (fixed[l] == Masu.NotBlack) {
                    FillMasu(Masu.NotBlack, i, j + l + 1);
                }
            }
        }

        if (arrow.direction == Direction.Left) {
            // 処理を行う。
            List<Masu[]> cands = GetCandidate(arrow, i, j);
            if (cands.size() == 0)
                return;
            if (cands.size() == 1) {
                // 結果を採用する
                Masu[] cand = cands.get(0);
                for (int l = 0; l < cand.length; l++) {

                    if (cand[l] == Masu.Black) {
                        FillMasu(Masu.Black, i, j - l - 1);
                    } else {
                        FillMasu(Masu.NotBlack, i, j - l - 1);
                    }
                }
                return;
            }
        }
    }

    // 矢印の数だけ黒ますを置くことができるかどうか
    private boolean CheckCorrectArrow(int i, int j) {
        Arrow arrow = arrows[i][j];
        int diffI = 0;
        int diffJ = 0;
        switch (arrow.direction) {
            case Down:
                diffI = -1;
                break;
            case Up:
                diffI = 1;
                break;
            case Right:
                diffJ = 1;
                break;
            case Left:
                diffJ = -1;
                break;
            default:
                diffI = 0;
        }
        int ci = i + diffI, cj = j + diffJ;
        int kuroCnt = 0, undefinedCnt = 0;
        while (true) {
            if (ci < 0 || ci >= height || cj < 0 || cj >= width) {
                break;
            }
            if (masu[ci][cj] == Masu.Black)
                kuroCnt++;
            if (masu[ci][cj] == Masu.UnDefined)
                undefinedCnt++;
            ci += diffI;
            cj += diffJ;
        }
        if (kuroCnt + undefinedCnt < arrow.value) {
            // System.out.println("FAFAFAFAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
            return false;
        }
        return true;
    }

    private boolean CanDrawOnePath() {

        System.out.println("can draw one path");
        ShowMasu();
        Position initialPosition = new Position(0, 0);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (masu[i][j] == Masu.NotBlack) {
                    initialPosition.i = i;
                    initialPosition.j = j;
                    i = height;
                    j = width;
                }
            }
        }
        CheckField cf = new CheckField(this, initialPosition);
        Stack<CheckField> stack = new Stack<>();
        stack.add(cf);

        int[] di = { 1, 0, -1, 0 };
        int[] dj = { 0, 1, 0, -1 };
        Position pos;

        while (!stack.isEmpty()) {
            cf = stack.pop();
            pos = cf.GetLast();
            if (pos != null) {
                for (int i = 0; i < di.length; i++) {
                    int ii = pos.i + di[i];
                    int jj = pos.j + dj[i];
                    if (ii == initialPosition.i && jj == initialPosition.j) {
                        return true;
                    }
                }
                continue;
            }
            for (int i = 0; i < di.length; i++) {
                CheckField next = cf.Move(di[i], dj[i]);
                if (next != null)
                    stack.add(next);
            }
        }
        return false;
    }

    private List<Masu[]> GetCandidate(Arrow arrow, int i, int j) {
        List<Masu[]> list = new ArrayList<Masu[]>();
        Direction dir = arrow.direction;
        int length = 0;
        if (dir == Direction.Right)
            length = width - j - 1;
        if (dir == Direction.Left)
            length = j;
        if (dir == Direction.Up)
            length = i;
        if (dir == Direction.Down)
            length = height - i - 1;
        if (arrow.value == 0) {
            Masu[] cand = CreateMasuArray(length);
            list.add(cand);
        } else if (arrow.value == 1) {
            for (int k = 0; k < length; k++) {
                Masu[] cand = CreateMasuArray(length);
                cand[k] = Masu.Black;
                list.add(cand);
            }
        } else if (arrow.value == 2) {
            Masu[] cand = CreateMasuArray(length);
            cand[0] = Masu.Black;
            cand[2] = Masu.Black;
            list.add(cand);
        }
        return list;
    }

    // 全てがNotBlackの配列を返す。
    private Masu[] CreateMasuArray(int size) {
        Masu[] masuArray = new Masu[size];
        for (int i = 0; i < size; i++) {
            masuArray[i] = Masu.NotBlack;
        }
        return masuArray;
    }

    private void FillByWall() {
        // System.out.println("Fill By Wall");
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                // 壁と通路をカウントする
                int wall = 0;
                int path = 0;
                if (tateWalls[i][j] == Wall.Exist)
                    wall++;
                if (tateWalls[i][j] == Wall.NotExist)
                    path++;
                if (tateWalls[i][j + 1] == Wall.Exist)
                    wall++;
                if (tateWalls[i][j + 1] == Wall.NotExist)
                    path++;
                if (yokoWalls[i][j] == Wall.Exist)
                    wall++;
                if (yokoWalls[i][j] == Wall.NotExist)
                    path++;
                if (yokoWalls[i + 1][j] == Wall.Exist)
                    wall++;
                if (yokoWalls[i + 1][j] == Wall.NotExist)
                    path++;

                if (wall == 3 && masu[i][j] == Masu.UnDefined)
                    FillMasu(Masu.Black, i, j);

                if (wall >= 3 && masu[i][j] == Masu.NotBlack) {
                    IsUnsolved = true;
                    return;
                }

                if (wall == 2 && path == 2) {
                    // パスと接続部分が NotBlackになる
                    if (tateWalls[i][j] == Wall.NotExist)
                        FillMasu(Masu.NotBlack, i, j - 1);
                    // masu[i][j - 1] = Masu.NotBlack;
                    if (tateWalls[i][j + 1] == Wall.NotExist)
                        FillMasu(Masu.NotBlack, i, j + 1);
                    // masu[i][j + 1] = Masu.NotBlack;
                    if (yokoWalls[i][j] == Wall.NotExist)
                        FillMasu(Masu.NotBlack, i - 1, j);
                    // masu[i - 1][j] = Masu.NotBlack;
                    if (yokoWalls[i + 1][j] == Wall.NotExist)
                        FillMasu(Masu.NotBlack, i + 1, j);
                    // masu[i + 1][j] = Masu.NotBlack;
                }

                if (path >= 3) {
                    // 通路が3つ以上はエラー
                    IsUnsolved = true;
                    return;
                }

            }
        }
    }

    private MasuInfo GetMasuInfo(int i, int j) {
        int wall = 0;
        int path = 0;
        if (tateWalls[i][j] == Wall.Exist)
            wall++;
        if (tateWalls[i][j] == Wall.NotExist)
            path++;
        if (tateWalls[i][j + 1] == Wall.Exist)
            wall++;
        if (tateWalls[i][j + 1] == Wall.NotExist)
            path++;
        if (yokoWalls[i][j] == Wall.Exist)
            wall++;
        if (yokoWalls[i][j] == Wall.NotExist)
            path++;
        if (yokoWalls[i + 1][j] == Wall.Exist)
            wall++;
        if (yokoWalls[i + 1][j] == Wall.NotExist)
            path++;
        return new MasuInfo(wall, path);
    }

    private void FillMasu(Masu masuType, int i, int j) {

        if (masu[i][j] != Masu.UnDefined)
            // 既に埋まっている場合は更新しない
            return;
        masu[i][j] = masuType;

        if (masuType == Masu.Black) {
            // 周囲にBlackがあ流場合解けない状態になる
            if (i > 0 && masu[i - 1][j] == Masu.Black)
                IsUnsolved = true;
            if (i < height - 1 && masu[i + 1][j] == Masu.Black)
                IsUnsolved = true;
            if (j > 0 && masu[i][j - 1] == Masu.Black)
                IsUnsolved = true;
            if (j < width - 1 && masu[i][j + 1] == Masu.Black)
                IsUnsolved = true;
        }
        if (masuType == Masu.Arrow || masuType == Masu.Black) {
            tateWalls[i][j] = Wall.Exist; // マスの左側
            tateWalls[i][j + 1] = Wall.Exist; // マスの右側
            yokoWalls[i][j] = Wall.Exist; // マスの上側
            yokoWalls[i + 1][j] = Wall.Exist;
        }

        if (masuType == Masu.Black) {
            // 周囲をNotBlackにする
            if (i > 0)
                FillMasu(Masu.NotBlack, i - 1, j);
            if (i < height - 1)
                FillMasu(Masu.NotBlack, i + 1, j);
            if (j > 0)
                FillMasu(Masu.NotBlack, i, j - 1);
            if (j < width - 1)
                FillMasu(Masu.NotBlack, i, j + 1);
        }
    }

}
