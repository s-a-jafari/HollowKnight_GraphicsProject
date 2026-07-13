package advanceProgramming.project.com.helper;

import com.badlogic.gdx.utils.Array;

public class AchievementManager {
    public static final String ACH_COMPLETION = "completion";
    public static final String ACH_SPEEDRUN = "speedrun";
    public static final String ACH_TRUE_HUNTER = "true_hunter";
    public static final String ACH_FALSE_KNIGHT = "false_knight";
    public static final String ACH_STEEL_SOUL = "steel_soul";
    private static AchievementManager instance;
    private final Array<AchievementObserver> observers;
    private final Array<String> unlockedAchievements;

    private AchievementManager() {
        observers = new Array<>();
        unlockedAchievements = new Array<>();
    }

    public static AchievementManager getInstance() {
        if (instance == null) {
            instance = new AchievementManager();
        }
        return instance;
    }

    public Array<String> getUnlockedAchievements() {
        return unlockedAchievements;
    }

    public void addObserver(AchievementObserver observer) {
        if (!observers.contains(observer, true)) observers.add(observer);
    }

    public void removeObserver(AchievementObserver observer) {
        observers.removeValue(observer, true);
    }

    public void unlockAchievement(String id, String title) {
        if (!unlockedAchievements.contains(id, false)) {
            unlockedAchievements.add(id);

            for (AchievementObserver observer : observers) {
                observer.onAchievementUnlocked(id, title);
            }
        }
    }

    public boolean isUnlocked(String id) {
        return unlockedAchievements.contains(id, false);
    }
}
