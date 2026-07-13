package advanceProgramming.project.com.view;

import advanceProgramming.project.com.helper.AchievementManager;
import advanceProgramming.project.com.helper.AchievementObserver;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

public class AchievementPopup implements AchievementObserver {
    private final float SHOW_DURATION = 4.0f;
    private final Array<String> popupQueue = new Array<>();
    private String currentTitle = "";
    private float showTimer = 0f;
    private final Texture popupBg;
    private final BitmapFont font;

    public AchievementPopup(BitmapFont font) {
        this.font = font;
        this.popupBg = new Texture(Gdx.files.internal("selector.png"));
        AchievementManager.getInstance().addObserver(this);
    }

    @Override
    public void onAchievementUnlocked(String id, String title) {
        popupQueue.add(title);
    }

    public void render(SpriteBatch batch, float delta) {
        if (showTimer <= 0 && popupQueue.size > 0) {
            currentTitle = popupQueue.removeIndex(0);
            showTimer = SHOW_DURATION;
        }

        if (showTimer > 0) {
            showTimer -= delta;

            Matrix4 oldMatrix = batch.getProjectionMatrix().cpy();
            batch.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, 1920f, 1080f));

            float popupWidth = 400f;
            float popupHeight = 100f;
            float x = (1920f - popupWidth) / 2f;
            float y = 1080f - popupHeight - 30f;

            batch.begin();
            batch.setColor(Color.WHITE);
            batch.draw(popupBg, x, y, popupWidth, popupHeight);

            font.setColor(Color.GOLD);
            font.draw(batch, "Achievement Unlocked!", x, y + 75f, popupWidth, Align.center, false);

            font.setColor(Color.WHITE);
            font.draw(batch, currentTitle, x, y + 35f, popupWidth, Align.center, false);
            batch.end();

            batch.setProjectionMatrix(oldMatrix);
        }
    }

    public void dispose() {
        if (popupBg != null) popupBg.dispose();
        AchievementManager.getInstance().removeObserver(this);
    }
}
