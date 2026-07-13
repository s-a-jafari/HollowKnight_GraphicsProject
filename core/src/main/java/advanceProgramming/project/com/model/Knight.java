package advanceProgramming.project.com.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Array;

public class Knight {

    public final int soulToHeal = 33;
    // region hud variable
    private final int maxHp = 5;
    private final int maxSoul = 99;
    public boolean hasPogoed = false;
    public float visualSoul = 33;
    public int soulPerHit = 11;
    public float focusDuration = 1.5f;
    public float focusTimer = 0f;
    public boolean canDoubleJump = false;
    public boolean isTouchingWall = false;
    // endregion
    public float wallSlideSpeed = -150f;
    // Variables for DASH :
    public float dashSpeed = 1200f;
    public float dashDuration = 0.2f;
    public float dashTimer = 0f;
    public float dashCooldown = 0.6f;
    public float dashCooldownTimer = 0f;
    // Variables for Slash :
    public boolean isSlashActive = false;
    public float slashStateTime = 0f;
    public float slashOffsetX = 50f;
    public float slashOffsetY = 0f;
    public boolean hasVoidHeartUnlocked = false;
    public float startRunDuration = 0.3f;
    public float x, y, spawnX, spawnY;
    public float width = 349f;
    public float height = 186f;
    public float velocityX = 0;
    public float velocityY = 0;
    public float speed = 350f;
    public float jumpVelocity = 750f;
    public float gravity = -1500f;
    public boolean isGrounded = true;
    public float attackDuration = 0.25f;
    public float attackTimer = 0f;
    public State currentState;
    public float stateTime;
    public boolean isFacingRight;
    public float invincibleTimer = 0f;
    public float knockbackTimer = 0f;
    public int deathCount = 0;
    public int enemiesKilled = 0;
    public float playTime = 0f;
    public boolean isNoclip = false;
    public boolean isGodMode = false;
    public int maxNotches = 3;
    public int usedNotches = 0;
    public Array<CharmType> ownedCharms = new Array<>();
    public Array<CharmType> equippedCharms = new Array<>();
    public float nailDamage = 1;
    public float spellDamage = 2;
    public float knockbackForce = 25f;
    public boolean hasSharpShadow = false;
    public boolean hasVoidHeart = false;
    public float castTimer = 0f;
    public float castDuration = 0.4f;
    public Array<VengefulSpirit> vengefulSpirits = new Array<>();
    public Array<HowlingWraiths> howlingWraiths = new Array<>();
    private int currentHp = 5;
    private final int coins = 154;
    private int currentSoul = 33;
    private final Sound gainSoulSound;
    private Sound slashSound;
    private final Sound damageSound;
    private final Sound focusSound;

    public Knight(float startX, float startY) {
        this.x = startX;
        this.y = startY;
        this.spawnX = startX;
        this.spawnY = startY;
        this.currentState = State.IDLE;
        this.stateTime = 0f;
        this.isFacingRight = false;


        damageSound = Gdx.audio.newSound(Gdx.files.internal("Audio/damage.ogg"));
        focusSound = Gdx.audio.newSound(Gdx.files.internal("Audio/focus.ogg"));
        gainSoulSound = Gdx.audio.newSound(Gdx.files.internal("Audio/gainSoul.ogg"));
    }

