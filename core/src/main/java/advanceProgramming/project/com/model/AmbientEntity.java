package advanceProgramming.project.com.model;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

public class AmbientEntity {
    private final float SPEED = 35f;
    public float x, y, width, height;
    public float velocityX, velocityY;
    public float stateTime;
    private float wanderAngle;

    public AmbientEntity(float startX, float startY) {
        this.x = startX;
        this.y = startY;
        this.width = 100;
        this.height = 100;
        this.wanderAngle = MathUtils.random(0, MathUtils.PI2);
    }

    public void update(float delta, Array<AmbientEntity> allEntities, float mapWidth, float mapHeight) {
        stateTime += delta;

        wanderAngle += MathUtils.random(-1.5f, 1.5f) * delta;
        float targetVx = MathUtils.cos(wanderAngle) * SPEED;
        float targetVy = MathUtils.sin(wanderAngle) * SPEED;

        float repulsionX = 0;
        float repulsionY = 0;

        for (int i = 0; i < allEntities.size; i++) {
            AmbientEntity other = allEntities.get(i);

            if (other != this) {
                float dx = this.x - other.x;
                float dy = this.y - other.y;
                float distance = (float) Math.sqrt(dx * dx + dy * dy);

                if (distance < 150f && distance > 0) {
                    repulsionX += (dx / distance) * (150f - distance);
                    repulsionY += (dy / distance) * (150f - distance);
                }
            }
        }

        targetVx += repulsionX * 0.8f;
        targetVy += repulsionY * 0.8f;

        velocityX += (targetVx - velocityX) * 2f * delta;
        velocityY += (targetVy - velocityY) * 2f * delta;

        x += velocityX * delta;
        y += velocityY * delta;

        if (x < 50) {
            x = 50;
            wanderAngle = 0;
        }
        if (x > mapWidth - 50) {
            x = mapWidth - 50;
            wanderAngle = MathUtils.PI;
        }
        if (y < 50) {
            y = 50;
            wanderAngle = MathUtils.PI / 2;
        }
        if (y > mapHeight - 50) {
            y = mapHeight - 50;
            wanderAngle = -MathUtils.PI / 2;
        }
    }
}
