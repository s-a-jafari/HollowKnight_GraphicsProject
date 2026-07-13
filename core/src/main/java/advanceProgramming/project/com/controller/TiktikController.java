package advanceProgramming.project.com.controller;

import advanceProgramming.project.com.helper.SolidBlock;
import advanceProgramming.project.com.model.Knight;
import advanceProgramming.project.com.model.Tiktik;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class TiktikController extends EnemyController {

    public TiktikController(Tiktik model, Knight knight, Array<SolidBlock> mapBlocks) {
        super(model, knight, mapBlocks);
    }

    @Override
    public void update(float delta) {
        enemyModel.stateTime += delta;

        if (enemyModel.invincibleTimer > 0) {
            enemyModel.invincibleTimer -= delta;
        }

        if (enemyModel.isDead()) {
            enemyModel.velocityX = 0;
            enemyModel.velocityY += enemyModel.gravity * delta;
            enemyModel.y += enemyModel.velocityY * delta;

            Rectangle corpseBox = new Rectangle(enemyModel.x, enemyModel.y, enemyModel.width, enemyModel.height);
            for (SolidBlock block : mapBlocks) {
                Rectangle blockBox = new Rectangle(block.x, block.y, block.width, block.height);
                if (corpseBox.overlaps(blockBox) && enemyModel.velocityY < 0) {
                    enemyModel.y = block.y + block.height;
                    enemyModel.velocityY = 0;
                }
            }
            return;
        }

        enemyModel.velocityY += enemyModel.gravity * delta;
        enemyModel.y += enemyModel.velocityY * delta;

        Rectangle tiktikBox = new Rectangle(enemyModel.x, enemyModel.y, enemyModel.width, enemyModel.height);
        for (SolidBlock block : mapBlocks) {
            Rectangle blockBox = new Rectangle(block.x, block.y, block.width, block.height);
            if (tiktikBox.overlaps(blockBox) && enemyModel.velocityY < 0) {
                enemyModel.y = block.y + block.height;
                enemyModel.velocityY = 0;
            }
        }

        if (enemyModel.knockbackTimer > 0) {
            enemyModel.knockbackTimer -= delta;
            enemyModel.velocityX = com.badlogic.gdx.math.MathUtils.lerp(enemyModel.velocityX, 0, 5f * delta);
        } else {
            enemyModel.velocityX = enemyModel.isFacingRight ? enemyModel.speed : -enemyModel.speed;
        }

        enemyModel.x += enemyModel.velocityX * delta;
        tiktikBox.setPosition(enemyModel.x, enemyModel.y);

        boolean hitWall = false;
        boolean isCliffAhead = true;

        float cliffSensorX = enemyModel.isFacingRight ? enemyModel.x + enemyModel.width + 5f : enemyModel.x - 5f;
        Rectangle cliffSensor = new Rectangle(cliffSensorX, enemyModel.y - 5f, 5f, 5f);

        for (SolidBlock block : mapBlocks) {
            Rectangle blockBox = new Rectangle(block.x, block.y, block.width, block.height);
            if (tiktikBox.overlaps(blockBox)) hitWall = true;
            if (cliffSensor.overlaps(blockBox)) isCliffAhead = false;
        }

        if (hitWall) {
            enemyModel.x -= enemyModel.velocityX * delta;
            enemyModel.velocityX = 0;

            if (enemyModel.knockbackTimer <= 0) {
                enemyModel.isFacingRight = !enemyModel.isFacingRight;
            }
        } else if (isCliffAhead && enemyModel.knockbackTimer <= 0) {
            enemyModel.x -= enemyModel.velocityX * delta;
            enemyModel.isFacingRight = !enemyModel.isFacingRight;
        }

        checkCollisionWithKnight();
        checkDamageFromKnight();
    }
}
