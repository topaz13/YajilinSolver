public class TestScripts {
    public static void main(String[] args) {
        Showbit(3);
    }

    private static void Showbit(int val) {
        StringBuilder sb = new StringBuilder();
        for (int bit = 0; bit < (1 << val); bit++) {
            sb.setLength(0);
            for (int i = 0; i < val; i++) {
                if ((bit & (1 << i)) > 0) {
                    sb.append("*");
                } else {
                    sb.append("-");
                }
            }
            System.out.println(sb.toString());
        }
    }
}
