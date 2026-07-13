package advanceProgramming.project.com.controller;

import advanceProgramming.project.com.helper.AchievementManager;
import advanceProgramming.project.com.helper.SolidBlock;
import advanceProgramming.project.com.model.FalseKnight;
import advanceProgramming.project.com.model.Knight;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class FalseKnightController extends EnemyController {
    private final FalseKnight boss;
    private boolean hasSpawnedWave = false;

    public FalseKnightController(FalseKnight boss, Knight knight, Array<SolidBlock> mapBlocks) {
        super(boss, knight, mapBlocks);
        this.boss = boss;
    }

    @Override
    public void update(float realDelta) {

        float delta = (boss.phase == 2) ? realDelta * 1.6f : realDelta;

        if (!boss.isFightStarted && !boss.isDead()) {
            boss.velocityX = 0;
            applyGravityAndYCollision(delta, new Rectangle(boss.x, boss.y, boss.width, boss.height));
            return;
        }

        boss.stateTime += delta;
        if (boss.invincibleTimer > 0) boss.invincibleTimer -= delta;

        if (boss.recentHitTimer > 0) {
            boss.recentHitTimer -= delta;
            if (boss.recentHitTimer <= 0) boss.recentHits = 0;
        }

        if (boss.isDead() && boss.currentState != FalseKnight.State.DEAD) {
            boss.currentState = FalseKnight.State.DEAD;
            boss.stateTime = 0f;
            boss.shakeTimeLeft = 1.0f;
            boss.shakeIntensity = 25f;
        }

        Rectangle bossBox = new Rectangle(boss.x, boss.y, boss.width, boss.height);
        boss.actionTimer -= delta;

        updateShockwaves(delta);

        switch (boss.currentState) {
            case DEAD:
                boss.velocityX = 0;
                applyGravityAndYCollision(delta, bossBox);
                return;

            case IDLE:
                boss.velocityX = 0;
                applyGravityAndYCollision(delta, bossBox);
                boss.isFacingRight = knightModel.x > boss.x;

                if (boss.actionTimer <= 0) {
                    float dist = Math.abs(knightModel.x - boss.x);

                    if (dist < 350f) {
                        if (boss.phase == 2 && boss.lastMove != FalseKnight.State.POWER_SLAM && Math.random() > 0.4) {
                            boss.currentState = FalseKnight.State.POWER_SLAM;
                            boss.velocityY = 1000f;
                            boss.velocityX = boss.isFacingRight ? boss.speed : -boss.speed;
                            hasSpawnedWave = false;
                        } else if (boss.lastMove != FalseKnight.State.MACE_SLAM) {
                            boss.currentState = FalseKnight.State.MACE_SLAM;
                        } else {
                            boss.currentState = FalseKnight.State.JUMP;
                            boss.velocityY = 1200f;
                            boss.velocityX = boss.isFacingRight ? boss.speed * 1.5f : -boss.speed * 1.5f;
                        }
                    } else {
                        if (Math.random() > 0.5 && boss.lastMove != FalseKnight.State.START_RUN) {
                            boss.currentState = FalseKnight.State.START_RUN;
                        } else if (boss.lastMove != FalseKnight.State.JUMP) {
                            boss.currentState = FalseKnight.State.JUMP;
                            boss.velocityY = 1200f;
                            boss.velocityX = boss.isFacingRight ? boss.speed * 1.8f : -boss.speed * 1.8f;
                        } else {
                            boss.currentState = FalseKnight.State.START_RUN;
                        }
                    }

                    boss.stateTime = 0f;
                    boss.actionTimer = 1.0f;
                    boss.lastMove = boss.currentState;
                }
                break;

            case DEFENSIVE_LEAP:
                applyXCollision(delta, bossBox);
                if (applyGravityAndYCollision(delta, bossBox)) {
                    boss.currentState = FalseKnight.State.IDLE;
                    boss.actionTimer = 1.0f;
                }
                break;

            case START_RUN:
                boss.velocityX = 0;
                applyGravityAndYCollision(delta, bossBox);
                if (boss.actionTimer <= 0) {
                    boss.currentState = FalseKnight.State.RUN;
                    boss.actionTimer = 2.0f;
                }
                break;

            case RUN:
                boss.velocityX = boss.isFacingRight ? boss.speed * 1.5f : -boss.speed * 1.5f;
                applyXCollision(delta, bossBox);
                applyGravityAndYCollision(delta, bossBox);

                if (boss.actionTimer <= 0 || Math.abs(knightModel.x - boss.x) < 250f || boss.velocityX == 0) {
                    boss.currentState = FalseKnight.State.MACE_SLAM;
                    boss.lastMove = FalseKnight.State.MACE_SLAM;
                    boss.stateTime = 0f;
                    boss.actionTimer = 1.0f;
                }
                break;

            case JUMP:
                applyXCollision(delta, bossBox);
                if (applyGravityAndYCollision(delta, bossBox)) {
                    boss.currentState = FalseKnight.State.LAND;
                    boss.velocityX = 0;
                    boss.stateTime = 0f;
                    boss.actionTimer = 0.5f;
                    boss.shakeTimeLeft = 0.2f;
                    boss.shakeIntensity = 5f;
                }
                break;

            case LAND:
                boss.velocityX = 0;
                applyGravityAndYCollision(delta, bossBox);
                if (boss.actionTimer <= 0) {
                    boss.currentState = FalseKnight.State.IDLE;
                    boss.actionTimer = 1.0f;
                }
                break;

            case MACE_SLAM:
                boss.velocityX = 0;
                applyGravityAndYCollision(delta, bossBox);

                if (boss.stateTime > 0.4f && boss.stateTime < 0.6f) {
                    checkMaceHitbox();
                    boss.shakeTimeLeft = 0.15f;
                    boss.shakeIntensity = 8f;
                }

                if (boss.actionTimer <= 0) {
                    boss.currentState = FalseKnight.State.IDLE;
                    boss.actionTimer = 1.0f;
                }
                break;

            case POWER_SLAM:
                applyXCollision(delta, bossBox);
                boolean landed = applyGravityAndYCollision(delta, bossBox);

                if (landed) {
                    boss.velocityX = 0;

                    if (!hasSpawnedWave) {
                        checkMaceHitbox();
                        boss.shakeTimeLeft = 0.4f;
                        boss.shakeIntensity = 18f;
                        spawnShockwaves();
                        hasSpawnedWave = true;
                        boss.actionTimer = 1.0f;
                    }
                }

                if (hasSpawnedWave) {
                    if (boss.actionTimer <= 0) {
                        boss.currentState = FalseKnight.State.IDLE;
                        boss.actionTimer = 1.5f;
                    }
                }
                break;

            case BEFORE_STUN:
                boss.velocityX = 0;
                applyGravityAndYCollision(delta, bossBox);
                if (boss.stateTime >= 1.0f) {
                    boss.currentState = FalseKnight.State.STUN;
                    boss.actionTimer = boss.stunDuration;
                }
                break;

            case STUN:
                boss.velocityX = 0;
                applyGravityAndYCollision(delta, bossBox);
                if (boss.actionTimer <= 0) {
                    boss.currentState = FalseKnight.State.AFTER_STUN;
                    boss.stateTime = 0f;
                }
                break;

            case AFTER_STUN:
                boss.velocityX = 0;
                applyGravityAndYCollision(delta, bossBox);
                if (boss.stateTime >= 1.0f) {
                    boss.currentState = FalseKnight.State.IDLE;
                    boss.actionTimer = 1.0f;
                    boss.currentHits = 0;
                    boss.phase = 2;
                }
                break;
        }

        if (boss.currentState != FalseKnight.State.STUN && boss.currentState != FalseKnight.State.BEFORE_STUN) {
            checkCollisionWithKnight();
        }
        checkBossDamage();
    }

    private boolean applyGravityAndYCollision(float delta, Rectangle bossBox) {
        boss.velocityY += boss.gravity * delta;
        boss.y += boss.velocityY * delta;
        bossBox.setPosition(boss.x, boss.y);

        boolean landed = false;
        for (SolidBlock block : mapBlocks) {
            Rectangle blockBox = new Rectangle(block.x, block.y, block.width, block.height);
            if (bossBox.overlaps(blockBox)) {
                if (boss.velocityY < 0) {
                    boss.y = block.y + block.height;
                    boss.velocityY = 0;
                    landed = true;
                } else if (boss.velocityY > 0) {
                    boss.y = block.y - boss.height;
                    boss.velocityY = 0;
                }
                bossBox.setPosition(boss.x, boss.y);
            }
        }
        return landed;
    }

    private void applyXCollision(float delta, Rectangle bossBox) {
        if (boss.velocityX == 0) return;

        float oldX = boss.x;
        boss.x += boss.velocityX * delta;
        bossBox.setPosition(boss.x, boss.y);

        for (SolidBlock block : mapBlocks) {
            Rectangle blockBox = new Rectangle(block.x, block.y, block.width, block.height);
            if (bossBox.overlaps(blockBox)) {
                boss.x = oldX;
                boss.velocityX = 0;
                bossBox.setPosition(boss.x, boss.y);
                break;
            }
        }
    }

    private void updateShockwaves(float delta) {
        Rectangle knightBox = new Rectangle(knightModel.x, knightModel.y, knightModel.width, knightModel.height);

        for (int i = boss.shockwaves.size - 1; i >= 0; i--) {
            FalseKnight.Shockwave wave = boss.shockwaves.get(i);
            wave.stateTime += delta;
            wave.speed += 300f * delta;

            float waveVelX = wave.isFacingRight ? wave.speed : -wave.speed;
            wave.x += waveVelX * delta;

            Rectangle waveBox = new Rectangle(wave.x, wave.y, wave.width, wave.height);

            if (waveBox.overlaps(knightBox) && knightModel.invincibleTimer <= 0) {
                knightModel.takeDamage(1);
            }

            if (wave.x < boss.arenaLeftBound || wave.x + wave.width > boss.arenaRightBound) {
                boss.shockwaves.removeIndex(i);
            }
        }
    }

    private void spawnShockwaves() {
        float hitX = boss.isFacingRight ? boss.x + boss.width - 50f : boss.x - 50f;
        boss.shockwaves.add(new FalseKnight.Shockwave(hitX, boss.y, boss.isFacingRight));
    }

    private void checkMaceHitbox() {
        float hitX = boss.isFacingRight ? boss.x + boss.width - 50f : boss.x - 150f;
        Rectangle maceBox = new Rectangle(hitX, boss.y, 250f, 200f);
        Rectangle knightBox = new Rectangle(knightModel.x, knightModel.y, knightModel.width, knightModel.height);

        if (maceBox.overlaps(knightBox)) {
            knightModel.takeDamage(1);
        }
    }

    private void checkBossDamage() {
        if (boss.isDead() || boss.invincibleTimer > 0) return;

        Rectangle bossBox = new Rectangle(boss.x, boss.y, boss.width, boss.height);
        boolean isHitSuccessfully = false;


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
            } else {
                slashY = knightModel.y + 10f;
                slashX = !knightModel.isFacingRight ? kHitBoxX + kHitBoxWidth : kHitBoxX - slashWidth;
            }

            Rectangle slashBox = new Rectangle(slashX, slashY, slashWidth, slashHeight);

            if (bossBox.overlaps(slashBox)) {
                if (knightModel.currentState == Knight.State.DOWN_ATTACKING) {
                    knightModel.triggerPogoBounce();
                }
                knightModel.gainSoul();
                isHitSuccessfully = true;
            }
        }

        for (Knight.VengefulSpirit vs : knightModel.vengefulSpirits) {
            Rectangle vsBox = new Rectangle(vs.x, vs.y, vs.width, vs.height);
            if (vsBox.overlaps(bossBox) && !vs.damagedEnemies.contains(boss, true)) {
                vs.damagedEnemies.add(boss);
                isHitSuccessfully = true;
            }
        }

        for (Knight.HowlingWraiths hw : knightModel.howlingWraiths) {
            Rectangle hwBox = new Rectangle(hw.x, hw.y, hw.width, hw.height);
            if (hwBox.overlaps(bossBox) && !hw.hitInCurrentTick.contains(boss, true)) {
                hw.hitInCurrentTick.add(boss);
                isHitSuccessfully = true;
            }
        }

        if (knightModel.currentState == Knight.State.DASHING && knightModel.hasSharpShadow) {
            float kHitBoxWidth = 60f;
            float kHitBoxX = knightModel.x + (knightModel.width / 2f) - (kHitBoxWidth / 2f);
            Rectangle knightBox = new Rectangle(kHitBoxX, knightModel.y, kHitBoxWidth, 130f);

            if (bossBox.overlaps(knightBox)) {
                isHitSuccessfully = true;
            }
        }

        if (isHitSuccessfully) {
            boss.invincibleTimer = 0.5f;

            if (boss.currentState != FalseKnight.State.STUN && boss.currentState != FalseKnight.State.BEFORE_STUN && boss.currentState != FalseKnight.State.AFTER_STUN) {
                boss.currentHits++;

                if (boss.currentHits >= boss.hitsToStun) {
                    if (!boss.hasBeenStunned) {
                        boss.currentState = FalseKnight.State.BEFORE_STUN;
                        boss.stateTime = 0f;
                        boss.hasBeenStunned = true;
                        return;
                    } else {
                        boss.hp = 0;
                        AchievementManager.getInstance().unlockAchievement(AchievementManager.ACH_FALSE_KNIGHT, "Boss Fight");
                        boss.currentState = FalseKnight.State.DEAD;
                        boss.stateTime = 0f;
                        return;
                    }
                }
            }

            if (boss.currentState != FalseKnight.State.STUN && boss.currentState != FalseKnight.State.BEFORE_STUN) {
                boss.recentHits++;
                boss.recentHitTimer = 2.0f;

                if (boss.recentHits >= 3 && boss.currentState == FalseKnight.State.IDLE) {
                    boss.currentState = FalseKnight.State.DEFENSIVE_LEAP;
                    boss.velocityY = 900f;
                    boss.velocityX = (knightModel.x > boss.x) ? -boss.speed * 2 : boss.speed * 2;
                    boss.recentHits = 0;
                }
            } else if (boss.currentState == FalseKnight.State.STUN) {
                boss.stunHits++;
                if (boss.stunHits >= 3) {
                    boss.currentState = FalseKnight.State.AFTER_STUN;
                    boss.stateTime = 0f;
                    boss.stunHits = 0;
                }
            }
        }
    }

    @Override
    public void checkCollisionWithKnight() {
        Rectangle bossBox = new Rectangle(boss.x, boss.y, boss.width, boss.height);
        Rectangle knightBox = new Rectangle(knightModel.x, knightModel.y, knightModel.width, knightModel.height);

        if (bossBox.overlaps(knightBox)) {

            if (knightModel.currentState == Knight.State.DASHING && knightModel.hasSharpShadow) {
                return;
            }

            if (knightModel.invincibleTimer <= 0 && boss.currentState != FalseKnight.State.DEAD) {
                knightModel.takeDamage(1);
            }
        }
    }
}
