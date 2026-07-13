package advanceProgramming.project.com.view;

import advanceProgramming.project.com.model.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class EnemyView {
    // region variables for animations
    private Animation<TextureRegion> tiktikWalkAnim;
    private Animation<TextureRegion> tiktikDeathAnim;
    private Animation<TextureRegion> mosqFlyAnim;
    private Animation<TextureRegion> mosqWarningAnim;
    private Animation<TextureRegion> mosqDashAnim;
    private Animation<TextureRegion> mosqDeathAnim;
    private Animation<TextureRegion> huskWalkAnim;
    private Animation<TextureRegion> huskIdleAnim;
    private Animation<TextureRegion> huskChargeAnim;
    private Animation<TextureRegion> huskDeathAnim;
    private Animation<TextureRegion> huskWarningAnim;
    private Animation<TextureRegion> cgIdleAnim;
    private Animation<TextureRegion> cgRunAnim;
    private Animation<TextureRegion> cgShootAnim;
    private Animation<TextureRegion> cgDeathAnim;
    private Animation<TextureRegion> cgLaserAnim;
    private TextureAtlas laserAtlas;
    // endregion

    public EnemyView() {
        loadAssets();
    }

    private void loadAssets() {
        //region Loading Tiktik assets :
        Array<TextureRegion> tiktikFrames = new Array<>();
        for (int i = 0; i <= 3; i++) {
            tiktikFrames.add(new TextureRegion(new Texture("Animation/Tiktik/Walk_" + String.format("%03d", i) + ".png")));
        }
        tiktikWalkAnim = new Animation<>(0.15f, tiktikFrames, Animation.PlayMode.LOOP);

        Array<TextureRegion> tiktikDeathFrames = new Array<>();
        for (int i = 0; i <= 6; i++) {
            tiktikDeathFrames.add(new TextureRegion(new Texture("Animation/Tiktik/Death Air_" + String.format("%03d", i) + ".png")));
        }
        tiktikDeathAnim = new Animation<>(0.1f, tiktikDeathFrames, Animation.PlayMode.NORMAL);
        //endregion

        //region Loading Mosquito assets:
        Array<TextureRegion> flyFrames = new Array<>();
        for (int i = 0; i <= 7; i++) {
            flyFrames.add(new TextureRegion(new Texture("Animation/Mosquito/Idle_" + String.format("%03d", i) + ".png")));
        }
        mosqFlyAnim = new Animation<>(0.10f, flyFrames, Animation.PlayMode.LOOP);

        Array<TextureRegion> warningFrames = new Array<>();
        for (int i = 0; i <= 5; i++) {
            warningFrames.add(new TextureRegion(new Texture("Animation/Mosquito/Attack Anticipate_" + String.format("%03d", i) + ".png")));
        }
        mosqWarningAnim = new Animation<>(0.10f, warningFrames, Animation.PlayMode.NORMAL);

        Array<TextureRegion> dashFrames = new Array<>();
        for (int i = 0; i <= 2; i++) {
            dashFrames.add(new TextureRegion(new Texture("Animation/Mosquito/Attack_" + String.format("%03d", i) + ".png")));
        }
        mosqDashAnim = new Animation<>(0.10f, dashFrames, Animation.PlayMode.LOOP);

        Array<TextureRegion> mosqDeath = new Array<>();
        for (int i = 0; i <= 4; i++) {
            mosqDeath.add(new TextureRegion(new Texture("Animation/Mosquito/Death Air_" + String.format("%03d", i) + ".png")));
        }
        mosqDeathAnim = new Animation<>(0.15f, mosqDeath, Animation.PlayMode.NORMAL);
        //endregion

        // region Loading HuskHornhead assets
        Array<TextureRegion> huskWalk = new Array<>();
        for (int i = 0; i <= 6; i++)
            huskWalk.add(new TextureRegion(new Texture("Animation/Husk_Hornhead/Walk_" + String.format("%03d", i) + ".png")));
        huskWalkAnim = new Animation<>(0.15f, huskWalk, Animation.PlayMode.LOOP);

        Array<TextureRegion> huskIdle = new Array<>();
        for (int i = 0; i <= 5; i++)
            huskIdle.add(new TextureRegion(new Texture("Animation/Husk_Hornhead/Idle_" + String.format("%03d", i) + ".png")));
        huskIdleAnim = new Animation<>(0.2f, huskIdle, Animation.PlayMode.LOOP);

        Array<TextureRegion> huskWarning = new Array<>();
        for (int i = 0; i <= 4; i++)
            huskWarning.add(new TextureRegion(new Texture("Animation/Husk_Hornhead/Attack Anticipate_" + String.format("%03d", i) + ".png")));
        huskWarningAnim = new Animation<>(0.1f, huskWarning, Animation.PlayMode.NORMAL);

        Array<TextureRegion> huskCharge = new Array<>();
        for (int i = 0; i <= 11; i++)
            huskCharge.add(new TextureRegion(new Texture("Animation/Husk_Hornhead/Attack Lunge_" + String.format("%03d", i) + ".png")));
        huskChargeAnim = new Animation<>(0.08f, huskCharge, Animation.PlayMode.LOOP);

        Array<TextureRegion> huskDeath = new Array<>();
        for (int i = 0; i <= 8; i++)
            huskDeath.add(new TextureRegion(new Texture("Animation/Husk_Hornhead/Death Land_" + String.format("%03d", i) + ".png")));
        huskDeathAnim = new Animation<>(0.15f, huskDeath, Animation.PlayMode.NORMAL);
        // endregion

        // region Loading  CrystalGuardian assets
        Array<TextureRegion> cgIdle = new Array<>();
        for (int i = 0; i <= 4; i++)
            cgIdle.add(new TextureRegion(new Texture("Animation/Crystallized/Idle_" + String.format("%03d", i) + ".png")));
        cgIdleAnim = new Animation<>(0.2f, cgIdle, Animation.PlayMode.LOOP);

        Array<TextureRegion> cgRun = new Array<>();
        for (int i = 0; i <= 5; i++)
            cgRun.add(new TextureRegion(new Texture("Animation/Crystallized/Run_" + String.format("%03d", i) + ".png")));
        cgRunAnim = new Animation<>(0.15f, cgRun, Animation.PlayMode.LOOP);

        Array<TextureRegion> cgShoot = new Array<>();
        for (int i = 0; i <= 6; i++)
            cgShoot.add(new TextureRegion(new Texture("Animation/Crystallized/Shoot_" + String.format("%03d", i) + ".png")));
        cgShootAnim = new Animation<>(0.15f, cgShoot, Animation.PlayMode.NORMAL);

        Array<TextureRegion> cgDeath = new Array<>();
        for (int i = 0; i <= 5; i++)
            cgDeath.add(new TextureRegion(new Texture("Animation/Crystallized/Death_" + String.format("%03d", i) + ".png")));
        cgDeathAnim = new Animation<>(0.15f, cgDeath, Animation.PlayMode.NORMAL);

        laserAtlas = new TextureAtlas(Gdx.files.internal("Animation/Crystallized/Laser.atlas"));
        cgLaserAnim = new Animation<>(0.05f, laserAtlas.getRegions(), Animation.PlayMode.LOOP);
        // endregion
    }

    public void render(SpriteBatch batch, Enemy enemy) {
        TextureRegion currentFrame = null;
        float rotation = 0f;

        if (enemy instanceof Tiktik) {
            if (enemy.isDead()) {
                currentFrame = tiktikDeathAnim.getKeyFrame(enemy.stateTime, false);
            } else {
                currentFrame = tiktikWalkAnim.getKeyFrame(enemy.stateTime, true);
            }
        } else if (enemy instanceof Mosquito) {
            Mosquito mosq = (Mosquito) enemy;
            rotation = mosq.rotationAngle;

            switch (mosq.currentState) {
                case DEAD:
                    currentFrame = mosqDeathAnim.getKeyFrame(mosq.stateTime, false);
                    break;
                case WARNING:
                    currentFrame = mosqWarningAnim.getKeyFrame(mosq.stateTime, true);
                    break;
                case DASHING:
                    currentFrame = mosqDashAnim.getKeyFrame(mosq.stateTime, true);
                    break;
                case IDLE:
                default:
                    currentFrame = mosqFlyAnim.getKeyFrame(mosq.stateTime, true);
                    break;
            }
        } else if (enemy instanceof HuskHornhead) {
            HuskHornhead husk = (HuskHornhead) enemy;

            switch (husk.currentState) {
                case DEAD:
                    currentFrame = huskDeathAnim.getKeyFrame(husk.stateTime, false);
                    break;
                case WARNING:
                    currentFrame = huskWarningAnim.getKeyFrame(husk.stateTime, false);
                    break;
                case IDLE:
                    currentFrame = huskIdleAnim.getKeyFrame(husk.stateTime, true);
                    break;
                case CHARGING:
                    currentFrame = huskChargeAnim.getKeyFrame(husk.stateTime, true);
                    break;
                case WALKING:
                default:
                    currentFrame = huskWalkAnim.getKeyFrame(husk.stateTime, true);
                    break;
            }
        } else if (enemy instanceof CrystalGuardian) {
            CrystalGuardian cg = (CrystalGuardian) enemy;

            switch (cg.currentState) {
                case DEAD:
                    currentFrame = cgDeathAnim.getKeyFrame(cg.stateTime, false);
                    break;
                case ENRAGED:
                    currentFrame = cgRunAnim.getKeyFrame(cg.stateTime, true);
                    break;
                case FIRING_LASER:
                    currentFrame = cgShootAnim.getKeyFrame(cg.stateTime, false);
                    break;
                case IDLE:
                default:
                    currentFrame = cgIdleAnim.getKeyFrame(cg.stateTime, true);
                    break;
            }

            if (cg.currentState == CrystalGuardian.State.FIRING_LASER) {
                TextureRegion laserFrame = cgLaserAnim.getKeyFrame(cg.stateTime, true);

                if (laserFrame != null) {
                    float laserX;
                    float laserY = cg.y + 40f;
                    float laserWidth = cg.sightRange;
                    float laserHeight = cg.sightHeight;

                    if (cg.isFacingRight) {
                        laserX = cg.x + cg.width - 30;
                        if (laserFrame.isFlipX()) laserFrame.flip(true, false);
                    } else {
                        laserX = cg.x - cg.sightRange;
                        if (!laserFrame.isFlipX()) laserFrame.flip(true, false);
                    }

                    batch.draw(laserFrame, laserX, laserY, laserWidth, laserHeight);
                }
            }
        }

        if (currentFrame != null) {
            boolean shouldDraw = true;

            // for blinking
            if (enemy.invincibleTimer > 0) {
                if ((int) (enemy.invincibleTimer * 15) % 2 == 0) shouldDraw = false;
            }

            if (shouldDraw) {
                float originX = enemy.width / 2f;
                float originY = enemy.height / 2f;
                float visualOffsetY = 15f;
                float scaleX = enemy.isFacingRight ? -1f : 1f;

                batch.draw(currentFrame, enemy.x, enemy.y - visualOffsetY,
                    originX, originY, enemy.width, enemy.height,
                    scaleX, 1.0f, rotation
                );
            }
        }
    }
}
