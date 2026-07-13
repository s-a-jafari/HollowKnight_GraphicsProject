package advanceProgramming.project.com.view;

import advanceProgramming.project.com.model.BreakableWall;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class BreakableWallView {
    private final Texture wallFull;
    private final Texture wallCracked1;
    private final Texture wallCracked2;
    private final Texture wallCracked3;

    public BreakableWallView() {
        // loading the assets
        wallFull = new Texture("Forgotten Crossroads/breakablePath_000.png");
        wallCracked1 = new Texture("Forgotten Crossroads/breakablePath_001.png");
        wallCracked2 = new Texture("Forgotten Crossroads/breakablePath_002.png");
        wallCracked3 = new Texture("Forgotten Crossroads/breakablePath_003.png");
    }

    public void render(SpriteBatch batch, BreakableWall wall) {
        Texture currentTex = null;

        if (wall.hp == 3) {
            currentTex = wallFull;
        } else if (wall.hp == 2) {
            currentTex = wallCracked1;
        } else if (wall.hp == 1) {
            currentTex = wallCracked2;
        } else {
            currentTex = wallCracked3;
        }


        batch.draw(currentTex, wall.x, wall.y, wall.width, wall.height);
    }
}
