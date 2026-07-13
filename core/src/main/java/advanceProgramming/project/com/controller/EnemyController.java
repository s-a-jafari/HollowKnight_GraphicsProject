package advanceProgramming.project.com.controller;

import advanceProgramming.project.com.helper.SolidBlock;
import advanceProgramming.project.com.model.Enemy;
import advanceProgramming.project.com.model.Knight;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public abstract class EnemyController {
    protected Enemy enemyModel;
    protected Knight knightModel;
    protected Array<SolidBlock> mapBlocks;

    public EnemyController(Enemy enemyModel, Knight knightModel, Array<SolidBlock> mapBlocks) {
        this.enemyModel = enemyModel;
        this.knightModel = knightModel;
        this.mapBlocks = mapBlocks;
    }

    public abstract void update(float delta);

    protected void checkCollisionWithKnight() {
        if (enemyModel.isDead()) return;

        Rectangle enemyBox = new Rectangle(enemyModel.x, enemyModel.y, enemyModel.width, enemyModel.height);

        float kHitBoxWidth = 60f;
        float kHitBoxHeight = 130f;
        float kHitBoxX = knightModel.x + (knightModel.width / 2f) - (kHitBoxWidth / 2f);
        Rectangle knightBox = new Rectangle(kHitBoxX, knightModel.y, kHitBoxWidth, kHitBoxHeight);

        if (enemyBox.overlaps(knightBox)) {

            if (knightModel.currentState == Knight.State.DASHING && knightModel.hasSharpShadow) {

                if (enemyModel.invincibleTimer <= 0) {
                    enemyModel.takeDamage(knightModel.nailDamage);
                    enemyModel.invincibleTimer = 0.4f;
                }
            } else {
                if (knightModel.invincibleTimer <= 0) {
                    knightModel.takeDamage(1);

                    knightModel.knockbackTimer = 0.2f;

                    float kCenter = knightModel.x + (knightModel.width / 2f);
                    float eCenter = enemyModel.x + (enemyModel.width / 2f);
                    float pushDirection = (kCenter < eCenter) ? -1f : 1f;

                    knightModel.velocityX = pushDirection * 600f;
                    knightModel.velocityY = 300f;
                }
            }
        }
    }

    protected void checkDamageFromKnight() {
        if (enemyModel.isDead() || enemyModel.invincibleTimer > 0) return;

        Rectangle enemyBox = new Rectangle(enemyModel.x, enemyModel.y, enemyModel.width, enemyModel.height);


        if (knightModel.isSlashActive) {
            float kHitBoxWidth = 60f;
            float kHitBoxX = knightModel.x + (knightModel.width / 2f) - (kHitBoxWidth / 2f);
            float slashWidth = 80f;
            float slashHeight = 110f;
            float slashX;
            float slashY;

            if (knightModel.currentState == Knight.State.DOWN_ATTACKING) {
                slashHeight = 60f;
                slashX = kHitBoxX + (kHitBoxWidth / 2f) - (slashWidth / 2f);
                slashY = knightModel.y - 30f;
            } else if (knightModel.currentState == Knight.State.UP_ATTACKING) {
                slashHeight = 80f;
                slashWidth = 110f;
                slashX = kHitBoxX + (kHitBoxWidth / 2f) - (slashWidth / 2f);

                slashY = knightModel.y + 5f;
            } else {
                slashY = knightModel.y + 10f;
                slashX = !knightModel.isFacingRight ? kHitBoxX + kHitBoxWidth : kHitBoxX - slashWidth;
            }

            Rectangle slashBox = new Rectangle(slashX, slashY, slashWidth, slashHeight);

            if (enemyBox.overlaps(slashBox)) {
                if (knightModel.currentState == Knight.State.DOWN_ATTACKING) {
                    knightModel.triggerPogoBounce();
                }

                enemyModel.takeDamage(knightModel.nailDamage);
                knightModel.gainSoul();


                enemyModel.invincibleTimer = 0.3f;

                enemyModel.knockbackTimer = 0.2f;

                float pushDirection = knightModel.isFacingRight ? -1f : 1f;
                enemyModel.velocityX = pushDirection * 950;
                enemyModel.velocityY = 70f;
            }
        }

        for (Knight.VengefulSpirit vs : knightModel.vengefulSpirits) {
            Rectangle vsBox = new Rectangle(vs.x, vs.y, vs.width, vs.height);
            if (vsBox.overlaps(enemyBox) && !vs.damagedEnemies.contains(enemyModel, true)) {
                enemyModel.takeDamage(knightModel.spellDamage);
                vs.damagedEnemies.add(enemyModel);
            }
        }

        for (Knight.HowlingWraiths hw : knightModel.howlingWraiths) {
            Rectangle hwBox = new Rectangle(hw.x, hw.y, hw.width, hw.height);
            if (hwBox.overlaps(enemyBox) && !hw.hitInCurrentTick.contains(enemyModel, true)) {
                float oldTimer = enemyModel.invincibleTimer;
                enemyModel.invincibleTimer = 0;
                enemyModel.takeDamage(knightModel.spellDamage / 2f);
                enemyModel.invincibleTimer = (oldTimer > 0) ? oldTimer : 0.2f;

                hw.hitInCurrentTick.add(enemyModel);
            }
        }
    }
}
