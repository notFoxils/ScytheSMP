package me.foxils.sytheSMP.databases;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.logging.Logger;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class Database {
    private final Logger logger;
    private final String hostname;
    private final String databaseName;
    private final int port;
    private final String username;
    private final String password;
    private final String databaseURL;

    public Database(Plugin plugin) {
        this.logger = plugin.getLogger();
        FileConfiguration databaseConfig = plugin.getConfig();
        this.hostname = databaseConfig.getString("database.host");
        this.databaseName = databaseConfig.getString("database.databaseName");
        this.port = databaseConfig.getInt("database.port");
        this.username = databaseConfig.getString("database.username");
        this.password = databaseConfig.getString("database.password");
        this.databaseURL = "jdbc:mysql://" + this.hostname + ":" + this.port + "/" + this.databaseName;
    }

    public void initializeDatabase() {
        Connection connection = this.createConnection();

        try {
            Statement statement = connection.createStatement();

            try {
                String sql = "create table if not exists player_stats(playerUUID varchar(36) PRIMARY KEY, playerGem tinytext, gemLevelMap MEDIUMTEXT)";
                statement.execute(sql);
                connection.close();
            } catch (Throwable var6) {
                if (statement != null) {
                    try {
                        statement.close();
                    } catch (Throwable var5) {
                        var6.addSuppressed(var5);
                    }
                }

                throw var6;
            }

            statement.close();
        } catch (SQLException var7) {
            throw new RuntimeException(var7);
        }
    }

    public Connection createConnection() {
        try {
            Connection connection = DriverManager.getConnection(this.databaseURL, this.username, this.password);
            this.logger.info("Successfully created connection to " + this.databaseName + ".");
            return connection;
        } catch (SQLException var2) {
            this.logger.severe("Cannot connect to the SQL database, contact developer");
            Arrays.stream(var2.getStackTrace()).toList().forEach((line) -> this.logger.severe(line.toString()));
            return null;
        }
    }
}
