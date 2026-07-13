package advanceProgramming.project.com.view;

import advanceProgramming.project.com.Main;
import advanceProgramming.project.com.helper.AchievementManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

public class WinMenuScreen implements Screen {
    private final Main game;
    private final int currentSaveSlot;
    private final SpriteBatch batch;
    private final OrthographicCamera camera;
    private final BitmapFont titleFont;
    private final BitmapFont buttonFont;
    private final BitmapFont statsFont;
    private final Music winMusic;
    private final Animation<TextureRegion> winAnimation;
    private float stateTime = 0f;
    private final GlyphLayout mainMenuLayout;
    private final float mainMenuX;
    private final float mainMenuY;

    private final GlyphLayout restartLayout;
    private final float restartX;
    private final float restartY;
    private final com.badlogic.gdx.graphics.Cursor cursor;
    private final Texture bgImage;
    private final int deaths;
    private final int kills;
    private final float time;

    private final AchievementPopup achievementPopup;

    public WinMenuScreen(Main game, int deaths, int kills, float time, int saveSlot) {
        this.game = game;
        this.deaths = deaths;
        this.kills = kills;
        this.time = time;
        this.currentSaveSlot = saveSlot;

        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1920, 1080);
        bgImage = new Texture(Gdx.files.internal("MenuAsset/WinBackground.png"));
        Pixmap pixmap = new Pixmap(Gdx.files.internal("cursor.png"));
        cursor = Gdx.graphics.newCursor(pixmap, 0, 0);
        pixmap.dispose();

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("trajan.ttf"));

        FreeTypeFontParameter titleParam = new FreeTypeFontParameter();
        titleParam.size = 120;
        titleParam.color = Color.GOLDENROD;
        titleParam.shadowOffsetX = 5;
        titleParam.shadowOffsetY = 5;
        titleParam.borderWidth = 2f;
        titleParam.borderColor = Color.BLACK;
        titleParam.shadowColor = new Color(0, 0, 0, 0.8f);
        titleFont = generator.generateFont(titleParam);

        FreeTypeFontParameter buttonParam = new FreeTypeFontParameter();
        buttonParam.size = 25;
        buttonParam.color = Color.WHITE;
        buttonParam.borderWidth = 2f;
        buttonParam.borderColor = Color.BLACK;
        buttonFont = generator.generateFont(buttonParam);

        FreeTypeFontParameter statsParam = new FreeTypeFontParameter();
        statsParam.size = 30;
        statsParam.color = Color.WHITE;
        statsParam.borderWidth = 4f;
        statsParam.borderColor = Color.BLACK;
        statsFont = generator.generateFont(statsParam);

        generator.dispose();

        mainMenuLayout = new GlyphLayout(buttonFont, "MAIN MENU");
        restartLayout = new GlyphLayout(buttonFont, "RESTART");

        mainMenuX = (1920 - mainMenuLayout.width) / 2f;
        restartX = (1920 - restartLayout.width) / 2f;

        mainMenuY = 55f;
        restartY = 105f;

        Array<TextureRegion> frames = new Array<>();
        for (int i = 0; i <= 5; i++) {
            frames.add(new TextureRegion(new Texture("Animation/Win_" + String.format("%03d", i) + ".png")));
        }
        winAnimation = new Animation<>(0.15f, frames, Animation.PlayMode.LOOP);

        winMusic = Gdx.audio.newMusic(Gdx.files.internal("Audio/Win.ogg"));
        if (winMusic != null) {
            winMusic.setLooping(false);
            winMusic.setVolume(1.0f);
            winMusic.play();
        }

        achievementPopup = new AchievementPopup(buttonFont);

        AchievementManager manager = AchievementManager.getInstance();

        manager.unlockAchievement(AchievementManager.ACH_COMPLETION, "Achievement: Completion!");

        if (this.time < 300f) {
            manager.unlockAchievement(AchievementManager.ACH_SPEEDRUN, "Achievement: Speedrun!");
        }

        if (this.deaths == 0) {
            manager.unlockAchievement(AchievementManager.ACH_STEEL_SOUL, "Achievement: Steel Soul!");
        }
    }

    @Override
    public void show() {
        Gdx.graphics.setCursor(cursor);
    }

    @Override
    public void render(float delta) {
        stateTime += delta;
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        if (bgImage != null) {
            batch.draw(bgImage, 0, 0, 1920, 1080);
        }
        Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mousePos);

        float shakeX = MathUtils.random(-5f, 5f);
        float shakeY = MathUtils.random(-5f, 5f);

        titleFont.draw(batch, "Y O U   W I N !", shakeX, 950 + shakeY, 1920, Align.center, false);

        int minutes = (int) (time / 60);
        int seconds = (int) (time % 60);
        String timeString = String.format("%02d : %02d", minutes, seconds);

        String statsText = "Total Deaths: " + deaths + "     Enemies Killed: " + kills + "     Play Time: " + timeString;
        statsFont.draw(batch, statsText, 0, 750, 1920, Align.center, false);

        if (winAnimation != null) {
            TextureRegion currentFrame = winAnimation.getKeyFrame(stateTime);
            batch.draw(currentFrame, 800, 285, 349, 186);
        }


        boolean isMainMenuHovering = (mousePos.x >= mainMenuX - 30 && mousePos.x <= mainMenuX + mainMenuLayout.width + 30 &&
            mousePos.y >= mainMenuY - mainMenuLayout.height - 30 && mousePos.y <= mainMenuY + 30);

        boolean isRestartHovering = (mousePos.x >= restartX - 30 && mousePos.x <= restartX + restartLayout.width + 30 &&
            mousePos.y >= restartY - restartLayout.height - 30 && mousePos.y <= restartY + 30);

        if (isMainMenuHovering) {
            buttonFont.setColor(Color.GOLD);
        } else {
            buttonFont.setColor(Color.WHITE);
        }
        buttonFont.draw(batch, "MAIN MENU", mainMenuX, mainMenuY);

        if (isRestartHovering) {
            buttonFont.setColor(Color.GOLD);
        } else {
            buttonFont.setColor(Color.WHITE);
        }
        buttonFont.draw(batch, "RESTART", restartX, restartY);

        batch.end();
        if (achievementPopup != null) {
            achievementPopup.render(batch, delta);
        }

        if (Gdx.input.justTouched()) {
            Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);

            if (touchPos.x >= mainMenuX - 30 && touchPos.x <= mainMenuX + mainMenuLayout.width + 30 &&
                touchPos.y >= mainMenuY - mainMenuLayout.height - 30 && touchPos.y <= mainMenuY + 30) {
                if (winMusic != null) winMusic.stop();
                game.setScreen(new MainMenuScreen(game));
            }

            if (touchPos.x >= restartX - 30 && touchPos.x <= restartX + restartLayout.width + 30 &&
                touchPos.y >= restartY - restartLayout.height - 30 && touchPos.y <= restartY + 30) {
                if (winMusic != null) winMusic.stop();
                game.setScreen(new GamePlayScreen(game, currentSaveSlot));
            }
        }
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        batch.dispose();
        if (winMusic != null) winMusic.dispose();
        if (titleFont != null) titleFont.dispose();
        if (buttonFont != null) buttonFont.dispose();
        if (statsFont != null) statsFont.dispose();
        if (cursor != null) cursor.dispose();
        if (bgImage != null) bgImage.dispose();
        if (achievementPopup != null) achievementPopup.dispose();
    }
}
