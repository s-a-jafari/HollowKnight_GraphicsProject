package advanceProgramming.project.com.controller;

import advanceProgramming.project.com.helper.SettingsManager;
import advanceProgramming.project.com.helper.SolidBlock;
import advanceProgramming.project.com.model.BreakableWall;
import advanceProgramming.project.com.model.Knight;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class BreakableWallController {
    private final BreakableWall wall;
    private final Knight knight;
    private final Array<SolidBlock> mapBlocks;
    private final SolidBlock mySolidBlock;
    private final Sound breakSound = com.badlogic.gdx.Gdx.audio.newSound(com.badlogic.gdx.Gdx.files.internal("Audio/break.wav"));

    public BreakableWallController(BreakableWall wall, Knight knight, Array<SolidBlock> mapBlocks) {
        this.wall = wall;
        this.knight = knight;
        this.mapBlocks = mapBlocks;

        this.mySolidBlock = new SolidBlock(wall.x, wall.y, wall.width, wall.height, false);
        this.mapBlocks.add(mySolidBlock);
    }

    public void update(float delta) {
        if (wall.isBroken) return;

        if (wall.invincibleTimer > 0) {
            wall.invincibleTimer -= delta;
        }

        checkDamageFromKnight();
    }

    private void checkDamageFromKnight() {
        if (wall.isBroken || wall.invincibleTimer > 0) return;

        if (knight.isSlashActive) {
            float kHitBoxWidth = 60f;
            float kHitBoxX = knight.x + (knight.width / 2f) - (kHitBoxWidth / 2f);

            float slashWidth = 80f;
            float slashHeight = 110f;
            float slashX;
            float slashY = knight.y + 10f;

            if (!knight.isFacingRight) {
                slashX = kHitBoxX + kHitBoxWidth;
            } else {
                slashX = kHitBoxX - slashWidth;
            }

            Rectangle slashBox = new Rectangle(slashX, slashY, slashWidth, slashHeight);
            Rectangle wallBox = new Rectangle(wall.x, wall.y, wall.width, wall.height);

            if (wallBox.overlaps(slashBox)) {
                wall.hp--;
                wall.invincibleTimer = 0.4f;

                if (wall.hp <= 0) {
                    wall.isBroken = true;
                    if (!SettingsManager.getInstance().isSfxMuted()) {
                        breakSound.play(1.0f);
                    }
                    mapBlocks.removeValue(mySolidBlock, true);
                }
            }
        }
    }
}
