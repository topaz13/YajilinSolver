public class URLPerser {

    public URLPerser() {
    }

    public static void main(String[] args) {
        String url = "";
        url = "https://puzz.link/p?yajilin/10/10/b2241u2222e3131zf2031e1231u4011b";
        String[] splited = url.split("/");
        for (int i = 0; i < splited.length; i++) {
            System.out.println(splited[i]);
        }
        int height = Integer.parseInt(splited[4]);
        int width = Integer.parseInt(splited[5]);
        String data = splited[6];

        int index = 0;
        for (int i = 0; i < data.length(); i++) {
            char c = data.charAt(i);
            if ('a' <= c && c <= 'z') {
                System.out.println(c);
                index += ((c - 'a') + 1);
            } else {
                // 数字がくる二つ目も取得する
                i++;
                // 数字
                char arrow = c;
                char value = data.charAt(i);
                int row = index / width;
                int columne = index % width;
                System.out.println("(" + row + "," + columne + ")" + " : ");
                index++;
            }
        }

    }

    /**
     * パズリンクのurlから問題を生成する
     * 
     * @param url
     * @return
     */
    public Field CreateFieldFromURL(String url) {
        // url = "https://puzz.link/p?yajilin/10/10/b2241u2222e3131zf2031e1231u4011b";
        String[] splited = url.split("/");
        int height = Integer.parseInt(splited[4]);
        int width = Integer.parseInt(splited[5]);
        String data = splited[6];

        Field f = new Field(height, width);
        int index = 0;
        for (int i = 0; i < data.length(); i++) {
            char c = data.charAt(i);
            if ('a' <= c && c <= 'z') {
                index += ((c - 'a') + 1);
            } else {
                // 数字がくる二つ目も取得する
                i++;
                // 数字
                char arrow = c;
                char value = data.charAt(i);
                int row = index / width;
                int columne = index % width;
                // System.out.println("(" + row + "," + columne + ")" + " : ");
                index++;
                // 2 下 4 右 1 上 3 左
                Direction dir = Direction.Down;
                switch ((arrow - '0')) {
                    case 1:
                        dir = Direction.Up;
                        break;
                    case 2:
                        dir = Direction.Down;
                        break;
                    case 3:
                        dir = Direction.Left;
                        break;
                    case 4:
                        dir = Direction.Right;
                        break;

                    default:
                        break;
                }
                f.FillArrow(row, columne, dir, (value - '0'));
            }
        }
        return f;
    }

}
