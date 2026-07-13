package advanceProgramming.project.com.view;

import advanceProgramming.project.com.Main;
import advanceProgramming.project.com.model.Knight;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Rectangle;

public class InventoryMenuScreen implements Screen {
    private final Main game;
    private final Screen previousScreen;
    private final Knight knightModel;
    private SpriteBatch batch;

    private BitmapFont font;
    private Cursor customCursor;

    private Texture bgTex, notchEmpty, notchSlotBg;
    private final Texture[] charmTextures = new Texture[8];

    private final Rectangle[] inventoryBounds = new Rectangle[8];
    private final Rectangle[] equippedBounds = new Rectangle[3];
    private Rectangle backBtnBounds;

    private final Knight.CharmType[] charmTypes = Knight.CharmType.values();

    private final String[] charmDescriptions = {
        "Soul Catcher: Gain more Soul when striking enemies.",
        "Dashmaster: Dash more frequently and with shorter cooldown.",
        "Unbreakable Strength: Increases the damage of your Nail.",
        "Quick Slash: Strike much faster with your Nail.",
        "Quick Focus: Heal yourself much faster.",
        "Heavy Blow: Increases the knockback force of your attacks.",
        "Sharp Shadow: Dash further and damage enemies you pass through.",
        "Void Heart: Increases the power of your spells."
    };

