package advanceProgramming.project.com.model;

import com.badlogic.gdx.utils.Array;

public class Zote {
    public State currentState = State.IDLE;
    public float stateTime = 0f;
    public float x, y;
    public float width = 186f;
    public float height = 186f;
    public boolean isFacingRight = false;
    public float velocityX = 0;
    public float velocityY = 0;
    public float speed = 200f;
    public float gravity = -1500f;
    public float angerTimer = 0f;
    public float invincibleTimer = 0f;
    public String[] mainDialogues = {
        "zote_main_1",
        "zote_main_2",
        "zote_main_3"
    };
    public Array<String> precepts = new Array<>();
    public int currentDialogueIndex = 0;
    public boolean hasFinishedMainDialogues = false;
    public String currentTargetText = "";
    public String currentDisplayedText = "";
    public int visibleChars = 0;
    public float textTimer = 0f;
    public float charsPerSecond = 30f;
    public Zote(float startX, float startY) {
        this.x = startX;
        this.y = startY;

        precepts.add("zote_precept_1");
        precepts.add("zote_precept_2");
        precepts.add("zote_precept_3");
        precepts.add("zote_precept_4");
    }

    public enum State {IDLE, TALKING, ANGRY}
}
