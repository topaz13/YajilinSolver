public class MasuInfo {
    public int wallCount = 0;
    public int notWallCount = 0;
    public int undefinedWallCnt = 0;

    public MasuInfo(int wall, int notWall) {
        this.wallCount = wall;
        this.notWallCount = notWall;
        this.undefinedWallCnt = 4 - wall - notWall;
    }
}
