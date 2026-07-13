package advanceProgramming.project.com.view;

import advanceProgramming.project.com.model.FalseKnight;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class FalseKnightView {
    // region Animation variable
    private Animation<TextureRegion> maceSlamAnimation;
    private Animation<TextureRegion> powerSlamAnimation;
    private Animation<TextureRegion> shockwaveAnimation;
    private Animation<TextureRegion> idleAnimation;
    private Animation<TextureRegion> beforeStunAnimation;
    private Animation<TextureRegion> afterStunAnimation;
    private Animation<TextureRegion> stunAnimation;
    private Animation<TextureRegion> startRunAnimation;
    private Animation<TextureRegion> runAnimation;
    private Animation<TextureRegion> jumpAnimation;
    private Animation<TextureRegion> landAnimation;
    private Animation<TextureRegion> deadAnimation;
    // endregion

    public FalseKnightView() {
        loadassets();
    }

    private void loadassets() {
        Array<TextureRegion> idleFrames = new Array<>();
        for (int i = 0; i <= 4; i++)
            idleFrames.add(new TextureRegion(new Texture("Animation/False_Knight/Idle_" + String.format("%03d", i) + ".png")));
        idleAnimation = new Animation<>(0.15f, idleFrames, Animation.PlayMode.LOOP);

        Array<TextureRegion> runFrames = new Array<>();
        for (int i = 0; i <= 4; i++)
            runFrames.add(new TextureRegion(new Texture("Animation/False_Knight/Run_" + String.format("%03d", i) + ".png")));
        runAnimation = new Animation<>(0.1f, runFrames, Animation.PlayMode.LOOP);

        Array<TextureRegion> startRunFrames = new Array<>();
        for (int i = 0; i <= 1; i++)
            startRunFrames.add(new TextureRegion(new Texture("Animation/False_Knight/Run Antic_" + String.format("%03d", i) + ".png")));
        startRunAnimation = new Animation<>(0.1f, startRunFrames, Animation.PlayMode.NORMAL);

        Array<TextureRegion> slamFrames = new Array<>();
        for (int i = 0; i <= 8; i++)
            slamFrames.add(new TextureRegion(new Texture("Animation/False_Knight/Attack Antic_" + String.format("%03d", i) + ".png")));
        maceSlamAnimation = new Animation<>(0.1f, slamFrames, Animation.PlayMode.NORMAL);

        Array<TextureRegion> PowerSlamFrames = new Array<>();
        for (int i = 0; i <= 7; i++)
            PowerSlamFrames.add(new TextureRegion(new Texture("Animation/False_Knight/Jump Attack_" + String.format("%03d", i) + ".png")));
        powerSlamAnimation = new Animation<>(0.15f, PowerSlamFrames, Animation.PlayMode.NORMAL);

        Array<TextureRegion> jumpFrames = new Array<>();
        for (int i = 0; i <= 3; i++)
            jumpFrames.add(new TextureRegion(new Texture("Animation/False_Knight/Jump_" + String.format("%03d", i) + ".png")));
        jumpAnimation = new Animation<>(0.15f, jumpFrames, Animation.PlayMode.NORMAL);

        Array<TextureRegion> landFrames = new Array<>();
        for (int i = 0; i <= 4; i++)
            landFrames.add(new TextureRegion(new Texture("Animation/False_Knight/Land_" + String.format("%03d", i) + ".png")));
        landAnimation = new Animation<>(0.1f, landFrames, Animation.PlayMode.NORMAL);

        Array<TextureRegion> stunFrames = new Array<>();
        for (int i = 0; i <= 4; i++)
            stunFrames.add(new TextureRegion(new Texture("Animation/False_Knight/Body_" + String.format("%03d", i) + ".png")));
        stunAnimation = new Animation<>(0.15f, stunFrames, Animation.PlayMode.LOOP);

        Array<TextureRegion> beforeStunFrames = new Array<>();
        for (int i = 0; i <= 13; i++)
            beforeStunFrames.add(new TextureRegion(new Texture("Animation/False_Knight/Death_" + String.format("%03d", i) + ".png")));
        beforeStunAnimation = new Animation<>(0.1f, beforeStunFrames, Animation.PlayMode.NORMAL);

        Array<TextureRegion> afterStunFrames = new Array<>();
        for (int i = 0; i <= 5; i++)
            afterStunFrames.add(new TextureRegion(new Texture("Animation/False_Knight/Stun Recover_" + String.format("%03d", i) + ".png")));
        afterStunAnimation = new Animation<>(0.1f, afterStunFrames, Animation.PlayMode.NORMAL);

        deadAnimation = beforeStunAnimation;

        Array<TextureRegion> shockFrames = new Array<>();
        for (int i = 0; i <= 7; i++)
            shockFrames.add(new TextureRegion(new Texture("Animation/False_Knight/Shockwave_" + String.format("%03d", i) + ".png")));
        shockwaveAnimation = new Animation<>(0.1f, shockFrames, Animation.PlayMode.NORMAL);
    }

    public void render(SpriteBatch batch, FalseKnight boss) {
        TextureRegion currentFrame = null;

        switch (boss.currentState) {
            case IDLE:
                currentFrame = idleAnimation.getKeyFrame(boss.stateTime);
                break;
            case START_RUN:
                currentFrame = startRunAnimation.getKeyFrame(boss.stateTime);
                break;
            case RUN:
                currentFrame = runAnimation.getKeyFrame(boss.stateTime);
                break;
            case JUMP:
            case DEFENSIVE_LEAP:
                currentFrame = jumpAnimation.getKeyFrame(boss.stateTime);
                break;
            case LAND:
                currentFrame = landAnimation.getKeyFrame(boss.stateTime);
                break;
            case MACE_SLAM:
                currentFrame = maceSlamAnimation.getKeyFrame(boss.stateTime);
                break;
            case POWER_SLAM:
                currentFrame = powerSlamAnimation.getKeyFrame(boss.stateTime);
                break;
            case BEFORE_STUN:
                currentFrame = beforeStunAnimation.getKeyFrame(boss.stateTime);
                break;
            case STUN:
                currentFrame = stunAnimation.getKeyFrame(boss.stateTime);
                break;
            case AFTER_STUN:
                currentFrame = afterStunAnimation.getKeyFrame(boss.stateTime);
                break;
            case DEAD:
                currentFrame = deadAnimation.getKeyFrame(boss.stateTime);
                break;
        }

        if (currentFrame != null) {
            boolean shouldDraw = true;
            if (boss.invincibleTimer > 0) {
                if ((int) (boss.invincibleTimer * 15) % 2 == 0) shouldDraw = false;
            }

            if (shouldDraw) {
                boolean needsFlip = (!boss.isFacingRight && currentFrame.isFlipX()) ||
                    (boss.isFacingRight && !currentFrame.isFlipX());
                if (needsFlip) currentFrame.flip(true, false);

                float yOffset = 50f;

                batch.draw(currentFrame, boss.x, boss.y - yOffset, boss.width, boss.height);
            }
        }

        if (shockwaveAnimation != null) {
            for (FalseKnight.Shockwave wave : boss.shockwaves) {
                TextureRegion waveFrame = shockwaveAnimation.getKeyFrame(wave.stateTime, true);

                boolean wFlip = (wave.isFacingRight && waveFrame.isFlipX()) ||
                    (!wave.isFacingRight && !waveFrame.isFlipX());
                if (wFlip) waveFrame.flip(true, false);

                batch.draw(waveFrame, wave.x, wave.y, wave.width, wave.height);
            }
        }
    }
}