    public void updateCharmEffects() {
        this.soulPerHit = 11;
        this.dashCooldown = 0.6f;
        this.nailDamage = 1;
        this.attackDuration = 0.25f;
        this.focusDuration = 1.5f;
        this.knockbackForce = 25f;
        this.dashDuration = 0.2f;
        this.hasSharpShadow = false;
        this.hasVoidHeart = false;
        this.spellDamage = 2;

        if (equippedCharms.contains(CharmType.SOUL_CATCHER, true)) this.soulPerHit = 15;
        if (equippedCharms.contains(CharmType.DASHMASTER, true)) this.dashCooldown = 0.3f;
        if (equippedCharms.contains(CharmType.UNBREAKABLE_STRENGTH, true)) this.nailDamage = 2;
        if (equippedCharms.contains(CharmType.QUICK_SLASH, true)) this.attackDuration = 0.15f;
        if (equippedCharms.contains(CharmType.QUICK_FOCUS, true)) this.focusDuration = 0.75f;
        if (equippedCharms.contains(CharmType.HEAVY_BLOW, true)) this.knockbackForce = 75f;
        if (equippedCharms.contains(CharmType.SHARP_SHADOW, true)) {
            this.hasSharpShadow = true;
            this.dashDuration = 0.24f;
        }
        if (equippedCharms.contains(CharmType.VOID_HEART, true)) {
            this.hasVoidHeart = true;
            this.spellDamage = 3;
        }
    }

    public void takeDamage(int amount) {
        if (currentHp > 0 && invincibleTimer <= 0) {
            if (!advanceProgramming.project.com.helper.SettingsManager.getInstance().isSfxMuted()) {
                damageSound.play(0.7f);
            }
            invincibleTimer = 1.0f;
            advanceProgramming.project.com.helper.CameraShake.shake(15f, 0.3f);
            if (!isGodMode) {
                currentHp = Math.max(0, currentHp - amount);
            }
        }
    }

    public void heal(int amount) {
        currentHp = Math.min(maxHp, currentHp + amount);
    }

    public void gainSoul() {
        if (!advanceProgramming.project.com.helper.SettingsManager.getInstance().isSfxMuted()) {
            gainSoulSound.play(1.0f);
        }
        currentSoul = Math.min(maxSoul, currentSoul + soulPerHit);
    }

    public int getCurrentHp() {
        return currentHp;
    }

    public void setCurrentHp(int currentHp) {
        this.currentHp = currentHp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public int getCurrentSoul() {
        return currentSoul;
    }

    public void setCurrentSoul(int currentSoul) {
        this.currentSoul = currentSoul;
    }

    public int getMaxSoul() {
        return maxSoul;
    }

    public int getCoins() {
        return coins;
    }

    public void triggerPogoBounce() {
        if (!hasPogoed) {
            this.velocityY = this.jumpVelocity;
            this.canDoubleJump = true;
            this.dashCooldownTimer = 0f;
            this.hasPogoed = true;
        }
    }

    public void playFocusSound() {
        if (!advanceProgramming.project.com.helper.SettingsManager.getInstance().isSfxMuted()) {
            focusSound.loop(1.0f);
        }
    }

    public void stopFocusSound() {
        focusSound.stop();
    }

    public enum State {
        IDLE, START_RUNNING, RUNNING, JUMPING, FALLING,
        ATTACKING, DASHING, DOUBLE_JUMPING, WALL_SLIDE,
        DOWN_ATTACKING, FOCUSING, TALKING, CASTING_VENGEFUL,
        CASTING_HOWLING, UP_ATTACKING
    }

    public enum CharmType {
        SOUL_CATCHER(1), DASHMASTER(1), UNBREAKABLE_STRENGTH(1), QUICK_SLASH(1),
        QUICK_FOCUS(1), HEAVY_BLOW(1), SHARP_SHADOW(1), VOID_HEART(1);

        public final int notchCost;

        CharmType(int cost) {
            this.notchCost = cost;
        }
    }

    public static class VengefulSpirit {
        public float x, y, width = 180f, height = 120f;
        public float velocityX;
        public boolean active = true;
        public float stateTime = 0f;
        public Array<Object> damagedEnemies = new Array<>();

        public VengefulSpirit(float x, float y, float velocityX) {
            this.x = x;
            this.y = y;
            this.velocityX = velocityX;
        }
    }

    public static class HowlingWraiths {
        public float x, y, width = 300f, height = 300f;
        public boolean active = true;
        public float stateTime = 0f;
        public float maxDuration = 0.6f;
        public int currentTick = 0;
        public Array<Object> hitInCurrentTick = new Array<>();

        public HowlingWraiths(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }
}
