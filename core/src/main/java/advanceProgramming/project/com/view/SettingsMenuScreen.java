package advanceProgramming.project.com.view;

import advanceProgramming.project.com.Main;
import advanceProgramming.project.com.helper.SettingsManager;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.I18NBundle;

public class SettingsMenuScreen implements Screen {
    private final Main game;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private BitmapFont fontLarge, fontSmall;
    private final SettingsManager settings;
    private Texture bgTex;
    private final com.badlogic.gdx.Screen previousScreen;
    private Rectangle langBtn, muteMusicBtn, muteSfxBtn;
    private Rectangle volSliderTrack, brightSliderTrack;
    private Rectangle resetAudioBtn, resetCtrlBtn, backBtn;

    private Rectangle leftKeyBtn, rightKeyBtn, jumpKeyBtn, attackKeyBtn, dashKeyBtn,
        spellKeyBtn, howlingKeyBtn, focusKeyBtn;
    private String waitingAction = null;
    private InputProcessor previousProcessor;

    public SettingsMenuScreen(Main game, Screen previousScreen) {
        this.game = game;
        this.settings = SettingsManager.getInstance();
        this.previousScreen = previousScreen;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1920, 1080);
        bgTex = new Texture("MenuAsset/SettingBg.png");

        FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal("perpetua.otf"));
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = 55;
        fontLarge = gen.generateFont(param);
        param.size = 35;
        fontSmall = gen.generateFont(param);
        gen.dispose();

        float cx = 1920 / 2f;

        langBtn = new Rectangle(cx - 200, 880, 400, 60);
        volSliderTrack = new Rectangle(cx - 150, 780, 400, 30);
        muteMusicBtn = new Rectangle(cx + 300, 770, 260, 50);
        brightSliderTrack = new Rectangle(cx - 150, 680, 400, 30);
        muteSfxBtn = new Rectangle(cx + 300, 670, 260, 50);

        float col1 = cx - 420;
        float col2 = cx + 20;

        leftKeyBtn = new Rectangle(col1, 550, 400, 50);
        attackKeyBtn = new Rectangle(col2, 550, 400, 50);

        rightKeyBtn = new Rectangle(col1, 470, 400, 50);
        dashKeyBtn = new Rectangle(col2, 470, 400, 50);

        jumpKeyBtn = new Rectangle(col1, 390, 400, 50);
        spellKeyBtn = new Rectangle(col2, 390, 400, 50);

        focusKeyBtn = new Rectangle(col2, 310, 400, 50);
        howlingKeyBtn = new Rectangle(col1, 310, 400, 50);

        resetAudioBtn = new Rectangle(cx - 420, 220, 400, 60);
        resetCtrlBtn = new Rectangle(cx + 20, 220, 400, 60);
        backBtn = new Rectangle(cx - 200, 100, 400, 60);

        previousProcessor = Gdx.input.getInputProcessor();
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (waitingAction != null) {
                    settings.setKey(waitingAction, keycode);
                    waitingAction = null;
                    return true;
                }
                return false;
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (waitingAction != null) return true;

                Vector3 touch = new Vector3(screenX, screenY, 0);
                camera.unproject(touch);
                float mx = touch.x;
                float my = touch.y;

                if (langBtn.contains(mx, my)) settings.toggleLanguage();
                else if (muteMusicBtn.contains(mx, my)) settings.setMusicMuted(!settings.isMusicMuted());
                else if (muteSfxBtn.contains(mx, my)) settings.setSfxMuted(!settings.isSfxMuted());

                else if (leftKeyBtn.contains(mx, my)) waitingAction = "keyLeft";
                else if (rightKeyBtn.contains(mx, my)) waitingAction = "keyRight";
                else if (jumpKeyBtn.contains(mx, my)) waitingAction = "keyJump";
                else if (attackKeyBtn.contains(mx, my)) waitingAction = "keyAttack";
                else if (focusKeyBtn.contains(mx, my)) waitingAction = "keyFocus";
                else if (dashKeyBtn.contains(mx, my)) waitingAction = "keyDash";
                else if (spellKeyBtn.contains(mx, my)) waitingAction = "keySpell";
                else if (howlingKeyBtn.contains(mx, my)) waitingAction = "keyHowling";

                else if (resetAudioBtn.contains(mx, my)) settings.resetAudio();
                else if (resetCtrlBtn.contains(mx, my)) settings.resetControls();
                else if (backBtn.contains(mx, my)) {
                    game.setScreen(previousScreen);
                } else if (volSliderTrack.contains(mx, my)) {
                    float percent = (mx - volSliderTrack.x) / volSliderTrack.width;
                    settings.setMusicVolume(Math.max(0f, Math.min(1f, percent)));
                } else if (brightSliderTrack.contains(mx, my)) {
                    float percent = (mx - brightSliderTrack.x) / brightSliderTrack.width;
                    settings.setBrightness(Math.max(0.2f, Math.min(1f, percent)));
                }
                return true;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                Vector3 touch = new Vector3(screenX, screenY, 0);
                camera.unproject(touch);
                float mx = touch.x;
                float my = touch.y;

                if (my >= volSliderTrack.y - 20 && my <= volSliderTrack.y + volSliderTrack.height + 20) {
                    float percent = (mx - volSliderTrack.x) / volSliderTrack.width;
                    settings.setMusicVolume(Math.max(0f, Math.min(1f, percent)));
                } else if (my >= brightSliderTrack.y - 20 && my <= brightSliderTrack.y + brightSliderTrack.height + 20) {
                    float percent = (mx - brightSliderTrack.x) / brightSliderTrack.width;
                    settings.setBrightness(Math.max(0.2f, Math.min(1f, percent)));
                }
                return true;
            }
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        batch.begin();
        batch.setColor(0.4f, 0.4f, 0.4f, 1f);
        if (bgTex != null) batch.draw(bgTex, 0, 0, 1920, 1080);
        batch.setColor(Color.WHITE);
        batch.end();

        SettingsManager settings = SettingsManager.getInstance();
        boolean isMuted = settings.isMusicMuted();
        float vol = settings.getMusicVolume();

        if (previousScreen instanceof advanceProgramming.project.com.view.GamePlayScreen) {
            ((advanceProgramming.project.com.view.GamePlayScreen) previousScreen).applyLiveMusicSettings(isMuted, vol);

            if (game.menuMusic != null && game.menuMusic.isPlaying()) game.menuMusic.pause();
        } else {
            game.updateMenuMusicState();
        }

        Vector3 mouse = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mouse);
        float mx = mouse.x;
        float my = mouse.y;

        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(volSliderTrack.x, volSliderTrack.y, volSliderTrack.width, volSliderTrack.height);
        shapeRenderer.setColor(Color.GOLD);
        shapeRenderer.rect(volSliderTrack.x, volSliderTrack.y, volSliderTrack.width * settings.getMusicVolume(), volSliderTrack.height);

        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(brightSliderTrack.x, brightSliderTrack.y, brightSliderTrack.width, brightSliderTrack.height);
        shapeRenderer.setColor(Color.GOLD);
        shapeRenderer.rect(brightSliderTrack.x, brightSliderTrack.y, brightSliderTrack.width * settings.getBrightness(), brightSliderTrack.height);

        drawButtonShape(langBtn, mx, my);
        drawButtonShape(muteMusicBtn, mx, my);
        drawButtonShape(muteSfxBtn, mx, my);
        drawButtonShape(resetAudioBtn, mx, my);
        drawButtonShape(resetCtrlBtn, mx, my);
        drawButtonShape(backBtn, mx, my);
        drawButtonShape(leftKeyBtn, mx, my);
        drawButtonShape(rightKeyBtn, mx, my);
        drawButtonShape(jumpKeyBtn, mx, my);
        drawButtonShape(attackKeyBtn, mx, my);
        drawButtonShape(dashKeyBtn, mx, my);
        drawButtonShape(spellKeyBtn, mx, my);
        drawButtonShape(howlingKeyBtn, mx, my);
        drawButtonShape(focusKeyBtn, mx, my);

        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        batch.begin();
        com.badlogic.gdx.utils.I18NBundle bundle = settings.getBundle();
        float cx = 1920 / 2f;

        fontLarge.draw(batch, bundle.get("settings"), 0, 1000, 1920, Align.center, false);
        fontSmall.draw(batch, bundle.get("language"), langBtn.x, langBtn.y + 45, langBtn.width, Align.center, false);
        fontSmall.draw(batch, bundle.get("music_volume"), cx - 400, volSliderTrack.y + 30);
        fontSmall.draw(batch, bundle.get("mute_music") + (settings.isMusicMuted() ? " ON" : " OFF"), muteMusicBtn.x, muteMusicBtn.y + 35, muteMusicBtn.width, Align.center, false);
        fontSmall.draw(batch, bundle.get("brightness"), cx - 400, brightSliderTrack.y + 30);
        fontSmall.draw(batch, bundle.get("mute_sfx") + (settings.isSfxMuted() ? " ON" : " OFF"), muteSfxBtn.x, muteSfxBtn.y + 35, muteSfxBtn.width, Align.center, false);

        drawKeyText(leftKeyBtn, "Move Left", "keyLeft", Input.Keys.LEFT, bundle);
        drawKeyText(rightKeyBtn, "Move Right", "keyRight", Input.Keys.RIGHT, bundle);
        drawKeyText(jumpKeyBtn, "Jump", "keyJump", Input.Keys.Z, bundle);
        drawKeyText(attackKeyBtn, "Attack", "keyAttack", Input.Keys.X, bundle);
        drawKeyText(dashKeyBtn, "Dash", "keyDash", Input.Keys.C, bundle);
        drawKeyText(spellKeyBtn, "Fireball", "keySpell", Input.Keys.S, bundle); // 💡
        drawKeyText(howlingKeyBtn, "Howling", "keyHowling", Input.Keys.D, bundle); // 💡
        drawKeyText(focusKeyBtn, "Focus (Heal)", "keyFocus", Input.Keys.A, bundle); // 💡
        fontSmall.draw(batch, bundle.get("reset_audio"), resetAudioBtn.x, resetAudioBtn.y + 45, resetAudioBtn.width, Align.center, false);
        fontSmall.draw(batch, bundle.get("reset_controls"), resetCtrlBtn.x, resetCtrlBtn.y + 45, resetCtrlBtn.width, Align.center, false);
        fontSmall.draw(batch, bundle.get("back"), backBtn.x, backBtn.y + 45, backBtn.width, Align.center, false);

        batch.end();
    }

    private void drawButtonShape(Rectangle bounds, float mx, float my) {
        if (bounds.contains(mx, my)) shapeRenderer.setColor(0.4f, 0.4f, 0.5f, 0.7f);
        else shapeRenderer.setColor(0.15f, 0.15f, 0.2f, 0.7f);
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    private void drawKeyText(Rectangle bounds, String label, String actionName, int defaultKey, I18NBundle bundle) {
        String keyStr = (waitingAction != null && waitingAction.equals(actionName))
            ? bundle.get("press_any_key") : "[ " + Input.Keys.toString(settings.getKey(actionName, defaultKey)) + " ]";
        fontSmall.setColor((waitingAction != null && waitingAction.equals(actionName)) ? Color.GOLD : Color.WHITE);
        fontSmall.draw(batch, label + " : " + keyStr, bounds.x, bounds.y + 35, bounds.width, Align.center, false);
        fontSmall.setColor(Color.WHITE);
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
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
        if (bgTex != null) bgTex.dispose();
    }
}
