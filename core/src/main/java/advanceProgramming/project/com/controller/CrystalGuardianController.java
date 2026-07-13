package advanceProgramming.project.com.controller;

import advanceProgramming.project.com.helper.SolidBlock;
import advanceProgramming.project.com.model.CrystalGuardian;
import advanceProgramming.project.com.model.Knight;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class CrystalGuardianController extends EnemyController {
    private final CrystalGuardian cg;

    public CrystalGuardianController(CrystalGuardian model, Knight knight, Array<SolidBlock> mapBlocks) {
        super(model, knight, mapBlocks);
        this.cg = model;
    }

    @Override
    public void update(float delta) {
        cg.stateTime += delta;
        if (cg.invincibleTimer > 0) cg.invincibleTimer -= delta;

        if (cg.isDead() && cg.currentState != CrystalGuardian.State.DEAD) {
            cg.currentState = CrystalGuardian.State.DEAD;
            cg.stateTime = 0f;
        }

        Rectangle cgBox = new Rectangle(cg.x, cg.y, cg.width, cg.height);
        if (cg.knockbackTimer > 0) {
            cg.knockbackTimer -= delta;

            cg.velocityX = MathUtils.lerp(cg.velocityX, 0, 5f * delta);
            cg.x += cg.velocityX * delta;

            applyGravity(delta, cgBox);

            cgBox.setPosition(cg.x, cg.y);
            for (SolidBlock block : mapBlocks) {
                if (cgBox.overlaps(new Rectangle(block.x, block.y, block.width, block.height))) {
                    cg.x -= cg.velocityX * delta;
                    cg.velocityX = 0;
                    break;
                }
            }
        } else {
            switch (cg.currentState) {
                case DEAD:
                    cg.velocityX = 0;
                    applyGravity(delta, cgBox);
                    return;

                case IDLE:
                    cg.velocityX = 0;
                    applyGravity(delta, cgBox);

                    if (checkPlayerInSight()) {
                        cg.currentState = CrystalGuardian.State.FIRING_LASER;
                        cg.actionTimer = 1.4f;
                        cg.stateTime = 0f;
                    }
                    break;

                case FIRING_LASER:
                    cg.velocityX = 0;
                    applyGravity(delta, cgBox);
                    cg.actionTimer -= delta;

                    if (cg.stateTime > 0.6f) {
                        checkLaserDamage();
                    }

                    if (cg.actionTimer <= 0) {
                        cg.currentState = CrystalGuardian.State.ENRAGED;
                        cg.actionTimer = cg.enragedDuration;
                        cg.stateTime = 0f;
                    }
                    break;

                case ENRAGED:
                    applyGravity(delta, cgBox);

                    cg.isFacingRight = knightModel.x > cg.x;
                    cg.velocityX = cg.isFacingRight ? cg.enragedSpeed : -cg.enragedSpeed;
                    cg.x += cg.velocityX * delta;

                    cg.actionTimer -= delta;

                    if (checkWallOrCliff(cgBox)) {
                        cg.x -= cg.velocityX * delta;
                    }

                    if (cg.actionTimer <= 0) {
                        cg.currentState = CrystalGuardian.State.IDLE;
                        cg.stateTime = 0f;
                    }
                    break;
            }
        }
        checkCollisionWithKnight();
        checkDamageFromKnight();
    }

    private void applyGravity(float delta, Rectangle box) {
        cg.velocityY += cg.gravity * delta;
        cg.y += cg.velocityY * delta;
        box.setPosition(cg.x, cg.y);

        for (SolidBlock block : mapBlocks) {
            if (box.overlaps(new Rectangle(block.x, block.y, block.width, block.height)) && cg.velocityY < 0) {
                cg.y = block.y + block.height;
                cg.velocityY = 0;
            }
        }
    }

    private boolean checkPlayerInSight() {
        if (knightModel.getCurrentHp() <= 0 || knightModel.invincibleTimer > 0) return false;

        float sightX = cg.isFacingRight ? cg.x + cg.width : cg.x - cg.sightRange;
        Rectangle sightBox = new Rectangle(sightX, cg.y + 30, cg.sightRange, cg.sightHeight);
        Rectangle knightBox = new Rectangle(knightModel.x, knightModel.y, knightModel.width, knightModel.height);

        return sightBox.overlaps(knightBox);
    }

    private void checkLaserDamage() {
        float laserX = cg.isFacingRight ? cg.x + cg.width : cg.x - cg.sightRange;
        Rectangle laserBox = new Rectangle(laserX, cg.y + 20f, cg.sightRange, cg.sightHeight);
        Rectangle knightBox = new Rectangle(knightModel.x, knightModel.y, knightModel.width, knightModel.height);

        if (laserBox.overlaps(knightBox)) {
            knightModel.takeDamage(1);
        }
    }

    private boolean checkWallOrCliff(Rectangle box) {
        box.setPosition(cg.x, cg.y);
        boolean hitWall = false;
        boolean isCliffAhead = true;

        float cliffSensorX = cg.isFacingRight ? cg.x + cg.width + 5f : cg.x - 5f;
        Rectangle cliffSensor = new Rectangle(cliffSensorX, cg.y - 10f, 5f, 5f);

        for (SolidBlock block : mapBlocks) {
            Rectangle blockBox = new Rectangle(block.x, block.y, block.width, block.height);
            if (box.overlaps(blockBox)) hitWall = true;
            if (cliffSensor.overlaps(blockBox)) isCliffAhead = false;
        }

        return hitWall || isCliffAhead;
    }
}
