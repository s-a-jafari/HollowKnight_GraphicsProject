package advanceProgramming.project.com.view;

import advanceProgramming.project.com.Main;
import advanceProgramming.project.com.helper.AchievementManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Align;

public class AchievementsMenuScreen implements Screen {
    private final Main game;
    private SpriteBatch batch;
    private BitmapFont font;
    private Texture bgTex;
    private Cursor customCursor;

    private final Texture[] achTextures = new Texture[5];
    private Rectangle backBtnBounds;

    public AchievementsMenuScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        game.updateMenuMusicState();
        bgTex = new Texture("MenuAsset/AchMenuBg.png");

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("trajan.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 35;
        parameter.color = Color.WHITE;
        font = generator.generateFont(parameter);
        generator.dispose();

        Pixmap pixmap = new Pixmap(Gdx.files.internal("cursor.png"));
        customCursor = Gdx.graphics.newCursor(pixmap, 0, 0);
        Gdx.graphics.setCursor(customCursor);
        pixmap.dispose();

        achTextures[0] = new Texture("MenuAsset/Completion.png");
        achTextures[1] = new Texture("MenuAsset/Speedrun.png");
        achTextures[2] = new Texture("MenuAsset/True Hunter.png");
        achTextures[3] = new Texture("MenuAsset/FalseKnight.png");
        achTextures[4] = new Texture("MenuAsset/Steel Soul.png");

        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();
        float btnWidth = 400f;
        float btnHeight = 80f;
        backBtnBounds = new Rectangle((screenW - btnWidth) / 2f, screenH * 0.05f, btnWidth, btnHeight);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) ||
            (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && backBtnBounds.contains(mouseX, mouseY))) {
            game.setScreen(new MainMenuScreen(game));
            return;
        }

        AchievementManager manager = AchievementManager.getInstance();

        String[] ids = {
            AchievementManager.ACH_COMPLETION,
            AchievementManager.ACH_SPEEDRUN,
            AchievementManager.ACH_TRUE_HUNTER,
            AchievementManager.ACH_FALSE_KNIGHT,
            AchievementManager.ACH_STEEL_SOUL
        };

        String[] titles = {
            "Completion: Finish the Game",
            "Speedrun: Finish under 5 mins",
            "True Hunter: Kill all enemy types",
            "False Knight Defeated",
            "Steel Soul: Win without dying!"
        };

        batch.begin();
        batch.draw(bgTex, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        float screenW = Gdx.graphics.getWidth();
        float startY = Gdx.graphics.getHeight() - 150f;


        float totalItemWidth = 600f;
        float iconX = (screenW - totalItemWidth) / 2f;
        float textX = iconX + 130f;

        for (int i = 0; i < 5; i++) {
            boolean isUnlocked = manager.isUnlocked(ids[i]);
            float yPos = startY - (i * 140f);


            if (isUnlocked) {
                batch.setColor(1f, 1f, 1f, 1f);
                font.setColor(Color.WHITE);
            } else {
                batch.setColor(0.15f, 0.15f, 0.15f, 0.4f);
                font.setColor(Color.DARK_GRAY);
            }

            batch.draw(achTextures[i], iconX, yPos, 100f, 100f);

            font.draw(batch, titles[i], textX, yPos + 65f);

            if (!isUnlocked) {
                font.setColor(Color.GRAY);
                font.draw(batch, "(Locked)", textX, yPos + 25f);
            }
        }


        if (backBtnBounds.contains(mouseX, mouseY)) {
            font.setColor(Color.GRAY);
        } else {
            font.setColor(Color.WHITE);
        }

        font.draw(batch, "Back To Main Menu",
            backBtnBounds.x,
            backBtnBounds.y + (backBtnBounds.height / 2f) + (font.getCapHeight() / 2f),
            backBtnBounds.width,
            Align.center,
            false);

        batch.setColor(Color.WHITE);
        batch.end();
    }

    @Override
    public void hide() {
        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
    }

    @Override
    public void resize(int w, int h) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        bgTex.dispose();
        if (customCursor != null) customCursor.dispose();
        for (Texture t : achTextures) {
            if (t != null) t.dispose();
        }
    }
}
