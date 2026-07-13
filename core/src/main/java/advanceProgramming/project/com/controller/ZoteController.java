package advanceProgramming.project.com.controller;

import advanceProgramming.project.com.helper.SettingsManager;
import advanceProgramming.project.com.helper.SolidBlock;
import advanceProgramming.project.com.model.Knight;
import advanceProgramming.project.com.model.Zote;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.I18NBundle;

public class ZoteController {
    public boolean isPlayerInRange = false;
    private final Zote zote;
    private final Knight knight;
    private final Array<SolidBlock> mapBlocks;
    private final Sound[] zoteVoices;

    public ZoteController(Zote zote, Knight knight, Array<SolidBlock> mapBlocks) {
        this.zote = zote;
        this.knight = knight;
        this.mapBlocks = mapBlocks;

        // region load sounds
        zoteVoices = new Sound[6];
        zoteVoices[0] = Gdx.audio.newSound(Gdx.files.internal("Audio/Zote_01.wav"));
        zoteVoices[1] = Gdx.audio.newSound(Gdx.files.internal("Audio/Zote_02.wav"));
        zoteVoices[2] = Gdx.audio.newSound(Gdx.files.internal("Audio/Zote_03.wav"));
        zoteVoices[3] = Gdx.audio.newSound(Gdx.files.internal("Audio/Zote_04.wav"));
        zoteVoices[4] = Gdx.audio.newSound(Gdx.files.internal("Audio/Zote_05.wav"));
        zoteVoices[5] = Gdx.audio.newSound(Gdx.files.internal("Audio/Zote_06.wav"));
        // endregion
    }

    public void update(float delta) {
        zote.stateTime += delta;
        if (zote.invincibleTimer > 0) zote.invincibleTimer -= delta;

        zote.velocityY += zote.gravity * delta;
        zote.y += zote.velocityY * delta;

        Rectangle zoteBox = new Rectangle(zote.x, zote.y, zote.width, zote.height);
        for (SolidBlock block : mapBlocks) {
            Rectangle blockBox = new Rectangle(block.x, block.y, block.width, block.height);
            if (zoteBox.overlaps(blockBox) && zote.velocityY < 0) {
                zote.y = block.y + block.height;
                zote.velocityY = 0;
            }
        }

        float dist = Vector2.dst(zote.x, zote.y, knight.x, knight.y);
        isPlayerInRange = (dist < 200f);

        checkDamageFromKnight();

        switch (zote.currentState) {
            case IDLE:
                zote.velocityX = 0;
                if (isPlayerInRange && knight.currentState != Knight.State.TALKING) {
                    if (Gdx.input.isKeyJustPressed(Input.Keys.E) || Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
                        startDialogue();
                    }
                }
                break;

            case TALKING:
                zote.velocityX = 0;
                updateTypingEffect(delta);
                if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                    advanceDialogue();
                }
                break;

            case ANGRY:
                zote.angerTimer -= delta;
                if (zote.angerTimer <= 0) {
                    zote.currentState = Zote.State.IDLE;
                } else {
                    zote.isFacingRight = knight.x > zote.x;
                    zote.velocityX = zote.isFacingRight ? zote.speed : -zote.speed;
                    zote.x += zote.velocityX * delta;
                }
                break;
        }
    }

    private void checkDamageFromKnight() {
        if (knight.isSlashActive && zote.invincibleTimer <= 0 && zote.currentState != Zote.State.TALKING) {
            float kHitBoxWidth = 60f;
            float kHitBoxX = knight.x + (knight.width / 2f) - (kHitBoxWidth / 2f);
            float slashWidth = 80f;
            float slashHeight = 110f;
            float slashX;
            float slashY = knight.y + 10f;

            if (knight.currentState == Knight.State.DOWN_ATTACKING) {
                slashWidth = 80f;
                slashHeight = 60f;
                slashX = kHitBoxX + (kHitBoxWidth / 2f) - (slashWidth / 2f);
                slashY = knight.y - 30f;
            } else {
                slashX = !knight.isFacingRight ? kHitBoxX + kHitBoxWidth : kHitBoxX - slashWidth;
            }

            Rectangle slashBox = new Rectangle(slashX, slashY, slashWidth, slashHeight);
            Rectangle zBox = new Rectangle(zote.x, zote.y, zote.width, zote.height);

            if (zBox.overlaps(slashBox)) {
                zote.currentState = Zote.State.ANGRY;
                zote.angerTimer = 2.5f;
                zote.invincibleTimer = 0.5f;
            }
        }
    }

    private void startDialogue() {
        knight.currentState = Knight.State.TALKING;
        knight.velocityX = 0;
        zote.currentState = Zote.State.TALKING;

        I18NBundle bundle = SettingsManager.getInstance().getBundle();

        if (!zote.hasFinishedMainDialogues) {
            zote.currentDialogueIndex = 0;
            String key = zote.mainDialogues[zote.currentDialogueIndex];
            zote.currentTargetText = bundle.get(key);
        } else {
            zote.currentDialogueIndex = MathUtils.random(0, zote.precepts.size - 1);
            String key = zote.precepts.get(zote.currentDialogueIndex);
            zote.currentTargetText = bundle.get(key);
        }
        resetTyping();
        playRandomVoice();
    }

    private void advanceDialogue() {
        if (zote.visibleChars < zote.currentTargetText.length()) {
            zote.visibleChars = zote.currentTargetText.length();
            zote.currentDisplayedText = zote.currentTargetText;
            return;
        }

        if (!zote.hasFinishedMainDialogues) {
            zote.currentDialogueIndex++;
            if (zote.currentDialogueIndex < zote.mainDialogues.length) {
                zote.currentTargetText = zote.mainDialogues[zote.currentDialogueIndex];
                resetTyping();
                playRandomVoice();
            } else {
                endDialogue();
            }
        } else {
            endDialogue();
        }
    }

    private void endDialogue() {
        zote.currentState = Zote.State.IDLE;
        knight.currentState = Knight.State.IDLE;
        zote.hasFinishedMainDialogues = true;
    }

    private void updateTypingEffect(float delta) {
        if (zote.visibleChars < zote.currentTargetText.length()) {
            zote.textTimer += delta;
            float timePerChar = 1f / zote.charsPerSecond;

            if (zote.textTimer >= timePerChar) {
                zote.visibleChars++;
                zote.currentDisplayedText = zote.currentTargetText.substring(0, zote.visibleChars);
                zote.textTimer = 0f;
            }
        }
    }

    private void playRandomVoice() {
        if (zoteVoices != null && zoteVoices.length > 0) {
            int randomIndex = MathUtils.random(0, zoteVoices.length - 1);
            zoteVoices[randomIndex].play(0.8f);
        }
    }

    private void resetTyping() {
        zote.visibleChars = 0;
        zote.textTimer = 0f;
        zote.currentDisplayedText = "";
    }
}
