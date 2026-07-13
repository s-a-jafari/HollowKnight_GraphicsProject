package advanceProgramming.project.com.model;

public class Mosquito extends Enemy {
    public State currentState = State.IDLE;
    public float actionTimer = 0f;
    public float dashVelX, dashVelY;
    public float territoryRadius = 250f;
    public float maxDashRange = 500f;
    public float dashStartX, dashStartY;
    public float rotationAngle = 0f;

    public Mosquito(float startX, float startY, boolean startFacingRight) {
        this.x = startX;
        this.y = startY;
        this.spawnX = startX;
        this.spawnY = startY;

        this.isFacingRight = startFacingRight;
        this.width = 183.33f;
        this.height = 129.16f;
        this.hp = 2;
        this.speed = 850f;
        this.gravity = 0f;
    }

    public enum State {IDLE, WARNING, DASHING, DEAD, RETURNING}
}
