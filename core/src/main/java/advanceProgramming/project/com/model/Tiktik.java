package advanceProgramming.project.com.model;

public class Tiktik extends Enemy {
    public Tiktik(float startX, float startY, boolean startFacingRight) {
        this.x = startX;
        this.y = startY;
        this.isFacingRight = startFacingRight;

        this.width = 115f;
        this.height = 105f;
        this.hp = 2;
        this.speed = 120f;
    }
}
