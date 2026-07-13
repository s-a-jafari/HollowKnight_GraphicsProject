package advanceProgramming.project.com.view;

import advanceProgramming.project.com.Main;
import advanceProgramming.project.com.helper.DatabaseManager;
import advanceProgramming.project.com.model.GameData;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Align;

public class StartMenuScreen implements Screen {
    private final Main game;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont fontLarge;
    private BitmapFont fontSmall;
    private Cursor customCursor;

    private final Texture bgTexture;

    private final Rectangle[] slotBounds = new Rectangle[4];
    private Rectangle backBtnBounds;
    private final GameData[] saveFiles = new GameData[4];

    public StartMenuScreen(Main game, Texture bgTexture) {
        this.game = game;
        this.bgTexture = bgTexture;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        game.updateMenuMusicState();
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("trajan.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter paramLarge = new FreeTypeFontGenerator.FreeTypeFontParameter();
        paramLarge.size = 50;
        paramLarge.color = Color.WHITE;
        fontLarge = generator.generateFont(paramLarge);

        FreeTypeFontGenerator.FreeTypeFontParameter paramSmall = new FreeTypeFontGenerator.FreeTypeFontParameter();
        paramSmall.size = 35;
        paramSmall.color = Color.WHITE;
        fontSmall = generator.generateFont(paramSmall);
        generator.dispose();

        Pixmap pixmap = new Pixmap(Gdx.files.internal("cursor.png"));
        customCursor = Gdx.graphics.newCursor(pixmap, 0, 0);
        Gdx.graphics.setCursor(customCursor);
        pixmap.dispose();

        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();

        float slotWidth = 800f;
        float slotHeight = 80f;
        float startX = (screenW - slotWidth) / 2f;
        float startY = screenH * 0.65f;

        for (int i = 0; i < 4; i++) {
            slotBounds[i] = new Rectangle(startX, startY - (i * 120f), slotWidth, slotHeight);
            saveFiles[i] = DatabaseManager.loadGame(i + 1);
        }

        backBtnBounds = new Rectangle((screenW - 200f) / 2f, screenH * 0.05f, 200f, 60f);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            if (backBtnBounds.contains(mouseX, mouseY)) {
                game.setScreen(new MainMenuScreen(game));
                return;
            }

            for (int i = 0; i < 4; i++) {
                if (slotBounds[i].contains(mouseX, mouseY)) {
                    game.setScreen(new GamePlayScreen(game, i + 1));
                    return;
                }
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MainMenuScreen(game));
            return;
        }

        batch.begin();
        if (bgTexture != null) {
            batch.setColor(0.5f, 0.5f, 0.5f, 1f);
            batch.draw(bgTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            batch.setColor(Color.WHITE);
        }

        fontLarge.draw(batch, "SELECT PROFILE", 0, Gdx.graphics.getHeight() * 0.85f, Gdx.graphics.getWidth(), Align.center, false);

        for (int i = 0; i < 4; i++) {
            Rectangle bounds = slotBounds[i];
            boolean isHovering = bounds.contains(mouseX, mouseY);

            fontSmall.setColor(isHovering ? Color.GOLD : Color.WHITE);

            boolean hasSave = saveFiles[i].isSaved;
            String levelName = "Unknown Area";
            if (hasSave) {
                if (saveFiles[i].currentLevel == 1) levelName = "Forgotten Crossroads";
                else if (saveFiles[i].currentLevel == 2) levelName = "City of Tears";
            }
            String slotText = (i + 1) + ".   " + (hasSave ? levelName + " (Saved)" : "NEW GAME");

            fontSmall.draw(batch, slotText, bounds.x + 20f, bounds.y + (bounds.height / 2f) + (fontSmall.getCapHeight() / 2f));
        }

        fontSmall.setColor(backBtnBounds.contains(mouseX, mouseY) ? Color.GOLD : Color.WHITE);
        fontSmall.draw(batch, "BACK", backBtnBounds.x, backBtnBounds.y + 40f, backBtnBounds.width, Align.center, false);
        batch.end();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1f, 1f, 1f, 0.5f);
        for (int i = 0; i < 4; i++) {
            Rectangle bounds = slotBounds[i];
            shapeRenderer.line(bounds.x, bounds.y, bounds.x + bounds.width, bounds.y);
        }
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
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
        shapeRenderer.dispose();
        fontLarge.dispose();
        fontSmall.dispose();
        if (customCursor != null) customCursor.dispose();
    }
}
