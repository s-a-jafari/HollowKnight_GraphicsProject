package advanceProgramming.project.com.view;

import advanceProgramming.project.com.Main;
import advanceProgramming.project.com.helper.SettingsManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Align;

public class GuideMenuScreen implements Screen {
    private final Main game;
    private SpriteBatch batch;
    private Texture bgTex;
    private BitmapFont fontTitle;
    private BitmapFont fontContent;
    private Cursor customCursor;

    private Rectangle backBtnBounds;

    private String keyLeft, keyRight, keyJump, keyAttack, keyDash, keySpell, keyHowling, keyFocus, keyInventory;

    public GuideMenuScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        game.updateMenuMusicState();
        bgTex = new Texture("MenuAsset/GuideMenuBg.png");

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("perpetua.otf"));

        FreeTypeFontGenerator.FreeTypeFontParameter paramTitle = new FreeTypeFontGenerator.FreeTypeFontParameter();
        paramTitle.size = 45;
        paramTitle.color = Color.GOLD;
        fontTitle = generator.generateFont(paramTitle);

        FreeTypeFontGenerator.FreeTypeFontParameter paramContent = new FreeTypeFontGenerator.FreeTypeFontParameter();
        paramContent.size = 25;
        paramContent.color = Color.WHITE;
        fontContent = generator.generateFont(paramContent);

        generator.dispose();

        Pixmap pixmap = new Pixmap(Gdx.files.internal("cursor.png"));
        customCursor = Gdx.graphics.newCursor(pixmap, 0, 0);
        Gdx.graphics.setCursor(customCursor);
        pixmap.dispose();

        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();
        backBtnBounds = new Rectangle((screenW - 400f) / 2f, screenH * 0.05f, 400f, 80f);

        Preferences prefs = Gdx.app.getPreferences("HollowKnightSettings");

        SettingsManager settings = SettingsManager.getInstance();

        keyLeft = Input.Keys.toString(settings.getKey("keyLeft", Input.Keys.LEFT));
        keyRight = Input.Keys.toString(settings.getKey("keyRight", Input.Keys.RIGHT));
        keyJump = Input.Keys.toString(settings.getKey("keyJump", Input.Keys.Z));
        keyAttack = Input.Keys.toString(settings.getKey("keyAttack", Input.Keys.X));
        keyDash = Input.Keys.toString(settings.getKey("keyDash", Input.Keys.C));
        keySpell = Input.Keys.toString(settings.getKey("keySpell", Input.Keys.S));
        keyHowling = Input.Keys.toString(settings.getKey("keyHowling", Input.Keys.D));
        keyFocus = Input.Keys.toString(settings.getKey("keyFocus", Input.Keys.A));
        keyInventory = Input.Keys.toString(settings.getKey("keyInventory", Input.Keys.I));
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

        batch.begin();

        batch.setColor(0.4f, 0.4f, 0.4f, 1f);
        batch.draw(bgTex, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.setColor(Color.WHITE);

        float col1X = 150f;
        float col2X = 750f;
        float col3X = 1350f;
        float startY = 850f;
        float lineSpacing = 45f;

        fontTitle.draw(batch, "CONTROLS", col1X, startY);
        float c1Y = startY - 80f;

        fontContent.draw(batch, "* Move Left :  [ " + keyLeft + " ]", col1X, c1Y);
        fontContent.draw(batch, "* Move Right :  [ " + keyRight + " ]", col1X, c1Y - lineSpacing);
        fontContent.draw(batch, "* Jump :  [ " + keyJump + " ]", col1X, c1Y - lineSpacing * 2);
        fontContent.draw(batch, "* Nail Attack :  [ " + keyAttack + " ]", col1X, c1Y - lineSpacing * 3);
        fontContent.draw(batch, "* Dash :  [ " + keyDash + " ]", col1X, c1Y - lineSpacing * 4);
        fontContent.draw(batch, "* Fireball Spell :  [ " + keySpell + " ]", col1X, c1Y - lineSpacing * 5);
        fontContent.draw(batch, "* Howling Spell :  [ " + keyHowling + " ]", col1X, c1Y - lineSpacing * 6);
        fontContent.draw(batch, "* Focus (Heal) :  [ " + keyFocus + " ]", col1X, c1Y - lineSpacing * 7);
        fontContent.draw(batch, "* Inventory :  [ " + keyInventory + " ]", col1X, c1Y - lineSpacing * 8);

        fontTitle.draw(batch, "ABILITIES & TRAITS", col2X, startY);
        float c2Y = startY - 80f;

        fontContent.setColor(Color.LIGHT_GRAY);
        fontContent.draw(batch, "HEALTH SYSTEM (MASKS):", col2X, c2Y);
        fontContent.setColor(Color.WHITE);
        fontContent.draw(batch, "You start with 5 masks. Taking damage\nremoves a mask. If all masks break,\nyou die and respawn at the start.", col2X, c2Y - 35f);

        float nextY = c2Y - 150f;
        fontContent.setColor(Color.CYAN);
        fontContent.draw(batch, "SOUL SYSTEM:", col2X, nextY);
        fontContent.setColor(Color.WHITE);
        fontContent.draw(batch, "Striking enemies with your Nail extracts\nSoul. The liquid Soul meter fills up\nwith each successful hit.", col2X, nextY - 35f);

        nextY -= 150f;
        fontContent.setColor(Color.GREEN);
        fontContent.draw(batch, "FOCUS (HEALING):", col2X, nextY);
        fontContent.setColor(Color.WHITE);
        fontContent.draw(batch, "Consume collected Soul to restore\nbroken masks and heal yourself.", col2X, nextY - 35f);

        fontTitle.draw(batch, "CHEAT CODES", col3X, startY);
        float c3Y = startY - 80f;

        fontContent.draw(batch, "* Ctrl + H : Emergency Heal", col3X, c3Y);
        fontContent.draw(batch, "* Ctrl + S : Refill Soul Meter", col3X, c3Y - lineSpacing);
        fontContent.draw(batch, "* Ctrl + N : Toggle Noclip Mode", col3X, c3Y - lineSpacing * 2);
        fontContent.draw(batch, "* Ctrl + G : Toggle God Mode", col3X, c3Y - lineSpacing * 3);
        fontContent.draw(batch, "* Ctrl + K : Insta-Kill All Enemies", col3X, c3Y - lineSpacing * 4);
        fontContent.draw(batch, "* Ctrl + B : Boss Arena Teleport", col3X, c3Y - lineSpacing * 5);
        fontContent.draw(batch, "* T Key : Test Achievement Popup", col3X, c3Y - lineSpacing * 6);

        if (backBtnBounds.contains(mouseX, mouseY)) {
            fontTitle.setColor(Color.GRAY);
        } else {
            fontTitle.setColor(Color.WHITE);
        }

        fontTitle.draw(batch, "BACK TO MENU",
            backBtnBounds.x,
            backBtnBounds.y + (backBtnBounds.height / 2f) + (fontTitle.getCapHeight() / 2f),
            backBtnBounds.width,
            Align.center,
            false);

        fontTitle.setColor(Color.GOLD);
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
        if (batch != null) batch.dispose();
        if (bgTex != null) bgTex.dispose();
        if (fontTitle != null) fontTitle.dispose();
        if (fontContent != null) fontContent.dispose();
        if (customCursor != null) customCursor.dispose();
    }
}
