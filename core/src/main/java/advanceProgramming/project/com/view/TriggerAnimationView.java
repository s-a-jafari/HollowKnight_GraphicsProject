package advanceProgramming.project.com.view;

import advanceProgramming.project.com.model.TriggerAnimation;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class TriggerAnimationView {
    private final Animation<TextureRegion> eventAnimation;

    public TriggerAnimationView() {
        Array<TextureRegion> frames = new Array<>();
        for (int i = 0; i <= 6; i++) {
            frames.add(new TextureRegion(new Texture("City Of Tears/GG_grimm_bow0" + String.format("%03d", i) + ".png")));
        }

        eventAnimation = new Animation<>(0.1f, frames, Animation.PlayMode.NORMAL);
    }

    public void render(SpriteBatch batch, TriggerAnimation anim) {
        if (eventAnimation != null) {

            TextureRegion currentFrame = eventAnimation.getKeyFrame(anim.stateTime, false);

            float yOffset = 0f;

            batch.draw(currentFrame, anim.x, anim.y + yOffset, anim.width, anim.height);
        }
    }
}
