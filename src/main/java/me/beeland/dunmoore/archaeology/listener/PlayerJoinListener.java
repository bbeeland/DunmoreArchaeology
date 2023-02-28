package me.beeland.dunmoore.archaeology.listener;

import me.beeland.dunmoore.archaeology.Archaeology;
import me.beeland.dunmoore.archaeology.handler.DatabaseHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.SQLException;

public class PlayerJoinListener implements Listener {

    private Archaeology plugin;
    private DatabaseHandler databaseHandler;

    public PlayerJoinListener(Archaeology plugin) {
        this.plugin = plugin;
        this.databaseHandler = plugin.getDatabaseHandler();
    }

    @EventHandler
    void onPlayerJoin(PlayerJoinEvent e) {

        try {
            if(databaseHandler.getConnection().isClosed()) {
                databaseHandler.openConnection();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        plugin.getArcheologistProfileHandler().load(e.getPlayer().getUniqueId());
    }

}
