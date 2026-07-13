package advanceProgramming.project.com.model;

import com.badlogic.gdx.utils.Array;

public class FalseKnight extends Enemy {
    public State currentState = State.IDLE;
    public State lastMove = State.IDLE;
    public boolean isFightStarted = false;
    public float actionTimer = 0f;
    public float stunDuration = 4.0f;
    public int phase = 1;
    public int hitsToStun = 5;
    public boolean hasBeenStunned = false;
    public int currentHits = 0;
    public int recentHits = 0;
    public float recentHitTimer = 0f;
    public float shakeTimeLeft = 0f;
    public float shakeIntensity = 0f;
    public float spawnX, spawnY;
    public int stunHits = 0;
    public float arenaLeftBound, arenaRightBound;
    public float animSpeed = 1.0f;
    public float actionCooldown = 1.0f;
    public Array<Shockwave> shockwaves = new Array<>();

    public FalseKnight(float startX, float startY, boolean startFacingRight) {
        this.x = startX;
        this.y = startY;
        this.spawnX = startX;
        this.spawnY = startY;
        this.isFacingRight = startFacingRight;

        this.width = 636f;
        this.height = 636f;

        this.hp = 15;
        this.speed = 250f;
        this.gravity = -2000f;

        this.arenaLeftBound = startX - 800f;
        this.arenaRightBound = startX + 800f;
    }

    public void resetBoss() {
        this.x = spawnX;
        this.y = spawnY;
        this.hp = 15;
        this.currentState = State.IDLE;
        this.lastMove = State.IDLE;
        this.isFightStarted = false;
        this.actionTimer = 0f;

        this.phase = 1;
        this.hasBeenStunned = false;
        this.currentHits = 0;
        this.stunHits = 0;
        this.recentHits = 0;

        this.animSpeed = 1.0f;
        this.actionCooldown = 1.0f;
        this.speed = 250f;

        this.recentHitTimer = 0f;
        this.shakeTimeLeft = 0f;
        this.shakeIntensity = 0f;
        this.velocityX = 0;
        this.velocityY = 0;
        this.invincibleTimer = 0f;
        this.shockwaves.clear();
    }

    public enum State {
        IDLE, START_RUN, RUN, JUMP, LAND,
        MACE_SLAM, POWER_SLAM, DEFENSIVE_LEAP,
        BEFORE_STUN, STUN, AFTER_STUN, DEAD
    }

    public static class Shockwave {
        public float x, y, width = 212f, height = 217f;
        public float speed = 500f;
        public boolean isFacingRight;
        public boolean active = true;
        public float stateTime = 0f;

        public Shockwave(float x, float y, boolean isFacingRight) {
            this.x = x;
            this.y = y;
            this.isFacingRight = isFacingRight;
            this.stateTime = 0f;
        }
    }
}
