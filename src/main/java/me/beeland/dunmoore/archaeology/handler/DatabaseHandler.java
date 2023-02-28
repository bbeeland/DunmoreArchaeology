package me.beeland.dunmoore.archaeology.handler;

import com.zaxxer.hikari.HikariDataSource;
import me.beeland.dunmoore.archaeology.Archaeology;
import me.beeland.dunmoore.archaeology.PluginConfiguration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseHandler {

    private HikariDataSource dataSource;
    private Connection connection;

    public DatabaseHandler(Archaeology plugin) {

        PluginConfiguration config = plugin.getPluginConfig();

        String host = config.getString("Options.MySQL.Host");
        int port = config.getInteger("Options.MySQL.Port");
        String database = config.getString("Options.MySQL.Database");
        String username = config.getString("Options.MySQL.Username");
        String password = config.getString("Options.MySQL.Password");

        this.dataSource = new HikariDataSource();
        this.dataSource.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
        this.dataSource.setUsername(username);
        this.dataSource.setPassword(password);

        this.dataSource.setMaximumPoolSize(10);

        openConnection();
    }

    public Connection getConnection() {
        return connection;
    }

    public void openConnection() {
        try {
            this.connection = dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {

        try {

            if(connection.isClosed()) return;
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public HikariDataSource getDataSource() {
        return dataSource;
    }

    public PreparedStatement prepareStatement(String statement) {


        try {

            if(connection.isClosed()) openConnection();
            return connection.prepareStatement(statement);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
