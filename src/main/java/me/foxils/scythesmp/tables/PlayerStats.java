package me.foxils.scythesmp.tables;

import com.thoughtworks.xstream.XStream;
import me.foxils.scythesmp.ScytheSMP;
import me.foxils.scythesmp.utilities.MapEntryConverter;
import me.foxils.scythesmp.utilities.RandomGemStuff;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.util.*;

public final class PlayerStats {

    private final UUID playerUUID;

    private String currentGem;
    private HashMap<String, Integer> gemLevelMap;

    private static final String tableName = "player_stats";
    private static final String identifierName = "playerUUID";

    private final XStream magicApi = new XStream();

    public PlayerStats(UUID uuid) {
        this.playerUUID = uuid;
        this.currentGem = RandomGemStuff.getRandomRawGemName();
        this.gemLevelMap = new HashMap<>();

        gemLevelMap.put(this.currentGem, 0);

        magicApi.registerConverter(new MapEntryConverter());
        magicApi.alias("root", Map.class);
    }

    @Nullable
    public static PlayerStats getDataObjectFromUUID(UUID uuid) {
        final String UUIDString = uuid.toString();

        // Would definitely like to switch this to dependency injection, but am too lazy to restructure the database
        // I will write a module inside foxutils to do this and then implement that and remove this.
        Connection connection = ScytheSMP.getDatabase().createConnection();

        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + tableName + " WHERE " + identifierName + " = ?")) {
            statement.setString(1, UUIDString);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet == null || !resultSet.next()) {
                return null;
            }

            PlayerStats playerStats = new PlayerStats(uuid);

            playerStats.setCurrentGem(resultSet.getString(2));
            playerStats.setGemLevelMapFromXML(resultSet.getString(3));

            connection.close();

            return playerStats;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void createColumn(PlayerStats playerStats) {
        Connection connection = ScytheSMP.getDatabase().createConnection();

        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO player_stats(playerUUID, playerGem, gemLevelMap) VALUES (?, ?, ?)")) {
            statement.setString(1, playerStats.getPlayerUUID());
            statement.setString(2, playerStats.getCurrentGem());
            statement.setString(3, playerStats.getGemLevelMapAsXML());

            statement.executeUpdate();

            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateColumn() {
        Connection connection = ScytheSMP.getDatabase().createConnection();

        try (PreparedStatement statement = connection.prepareStatement("UPDATE player_stats SET playerGem = ?, gemLevelMap = ? WHERE playerUUID = ?")) {
            statement.setString(1, getCurrentGem());
            statement.setString(2, getGemLevelMapAsXML());
            statement.setString(3, getPlayerUUID());

            statement.executeUpdate();

            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteColumn() {
        Connection connection = ScytheSMP.getDatabase().createConnection();

        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM player_stats WHERE playerUUID = ?")) {
            statement.setString(1, getPlayerUUID());

            statement.executeUpdate();

            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getCurrentGem() {
        return currentGem;
    }

    public void setCurrentGem(String gemName) {
        this.currentGem = gemName;
    }

    public String getPlayerUUID() {
        return playerUUID.toString();
    }

    public HashMap<String, Integer> getGemLevelMap() {
        return gemLevelMap;
    }

    public String getGemLevelMapAsXML() {
        return magicApi.toXML(gemLevelMap);
    }

    public void setGemLevelMap(HashMap<String, Integer> gemLevelMap) {
        this.gemLevelMap = gemLevelMap;
    }

    public void setGemLevelMapFromXML(String xml) {
        HashMap<String, Integer> basedMap = new HashMap<>();

        // *dies of death*
        // this is most definitely a byproduct of my terrible database code
        @SuppressWarnings("unchecked")
        Map<String, String> weirdMap = (Map<String, String>) magicApi.fromXML(xml);

        weirdMap.forEach((string, stringInteger) -> basedMap.put(string, Integer.valueOf(stringInteger)));

        this.gemLevelMap = basedMap;
    }
}
