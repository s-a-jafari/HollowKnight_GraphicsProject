package advanceProgramming.project.com.model;

public class HuskHornhead extends Enemy {
    public State currentState = State.WALKING;
    public float actionTimer = 0f;
    public float patrolRange = 250f;
    public float walkSpeed = 150f;
    public float chargeSpeed = 600f;
    public float sightRange = 400f;
    public float sightHeight = 120f;
    public HuskHornhead(float startX, float startY, boolean startFacingRight) {
        this.x = startX;
        this.y = startY;
        this.isFacingRight = startFacingRight;

        this.spawnX = startX;

        this.width = 239f;
        this.height = 219f;

        this.hp = 4;
        this.speed = walkSpeed;
        this.gravity = -1500f;

        this.actionTimer = 2.0f;
    }

    public enum State {IDLE, WALKING, WARNING, CHARGING, DEAD}
}
