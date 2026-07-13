package advanceProgramming.project.com.model;

public abstract class Enemy {
    public float x, y;
    public float spawnX;
    public float spawnY;
    public float width, height;

    public float speed;
    public float velocityX = 0;
    public float velocityY = 0;
    public float gravity = -1500f;
    public float hp;

    public boolean isFacingRight;
    public float stateTime = 0f;
    public float invincibleTimer = 0f;
    public float knockbackTimer = 0f;
    public boolean isScoreCounted = false;

    public void takeDamage(float amount) {
        if (invincibleTimer <= 0 && !isDead()) {
            this.hp -= amount;
            this.invincibleTimer = 0.5f;

            if (isDead()) {
                this.stateTime = 0f;
            }
        }
    }

    public boolean isDead() {
        return hp <= 0;
    }
}
