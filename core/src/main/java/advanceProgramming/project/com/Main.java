package advanceProgramming.project.com;

import advanceProgramming.project.com.helper.DatabaseManager;
import advanceProgramming.project.com.helper.SettingsManager;
import advanceProgramming.project.com.view.MainMenuScreen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

public class Main extends Game {
    public static final float VIRTUAL_WIDTH = 1920f;
    public static final float VIRTUAL_HEIGHT = 1080f;
    public Music menuMusic;

    @Override
    public void create() {
        DatabaseManager.initializeDatabase();

        menuMusic = Gdx.audio.newMusic(Gdx.files.internal("MenuAsset/bgMusic_mainMenu.mp3"));
        menuMusic.setLooping(true);

        this.setScreen(new MainMenuScreen(this));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    public void updateMenuMusicState() {
        if (menuMusic != null) {
            SettingsManager settings = SettingsManager.getInstance();
            if (settings.isMusicMuted()) {
                if (menuMusic.isPlaying()) menuMusic.pause();
            } else {
                menuMusic.setVolume(settings.getMusicVolume());
                if (!menuMusic.isPlaying()) menuMusic.play();
            }
        }
    }
}
