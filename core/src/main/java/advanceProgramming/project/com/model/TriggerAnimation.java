package advanceProgramming.project.com.model;

import com.badlogic.gdx.math.Rectangle;

public class TriggerAnimation {
    public float x, y, width, height;
    public float stateTime = 0f;
    public boolean isTriggered = false;
    public Rectangle hitbox;

    public TriggerAnimation(Rectangle box) {
        this.x = box.x;
        this.y = box.y;
        this.width = box.width;
        this.height = box.height;
        this.hitbox = box;
    }
}
