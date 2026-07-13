package advanceProgramming.project.com.view;

import advanceProgramming.project.com.model.Knight;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;

public class KnightView {
    public boolean isNoclip = false;
    public boolean isGodMode = false;
    // region variable of animations
    private Animation<TextureRegion> StartRunAnimation;
    private Animation<TextureRegion> runAnimation;
    private Animation<TextureRegion> slashAnimation;
    private Animation<TextureRegion> idleAnimation;
    private Animation<TextureRegion> jumpAnimation;
    private Animation<TextureRegion> fallAnimation;
    private Animation<TextureRegion> slashEffectAnimation;
    private Animation<TextureRegion> dashAnimation;
    private Animation<TextureRegion> doubleJumpAnimation;
    private Animation<TextureRegion> wallSlideAnimation;
    private Animation<TextureRegion> downSlashAnimation;
    private Animation<TextureRegion> upSlashAnimation;
    private Animation<TextureRegion> soulLiquidAnim;
    private Animation<TextureRegion> focusAnimation;
    private Animation<TextureRegion> maskBreakAnim;
    private Animation<TextureRegion> maskFormAnim;
    private Animation<TextureRegion> vengefulSpellAnim;
    private Animation<TextureRegion> howlingSpellAnim;
    private Animation<TextureRegion> vengefulSpell2Anim;
    private Animation<TextureRegion> howlingSpell2Anim;
    private Animation<TextureRegion> castVengefulAnim;
    private Animation<TextureRegion> castHowlingAnim;
    // endregion
    private TextureAtlas spellsAtlas;
    private Animation<TextureRegion> sharpShadowDashAnim;
    private BitmapFont font;
    private final ShapeRenderer shapeRenderer;
    private TextureAtlas soulAtlas;
    private Texture coinIcon;
    private Texture soulVesselGlass;
    private Texture fullMask;
    private Texture emptyMask;
    private Texture soulVessel;
    private final OrthographicCamera hudCamera;
    private final SpriteBatch hudBatch;
    private int lastHp = 5;
    private final int[] maskStates;
    private final float[] maskStateTimes;

    public KnightView() {
        hudBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        hudCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        hudCamera.position.set(hudCamera.viewportWidth / 2f, hudCamera.viewportHeight / 2f, 0);
        hudCamera.update();
        loadAnimations();

        maskStates = new int[5];
        maskStateTimes = new float[5];

        for (int i = 0; i < 5; i++) {
            maskStates[i] = 0;
            maskStateTimes[i] = 0f;
        }

        loadHUDAnimations();
    }

    private void loadHUDAnimations() {
        coinIcon = new Texture("Animation/HUD/HUD Cln_143.png");
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("trajan.ttf"));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();

        parameter.size = 36;
        parameter.color = Color.WHITE;
        parameter.borderColor = Color.BLACK;
        parameter.borderWidth = 2f;
        font = generator.generateFont(parameter);
        generator.dispose();

        soulAtlas = new TextureAtlas(Gdx.files.internal("Animation/HUD/Soulorb.atlas"));
        Array<TextureRegion> soulFrames = new Array<>();
        for (int i = 0; i <= 5; i++) {
            String regionName = "HUD_Soulorb_fills_soul_idle" + String.format("%04d", i);
            TextureAtlas.AtlasRegion region = soulAtlas.findRegion(regionName);

            soulFrames.add(region);
        }
        soulLiquidAnim = new Animation<>(0.1f, soulFrames, Animation.PlayMode.LOOP);

        Array<TextureRegion> breakFrames = new Array<>();
        for (int i = 0; i <= 5; i++) {
            breakFrames.add(new TextureRegion(new Texture("Animation/HUD/BreakHealth_" + String.format("%03d", i) + ".png")));
        }
        maskBreakAnim = new Animation<>(0.1f, breakFrames, Animation.PlayMode.NORMAL);

