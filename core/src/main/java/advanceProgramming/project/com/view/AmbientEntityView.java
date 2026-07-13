package advanceProgramming.project.com.view;

import advanceProgramming.project.com.model.AmbientEntity;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class AmbientEntityView {
    private final Animation<TextureRegion> floatAnimation;

    public AmbientEntityView(String texturePrefix, int frameCount) {
        Array<TextureRegion> frames = new Array<>();
        for (int i = 1; i <= frameCount; i++) {
            frames.add(new TextureRegion(new Texture("Animation/" + texturePrefix + i + ".png")));
        }
        floatAnimation = new Animation<>(0.15f, frames, Animation.PlayMode.LOOP);
    }

    public void render(SpriteBatch batch, AmbientEntity entity) {
        TextureRegion currentFrame = floatAnimation.getKeyFrame(entity.stateTime);
        if (entity.velocityX < 0 && !currentFrame.isFlipX()) {
            currentFrame.flip(true, false);
        } else if (entity.velocityX > 0 && currentFrame.isFlipX()) {
            currentFrame.flip(true, false);
        }
        batch.draw(currentFrame, entity.x, entity.y, entity.width, entity.height);
    }

    public void dispose() {
        if (floatAnimation != null) {
            Object[] frames = floatAnimation.getKeyFrames();

            for (Object obj : frames) {
                TextureRegion frame = (TextureRegion) obj;
                if (frame.getTexture() != null) {
                    frame.getTexture().dispose();
                }
            }
        }
    }
}
