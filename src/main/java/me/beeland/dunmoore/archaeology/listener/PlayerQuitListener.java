package me.beeland.dunmoore.archaeology.listener;

import me.beeland.dunmoore.archaeology.Archaeology;
import me.beeland.dunmoore.archaeology.handler.ArcheologistProfileHandler;
import me.beeland.dunmoore.archaeology.profile.ArcheologistProfile;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.SQLException;

public class PlayerQuitListener implements Listener {

    private Archaeology plugin;

    public PlayerQuitListener(Archaeology plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    void onPlayerQuit(PlayerQuitEvent e) {

        ArcheologistProfileHandler archeologistHandler = plugin.getArcheologistProfileHandler();
        ArcheologistProfile profile = archeologistHandler.getByUUID(e.getPlayer().getUniqueId());

        archeologistHandler.save(profile);
        archeologistHandler.getProfiles().remove(profile);

        try {

            if(Bukkit.getOnlinePlayers().size() == 0) {
                if(!plugin.getDatabaseHandler().getConnection().isClosed()) {
                    plugin.getDatabaseHandler().closeConnection();
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }
}
