package advanceProgramming.project.com.controller;

import advanceProgramming.project.com.helper.SolidBlock;
import advanceProgramming.project.com.model.HuskHornhead;
import advanceProgramming.project.com.model.Knight;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class HuskHornheadController extends EnemyController {
    private final HuskHornhead husk;

    public HuskHornheadController(HuskHornhead model, Knight knight, Array<SolidBlock> mapBlocks) {
        super(model, knight, mapBlocks);
        this.husk = model;
    }

    @Override
    public void update(float delta) {
        husk.stateTime += delta;

        if (husk.invincibleTimer > 0) husk.invincibleTimer -= delta;

        if (husk.isDead() && husk.currentState != HuskHornhead.State.DEAD) {
            husk.currentState = HuskHornhead.State.DEAD;
            husk.stateTime = 0f;
        }

        Rectangle huskBox = new Rectangle(husk.x, husk.y, husk.width, husk.height);
        if (husk.knockbackTimer > 0) {
            husk.knockbackTimer -= delta;

            husk.velocityX = com.badlogic.gdx.math.MathUtils.lerp(husk.velocityX, 0, 5f * delta);
            husk.x += husk.velocityX * delta;

            applyGravityAndFloorCollision(delta, huskBox);

            huskBox.setPosition(husk.x, husk.y);
            for (SolidBlock block : mapBlocks) {
                if (huskBox.overlaps(new Rectangle(block.x, block.y, block.width, block.height))) {
                    husk.x -= husk.velocityX * delta;
                    husk.velocityX = 0;
                    break;
                }
            }
        } else {
            switch (husk.currentState) {
                case DEAD:
                    husk.velocityX = 0;
                    husk.velocityY += husk.gravity * delta;
                    husk.y += husk.velocityY * delta;

                    for (SolidBlock block : mapBlocks) {
                        if (huskBox.overlaps(new Rectangle(block.x, block.y, block.width, block.height)) && husk.velocityY < 0) {
                            husk.y = block.y + block.height;
                            husk.velocityY = 0;
                        }
                    }
                    return;

                case IDLE:
                    husk.velocityX = 0;
                    applyGravityAndFloorCollision(delta, huskBox);

                    husk.actionTimer -= delta;
                    if (husk.actionTimer <= 0) {
                        husk.currentState = HuskHornhead.State.WALKING;
                        husk.actionTimer = 2.5f;
                        husk.stateTime = 0f;
                    }
                    checkPlayerInSight();
                    break;

                case WALKING:
                    applyGravityAndFloorCollision(delta, huskBox);

                    husk.velocityX = husk.isFacingRight ? husk.walkSpeed : -husk.walkSpeed;
                    husk.x += husk.velocityX * delta;

                    husk.actionTimer -= delta;

                    boolean outOfRange = Math.abs(husk.x - husk.spawnX) > husk.patrolRange;

                    if (checkWallOrCliff(huskBox) || outOfRange) {
                        husk.x -= husk.velocityX * delta;
                        husk.isFacingRight = !husk.isFacingRight;
                    } else if (husk.actionTimer <= 0) {
                        husk.currentState = HuskHornhead.State.IDLE;
                        husk.actionTimer = 1.5f;
                        husk.stateTime = 0f;
                    }

                    checkPlayerInSight();
                    break;

                case WARNING:
                    husk.velocityX = 0;
                    applyGravityAndFloorCollision(delta, huskBox);

                    husk.actionTimer -= delta;
                    if (husk.actionTimer <= 0) {
                        husk.currentState = HuskHornhead.State.CHARGING;
                        husk.stateTime = 0f;
                    }
                    break;

                case CHARGING:
                    applyGravityAndFloorCollision(delta, huskBox);

                    husk.velocityX = husk.isFacingRight ? husk.chargeSpeed : -husk.chargeSpeed;
                    husk.x += husk.velocityX * delta;

                    Rectangle coreBox = new Rectangle(husk.x + 10f, husk.y + 10f,
                        husk.width - 20f, husk.height - 20f);

                    if (checkWallOrCliff(coreBox)) {
                        husk.x -= husk.velocityX * delta;
                        husk.currentState = HuskHornhead.State.IDLE;
                        husk.actionTimer = 2.0f;
                        husk.stateTime = 0f;
                    }
                    break;
            }
        }
        checkCollisionWithKnight();
        checkDamageFromKnight();
    }

    private void applyGravityAndFloorCollision(float delta, Rectangle huskBox) {
        husk.velocityY += husk.gravity * delta;
        husk.y += husk.velocityY * delta;
        huskBox.setPosition(husk.x, husk.y);

        for (SolidBlock block : mapBlocks) {
            if (huskBox.overlaps(new Rectangle(block.x, block.y, block.width, block.height)) && husk.velocityY < 0) {
                husk.y = block.y + block.height;
                husk.velocityY = 0;
            }
        }
    }

    private void checkPlayerInSight() {
        if (knightModel.getCurrentHp() == 0 || knightModel.invincibleTimer > 0) return;

        float sightX = husk.isFacingRight ? husk.x + husk.width : husk.x - husk.sightRange;
        float sightY = husk.y;

        Rectangle sightBox = new Rectangle(sightX, sightY, husk.sightRange, husk.sightHeight);
        Rectangle knightBox = new Rectangle(knightModel.x, knightModel.y, knightModel.width, knightModel.height);

        if (sightBox.overlaps(knightBox)) {
            husk.currentState = HuskHornhead.State.WARNING;
            husk.actionTimer = 0.4f;
            husk.stateTime = 0f;
        }
    }

    private boolean checkWallOrCliff(Rectangle box) {
        box.setPosition(husk.x, husk.y);
        boolean hitWall = false;
        boolean isCliffAhead = true;

        float cliffSensorX = husk.isFacingRight ? husk.x + husk.width + 5f : husk.x - 5f;
        Rectangle cliffSensor = new Rectangle(cliffSensorX, husk.y - 10f, 5f, 5f);

        for (SolidBlock block : mapBlocks) {
            Rectangle blockBox = new Rectangle(block.x, block.y, block.width, block.height);
            if (box.overlaps(blockBox)) hitWall = true;
            if (cliffSensor.overlaps(blockBox)) isCliffAhead = false;
        }

        return hitWall || isCliffAhead;
    }
}
