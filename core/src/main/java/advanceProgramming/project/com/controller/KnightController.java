package advanceProgramming.project.com.controller;

import advanceProgramming.project.com.helper.CameraShake;
import advanceProgramming.project.com.helper.SettingsManager;
import advanceProgramming.project.com.helper.SolidBlock;
import advanceProgramming.project.com.model.Knight;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class KnightController {
    private final Knight model;
    private final Array<SolidBlock> mapBlocks;
    private final Sound slashSound;

    public KnightController(Knight model, Array<SolidBlock> mapBlocks) {
        this.model = model;
        this.mapBlocks = mapBlocks;

        slashSound = Gdx.audio.newSound(Gdx.files.internal("Audio/slash.ogg"));
    }

    public void update(float delta) {
        model.stateTime += delta;
        model.playTime += delta;
        model.visualSoul += (model.getCurrentSoul() - model.visualSoul) * 5f * delta;

        if (model.isNoclip) {
            float noclipSpeed = 800f;
            model.velocityX = 0;
            model.velocityY = 0;

            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D))
                model.x += noclipSpeed * delta;
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A))
                model.x -= noclipSpeed * delta;
            if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W))
                model.y += noclipSpeed * delta;
            if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S))
                model.y -= noclipSpeed * delta;

            return;
        }

        if (model.getCurrentHp() <= 0) {
            model.setCurrentHp(model.getMaxHp());
            model.setCurrentSoul(0);
            model.deathCount++;

            model.x = model.spawnX;
            model.y = model.spawnY;
            model.velocityX = 0;
            model.velocityY = 0;

            model.currentState = Knight.State.IDLE;
            model.stateTime = 0f;
            model.isGrounded = false;
            model.isTouchingWall = false;

            model.isSlashActive = false;
            model.slashStateTime = 0f;
            model.attackTimer = 0f;
            model.dashTimer = 0f;

            model.invincibleTimer = 1.0f;
        }

        if (model.invincibleTimer > 0) {
            model.invincibleTimer -= delta;
        }

        if (model.dashCooldownTimer > 0) {
            model.dashCooldownTimer -= delta;
        }

        // 💡 آپدیت مکان Vengeful Spirit و برخورد با دیوار
        for (int i = model.vengefulSpirits.size - 1; i >= 0; i--) {
            Knight.VengefulSpirit vs = model.vengefulSpirits.get(i);
            vs.stateTime += delta;
            vs.x += vs.velocityX * delta; // حرکت بدون تاثیر جاذبه

            Rectangle vsBox = new Rectangle(vs.x, vs.y, vs.width, vs.height);
            for (SolidBlock block : mapBlocks) {
                if (vsBox.overlaps(new Rectangle(block.x, block.y, block.width, block.height))) {
                    vs.active = false; // نابودی در صورت برخورد با دیوار
                    break;
                }
            }
            if (!vs.active) model.vengefulSpirits.removeIndex(i);
        }

