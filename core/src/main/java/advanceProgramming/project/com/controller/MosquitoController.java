package advanceProgramming.project.com.controller;

import advanceProgramming.project.com.helper.SolidBlock;
import advanceProgramming.project.com.model.Knight;
import advanceProgramming.project.com.model.Mosquito;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class MosquitoController extends EnemyController {
    private final Mosquito mosquito;

    public MosquitoController(Mosquito model, Knight knight, Array<SolidBlock> mapBlocks) {
        super(model, knight, mapBlocks);
        this.mosquito = model;
    }

    @Override
    public void update(float delta) {
        mosquito.stateTime += delta;

        if (mosquito.invincibleTimer > 0) {
            mosquito.invincibleTimer -= delta;
        }

        if (mosquito.isDead() && mosquito.currentState != Mosquito.State.DEAD) {
            mosquito.currentState = Mosquito.State.DEAD;
            mosquito.stateTime = 0f;
        }


        if (mosquito.knockbackTimer > 0) {
            mosquito.knockbackTimer -= delta;

            mosquito.velocityX = com.badlogic.gdx.math.MathUtils.lerp(mosquito.velocityX, 0, 5f * delta);
            mosquito.velocityY = com.badlogic.gdx.math.MathUtils.lerp(mosquito.velocityY, 0, 5f * delta);

            mosquito.x += mosquito.velocityX * delta;
            mosquito.y += mosquito.velocityY * delta;

            Rectangle knockBox = new Rectangle(mosquito.x, mosquito.y, mosquito.width, mosquito.height);
            for (SolidBlock block : mapBlocks) {
                if (knockBox.overlaps(new Rectangle(block.x, block.y, block.width, block.height))) {
                    mosquito.x -= mosquito.velocityX * delta;
                    mosquito.y -= mosquito.velocityY * delta;
                    mosquito.velocityX = 0;
                    mosquito.velocityY = 0;
                    break;
                }
            }
        } else {
            mosquito.actionTimer -= delta;
            Rectangle mosqBox = new Rectangle(mosquito.x, mosquito.y, mosquito.width, mosquito.height);

            switch (mosquito.currentState) {
                case DEAD:
                    mosquito.velocityX = 0;
                    mosquito.rotationAngle = 0;
                    mosquito.velocityY += -1500f * delta;
                    mosquito.y += mosquito.velocityY * delta;

                    Rectangle corpseBox = new Rectangle(mosquito.x, mosquito.y, mosquito.width, mosquito.height);
                    for (SolidBlock block : mapBlocks) {
                        Rectangle blockBox = new Rectangle(block.x, block.y, block.width, block.height);
                        if (corpseBox.overlaps(blockBox) && mosquito.velocityY < 0) {
                            mosquito.y = block.y + block.height;
                            mosquito.velocityY = 0;
                        }
                    }
                    return;

                case IDLE:
                    mosquito.rotationAngle = 0;
                    mosquito.velocityY = 0;

                    if (mosquito.actionTimer > 0) break;

                    float distToKnight = Vector2.dst(mosquito.spawnX, mosquito.spawnY, knightModel.x, knightModel.y);
                    if (distToKnight <= mosquito.territoryRadius) {
                        mosquito.currentState = Mosquito.State.WARNING;
                        mosquito.actionTimer = 0.5f;
                        mosquito.isFacingRight = knightModel.x > mosquito.x;
                        mosquito.stateTime = 0f;
                    } else {
                        float distToHome = Vector2.dst(mosquito.x, mosquito.y, mosquito.spawnX, mosquito.spawnY);
                        if (distToHome > 150f) {
                            mosquito.currentState = Mosquito.State.RETURNING;
                            mosquito.stateTime = 0f;
                        } else {
                            if (mosquito.velocityX == 0) mosquito.velocityX = 100f;
                            mosquito.x += mosquito.velocityX * delta;
                            mosquito.isFacingRight = mosquito.velocityX > 0;

                            mosqBox.setPosition(mosquito.x, mosquito.y);
                            boolean hitWallWander = false;
                            for (SolidBlock block : mapBlocks) {
                                if (mosqBox.overlaps(new Rectangle(block.x, block.y, block.width, block.height))) {
                                    hitWallWander = true;
                                    break;
                                }
                            }

                            if (hitWallWander || Math.abs(mosquito.x - mosquito.spawnX) > 100f) {
                                mosquito.x -= mosquito.velocityX * delta;
                                mosquito.velocityX = -mosquito.velocityX;
                            }
                        }
                    }
                    break;

                case RETURNING:
                    mosquito.rotationAngle = 0;

                    float distHome = Vector2.dst(mosquito.x, mosquito.y, mosquito.spawnX, mosquito.spawnY);
                    if (distHome <= 10f) {
                        mosquito.x = mosquito.spawnX;
                        mosquito.y = mosquito.spawnY;
                        mosquito.currentState = Mosquito.State.IDLE;
                        mosquito.stateTime = 0f;
                    } else {
                        Vector2 dirHome = new Vector2(mosquito.spawnX - mosquito.x, mosquito.spawnY - mosquito.y).nor();
                        mosquito.velocityX = dirHome.x * 250f;
                        mosquito.velocityY = dirHome.y * 250f;
                        mosquito.x += mosquito.velocityX * delta;
                        mosquito.y += mosquito.velocityY * delta;
                        mosquito.isFacingRight = mosquito.velocityX > 0;
                    }
                    break;

                case WARNING:
                    mosquito.velocityX = 0;
                    mosquito.velocityY = 0;

                    float targetX = knightModel.x + (knightModel.width / 2f);
                    float targetY = knightModel.y + (knightModel.height / 2f);
                    float startX = mosquito.x + (mosquito.width / 2f);
                    float startY = mosquito.y + (mosquito.height / 2f);

                    Vector2 direction = new Vector2(targetX - startX, targetY - startY).nor();
                    mosquito.isFacingRight = direction.x > 0;

                    float baseAngle = (float) (Math.atan2(direction.y, Math.abs(direction.x)) * MathUtils.radiansToDegrees);
                    mosquito.rotationAngle = mosquito.isFacingRight ? baseAngle : -baseAngle;

                    if (mosquito.actionTimer <= 0) {
                        mosquito.currentState = Mosquito.State.DASHING;
                        mosquito.actionTimer = 2.0f;
                        mosquito.stateTime = 0f;

                        mosquito.dashStartX = mosquito.x;
                        mosquito.dashStartY = mosquito.y;

                        mosquito.dashVelX = direction.x * mosquito.speed;
                        mosquito.dashVelY = direction.y * mosquito.speed;
                    }
                    break;

                case DASHING:
                    mosquito.velocityX = mosquito.dashVelX;
                    mosquito.velocityY = mosquito.dashVelY;
                    mosquito.x += mosquito.velocityX * delta;
                    mosquito.y += mosquito.velocityY * delta;

                    boolean hitWall = false;
                    Rectangle coreBox = new Rectangle(
                        mosquito.x + 20f,
                        mosquito.y + 20f,
                        mosquito.width - 40f,
                        mosquito.height - 40f
                    );

                    for (SolidBlock block : mapBlocks) {
                        Rectangle blockBox = new Rectangle(block.x, block.y, block.width, block.height);
                        if (coreBox.overlaps(blockBox)) {
                            hitWall = true;
                            mosquito.x -= mosquito.velocityX * delta;
                            mosquito.y -= mosquito.velocityY * delta;
                            break;
                        }
                    }

                    float distanceDashed = Vector2.dst(
                        mosquito.dashStartX, mosquito.dashStartY,
                        mosquito.x, mosquito.y
                    );

                    if (hitWall || distanceDashed >= mosquito.maxDashRange || mosquito.actionTimer <= 0) {
                        mosquito.currentState = Mosquito.State.IDLE;
                        mosquito.actionTimer = 0.5f;
                        mosquito.stateTime = 0f;
                    }
                    break;
            }
        }

        checkCollisionWithKnight();
        checkDamageFromKnight();
    }
}
