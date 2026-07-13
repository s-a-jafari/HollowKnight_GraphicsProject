package advanceProgramming.project.com.view;

import advanceProgramming.project.com.controller.ZoteController;
import advanceProgramming.project.com.model.Zote;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;

public class ZoteView {
    private final Animation<TextureRegion> idleAnimation;
    private final Animation<TextureRegion> talkAnimation;
    private final Animation<TextureRegion> angryAnimation;

    private final BitmapFont dialogFont;
    private final BitmapFont nameFont;
    private final BitmapFont promptFont;
    private final ShapeRenderer shapeRenderer;
    private final OrthographicCamera hudCamera;
    private final SpriteBatch hudBatch;

    public ZoteView() {
        // region loading Zote animations
        Array<TextureRegion> idleFrames = new Array<>();
        for (int i = 0; i <= 4; i++)
            idleFrames.add(new TextureRegion(new Texture("Animation/Zote/Idle_" + String.format("%03d", i) + ".png")));
        idleAnimation = new Animation<>(0.2f, idleFrames, Animation.PlayMode.LOOP);

        Array<TextureRegion> talkFrames = new Array<>();
        for (int i = 0; i <= 4; i++)
            talkFrames.add(new TextureRegion(new Texture("Animation/Zote/Talk_" + String.format("%03d", i) + ".png")));
        talkAnimation = new Animation<>(0.15f, talkFrames, Animation.PlayMode.LOOP);

        Array<TextureRegion> angryFrames = new Array<>();
        for (int i = 0; i <= 3; i++)
            angryFrames.add(new TextureRegion(new Texture("Animation/Zote/Attack_" + String.format("%03d", i) + ".png")));
        angryAnimation = new Animation<>(0.1f, angryFrames, Animation.PlayMode.LOOP);
        // endregion

        shapeRenderer = new ShapeRenderer();
        hudBatch = new SpriteBatch();
        hudCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        hudCamera.position.set(hudCamera.viewportWidth / 2f, hudCamera.viewportHeight / 2f, 0);
        hudCamera.update();

        // region loading Zote texts types
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("trajan.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        parameter.size = 28;
        parameter.color = Color.WHITE;
        dialogFont = generator.generateFont(parameter);

        parameter.size = 32;
        parameter.color = Color.LIGHT_GRAY;
        nameFont = generator.generateFont(parameter);

        parameter.size = 40;
        parameter.color = Color.WHITE;
        promptFont = generator.generateFont(parameter);

        generator.dispose();
        // endregion
    }

    public void render(SpriteBatch batch, Zote zote, ZoteController controller) {
        TextureRegion currentFrame;

        if (zote.currentState == Zote.State.TALKING) {
            currentFrame = talkAnimation.getKeyFrame(zote.stateTime);
        } else if (zote.currentState == Zote.State.ANGRY) {
            currentFrame = angryAnimation.getKeyFrame(zote.stateTime);
        } else {
            currentFrame = idleAnimation.getKeyFrame(zote.stateTime);
        }

        boolean needsFlip = (!zote.isFacingRight && currentFrame.isFlipX()) ||
            (zote.isFacingRight && !currentFrame.isFlipX());
        if (needsFlip) currentFrame.flip(true, false);

        batch.draw(currentFrame, zote.x, zote.y, zote.width, zote.height);

        if (controller.isPlayerInRange && zote.currentState == Zote.State.IDLE) {
            promptFont.draw(batch, "Press E", zote.x + 35, zote.y + zote.height + 40f);
        }
    }

    public void renderHUD(Zote zote) {
        if (zote.currentState == Zote.State.TALKING) {
            hudCamera.update();
            shapeRenderer.setProjectionMatrix(hudCamera.combined);
            hudBatch.setProjectionMatrix(hudCamera.combined);

            float boxWidth = 1500f;
            float boxHeight = 220f;
            float boxX = (hudCamera.viewportWidth - boxWidth) / 2f;
            float boxY = 50f;

            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0f, 0f, 0f, 0.75f);
            shapeRenderer.rect(boxX, boxY, boxWidth, boxHeight);
            shapeRenderer.end();

            Gdx.gl.glDisable(GL20.GL_BLEND);

            hudBatch.begin();
            nameFont.draw(hudBatch, "Zote The Mighty", boxX + 40, boxY + boxHeight - 20);
            dialogFont.draw(hudBatch, zote.currentDisplayedText, boxX + 40, boxY + boxHeight - 80);

            if (zote.visibleChars == zote.currentTargetText.length()) {
                if ((int) (zote.stateTime * 2) % 2 == 0) {
                    promptFont.draw(hudBatch, "Press ENTER", boxX + boxWidth - 310, boxY + 60);
                }
            }

            hudBatch.end();
        }
    }
}
