package advanceProgramming.project.com.view;

import advanceProgramming.project.com.Main;
import advanceProgramming.project.com.helper.SettingsManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.concurrent.ThreadLocalRandom;

public class MainMenuScreen implements Screen {
    private final Main game;
    private final Stage stage;
    private Texture bgOfTheMainMenu, titleOfTheMainMenu, bgOfTheMainMenu2;
    private Texture bgOfTheMainMenu3, bgOfTheMainMenu4, bgOfTheMainMenu5;
    private Image bgImage;
    private BitmapFont font;
    private Cursor cursor;
    private Sound hoverMenu;
    private Texture changeBgTexture;

    private Texture activeBg;

    public MainMenuScreen(final Main game) {
        this.game = game;
        stage = new Stage(new FitViewport(Main.VIRTUAL_WIDTH, Main.VIRTUAL_HEIGHT));
        Gdx.input.setInputProcessor(stage);
        loadAssets();
        setupUI();
    }

    private void loadAssets() {
        bgOfTheMainMenu = new Texture(Gdx.files.internal("MenuAsset/bg_mainMenu.png"));
        bgOfTheMainMenu2 = new Texture(Gdx.files.internal("MenuAsset/bg_mainMenu2.png"));
        bgOfTheMainMenu3 = new Texture(Gdx.files.internal("MenuAsset/bg_mainMenu3.png"));
        bgOfTheMainMenu4 = new Texture(Gdx.files.internal("MenuAsset/bg_mainMenu4.png"));
        bgOfTheMainMenu5 = new Texture(Gdx.files.internal("MenuAsset/bg_mainMenu5.png"));
        titleOfTheMainMenu = new Texture(Gdx.files.internal("MenuAsset/title_hollowKnight.png"));
        hoverMenu = Gdx.audio.newSound(Gdx.files.internal("MenuAsset/hoverMenu.wav"));
        Pixmap pixmap = new Pixmap(Gdx.files.internal("cursor.png"));
        changeBgTexture = new Texture(Gdx.files.internal("MenuAsset/refresh.png"));

        cursor = Gdx.graphics.newCursor(pixmap, 0, 0);
        pixmap.dispose();

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("trajan.ttf"));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = 40;
        parameter.color = Color.WHITE;

        font = generator.generateFont(parameter);
        generator.dispose();
    }

    private void setupUI() {
        int randomNum = ThreadLocalRandom.current().nextInt(5);
        if (randomNum == 0) {
            activeBg = bgOfTheMainMenu;
        } else if (randomNum == 1) {
            activeBg = bgOfTheMainMenu2;
        } else if (randomNum == 2) {
            activeBg = bgOfTheMainMenu3;
        } else if (randomNum == 3) {
            activeBg = bgOfTheMainMenu4;
        } else {
            activeBg = bgOfTheMainMenu5;
        }

        bgImage = new Image(activeBg);
        bgImage.setSize(Main.VIRTUAL_WIDTH, Main.VIRTUAL_HEIGHT);
        stage.addActor(bgImage);

        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.top();
        stage.addActor(mainTable);

        Image logoImage = new Image(titleOfTheMainMenu);
        mainTable.add(logoImage).padTop(100f).padBottom(20f).row();

        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        I18NBundle bundle = SettingsManager.getInstance().getBundle();

        String[] menuItems = {
            bundle.get("menu_start"),
            bundle.get("menu_settings"),
            bundle.get("menu_guide"),
            bundle.get("menu_achievements"),
            bundle.get("menu_quit")
        };

        for (final String item : menuItems) {
            final Label label = new Label(item, labelStyle);
            label.setAlignment(Align.center);
            label.addListener(new ClickListener() {
                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    label.setColor(Color.LIGHT_GRAY);
                    hoverMenu.play(1.0f);
                }

                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    label.setColor(Color.WHITE);
                }

                @Override
                public void clicked(InputEvent event, float x, float y) {
                    handleMenuClick(item);
                }
            });

            mainTable.add(label).padBottom(30f).row();
        }

        final Image changeBgBtn = new Image(changeBgTexture);
        changeBgBtn.setPosition(50f, 50f);

        changeBgBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                int rand = ThreadLocalRandom.current().nextInt(5);
                Texture selectedBg;

                if (rand == 0) selectedBg = bgOfTheMainMenu;
                else if (rand == 1) selectedBg = bgOfTheMainMenu2;
                else if (rand == 2) selectedBg = bgOfTheMainMenu3;
                else if (rand == 3) selectedBg = bgOfTheMainMenu4;
                else selectedBg = bgOfTheMainMenu5;

                activeBg = selectedBg;

                bgImage.setDrawable(new TextureRegionDrawable(new TextureRegion(activeBg)));
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                changeBgBtn.setColor(Color.LIGHT_GRAY);
                hoverMenu.play(1.0f);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                changeBgBtn.setColor(Color.WHITE);
            }
        });

        stage.addActor(changeBgBtn);
    }

    private void handleMenuClick(String item) {
        if (item.equals("START GAME")) {
            game.setScreen(new StartMenuScreen(game, activeBg));
        } else if (item.equals("SETTING")) {
            game.setScreen(new SettingsMenuScreen(game, this));
        } else if (item.equals("GUIDE")) {
            game.setScreen(new GuideMenuScreen(game));
        } else if (item.equals("ACHIEVEMENTS")) {
            game.setScreen(new AchievementsMenuScreen(game));
        } else if (item.equals("QUIT GAME")) {
            Gdx.app.exit();
        }
    }

    @Override
    public void show() {
        Gdx.graphics.setCursor(cursor);
        game.updateMenuMusicState();
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
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
        stage.dispose();
        bgOfTheMainMenu.dispose();
        bgOfTheMainMenu2.dispose();
        bgOfTheMainMenu3.dispose();
        bgOfTheMainMenu4.dispose();
        bgOfTheMainMenu5.dispose();
        titleOfTheMainMenu.dispose();
        font.dispose();
        hoverMenu.dispose();
        if (changeBgTexture != null) changeBgTexture.dispose();
    }
}
