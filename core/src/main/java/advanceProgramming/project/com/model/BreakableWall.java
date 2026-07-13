package advanceProgramming.project.com.model;

public class BreakableWall {
    public float x, y, width, height;
    public int hp = 3;
    public float invincibleTimer = 0f;
    public boolean isBroken = false;

    public BreakableWall(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
}