        Array<TextureRegion> formFrames = new Array<>();
        for (int i = 0; i <= 4; i++) {
            formFrames.add(new TextureRegion(new Texture("Animation/HUD/HealthRefill_" + String.format("%03d", i) + ".png")));
        }
        maskFormAnim = new Animation<>(0.1f, formFrames, Animation.PlayMode.NORMAL);
    }

    private void loadAnimations() {
        fullMask = new Texture("Animation/HUD/FilledHealth.png");
        emptyMask = new Texture("Animation/HUD/EmptyHealth.png");
        soulVessel = new Texture("Animation/HUD/HealthBar_005.png");

        // region loading animations of "The Knight"
        Array<TextureRegion> wallSlideFrames = new Array<>();
        for (int i = 0; i <= 3; i++) {
            wallSlideFrames.add(new TextureRegion(new Texture("Animation/Wall Slide_" + String.format("%03d", i) + ".png")));
        }
        wallSlideAnimation = new Animation<>(0.1f, wallSlideFrames, Animation.PlayMode.NORMAL);

        Array<TextureRegion> doubleJumpFrames = new Array<>();
        for (int i = 0; i <= 7; i++) {
            doubleJumpFrames.add(new TextureRegion(new Texture("Animation/Double Jump_" + String.format("%03d", i) + ".png")));
        }
        doubleJumpAnimation = new Animation<>(0.1f, doubleJumpFrames, Animation.PlayMode.NORMAL);

        Array<TextureRegion> dashFrames = new Array<>();
        for (int i = 0; i <= 11; i++) {
            dashFrames.add(new TextureRegion(new Texture("Animation/Dash_" + String.format("%03d", i) + ".png")));
        }
        dashAnimation = new Animation<>(0.05f, dashFrames, Animation.PlayMode.NORMAL);

        Array<TextureRegion> StartRunFrames = new Array<>();
        for (int i = 0; i <= 5; i++) {
            StartRunFrames.add(new TextureRegion(new Texture("Animation/Run_" + String.format("%03d", i) + ".png")));
        }
        StartRunAnimation = new Animation<>(0.1f, StartRunFrames, Animation.PlayMode.NORMAL);

        Array<TextureRegion> upSlashFrames = new Array<>();
        for (int i = 0; i <= 4; i++) {
            upSlashFrames.add(new TextureRegion(new Texture("Animation/UpSlash_" + String.format("%03d", i) + ".png")));
        }
        upSlashAnimation = new Animation<>(0.05f, upSlashFrames, Animation.PlayMode.NORMAL);

        Array<TextureRegion> runFrames = new Array<>();
        for (int i = 5; i <= 12; i++) {
            runFrames.add(new TextureRegion(new Texture("Animation/Run_" + String.format("%03d", i) + ".png")));
        }
        runAnimation = new Animation<>(0.1f, runFrames, Animation.PlayMode.LOOP);

        Array<TextureRegion> idleFrames = new Array<>();
        for (int i = 1; i <= 8; i++) {
            idleFrames.add(new TextureRegion(new Texture("Animation/Idle_" + String.format("%03d", i) + ".png")));
        }
        idleAnimation = new Animation<>(0.15f, idleFrames, Animation.PlayMode.LOOP);

        Array<TextureRegion> jumpFrames = new Array<>();
        for (int i = 0; i <= 4; i++) {
            jumpFrames.add(new TextureRegion(new Texture("Animation/Airborne_" + String.format("%03d", i) + ".png")));
        }
        jumpAnimation = new Animation<>(0.1f, jumpFrames, Animation.PlayMode.NORMAL);

        Array<TextureRegion> fallFrames = new Array<>();
        for (int i = 5; i <= 11; i++) {
            fallFrames.add(new TextureRegion(new Texture("Animation/Airborne_" + String.format("%03d", i) + ".png")));
        }
        fallAnimation = new Animation<>(0.1f, fallFrames, Animation.PlayMode.LOOP);

        Array<TextureRegion> slashFrames = new Array<>();
        for (int i = 0; i <= 4; i++) {
            slashFrames.add(new TextureRegion(new Texture("Animation/Slash_" + String.format("%03d", i) + ".png")));
        }
        slashAnimation = new Animation<>(0.05f, slashFrames, Animation.PlayMode.NORMAL);

        Array<TextureRegion> downSlashFrames = new Array<>();
        for (int i = 0; i <= 4; i++) {
            downSlashFrames.add(new TextureRegion(new Texture("Animation/DownSlash_" + String.format("%03d", i) + ".png")));
        }
        downSlashAnimation = new Animation<>(0.05f, downSlashFrames, Animation.PlayMode.NORMAL);

        Array<TextureRegion> slashEffectFrames = new Array<>();
        for (int i = 0; i <= 4; i++) {
            slashEffectFrames.add(new TextureRegion(new Texture("Animation/Effects/SlashEffect_" + String.format("%03d", i) + ".png")));
        }
        slashEffectAnimation = new Animation<>(0.05f, slashEffectFrames, Animation.PlayMode.NORMAL);

        Array<TextureRegion> focusFrames = new Array<>();
        for (int i = 0; i <= 6; i++) {
            focusFrames.add(new TextureRegion(new Texture("Animation/Focus_" + String.format("%03d", i) + ".png")));
        }
        focusAnimation = new Animation<>(0.05f, focusFrames, Animation.PlayMode.NORMAL);

        Array<TextureRegion> castVengefulFrames = new Array<>();
        for (int i = 0; i <= 8; i++) { // تعداد عکس‌ها را اصلاح کن
            castVengefulFrames.add(new TextureRegion(new Texture("Animation/Fireball Cast_" + String.format("%03d", i) + ".png")));
        }
        castVengefulAnim = new Animation<>(0.08f, castVengefulFrames, Animation.PlayMode.NORMAL);

        Array<TextureRegion> castHowlingFrames = new Array<>();
        for (int i = 0; i <= 6; i++) {
            castHowlingFrames.add(new TextureRegion(new Texture("Animation/Scream_" + String.format("%03d", i) + ".png")));
        }
        castHowlingAnim = new Animation<>(0.08f, castHowlingFrames, Animation.PlayMode.NORMAL);

        spellsAtlas = new TextureAtlas(Gdx.files.internal("Animation/Effects/Spell.atlas"));

        // 💡 لود کردن فریم‌های Vengeful Spirit (نام‌ها در اطلس دارای 3 رقم صفر هستند: 000 تا 009)
        Array<TextureRegion> vengefulFrames = new Array<>();
        for (int i = 0; i <= 9; i++) {
            String frameName = "Spell Effects_fireball_v020" + String.format("%03d", i);
            TextureAtlas.AtlasRegion region = spellsAtlas.findRegion(frameName);
            if (region != null) vengefulFrames.add(region);
        }
        vengefulSpellAnim = new Animation<>(0.05f, vengefulFrames, Animation.PlayMode.NORMAL);

        Array<TextureRegion> howlingFrames = new Array<>();
        for (int i = 0; i <= 12; i++) {
            String frameName = "Spell Effects 2_scream_effect" + String.format("%04d", i);
            TextureAtlas.AtlasRegion region = spellsAtlas.findRegion(frameName);
            if (region != null) howlingFrames.add(region);
        }
        howlingSpellAnim = new Animation<>(0.05f, howlingFrames, Animation.PlayMode.NORMAL);

        Array<TextureRegion> sharpFrames = new Array<>();
        for (int i = 0; i <= 10; i++) {
            sharpFrames.add(new TextureRegion(new Texture("Animation/Shadow Dash_" + String.format("%03d", i) + ".png")));
        }
        sharpShadowDashAnim = new Animation<>(0.05f, sharpFrames, Animation.PlayMode.NORMAL);

        Array<TextureRegion> vengeful2Frames = new Array<>();
        for (int i = 1; i <= 5; i++) {
            String frameName = "Spell Effects Neutral_single_fireball000" + i;
            TextureAtlas.AtlasRegion region = spellsAtlas.findRegion(frameName);
            if (region != null) vengeful2Frames.add(region);
        }
        vengefulSpell2Anim = new Animation<>(0.05f, vengeful2Frames, Animation.PlayMode.NORMAL);

        Array<TextureAtlas.AtlasRegion> howling2Frames =
            spellsAtlas.findRegions("Spell Effects Neutral_scream_blast_level");
        howlingSpell2Anim = new Animation<>(0.05f, howling2Frames, Animation.PlayMode.NORMAL);

        // endregion of
    }

    public void render(SpriteBatch batch, Knight model) {
        TextureRegion currentFrame = null;

        switch (model.currentState) {
            case FOCUSING:
                currentFrame = focusAnimation.getKeyFrame(model.stateTime);
                break;
            case WALL_SLIDE:
                currentFrame = wallSlideAnimation.getKeyFrame(model.stateTime);
                break;
            case DOUBLE_JUMPING:
                currentFrame = doubleJumpAnimation.getKeyFrame(model.stateTime);
                break;
            case DASHING:
                if (model.hasSharpShadow) {
                    currentFrame = sharpShadowDashAnim.getKeyFrame(model.stateTime);
                } else {
                    currentFrame = dashAnimation.getKeyFrame(model.stateTime);
                }
                break;
            case START_RUNNING:
                currentFrame = StartRunAnimation.getKeyFrame(model.stateTime);
                break;
            case RUNNING:
                currentFrame = runAnimation.getKeyFrame(model.stateTime);
                break;
            case DOWN_ATTACKING:
                currentFrame = downSlashAnimation.getKeyFrame(model.stateTime);
                break;
            case UP_ATTACKING:
                currentFrame = upSlashAnimation.getKeyFrame(model.stateTime);
                break;
            case ATTACKING:
                currentFrame = slashAnimation.getKeyFrame(model.stateTime);
                break;
            case JUMPING:
                currentFrame = jumpAnimation.getKeyFrame(model.stateTime);
                break;
            case FALLING:
                currentFrame = fallAnimation.getKeyFrame(model.stateTime);
                break;
            case CASTING_VENGEFUL:
                currentFrame = castVengefulAnim.getKeyFrame(model.castTimer);
                break;
            case CASTING_HOWLING:
                currentFrame = castHowlingAnim.getKeyFrame(model.castTimer);
                break;
            case IDLE:
            default:
                currentFrame = idleAnimation.getKeyFrame(model.stateTime);
                break;
        }

        boolean shouldDraw = true;

        if (model.invincibleTimer > 0) {
            if ((int) (model.invincibleTimer * 10) % 2 == 0) {
                shouldDraw = false;
            }
        }

        for (Knight.HowlingWraiths hw : model.howlingWraiths) {
            TextureRegion frame;
            if (model.hasVoidHeart) {
                frame = howlingSpell2Anim.getKeyFrame(hw.stateTime, false);
            } else {
                frame = howlingSpellAnim.getKeyFrame(hw.stateTime, false);
            }
            batch.draw(frame, hw.x, hw.y, hw.width, hw.height);
        }

        if (currentFrame != null && shouldDraw) {
            // region checking the direction of the frames
            boolean needsFlip = (!model.isFacingRight && !currentFrame.isFlipX()) ||
                (model.isFacingRight && currentFrame.isFlipX());
            if (needsFlip) {
                currentFrame.flip(true, false);
            }
            // endregion

            // checking if become sliding
            float modelX = model.x;
            if (model.currentState == Knight.State.WALL_SLIDE) {
                float extraDistance = 20f;
                if (model.isFacingRight) {
                    modelX -= extraDistance;
                } else {
                    modelX += extraDistance;
                }
            }

            batch.draw(currentFrame, modelX, model.y, currentFrame.getRegionWidth(), currentFrame.getRegionHeight());
        }

        for (Knight.VengefulSpirit vs : model.vengefulSpirits) {
            TextureRegion frame;
            if (model.hasVoidHeart) {
                frame = vengefulSpell2Anim.getKeyFrame(vs.stateTime, true);
            } else {
                frame = vengefulSpellAnim.getKeyFrame(vs.stateTime, true);
            }

            boolean flip = (vs.velocityX < 0 && !frame.isFlipX()) || (vs.velocityX > 0 && frame.isFlipX());
            if (flip) frame.flip(true, false);
            batch.draw(frame, vs.x, vs.y, vs.width, vs.height);
        }

        if (model.isSlashActive) {
            TextureRegion slashFrame = slashEffectAnimation.getKeyFrame(model.slashStateTime);

            if (slashFrame != null) {
                float slashX;
                float slashY;
                float rotation = 0f;

                if (model.currentState == Knight.State.DOWN_ATTACKING) {
                    if (slashFrame.isFlipX()) slashFrame.flip(true, false);

                    float effWidth = slashFrame.getRegionWidth();
                    float effHeight = slashFrame.getRegionHeight();
                    slashX = model.x + (model.width / 2f) - (effWidth / 2f);

                    slashY = model.y - 90f;

                    batch.draw(slashFrame, slashX, slashY, effWidth / 2f, effHeight / 2f, effWidth, effHeight, 1f, 1f, 90f);

                } else if (model.currentState == Knight.State.UP_ATTACKING) {
                    if (slashFrame.isFlipX()) slashFrame.flip(true, false);

                    float effWidth = slashFrame.getRegionWidth();
                    float effHeight = slashFrame.getRegionHeight();

                    slashX = model.x + (model.width / 2f) - (effWidth / 2f);
                    slashY = model.y;

                    batch.draw(slashFrame, slashX, slashY, effWidth / 2f, effHeight / 2f, effWidth, effHeight, 1f, 1f, -90f);
                } else {
                    slashY = model.y + model.slashOffsetY;
                    if (model.isFacingRight) {
                        slashX = model.x - model.slashOffsetX;
                        if (slashFrame.isFlipX()) slashFrame.flip(true, false);
                    } else {
                        slashX = model.x + model.slashOffsetX;
                        if (!slashFrame.isFlipX()) slashFrame.flip(true, false);
                    }
                    batch.draw(slashFrame, slashX, slashY, slashFrame.getRegionWidth(), slashFrame.getRegionHeight());
                }
            }
        }
    }

    public void renderHUD(Knight model) {
        hudBatch.setProjectionMatrix(hudCamera.combined);
        shapeRenderer.setProjectionMatrix(hudCamera.combined);

        float frameWidth = 257f;
        float frameHeight = 164f;
        float frameX = 20f;
        float frameY = hudCamera.viewportHeight - frameHeight - 20f;
        float vesselCenterX = frameX + 75f;
        float vesselCenterY = frameY + 65f;
        float circleRadius = 55f;

        //  region circle mask
        Gdx.gl.glEnable(GL20.GL_STENCIL_TEST);
        Gdx.gl.glStencilMask(0xFF);
        Gdx.gl.glClearStencil(0);
        Gdx.gl.glClear(GL20.GL_STENCIL_BUFFER_BIT);

        Gdx.gl.glStencilFunc(GL20.GL_ALWAYS, 1, 0xFF);
        Gdx.gl.glStencilOp(GL20.GL_KEEP, GL20.GL_KEEP, GL20.GL_REPLACE);
        Gdx.gl.glColorMask(false, false, false, false);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1f, 1f, 1f, 1f);
        shapeRenderer.circle(vesselCenterX, vesselCenterY, circleRadius);
        shapeRenderer.end();

        Gdx.gl.glColorMask(true, true, true, true);
        Gdx.gl.glStencilFunc(GL20.GL_EQUAL, 1, 0xFF);
        Gdx.gl.glStencilOp(GL20.GL_KEEP, GL20.GL_KEEP, GL20.GL_KEEP);
        // endregion

        // region drawing soul vessel
        hudBatch.begin();

        TextureRegion currentLiquidFrame = soulLiquidAnim.getKeyFrame(model.stateTime, true);
        float liquidWidth = 140f;
        float liquidHeight = 140f;

        float percentage = model.visualSoul / (float) model.getMaxSoul();

        float targetTopY = (vesselCenterY - circleRadius) + (percentage * 2 * circleRadius) + 20f;

        float drawX = vesselCenterX - (liquidWidth / 2f);
        float drawY = targetTopY - liquidHeight;

        if (percentage > 0.01f) {
            hudBatch.draw(currentLiquidFrame, drawX, drawY, liquidWidth, liquidHeight);
        }

        hudBatch.end();

        Gdx.gl.glDisable(GL20.GL_STENCIL_TEST);
        // endregion

        // region drawing Hps
        hudBatch.begin();
        hudBatch.draw(soulVessel, frameX, frameY, frameWidth, frameHeight);

        float maskStartX = frameX + 140f;
        float maskY = frameY + 20f;
        float maskPadding = 55f;
        float maskWidth = 94.5f;
        float maskHeight = 125.25f;

        if (model.getCurrentHp() < lastHp) {
            for (int i = model.getCurrentHp(); i < lastHp; i++) {
                maskStates[i] = 2;
                maskStateTimes[i] = 0f;
            }
        } else if (model.getCurrentHp() > lastHp) {
            for (int i = lastHp; i < model.getCurrentHp(); i++) {
                maskStates[i] = 3;
                maskStateTimes[i] = 0f;
            }
        }
        lastHp = model.getCurrentHp();

        for (int i = 0; i < model.getMaxHp(); i++) {
            float currentX = maskStartX + (i * maskPadding);

            if (maskStates[i] == 2) {
                maskStateTimes[i] += Gdx.graphics.getDeltaTime();
                if (maskBreakAnim.isAnimationFinished(maskStateTimes[i])) {
                    maskStates[i] = 1;
                }
            } else if (maskStates[i] == 3) {
                maskStateTimes[i] += Gdx.graphics.getDeltaTime();
                if (maskFormAnim.isAnimationFinished(maskStateTimes[i])) {
                    maskStates[i] = 0;
                }
            } else {
                maskStates[i] = (i < model.getCurrentHp()) ? 0 : 1;
            }

            if (maskStates[i] == 0) {
                hudBatch.draw(fullMask, currentX, maskY, maskWidth, maskHeight);
            } else if (maskStates[i] == 1) {
                hudBatch.draw(emptyMask, currentX, maskY, maskWidth, maskHeight);
            } else if (maskStates[i] == 2) {
                TextureRegion frame = maskBreakAnim.getKeyFrame(maskStateTimes[i]);
                hudBatch.draw(frame, currentX, maskY, maskWidth, maskHeight);
            } else if (maskStates[i] == 3) {
                TextureRegion frame = maskFormAnim.getKeyFrame(maskStateTimes[i]);
                hudBatch.draw(frame, currentX, maskY, maskWidth, maskHeight);
            }
        }

        float coinX = frameX + 165f;
        float coinY = frameY + 40f;
        float coinIconSize = 40f;

        hudBatch.draw(coinIcon, coinX, coinY - coinIconSize, coinIconSize, coinIconSize);

        font.draw(hudBatch, String.valueOf(model.getCoins()), coinX + 50f, coinY - 5f);
        hudBatch.end();
        // endregion
    }
}