//   Howling Wraiths
        for (int i = model.howlingWraiths.size - 1; i >= 0; i--) {
            Knight.HowlingWraiths hw = model.howlingWraiths.get(i);
            hw.stateTime += delta;
            //
            int expectedTick = (int) (hw.stateTime / 0.2f);
            if (expectedTick > hw.currentTick) {
                hw.currentTick = expectedTick;
                hw.hitInCurrentTick.clear(); //
            }
            if (hw.stateTime >= hw.maxDuration) {
                model.howlingWraiths.removeIndex(i);
            }
        }

        if (model.knockbackTimer > 0) {
            model.knockbackTimer -= delta;

            // 💡 اضافه کردن اصطکاک: سرعتِ پرتاب به مرور کم می‌شود
            model.velocityX = MathUtils.lerp(model.velocityX, 0, 5f * delta);
        } else {
            handleInput();
        }
        checkSpikePogo(); // 💡 فراخوانی چک کردن خارها قبل از اعمال فیزیک
        applyPhysics(delta);
        updateState(delta);
    }

    private void handleInput() {
        model.velocityX = 0;

        if (model.currentState == Knight.State.FOCUSING ||
            model.currentState == Knight.State.TALKING ||
            model.currentState == Knight.State.DOWN_ATTACKING ||
            model.currentState == Knight.State.UP_ATTACKING ||
            model.currentState == Knight.State.ATTACKING ||
            model.currentState == Knight.State.DASHING ||
            model.currentState == Knight.State.CASTING_VENGEFUL ||
            model.currentState == Knight.State.CASTING_HOWLING) {
            return;
        }

        SettingsManager settings = SettingsManager.getInstance();

        int keyLeft = settings.getKey("keyLeft", com.badlogic.gdx.Input.Keys.LEFT);
        int keyRight = settings.getKey("keyRight", com.badlogic.gdx.Input.Keys.RIGHT);
        int keyJump = settings.getKey("keyJump", com.badlogic.gdx.Input.Keys.Z);
        int keyAttack = settings.getKey("keyAttack", com.badlogic.gdx.Input.Keys.X);
        int keyDash = settings.getKey("keyDash", com.badlogic.gdx.Input.Keys.C);
        int keySpell = settings.getKey("keySpell", com.badlogic.gdx.Input.Keys.S);
        int keyHowling = settings.getKey("keyHowling", com.badlogic.gdx.Input.Keys.D);
        int keyFocus = settings.getKey("keyFocus", com.badlogic.gdx.Input.Keys.A);

        if (Gdx.input.isKeyPressed(keyFocus) && model.isGrounded) {
            if (model.getCurrentSoul() >= 33 && model.getCurrentHp() < model.getMaxHp()) {
                model.currentState = Knight.State.FOCUSING;
                model.focusTimer = 0f;
                model.playFocusSound();
                return;
            }
        }

        if (Gdx.input.isKeyJustPressed(keyDash)) {
            if (model.dashCooldownTimer <= 0) {
                model.currentState = Knight.State.DASHING;
                model.dashTimer = 0;
                model.stateTime = 0;
                return;
            }
        }

        if (Gdx.input.isKeyJustPressed(keySpell)) {
            if (model.getCurrentSoul() >= 33) {
                model.setCurrentSoul(model.getCurrentSoul() - 33);
                model.castTimer = 0f;
                model.stateTime = 0f;
                model.velocityX = 0;

                model.currentState = Knight.State.CASTING_VENGEFUL;
                CameraShake.shake(8f, 0.2f);
                float vX = !model.isFacingRight ? 1200f : -1200f;
                float startX = !model.isFacingRight ? model.x + (model.width / 2f) : model.x + (model.width / 2f) - 350f;
                model.vengefulSpirits.add(new Knight.VengefulSpirit(startX, model.y + 20f, vX));
            }
        }

        if (Gdx.input.isKeyJustPressed(keyHowling)) {
            if (model.getCurrentSoul() >= 33) {
                model.setCurrentSoul(model.getCurrentSoul() - 33);
                model.castTimer = 0f;
                model.stateTime = 0f;
                model.velocityX = 0;

                model.currentState = Knight.State.CASTING_HOWLING;
                CameraShake.shake(12f, 0.4f);
                float magicWidth = 300;
                float hwX = model.x + (model.width / 2f) - (magicWidth / 2f);
                float hwY = model.y + 20f;
                model.howlingWraiths.add(new Knight.HowlingWraiths(hwX, hwY));
            }
        }

        if (Gdx.input.isKeyPressed(keyRight)) {
            model.velocityX = model.speed;
            model.isFacingRight = false;
        } else if (Gdx.input.isKeyPressed(keyLeft)) {
            model.velocityX = -model.speed;
            model.isFacingRight = true;
        }

        if (Gdx.input.isKeyJustPressed(keyJump)) {
            if (model.isGrounded) {
                model.velocityY = model.jumpVelocity;
                model.isGrounded = false;
            } else if (model.isTouchingWall) {
                model.velocityY = model.jumpVelocity;
                model.isTouchingWall = false;
                model.canDoubleJump = true;
                if (!model.isFacingRight) {
                    model.velocityX = -model.speed;
                    model.isFacingRight = true;
                } else {
                    model.velocityX = model.speed;
                    model.isFacingRight = false;
                }
            } else if (model.canDoubleJump) {
                model.velocityY = model.jumpVelocity;
                model.canDoubleJump = false;
                model.currentState = Knight.State.DOUBLE_JUMPING;
                model.stateTime = 0;
            }
        }

        if (Gdx.input.isKeyJustPressed(keyAttack)) {
            if (!SettingsManager.getInstance().isSfxMuted()) {

            }
            if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.UP)) {
                model.currentState = Knight.State.UP_ATTACKING;
            } else if (!model.isGrounded && Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.DOWN)) {
                model.currentState = Knight.State.DOWN_ATTACKING;
            } else {
                model.currentState = Knight.State.ATTACKING;
            }
            model.attackTimer = 0;
            model.stateTime = 0;
            model.isSlashActive = true;
            model.slashStateTime = 0;
            model.hasPogoed = false;
        }
    }

    private void checkSpikePogo() {
        if (model.isSlashActive && model.currentState == Knight.State.DOWN_ATTACKING) {
            float kHitBoxWidth = 60f;
            float kHitBoxX = model.x + (model.width / 2f) - (kHitBoxWidth / 2f);

            Rectangle pogoBox = new Rectangle(kHitBoxX + (kHitBoxWidth / 2f) - 40f, model.y - 30f, 80f, 60f);

            for (SolidBlock block : mapBlocks) {
                if (block.isDeadly) {
                    if (pogoBox.overlaps(new Rectangle(block.x, block.y, block.width, block.height))) {
                        model.triggerPogoBounce();
                        break;
                    }
                }
            }
        }
    }

    private void applyPhysics(float delta) {
        // applying gravity
        if (model.currentState == Knight.State.DASHING) {
            model.velocityY = 0;
            if (model.isFacingRight) {
                model.velocityX = -model.dashSpeed;
            } else {
                model.velocityX = model.dashSpeed;
            }
        } else {
            boolean isJumpKeyHeld = Gdx.input.isKeyPressed(Input.Keys.Z);
            if (model.velocityY > 0 && !isJumpKeyHeld) {
                model.velocityY += (model.gravity * 3.5f) * delta;
            } else {
                model.velocityY += model.gravity * delta;
            }
        }

        model.isTouchingWall = false;


        // region checking collision in X axis
        float oldX = model.x;
        model.x += model.velocityX * delta;

        int collisionX = getCollisionType();
        if (collisionX == 2) {
            handleSpikeRespawn();
            return;
        } else if (collisionX == 1) {
            model.x = oldX;

            model.velocityX = 0;

            if (!model.isGrounded && model.currentState != Knight.State.DASHING) {
                model.isTouchingWall = true;
            }
        }
        // endregion

        //region checking collision in Y axis
        float oldY = model.y;
        model.y += model.velocityY * delta;
        model.isGrounded = false;

        Rectangle knightBox = getKnightHitbox();
        boolean hitWallY = false;
        Rectangle hitBlock = null;

        for (SolidBlock block : mapBlocks) {
            Rectangle blockBox = new Rectangle(block.x, block.y, block.width, block.height);
            if (knightBox.overlaps(blockBox)) {
                if (block.isDeadly) {
                    handleSpikeRespawn();
                    return;
                }
                hitWallY = true;
                hitBlock = blockBox;
                break;
            }
        }

        if (hitWallY) {
            if (model.velocityY < 0) {
                model.y = hitBlock.y + hitBlock.height;
                model.isGrounded = true;
                model.canDoubleJump = true;

            } else if (model.velocityY > 0) {
                model.y = hitBlock.y - knightBox.height;
            }

            model.velocityY = 0;
        }
        //endregion


        if (model.isTouchingWall && model.velocityY < 0) {
            if (model.velocityY < model.wallSlideSpeed) {
                model.velocityY = model.wallSlideSpeed;
            }
        }
    }

    private Rectangle getKnightHitbox() {
        float hitBoxWidth = 60f;
        float hitBoxHeight = 130f;
        float hitBoxX = model.x + (model.width / 2f) - (hitBoxWidth / 2f);
        float hitBoxY = model.y;
        return new Rectangle(hitBoxX, hitBoxY, hitBoxWidth, hitBoxHeight);
    }

    private int getCollisionType() {
        Rectangle knightBox = getKnightHitbox();
        int result = 0;

        for (SolidBlock block : mapBlocks) {
            Rectangle blockBox = new Rectangle(block.x, block.y, block.width, block.height);

            if (knightBox.overlaps(blockBox)) {
                if (block.isDeadly) {
                    return 2;
                }
                result = 1;
            }
        }
        return result;
    }

    private void handleSpikeRespawn() {
        if (model.invincibleTimer <= 0) {
            model.takeDamage(1);

            model.x = model.spawnX;
            model.y = model.spawnY;
            model.velocityX = 0;
            model.velocityY = 0;

            model.currentState = Knight.State.IDLE;
            model.stateTime = 0f;
            model.isGrounded = false;
            model.isTouchingWall = false;
            model.canDoubleJump = true;

            model.isSlashActive = false;
            model.slashStateTime = 0f;
            model.attackTimer = 0f;
            model.dashTimer = 0f;
        }
    }

    private void updateState(float delta) {
        if (model.currentState == Knight.State.CASTING_VENGEFUL || model.currentState == Knight.State.CASTING_HOWLING) {
            model.castTimer += delta;
            if (model.castTimer >= model.castDuration) {
                model.currentState = !model.isGrounded ? Knight.State.FALLING : Knight.State.IDLE;
            }
            return;
        }

        if (model.currentState == Knight.State.FOCUSING) {
            int currentFocusKey = advanceProgramming.project.com.helper.SettingsManager.getInstance().getKey("keyFocus", com.badlogic.gdx.Input.Keys.A);

            if (Gdx.input.isKeyPressed(currentFocusKey)) {
                model.focusTimer += delta;

                if (model.focusTimer >= model.focusDuration) {
                    model.setCurrentSoul(model.getCurrentSoul() - 33);
                    model.setCurrentHp(model.getCurrentHp() + 1);
                    model.focusTimer = 0f;

                    if (model.getCurrentHp() >= model.getMaxHp() || model.getCurrentSoul() < 33) {
                        model.currentState = Knight.State.IDLE;
                        model.stopFocusSound();
                    }
                }
            } else {
                model.currentState = Knight.State.IDLE;
                model.focusTimer = 0f;
                model.stopFocusSound();
            }
            return;
        }

        if (model.currentState == Knight.State.TALKING) {
            return;
        }
        if (model.isSlashActive) {
            model.slashStateTime += delta;
            if (model.slashStateTime >= model.attackDuration) {
                model.isSlashActive = false;
            }
        }

        if (model.currentState == Knight.State.ATTACKING ||
            model.currentState == Knight.State.DOWN_ATTACKING ||
            model.currentState == Knight.State.UP_ATTACKING) {
            model.attackTimer += delta;
            if (model.attackTimer >= model.attackDuration) {
                model.currentState = !model.isGrounded ? Knight.State.FALLING : Knight.State.IDLE;
            }
        } else {
            if (model.currentState == Knight.State.DASHING) {
                model.dashTimer += delta;

                if (model.dashTimer >= model.dashDuration) {
                    model.currentState = Knight.State.FALLING;
                    model.dashCooldownTimer = model.dashCooldown;
                }
                return;
            }
            if (!model.isGrounded) {
                if (model.isTouchingWall && model.velocityY < 0) {
                    model.currentState = Knight.State.WALL_SLIDE;
                } else if (model.velocityY > 0) {
                    if (model.currentState != Knight.State.JUMPING && model.currentState != Knight.State.DOUBLE_JUMPING) {
                        model.currentState = Knight.State.JUMPING;
                        model.stateTime = 0;
                    }
                } else {
                    if (model.currentState != Knight.State.FALLING) {
                        model.currentState = Knight.State.FALLING;
                        model.stateTime = 0;
                    }
                }
            } else if (model.velocityX != 0) {
                if (model.currentState == Knight.State.IDLE) {
                    model.currentState = Knight.State.START_RUNNING;
                    model.stateTime = 0;
                } else if (model.currentState == Knight.State.START_RUNNING) {
                    if (model.stateTime >= model.startRunDuration) {
                        model.currentState = Knight.State.RUNNING;
                        model.stateTime = 0;
                    }
                } else if (model.currentState != Knight.State.RUNNING) {
                    model.currentState = Knight.State.RUNNING;
                }
            } else {
                model.currentState = Knight.State.IDLE;
            }
        }
    }
}
