package advanceProgramming.project.com.helper;

import advanceProgramming.project.com.model.SettingsData;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.I18NBundle;

import java.util.Locale;

public class SettingsManager {
    private static SettingsManager instance;
    private final SettingsData data;
    private I18NBundle bundle;

    private SettingsManager() {
        this.data = DatabaseManager.loadSettings();
        loadBundle();
    }

    public static SettingsManager getInstance() {
        if (instance == null) instance = new SettingsManager();
        return instance;
    }

    private void save() {
        DatabaseManager.saveSettings(data);
    }

    public void loadBundle() {
        FileHandle baseFileHandle = Gdx.files.internal("MyBundle");
        Locale locale = data.language.equals("fr") ? Locale.FRENCH : Locale.ENGLISH;
        bundle = I18NBundle.createBundle(baseFileHandle, locale);
    }

    public I18NBundle getBundle() {
        return bundle;
    }

    public void toggleLanguage() {
        data.language = data.language.equals("en") ? "fr" : "en";
        save();
        loadBundle();
    }

    public float getMusicVolume() {
        return data.musicVol;
    }

    public void setMusicVolume(float v) {
        data.musicVol = v;
        save();
    }

    public boolean isMusicMuted() {
        return data.muteMusic;
    }

    public void setMusicMuted(boolean m) {
        data.muteMusic = m;
        save();
    }

    public boolean isSfxMuted() {
        return data.muteSfx;
    }

    public void setSfxMuted(boolean m) {
        data.muteSfx = m;
        save();
    }

    public void resetAudio() {
        setMusicVolume(0.5f);
        setMusicMuted(false);
        setSfxMuted(false);
    }

    public float getBrightness() {
        return data.brightness;
    }

    public void setBrightness(float b) {
        data.brightness = b;
        save();
    }

    public int getKey(String action, int defaultKey) {
        switch (action) {
            case "keyLeft":
                return data.keyLeft;
            case "keyRight":
                return data.keyRight;
            case "keyJump":
                return data.keyJump;
            case "keyAttack":
                return data.keyAttack;
            case "keyDash":
                return data.keyDash;
            case "keySpell":
                return data.keySpell;
            case "keyHowling":
                return data.keyHowling;
            case "keyFocus":
                return data.keyFocus;
            case "keyInventory":
                return data.keyInventory;
            default:
                return defaultKey;
        }
    }

    public void setKey(String action, int keycode) {
        switch (action) {
            case "keyLeft":
                data.keyLeft = keycode;
                break;
            case "keyRight":
                data.keyRight = keycode;
                break;
            case "keyJump":
                data.keyJump = keycode;
                break;
            case "keyAttack":
                data.keyAttack = keycode;
                break;
            case "keyDash":
                data.keyDash = keycode;
                break;
            case "keySpell":
                data.keySpell = keycode;
                break;
            case "keyHowling":
                data.keyHowling = keycode;
                break;
            case "keyFocus":
                data.keyFocus = keycode;
                break;
            case "keyInventory":
                data.keyInventory = keycode;
                break;
        }
        save();
    }

    public void resetControls() {
        data.keyLeft = Input.Keys.LEFT;
        data.keyRight = Input.Keys.RIGHT;
        data.keyJump = Input.Keys.Z;
        data.keyAttack = Input.Keys.X;
        data.keyDash = Input.Keys.C;
        data.keySpell = Input.Keys.S;
        data.keyHowling = Input.Keys.D;
        data.keyFocus = Input.Keys.A;
        data.keyInventory = Input.Keys.I;
        save();
    }
}
