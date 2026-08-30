package advanceProgramming.project.com.view;

import advanceProgramming.project.com.Main;
import advanceProgramming.project.com.controller.*;
import advanceProgramming.project.com.helper.*;
import advanceProgramming.project.com.model.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GamePlayScreen implements Screen {
    private final Main game;
    private final float FADE_SPEED = 0.5f;
    private final Array<Enemy> enemies = new Array<>();
    private final Array<EnemyController> enemyControllers = new Array<>();
    private final Array<BreakableWall> breakableWalls = new Array<>();
    private final Array<BreakableWallController> wallControllers = new Array<>();
    private final Array<SolidBlock> bossBarrier = new Array<>();
    private final Array<AmbientEntity> ambients = new Array<>();
    private final Array<String> killedEnemyTypes = new Array<>();
    private final int currentSaveSlot;
    private TiledMap map;
    private float mapPixelWidth, mapPixelHeight;
    private OrthogonalTiledMapRenderer mapRenderer;
    private OrthographicCamera camera;
    private Viewport viewport;
    private TiledMapHelper tiledMapHelper;
    private Array<SolidBlock> solidBlocks;
    private com.badlogic.gdx.graphics.glutils.ShapeRenderer shapeRenderer;
    private Rectangle voidHeartTriggerBox;
    private int[] backgroundLayers = new int[0];
    private int[] foregroundLayers = new int[0];
    private Knight knightModel;
    private KnightController knightController;
    private KnightView knightView;
    private EnemyView enemyView;
    private Zote zote;
    private ZoteController zoteController;
    private ZoteView zoteView;
    private FalseKnight falseKnight;
    private FalseKnightController falseKnightController;
    private FalseKnightView falseKnightView;
    private CrystalGuardian crystalGuardian;
    private CrystalGuardianController crystalGuardianController;
    private BreakableWallView wallView;
    private SpriteBatch batch;
    private Rectangle bossTrigger;
    private Rectangle bossCameraBounds;
    private boolean isBossArenaActive = false;
    private TriggerAnimation mapEventAnim;
    private TriggerAnimationView mapEventView;
    private int currentLevel = 1;
    private Rectangle nextTriggerBox;
    private com.badlogic.gdx.audio.Music level1Music, level2Music, currentMusic;
    private BitmapFont uiFont;
    private AmbientEntity ambientEntity;
    private AmbientEntityView ambientView;
    private boolean isInitialized = false;
    private AchievementPopup achievementPopup;
    private float currentMusicVolume = 0f;
    private boolean isFadingOut = false;
    private boolean isTransitioningLevel = false;
    private boolean isExitingToMenu = false;
    private boolean isPaused = false;
    private boolean showCheats = false;
    private OrthographicCamera uiCamera;
    private BitmapFont pauseFontLarge, pauseFontSmall;
    private Rectangle resumeBtn, cheatsBtn, settingsBtn, saveExitBtn;

    public GamePlayScreen(Main game, int saveSlot) {
        this.game = game;
        this.currentSaveSlot = saveSlot;
    }

    private void loadLevel(int level) {
        if (map != null) map.dispose();
        if (mapRenderer != null) mapRenderer.dispose();

        String mapName = (level == 1) ? "ForgottenCrossroads.tmx" : "CityOfTears.tmx";
        map = tiledMapHelper.loadMap(mapName);
        mapRenderer = new OrthogonalTiledMapRenderer(map);

        com.badlogic.gdx.maps.MapProperties props = map.getProperties();
        mapPixelWidth = props.get("width", Integer.class) * props.get("tilewidth", Integer.class);
        mapPixelHeight = props.get("height", Integer.class) * props.get("tileheight", Integer.class);

        solidBlocks = tiledMapHelper.getSolidRectangles();
        Vector2 spawnPoint = tiledMapHelper.getSpawnPoint();


        knightModel.x = spawnPoint.x;
        knightModel.y = spawnPoint.y;
        knightModel.spawnX = spawnPoint.x;
        knightModel.spawnY = spawnPoint.y;
        knightModel.velocityX = 0;
        knightModel.velocityY = 0;
        knightModel.currentState = Knight.State.IDLE;
        knightModel.isGrounded = false;

        camera.position.set(spawnPoint.x + (knightModel.width / 2f), spawnPoint.y + (knightModel.height / 2f), 0);
        camera.update();

        enemies.clear();
        enemyControllers.clear();
        breakableWalls.clear();
        wallControllers.clear();
        zote = null;
        zoteController = null;
        falseKnight = null;
        falseKnightController = null;
        crystalGuardian = null;
        crystalGuardianController = null;

        ambients.clear();
        if (ambientView != null) {
            ambientView.dispose();
        }

        if (level == 1) {
            for (Vector2 sp : tiledMapHelper.getEnemySpawns("SpawnTiktik")) {
                Tiktik t = new Tiktik(sp.x, sp.y, false);
                t.spawnX = sp.x;
                t.spawnY = sp.y;
                enemies.add(t);
                enemyControllers.add(new TiktikController(t, knightModel, solidBlocks));
            }
            for (Vector2 sp : tiledMapHelper.getEnemySpawns("SpawnMosquito")) {
                Mosquito m = new Mosquito(sp.x, sp.y, false);
                m.spawnX = sp.x;
                m.spawnY = sp.y;
                enemies.add(m);
                enemyControllers.add(new MosquitoController(m, knightModel, solidBlocks));
            }
            for (Vector2 sp : tiledMapHelper.getEnemySpawns("SpawnHusk")) {
                HuskHornhead h = new HuskHornhead(sp.x, sp.y, false);
                h.spawnX = sp.x;
                h.spawnY = sp.y;
                enemies.add(h);
                enemyControllers.add(new HuskHornheadController(h, knightModel, solidBlocks));
            }
            Array<Vector2> zoteSpawns = tiledMapHelper.getEnemySpawns("SpawnZote");
            if (zoteSpawns.size > 0) {
                zote = new Zote(zoteSpawns.get(0).x, zoteSpawns.get(0).y);
                zoteController = new ZoteController(zote, knightModel, solidBlocks);
            }
            for (BreakableWall w : tiledMapHelper.getBreakableWalls()) {
                breakableWalls.add(w);
                wallControllers.add(new BreakableWallController(w, knightModel, solidBlocks));
            }
            ambientView = new advanceProgramming.project.com.view.AmbientEntityView("fly_", 4);

            Array<Rectangle> vhTriggers = tiledMapHelper.getKindOfBlock("voidheart");
            if (vhTriggers != null && vhTriggers.size > 0) {
                voidHeartTriggerBox = vhTriggers.get(0);
            } else {
                voidHeartTriggerBox = null;
            }
        } else if (level == 2) {
            Array<Vector2> cgSpawns = tiledMapHelper.getEnemySpawns("SpawnCrystal");
            for (Vector2 sp : cgSpawns) {
                CrystalGuardian h = new CrystalGuardian(sp.x, sp.y, true);
                h.spawnX = sp.x;
                h.spawnY = sp.y;
                h.isFacingRight = true;
                enemies.add(h);
                crystalGuardianController = new CrystalGuardianController(h, knightModel, solidBlocks);
                enemyControllers.add(crystalGuardianController);
            }

            Array<Vector2> fkSpawns = tiledMapHelper.getEnemySpawns("SpawnFalseKnight");
            if (fkSpawns.size > 0) {
                falseKnight = new FalseKnight(fkSpawns.get(0).x, fkSpawns.get(0).y, false);

                if (bossCameraBounds != null) {
                    falseKnight.arenaLeftBound = bossCameraBounds.x;
                    falseKnight.arenaRightBound = bossCameraBounds.x + bossCameraBounds.width;
                }

                falseKnightController = new FalseKnightController(falseKnight, knightModel, solidBlocks);

            }
            ambientView = new AmbientEntityView("pare_", 6);
        }
        for (int i = 0; i < 15; i++) {
            float randomX = MathUtils.random(200f, mapPixelWidth - 200f);
            float randomY = MathUtils.random(200f, mapPixelHeight - 200f);
            ambients.add(new AmbientEntity(randomX, randomY));
        }
        if (currentMusic != null) currentMusic.stop();
        currentMusic = (level == 1) ? level1Music : level2Music;

        nextTriggerBox = tiledMapHelper.getKindOfBlock("next").get(0);

        if (level == 2) {
            bossTrigger = tiledMapHelper.getKindOfBlock("startBlock").get(0);
            Array<Rectangle> cameraRects = tiledMapHelper.getKindOfBlock("bossCameraArena");
            if (cameraRects != null && cameraRects.size > 0) {
                bossCameraBounds = cameraRects.get(0);
            }
            Array<Rectangle> barrierRect = tiledMapHelper.getKindOfBlock("triggerBlock");
            if (barrierRect != null && barrierRect.size > 0) {
                for (Rectangle thisRec : barrierRect) {
                    bossBarrier.add(new SolidBlock(thisRec.x, thisRec.y, thisRec.width, thisRec.height, false));
                }
            }
            if (bossTrigger != null) {
                mapEventAnim = new TriggerAnimation(bossTrigger);
                mapEventView = new TriggerAnimationView();
            } else {
                mapEventAnim = null;
                mapEventView = null;
            }
        }

        if (map.getLayers().get("bg2") != null) {
            map.getLayers().get("bg2").setVisible(false);
        }

        Array<Integer> bgIndices = new Array<>();
        String[] bgNames = {"bg3", "bg", "bg2", "backGround", "main"};
        for (String name : bgNames) {
            for (int i = 0; i < map.getLayers().getCount(); i++) {
                if (map.getLayers().get(i).getName().equalsIgnoreCase(name)) {
                    bgIndices.add(i);
                    break;
                }
            }
        }
        backgroundLayers = new int[bgIndices.size];
        for (int i = 0; i < bgIndices.size; i++) backgroundLayers[i] = bgIndices.get(i);

        Array<Integer> fgIndices = new Array<>();
        String[] fgNames = {"foreGround", "fg", "front_secret", "front_secret2"};
        for (String name : fgNames) {
            for (int i = 0; i < map.getLayers().getCount(); i++) {
                if (map.getLayers().get(i).getName().equalsIgnoreCase(name)) {
                    fgIndices.add(i);
                    break;
                }
            }
        }
        foregroundLayers = new int[fgIndices.size];
        for (int i = 0; i < fgIndices.size; i++) foregroundLayers[i] = fgIndices.get(i);
    }

    @Override
    public void show() {
        if (game.menuMusic != null) {
            game.menuMusic.stop();
        }
        if (!isInitialized) {
            camera = new OrthographicCamera();
            camera.zoom = 0.7f;
            camera.setToOrtho(false, 1920, 1080);
            viewport = new FitViewport(1920, 1080, camera);
            tiledMapHelper = new advanceProgramming.project.com.helper.TiledMapHelper();
            batch = new SpriteBatch();
            ambientView = new advanceProgramming.project.com.view.AmbientEntityView("fly_", 4);
            shapeRenderer = new com.badlogic.gdx.graphics.glutils.ShapeRenderer();

            level1Music = Gdx.audio.newMusic(Gdx.files.internal("Audio/Crossroads.mp3"));
            if (level1Music != null) {
                level1Music.setLooping(true);
                level1Music.setVolume(0.5f);
            }

            level2Music = Gdx.audio.newMusic(Gdx.files.internal("Audio/CityOfTears.mp3"));
            if (level2Music != null) {
                level2Music.setLooping(true);
                level2Music.setVolume(0.5f);
            }

            knightModel = new Knight(0, 0);
            knightView = new KnightView();

            GameData saveData = DatabaseManager.loadGame(currentSaveSlot);

            if (saveData.isSaved) {
                currentLevel = saveData.currentLevel;
            } else {
                currentLevel = 1;
            }

            loadLevel(currentLevel);

            if (saveData.isSaved) {
                knightModel.x = saveData.knightX;
                knightModel.y = saveData.knightY;
                knightModel.setCurrentHp(saveData.currentHp);
                knightModel.setCurrentSoul(saveData.currentSoul);
                knightModel.deathCount = saveData.deathCount;
                knightModel.enemiesKilled = saveData.enemiesKilled;
                knightModel.playTime = saveData.playTime;

                if (saveData.isFalseKnightDead && falseKnight != null) falseKnight.hp = 0;
                if (saveData.isCrystalGuardianDead && crystalGuardian != null) crystalGuardian.hp = 0;

                if (saveData.brokenWallsState != null && !saveData.brokenWallsState.isEmpty()) {
                    String[] wallStates = saveData.brokenWallsState.split(",");
                    for (int i = 0; i < Math.min(wallStates.length, breakableWalls.size); i++) {
                        if (wallStates[i].equals("1")) {
                            breakableWalls.get(i).isBroken = true;
                            breakableWalls.get(i).hp = 0;
                        }
                    }
                }

                if (saveData.enemiesState != null && !saveData.enemiesState.isEmpty()) {
                    String[] eStates = saveData.enemiesState.split(",");
                    for (int i = 0; i < Math.min(eStates.length, enemies.size); i++) {
                        if (eStates[i].equals("1")) {
                            enemies.get(i).hp = 0;
                            enemies.get(i).isScoreCounted = true;
                        }
                    }
                }

                if (saveData.isMapEventTriggered && mapEventAnim != null) {
                    mapEventAnim.isTriggered = true;
                    mapEventAnim.stateTime = 999f;
                }

                AchievementManager.getInstance().getUnlockedAchievements().clear();
                if (saveData.unlockedAchievements != null && !saveData.unlockedAchievements.isEmpty()) {
                    String[] unlocked = saveData.unlockedAchievements.split(",");
                    for (String ach : unlocked) {
                        if (!ach.isEmpty()) {
                            AchievementManager.getInstance().getUnlockedAchievements().add(ach);
                        }
                    }
                }
            }

            knightController = new KnightController(knightModel, solidBlocks);

            enemyView = new EnemyView();
            wallView = new BreakableWallView();
            zoteView = new ZoteView();
            falseKnightView = new FalseKnightView();

            if (uiFont == null) {
                FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("perpetua.otf"));
                FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

                parameter.size = 35;
                parameter.color = com.badlogic.gdx.graphics.Color.WHITE;

                uiFont = generator.generateFont(parameter);
                generator.dispose();
            }

            achievementPopup = new AchievementPopup(uiFont);

            isInitialized = true;
        }

        uiCamera = new OrthographicCamera();
        uiCamera.setToOrtho(false, 1920, 1080);

        FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal("perpetua.otf"));
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();

        param.size = 70;
        pauseFontLarge = gen.generateFont(param);
        param.size = 35;
        pauseFontSmall = gen.generateFont(param);
        gen.dispose();

        float cx = 1920 / 2f;
        float cy = 1080 / 2f;
        resumeBtn = new Rectangle(cx - 200, cy + 120, 400, 60);
        cheatsBtn = new Rectangle(cx - 200, cy + 30, 400, 60);
        settingsBtn = new Rectangle(cx - 200, cy - 60, 400, 60);
        saveExitBtn = new Rectangle(cx - 200, cy - 150, 400, 60);
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void render(float delta) {
        if (delta > 0.05f) delta = 0.05f;

        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ESCAPE)) {
            isPaused = !isPaused;
            showCheats = false;
        }

        SettingsManager settings = SettingsManager.getInstance();

        if (currentMusic != null) {
            if (settings.isMusicMuted()) {
                if (currentMusic.isPlaying()) currentMusic.pause();
                currentMusicVolume = 0f;
            } else {
                float targetVolume = isFadingOut ? 0f : settings.getMusicVolume();

                if (currentMusicVolume < targetVolume) {
                    currentMusicVolume += FADE_SPEED * delta;
                    if (currentMusicVolume > targetVolume) currentMusicVolume = targetVolume;
                } else if (currentMusicVolume > targetVolume) {
                    currentMusicVolume -= FADE_SPEED * delta;
                    if (currentMusicVolume < targetVolume) currentMusicVolume = targetVolume;
                }

                currentMusic.setVolume(currentMusicVolume);

                if (!currentMusic.isPlaying() && currentMusicVolume > 0) {
                    currentMusic.play();
                }
            }
        }

        if (!isPaused) {
            checkCheatCodes();

            float hitBoxWidth = 60f;
            float hitBoxHeight = 130f;
            float hitBoxX = knightModel.x + (knightModel.width / 2f) - (hitBoxWidth / 2f);
            float hitBoxY = knightModel.y;

            Rectangle knightBox = new Rectangle(hitBoxX, hitBoxY, hitBoxWidth, hitBoxHeight);

            if (voidHeartTriggerBox != null && knightBox.overlaps(voidHeartTriggerBox)) {
                if (!knightModel.hasVoidHeartUnlocked) {
                    knightModel.hasVoidHeartUnlocked = true;

                    if (!knightModel.ownedCharms.contains(Knight.CharmType.VOID_HEART, true)) {
                        knightModel.ownedCharms.add(Knight.CharmType.VOID_HEART);
                    }

                    knightModel.updateCharmEffects();

                }
            }

            if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.I)) {
                if (knightModel.currentState != Knight.State.CASTING_HOWLING && knightModel.currentState != Knight.State.CASTING_VENGEFUL && knightModel.currentState != Knight.State.ATTACKING && knightModel.currentState != Knight.State.DOWN_ATTACKING) {

                    game.setScreen(new InventoryMenuScreen(game, this, knightModel));
                    return;
                }
            }


            if (knightBox.overlaps(nextTriggerBox) && !isTransitioningLevel && !isExitingToMenu) {
                isFadingOut = true;

                if (currentLevel == 1) {
                    isTransitioningLevel = true;
                } else if (currentLevel == 2) {
                    isExitingToMenu = true;
                }
            }

            if (isFadingOut && currentMusicVolume <= 0f) {
                if (isTransitioningLevel) {
                    currentLevel = 2;
                    loadLevel(currentLevel);
                    knightController = new KnightController(knightModel, solidBlocks);
                    isTransitioningLevel = false;
                    isFadingOut = false;
                } else if (isExitingToMenu) {
                    if (currentMusic != null) currentMusic.stop();
                    game.setScreen(new WinMenuScreen(game, knightModel.deathCount, knightModel.enemiesKilled, knightModel.playTime, currentSaveSlot));
                    return;
                }
            }

            if (knightModel.getCurrentHp() <= 0) {
                if (falseKnight != null && !falseKnight.isDead()) {
                    falseKnight.isFightStarted = false;
                    isBossArenaActive = false;

                    if (map.getLayers().get("bg2") != null) {
                        map.getLayers().get("bg2").setVisible(false);
                    }
                    if (bossBarrier != null) {
                        for (SolidBlock thisBlock : bossBarrier) {
                            solidBlocks.removeValue(thisBlock, true);
                        }
                    }
                }
            }

            for (BreakableWall wall : breakableWalls) {
                if (wall.isBroken) {
                    if (map.getLayers().get("front_secret") != null && map.getLayers().get("front_secret").isVisible()) {
                        map.getLayers().get("front_secret").setVisible(false);
                    }
                    if (map.getLayers().get("front_secret2") != null && map.getLayers().get("front_secret2").isVisible()) {
                        map.getLayers().get("front_secret2").setVisible(false);
                    }
                }
            }

            if (falseKnight != null && !falseKnight.isDead() && bossTrigger != null) {

                if (!falseKnight.isFightStarted && knightBox.overlaps(bossTrigger)) {

                    falseKnight.isFightStarted = true;
                    isBossArenaActive = true;

                    if (map.getLayers().get("bg2") != null) {
                        map.getLayers().get("bg2").setVisible(true);
                    }

                    if (bossBarrier != null && !solidBlocks.contains(bossBarrier.get(0), true)) {
                        for (SolidBlock thisBlock : bossBarrier) {
                            solidBlocks.add(thisBlock);
                        }
                    }
                }
            }

            if (falseKnight != null && falseKnight.isDead() && isBossArenaActive) {
                isBossArenaActive = false;

                if (map.getLayers().get("bg2") != null) {
                    map.getLayers().get("bg2").setVisible(false);
                }
                if (bossBarrier != null) {
                    for (SolidBlock thisBlock : bossBarrier) {
                        solidBlocks.removeValue(thisBlock, true);
                    }
                }
            }

            if (mapEventAnim != null) {
                if (!mapEventAnim.isTriggered && knightBox.overlaps(mapEventAnim.hitbox)) {
                    mapEventAnim.isTriggered = true;
                }
                if (mapEventAnim.isTriggered) mapEventAnim.stateTime += delta;
            }

            for (Enemy enemy : enemies) {
                if (enemy.isDead()) {

                    if (!enemy.isScoreCounted) {
                        knightModel.enemiesKilled++;

                        String typeName = enemy.getClass().getSimpleName();
                        if (!killedEnemyTypes.contains(typeName, false)) {
                            killedEnemyTypes.add(typeName);

                            if (killedEnemyTypes.size == 5) {
                                AchievementManager.getInstance().unlockAchievement(AchievementManager.ACH_TRUE_HUNTER, "Achievement: True Hunter!");
                            }
                        }
                        enemy.isScoreCounted = true;
                    }

                    if (!(enemy instanceof CrystalGuardian)) {
                        float distFromSpawn = Vector2.dst(knightModel.x, knightModel.y, enemy.spawnX, enemy.spawnY);

                        if (distFromSpawn > 1500f) {
                            respawnEnemy(enemy);
                        }
                    }
                }
            }

            if (falseKnight != null && falseKnight.isDead() && !falseKnight.isScoreCounted) {
                knightModel.enemiesKilled++;
                if (!killedEnemyTypes.contains("FalseKnight", false)) killedEnemyTypes.add("FalseKnight");
                falseKnight.isScoreCounted = true;
            }

            if (crystalGuardian != null && crystalGuardian.isDead() && !crystalGuardian.isScoreCounted) {
                knightModel.enemiesKilled++;
                if (!killedEnemyTypes.contains("CrystalGuardian", false)) killedEnemyTypes.add("CrystalGuardian");
                crystalGuardian.isScoreCounted = true;
            }

            knightController.update(delta);
            if (zoteController != null) zoteController.update(delta);
            if (falseKnightController != null) falseKnightController.update(delta);
            for (EnemyController controller : enemyControllers) controller.update(delta);
            for (BreakableWallController wc : wallControllers) wc.update(delta);

            float targetX = knightModel.x + (knightModel.width / 2f);
            float targetY = knightModel.y + (knightModel.height / 2f);

            float halfCamWidth = (camera.viewportWidth * camera.zoom) / 2f;
            float halfCamHeight = (camera.viewportHeight * camera.zoom) / 2f;

            float minX = halfCamWidth;
            float maxX = mapPixelWidth - halfCamWidth;
            float minY = halfCamHeight;
            float maxY = mapPixelHeight - halfCamHeight;

            if (falseKnight != null && falseKnight.isFightStarted && !falseKnight.isDead() && bossCameraBounds != null) {
                minX = bossCameraBounds.x + halfCamWidth;
                maxX = bossCameraBounds.x + bossCameraBounds.width - halfCamWidth;
                minY = bossCameraBounds.y + halfCamHeight;
                maxY = bossCameraBounds.y + bossCameraBounds.height - halfCamHeight;

                if (minX > maxX) {
                    minX = bossCameraBounds.x + (bossCameraBounds.width / 2f);
                    maxX = minX;
                }
                if (minY > maxY) {
                    minY = bossCameraBounds.y + (bossCameraBounds.height / 2f);
                    maxY = minY;
                }
            }

            float clampedTargetX = com.badlogic.gdx.math.MathUtils.clamp(targetX, minX, maxX);
            float clampedTargetY = com.badlogic.gdx.math.MathUtils.clamp(targetY, minY, maxY);

            camera.position.x += (clampedTargetX - camera.position.x) * 6f * delta;
            camera.position.y += (clampedTargetY - camera.position.y) * 6f * delta;

            if (falseKnight != null && falseKnight.shakeTimeLeft > 0) {
                falseKnight.shakeTimeLeft -= delta;
                float shakeX = (MathUtils.randomBoolean() ? 1 : -1) * falseKnight.shakeIntensity;
                float shakeY = (MathUtils.randomBoolean() ? 1 : -1) * falseKnight.shakeIntensity;
                camera.position.add(shakeX, shakeY, 0);
            }
            CameraShake.update(delta, camera);
            camera.update();
            mapRenderer.setView(camera);

            if (backgroundLayers.length > 0) {
                mapRenderer.render(backgroundLayers);
            } else {
                mapRenderer.render();
            }

            if (ambients != null) {
                for (int i = 0; i < ambients.size; i++) {
                    ambients.get(i).update(delta, ambients, mapPixelWidth, mapPixelHeight);
                }
            }
        }

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        if (mapEventAnim != null && mapEventView != null) mapEventView.render(batch, mapEventAnim);
        for (BreakableWall wall : breakableWalls) wallView.render(batch, wall);
        if (zoteView != null && zote != null) zoteView.render(batch, zote, zoteController);
        if (falseKnightView != null && falseKnight != null) falseKnightView.render(batch, falseKnight);
        for (Enemy enemy : enemies) enemyView.render(batch, enemy);
        knightView.render(batch, knightModel);

        batch.end();

        if (foregroundLayers.length > 0) {
            mapRenderer.render(foregroundLayers);
        }

        batch.begin();
        if (ambientView != null && ambients != null) {
            for (int i = 0; i < ambients.size; i++) {
                ambientView.render(batch, ambients.get(i));
            }
        }
        batch.end();

        if (achievementPopup != null) {
            achievementPopup.render(batch, delta);
        }
        knightView.renderHUD(knightModel);
        if (zoteView != null && zote != null) zoteView.renderHUD(zote);

        float alpha = 1.0f - SettingsManager.getInstance().getBrightness();
        if (alpha > 0) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0, 0, 0, alpha);
            shapeRenderer.rect(0, 0, 1920, 1080);
            shapeRenderer.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }

        if (isPaused) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            shapeRenderer.setProjectionMatrix(uiCamera.combined);
            shapeRenderer.begin(com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Filled);

            shapeRenderer.setColor(0, 0, 0, 0.75f);
            shapeRenderer.rect(0, 0, 1920, 1080);

            Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            uiCamera.unproject(mousePos);
            float mx = mousePos.x;
            float my = mousePos.y;

            drawPauseButton(resumeBtn, mx, my);
            drawPauseButton(cheatsBtn, mx, my);
            drawPauseButton(settingsBtn, mx, my);
            drawPauseButton(saveExitBtn, mx, my);

            shapeRenderer.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);

            batch.setProjectionMatrix(uiCamera.combined);

            batch.begin();
            pauseFontLarge.draw(batch, "PAUSED", 0, 950, 1920, com.badlogic.gdx.utils.Align.center, false);
            pauseFontSmall.draw(batch, "Continue", resumeBtn.x, resumeBtn.y + 40, resumeBtn.width, com.badlogic.gdx.utils.Align.center, false);
            pauseFontSmall.draw(batch, "Cheat Codes", cheatsBtn.x, cheatsBtn.y + 40, cheatsBtn.width, com.badlogic.gdx.utils.Align.center, false);
            pauseFontSmall.draw(batch, "Settings", settingsBtn.x, settingsBtn.y + 40, settingsBtn.width, com.badlogic.gdx.utils.Align.center, false);
            pauseFontSmall.draw(batch, "Save & Exit", saveExitBtn.x, saveExitBtn.y + 40, saveExitBtn.width, com.badlogic.gdx.utils.Align.center, false);
            if (showCheats) {
                float cx = 1920 / 2f;
                float cy = 1080 / 2f;
                float chX = cx + 250;
                float chY = cy + 150;
                pauseFontSmall.setColor(com.badlogic.gdx.graphics.Color.GOLD);
                pauseFontSmall.draw(batch, "--- Cheat Codes ---", chX, chY);
                pauseFontSmall.setColor(com.badlogic.gdx.graphics.Color.WHITE);
                pauseFontSmall.draw(batch, "Ctrl + B : Boss Teleport", chX, chY - 60);
                pauseFontSmall.draw(batch, "Ctrl + N : Noclip Mode", chX, chY - 120);
                pauseFontSmall.draw(batch, "Ctrl + H : Emergency Heal", chX, chY - 180);
                pauseFontSmall.draw(batch, "Ctrl + S : Refill Soul", chX, chY - 240);
                pauseFontSmall.draw(batch, "Ctrl + G : God Mode", chX, chY - 300);
                pauseFontSmall.draw(batch, "Ctrl + K : Insta-Kill", chX, chY - 360);
            }
            batch.end();

            if (Gdx.input.justTouched()) {
                if (resumeBtn.contains(mx, my)) {
                    isPaused = false;
                } else if (cheatsBtn.contains(mx, my)) {
                    showCheats = !showCheats;
                } else if (settingsBtn.contains(mx, my)) {
                    game.setScreen(new SettingsMenuScreen(game, this));
                } else if (saveExitBtn.contains(mx, my)) {
                    saveGameData();
                    if (currentMusic != null) currentMusic.stop();
                    game.setScreen(new MainMenuScreen(game));
                }
            }
        }
    }

    private void checkCheatCodes() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.F5)) {
            GameData dataToSave = new GameData();

            dataToSave.slotNumber = currentSaveSlot;
            dataToSave.isSaved = true;
            dataToSave.knightX = knightModel.x;
            dataToSave.knightY = knightModel.y;
            dataToSave.currentLevel = currentLevel;

            dataToSave.maxHp = knightModel.getMaxHp();
            dataToSave.currentHp = knightModel.getCurrentHp();
            dataToSave.currentSoul = knightModel.getCurrentSoul();
            dataToSave.coins = knightModel.getCoins();

            dataToSave.deathCount = knightModel.deathCount;
            dataToSave.enemiesKilled = knightModel.enemiesKilled;
            dataToSave.playTime = knightModel.playTime;

            dataToSave.isFalseKnightDead = (falseKnight != null && falseKnight.isDead());
            dataToSave.isCrystalGuardianDead = (crystalGuardian != null && crystalGuardian.isDead());

            StringBuilder wallsStr = new StringBuilder();
            for (BreakableWall wall : breakableWalls) {
                wallsStr.append(wall.isBroken ? "1" : "0").append(",");
            }
            dataToSave.brokenWallsState = wallsStr.toString();

            StringBuilder enemiesStr = new StringBuilder();
            for (Enemy enemy : enemies) {
                enemiesStr.append(enemy.isDead() ? "1" : "0").append(",");
            }
            dataToSave.enemiesState = enemiesStr.toString();

            dataToSave.isMapEventTriggered = (mapEventAnim != null && mapEventAnim.isTriggered);

            StringBuilder achStr = new StringBuilder();
            Array<String> unlockedList = AchievementManager.getInstance().getUnlockedAchievements();
            for (String ach : unlockedList) {
                achStr.append(ach).append(",");
            }
            dataToSave.unlockedAchievements = achStr.toString();

            DatabaseManager.saveGame(dataToSave);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {

            if (Gdx.input.isKeyJustPressed(Input.Keys.B)) {
                if (currentLevel != 2) {
                    currentLevel = 2;
                    loadLevel(currentLevel);

                    knightController = new KnightController(knightModel, solidBlocks);
                }

                if (bossCameraBounds != null) {
                    knightModel.x = bossCameraBounds.x + 150f;
                    knightModel.y = bossCameraBounds.y + 300f;

                    knightModel.velocityX = 0;
                    knightModel.velocityY = 0;
                    knightModel.currentState = Knight.State.FALLING;
                }
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.N)) {
                knightModel.isNoclip = !knightModel.isNoclip;
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.H)) {
                if (knightModel.getCurrentHp() < knightModel.getMaxHp()) {
                    knightModel.heal(1);
                }
            }

            if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.S)) {
                knightModel.setCurrentSoul(knightModel.getMaxSoul());
            }

            if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.G)) {
                knightModel.isGodMode = !knightModel.isGodMode;
            }

            if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.K)) {
                for (Enemy enemy : enemies) {
                    enemy.hp = 0;
                }
                if (falseKnight != null) {
                    falseKnight.hp = 0;
                    falseKnight.currentHits = 99;
                }
                if (crystalGuardian != null) {
                    crystalGuardian.hp = 0;
                }
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, false);
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
        if (map != null) map.dispose();
        if (mapRenderer != null) mapRenderer.dispose();
        if (batch != null) batch.dispose();
        if (level1Music != null) level1Music.dispose();
        if (level2Music != null) level2Music.dispose();
        if (achievementPopup != null) achievementPopup.dispose();
    }

    public void saveGameData() {
        GameData dataToSave = new GameData();

        dataToSave.slotNumber = currentSaveSlot;
        dataToSave.isSaved = true;
        dataToSave.knightX = knightModel.x;
        dataToSave.knightY = knightModel.y;
        dataToSave.currentLevel = currentLevel;

        dataToSave.maxHp = knightModel.getMaxHp();
        dataToSave.currentHp = knightModel.getCurrentHp();
        dataToSave.currentSoul = knightModel.getCurrentSoul();
        dataToSave.coins = knightModel.getCoins();

        dataToSave.deathCount = knightModel.deathCount;
        dataToSave.enemiesKilled = knightModel.enemiesKilled;
        dataToSave.playTime = knightModel.playTime;

        dataToSave.isFalseKnightDead = (falseKnight != null && falseKnight.isDead());
        dataToSave.isCrystalGuardianDead = (crystalGuardian != null && crystalGuardian.isDead());

        StringBuilder wallsStr = new StringBuilder();
        for (BreakableWall wall : breakableWalls) wallsStr.append(wall.isBroken ? "1" : "0").append(",");
        dataToSave.brokenWallsState = wallsStr.toString();

        StringBuilder enemiesStr = new StringBuilder();
        for (Enemy enemy : enemies) enemiesStr.append(enemy.isDead() ? "1" : "0").append(",");
        dataToSave.enemiesState = enemiesStr.toString();

        dataToSave.isMapEventTriggered = (mapEventAnim != null && mapEventAnim.isTriggered);

        StringBuilder achStr = new StringBuilder();
        Array<String> unlockedList = AchievementManager.getInstance().getUnlockedAchievements();
        for (String ach : unlockedList) achStr.append(ach).append(",");
        dataToSave.unlockedAchievements = achStr.toString();

        DatabaseManager.saveGame(dataToSave);
    }

    private void drawPauseButton(Rectangle bounds, float mx, float my) {
        if (bounds.contains(mx, my)) {
            shapeRenderer.setColor(0.3f, 0.3f, 0.4f, 0.9f);
        } else {
            shapeRenderer.setColor(0.15f, 0.15f, 0.2f, 0.9f);
        }
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    public void applyLiveMusicSettings(boolean isMuted, float volume) {
        if (currentMusic != null) {
            if (isMuted) {
                if (currentMusic.isPlaying()) currentMusic.pause();
                currentMusicVolume = 0f;
            } else {
                currentMusicVolume = volume;
                currentMusic.setVolume(volume);
                if (!currentMusic.isPlaying()) currentMusic.play();
            }
        }
    }

    private void respawnEnemy(Enemy enemy) {
        enemy.x = enemy.spawnX;
        enemy.y = enemy.spawnY;
        enemy.velocityX = 0;
        enemy.velocityY = 0;
        enemy.stateTime = 0f;
        enemy.invincibleTimer = 0f;
        enemy.knockbackTimer = 0f;
        enemy.isScoreCounted = false;

        if (enemy instanceof Tiktik) {
            enemy.hp = 2;
        } else if (enemy instanceof Mosquito) {
            enemy.hp = 2;
            ((Mosquito) enemy).currentState = Mosquito.State.IDLE;
        } else if (enemy instanceof HuskHornhead) {
            enemy.hp = 4;
            ((HuskHornhead) enemy).currentState = HuskHornhead.State.WALKING;
        }
    }
}
