package advanceProgramming.project.com.model;

import com.badlogic.gdx.Input;

public class SettingsData {
    public int id = 1;

    public String language = "en";
    public float musicVol = 0.5f;
    public boolean muteMusic = false;
    public boolean muteSfx = false;
    public float brightness = 1.0f;

    public int keyLeft = Input.Keys.LEFT;
    public int keyRight = Input.Keys.RIGHT;
    public int keyJump = Input.Keys.Z;
    public int keyAttack = Input.Keys.X;
    public int keyDash = Input.Keys.C;
    public int keySpell = Input.Keys.S;
    public int keyHowling = Input.Keys.D;
    public int keyFocus = Input.Keys.A;
    public int keyInventory = Input.Keys.I;

}
