package advanceProgramming.project.com.helper;

import advanceProgramming.project.com.model.GameData;
import advanceProgramming.project.com.model.SettingsData;

import java.sql.*;

public class DatabaseManager {
    private static final String URL = "jdbc:sqlite:savedata.db";

    public static void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {

            String sqlSaves = "CREATE TABLE IF NOT EXISTS saves ("
                + "slotNumber INTEGER PRIMARY KEY,"
                + "isSaved BOOLEAN,"
                + "knightX REAL,"
                + "knightY REAL,"
                + "currentLevel INTEGER,"
                + "maxHp INTEGER,"
                + "currentHp INTEGER,"
                + "currentSoul INTEGER,"
                + "coins INTEGER,"
                + "deathCount INTEGER,"
                + "enemiesKilled INTEGER,"
                + "playTime REAL,"
                + "isFalseKnightDead BOOLEAN,"
                + "isCrystalGuardianDead BOOLEAN,"
                + "brokenWallsState TEXT,"
                + "enemiesState TEXT,"
                + "isMapEventTriggered BOOLEAN,"
                + "unlockedAchievements TEXT"
                + ");";
            stmt.execute(sqlSaves);

            String sqlSettings = "CREATE TABLE IF NOT EXISTS settings ("
                + "id INTEGER PRIMARY KEY,"
                + "language TEXT,"
                + "musicVol REAL,"
                + "muteMusic BOOLEAN,"
                + "muteSfx BOOLEAN,"
                + "brightness REAL,"
                + "keyLeft INTEGER,"
                + "keyRight INTEGER,"
                + "keyJump INTEGER,"
                + "keyAttack INTEGER,"
                + "keyDash INTEGER,"
                + "keySpell INTEGER,"
                + "keyHowling INTEGER,"
                + "keyFocus INTEGER,"
                + "keyInventory INTEGER"
                + ");";
            stmt.execute(sqlSettings);

        } catch (SQLException e) {
            System.out.println("Error initializing DB: " + e.getMessage());
        }
    }

    public static void saveGame(GameData data) {
        String sql = "INSERT OR REPLACE INTO saves (slotNumber, isSaved, knightX, knightY, currentLevel, maxHp, currentHp, currentSoul, coins, deathCount, enemiesKilled, playTime, isFalseKnightDead, isCrystalGuardianDead, brokenWallsState, enemiesState, isMapEventTriggered, unlockedAchievements) "
            + "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, data.slotNumber);
            pstmt.setBoolean(2, data.isSaved);
            pstmt.setFloat(3, data.knightX);
            pstmt.setFloat(4, data.knightY);
            pstmt.setInt(5, data.currentLevel);
            pstmt.setInt(6, data.maxHp);
            pstmt.setInt(7, data.currentHp);
            pstmt.setInt(8, data.currentSoul);
            pstmt.setInt(9, data.coins);
            pstmt.setInt(10, data.deathCount);
            pstmt.setInt(11, data.enemiesKilled);
            pstmt.setFloat(12, data.playTime);
            pstmt.setBoolean(13, data.isFalseKnightDead);
            pstmt.setBoolean(14, data.isCrystalGuardianDead);
            pstmt.setString(15, data.brokenWallsState);
            pstmt.setString(16, data.enemiesState);
            pstmt.setBoolean(17, data.isMapEventTriggered);
            pstmt.setString(18, data.unlockedAchievements);
            pstmt.executeUpdate();
        } catch (SQLException e) {
        }
    }

    public static GameData loadGame(int slotNumber) {
        String sql = "SELECT * FROM saves WHERE slotNumber = ?";
        GameData data = new GameData();
        data.slotNumber = slotNumber;
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, slotNumber);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                data.isSaved = rs.getBoolean("isSaved");
                data.knightX = rs.getFloat("knightX");
                data.knightY = rs.getFloat("knightY");
                data.currentLevel = rs.getInt("currentLevel");
                data.maxHp = rs.getInt("maxHp");
                data.currentHp = rs.getInt("currentHp");
                data.currentSoul = rs.getInt("currentSoul");
                data.coins = rs.getInt("coins");
                data.deathCount = rs.getInt("deathCount");
                data.enemiesKilled = rs.getInt("enemiesKilled");
                data.playTime = rs.getFloat("playTime");
                data.isFalseKnightDead = rs.getBoolean("isFalseKnightDead");
                data.isCrystalGuardianDead = rs.getBoolean("isCrystalGuardianDead");
                data.brokenWallsState = rs.getString("brokenWallsState");
                data.enemiesState = rs.getString("enemiesState");
                data.isMapEventTriggered = rs.getBoolean("isMapEventTriggered");
                data.unlockedAchievements = rs.getString("unlockedAchievements");
            }
        } catch (SQLException e) {
        }
        return data;
    }

    public static void saveSettings(SettingsData data) {
        String sql = "INSERT OR REPLACE INTO settings (id, language, musicVol, muteMusic, muteSfx," +
            " brightness, keyLeft, keyRight, keyJump, keyAttack, keyDash, keySpell, keyHowling, keyFocus, keyInventory) "
            + "VALUES(1,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, data.language);
            pstmt.setFloat(2, data.musicVol);
            pstmt.setBoolean(3, data.muteMusic);
            pstmt.setBoolean(4, data.muteSfx);
            pstmt.setFloat(5, data.brightness);
            pstmt.setInt(6, data.keyLeft);
            pstmt.setInt(7, data.keyRight);
            pstmt.setInt(8, data.keyJump);
            pstmt.setInt(9, data.keyAttack);
            pstmt.setInt(10, data.keyDash);
            pstmt.setInt(11, data.keySpell);
            pstmt.setInt(12, data.keyHowling);
            pstmt.setInt(13, data.keyFocus);
            pstmt.setInt(14, data.keyInventory);
            pstmt.executeUpdate();
        } catch (SQLException e) {
        }
    }

    public static SettingsData loadSettings() {
        String sql = "SELECT * FROM settings WHERE id = 1";
        SettingsData data = new SettingsData();
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                data.language = rs.getString("language");
                data.musicVol = rs.getFloat("musicVol");
                data.muteMusic = rs.getBoolean("muteMusic");
                data.muteSfx = rs.getBoolean("muteSfx");
                data.brightness = rs.getFloat("brightness");
                data.keyLeft = rs.getInt("keyLeft");
                data.keyRight = rs.getInt("keyRight");
                data.keyJump = rs.getInt("keyJump");
                data.keyAttack = rs.getInt("keyAttack");
                data.keyDash = rs.getInt("keyDash");
                data.keySpell = rs.getInt("keySpell");
                data.keyHowling = rs.getInt("keyHowling");
                data.keyFocus = rs.getInt("keyFocus");
                data.keyInventory = rs.getInt("keyInventory");
            } else {
                saveSettings(data);
            }
        } catch (SQLException e) {
        }
        return data;
    }
}
