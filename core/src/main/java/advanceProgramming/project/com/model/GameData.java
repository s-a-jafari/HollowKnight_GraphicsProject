package advanceProgramming.project.com.model;

public class GameData {
    public int slotNumber;
    public boolean isSaved;

    public float knightX;
    public float knightY;
    public int currentLevel;

    public int maxHp;
    public int currentHp;
    public int currentSoul;
    public int coins;

    public int deathCount;
    public int enemiesKilled;
    public float playTime;

    public boolean isFalseKnightDead;
    public boolean isCrystalGuardianDead;
    public String brokenWallsState;
    public String enemiesState;
    public boolean isMapEventTriggered;

    public String unlockedAchievements;

    public GameData() {
        this.isSaved = false;
    }
}
