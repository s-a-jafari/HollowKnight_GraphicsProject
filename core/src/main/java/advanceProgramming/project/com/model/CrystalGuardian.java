package advanceProgramming.project.com.model;

public class CrystalGuardian extends Enemy {
    public State currentState = State.IDLE;
    public float actionTimer = 0f;
    public float sightRange = 600f;
    public float sightHeight = 100f;
    public float enragedSpeed = 500f;
    public float enragedDuration = 3.0f;
    public CrystalGuardian(float startX, float startY, boolean startFacingRight) {
        this.x = startX;
        this.y = startY;
        this.isFacingRight = startFacingRight;
        this.width = 285f;
        this.height = 189f;
        this.hp = 5;
        this.speed = 0f;
        this.gravity = -1500f;
    }

    public enum State {IDLE, FIRING_LASER, ENRAGED, DEAD}
}