    public InventoryMenuScreen(Main game, Screen previousScreen, Knight knightModel) {
        this.game = game;
        this.previousScreen = previousScreen;
        this.knightModel = knightModel;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("trajan.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 30;
        parameter.color = Color.WHITE;
        font = generator.generateFont(parameter);
        generator.dispose();

        Pixmap pixmap = new Pixmap(Gdx.files.internal("cursor.png"));
        customCursor = Gdx.graphics.newCursor(pixmap, 0, 0);
        Gdx.graphics.setCursor(customCursor);
        pixmap.dispose();

        bgTex = new Texture("MenuAsset/InventoryBg.png");
        notchEmpty = new Texture("MenuAsset/NotchEmpty.png");
        notchSlotBg = new Texture("MenuAsset/NotchSlotBg.png");

        charmTextures[0] = new Texture("MenuAsset/Soul Catcher.png");
        charmTextures[1] = new Texture("MenuAsset/Dashmaster.png");
        charmTextures[2] = new Texture("MenuAsset/Unbreakable Strength.png");
        charmTextures[3] = new Texture("MenuAsset/Quick Slash.png");
        charmTextures[4] = new Texture("MenuAsset/Quick Focus.png");
        charmTextures[5] = new Texture("MenuAsset/Heavy Blow.png");
        charmTextures[6] = new Texture("MenuAsset/Sharp Shadow.png");
        charmTextures[7] = new Texture("MenuAsset/Void Heart.png");

        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();

        float charmSize = 130f;
        float padding = 40f;

        float activeTotalWidth = (3 * charmSize) + (2 * padding);
        float activeStartX = (screenW - activeTotalWidth) / 2f;
        float activeY = screenH * 0.70f;

        for (int i = 0; i < 3; i++) {
            equippedBounds[i] = new Rectangle(activeStartX + (i * (charmSize + padding)), activeY, charmSize, charmSize);
        }

        float invTotalWidth = (4 * charmSize) + (3 * padding);
        float invStartX = (screenW - invTotalWidth) / 2f;
        float invStartY = screenH * 0.40f;

        for (int i = 0; i < 8; i++) {
            float x = invStartX + (i % 4) * (charmSize + padding);
            float y = invStartY - (i / 4) * (charmSize + padding);
            inventoryBounds[i] = new Rectangle(x, y, charmSize, charmSize);

            if (charmTypes[i] != Knight.CharmType.VOID_HEART) {
                if (!knightModel.ownedCharms.contains(charmTypes[i], true)) {
                    knightModel.ownedCharms.add(charmTypes[i]);
                }
            }
        }

        float btnWidth = 400f;
        float btnHeight = 80f;
        backBtnBounds = new Rectangle((screenW - btnWidth) / 2f, screenH * 0.02f, btnWidth, btnHeight);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.1f, 0.95f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (Gdx.input.isKeyJustPressed(Input.Keys.I) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(previousScreen);
            return;
        }

        handleMouseClicks();

        batch.begin();
        if (bgTex != null) batch.draw(bgTex, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        for (int i = 0; i < knightModel.maxNotches; i++) {
            Rectangle bounds = equippedBounds[i];

            if (notchEmpty != null) batch.draw(notchEmpty, bounds.x, bounds.y, bounds.width, bounds.height);

            if (i < knightModel.equippedCharms.size) {
                Knight.CharmType equippedType = knightModel.equippedCharms.get(i);
                int texIndex = getCharmIndex(equippedType);
                if (texIndex != -1 && charmTextures[texIndex] != null) {
                    batch.draw(charmTextures[texIndex], bounds.x, bounds.y, bounds.width, bounds.height);
                }
            }
        }

        for (int i = 0; i < 8; i++) {
            Rectangle bounds = inventoryBounds[i];
            Knight.CharmType type = charmTypes[i];

            if (type == Knight.CharmType.VOID_HEART && !knightModel.hasVoidHeartUnlocked) {
                continue;
            }

            if (knightModel.ownedCharms.contains(type, true)) {
                if (knightModel.equippedCharms.contains(type, true)) {
                    if (notchSlotBg != null) batch.draw(notchSlotBg, bounds.x, bounds.y, bounds.width, bounds.height);
                } else {
                    if (charmTextures[i] != null) {
                        batch.draw(charmTextures[i], bounds.x, bounds.y, bounds.width, bounds.height);
                    }
                }
            }
        }


        float currentMouseX = Gdx.input.getX();
        float currentMouseY = Gdx.graphics.getHeight() - Gdx.input.getY();

        if (backBtnBounds.contains(currentMouseX, currentMouseY)) {
            font.setColor(Color.GRAY);
        } else {
            font.setColor(Color.WHITE);
        }

        font.draw(batch, "Back To Game",
            backBtnBounds.x,
            backBtnBounds.y + (backBtnBounds.height / 2f) + (font.getCapHeight() / 2f),
            backBtnBounds.width,
            com.badlogic.gdx.utils.Align.center,
            false);

        String hoveredDescription = "";

        for (int i = 0; i < knightModel.equippedCharms.size; i++) {
            if (equippedBounds[i].contains(currentMouseX, currentMouseY)) {
                Knight.CharmType type = knightModel.equippedCharms.get(i);
                int index = getCharmIndex(type);
                if (index != -1) hoveredDescription = charmDescriptions[index];
            }
        }

        for (int i = 0; i < 8; i++) {
            if (inventoryBounds[i].contains(currentMouseX, currentMouseY)) {
                hoveredDescription = charmDescriptions[i];
            }
        }

        if (!hoveredDescription.isEmpty()) {
            font.setColor(Color.WHITE);

            font.draw(batch, hoveredDescription,
                0,
                Gdx.graphics.getHeight() * 0.55f + 50,
                Gdx.graphics.getWidth(),
                com.badlogic.gdx.utils.Align.center,
                true);

            font.setColor(Color.WHITE);
        }
        batch.end();
    }

    private void handleMouseClicks() {
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            float mouseX = Gdx.input.getX();
            float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();

            if (backBtnBounds.contains(mouseX, mouseY)) {
                game.setScreen(previousScreen);
                return;
            }

            for (int i = 0; i < knightModel.equippedCharms.size; i++) {
                if (equippedBounds[i].contains(mouseX, mouseY)) {
                    Knight.CharmType typeToRemove = knightModel.equippedCharms.get(i);
                    knightModel.equippedCharms.removeIndex(i);
                    knightModel.usedNotches -= typeToRemove.notchCost;
                    knightModel.updateCharmEffects();
                    return;
                }
            }

            for (int i = 0; i < 8; i++) {
                Knight.CharmType type = charmTypes[i];

                if (type == Knight.CharmType.VOID_HEART && !knightModel.hasVoidHeartUnlocked) {
                    continue;
                }

                if (knightModel.ownedCharms.contains(type, true) && !knightModel.equippedCharms.contains(type, true)) {
                    if (inventoryBounds[i].contains(mouseX, mouseY)) {

                        if (knightModel.usedNotches + type.notchCost <= knightModel.maxNotches) {
                            knightModel.equippedCharms.add(type);
                            knightModel.usedNotches += type.notchCost;
                            knightModel.updateCharmEffects();
                        }
                        return;
                    }
                }
            }
        }
    }

    private int getCharmIndex(Knight.CharmType type) {
        for (int i = 0; i < charmTypes.length; i++) {
            if (charmTypes[i] == type) return i;
        }
        return -1;
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
        if (customCursor != null) customCursor.dispose();
        for (Texture tex : charmTextures) {
            if (tex != null) tex.dispose();
        }
    }
}
