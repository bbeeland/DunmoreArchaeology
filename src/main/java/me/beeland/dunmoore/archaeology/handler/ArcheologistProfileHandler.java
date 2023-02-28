package me.beeland.dunmoore.archaeology.handler;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.beeland.dunmoore.archaeology.Archaeology;
import me.beeland.dunmoore.archaeology.profile.ArcheologistProfile;
import org.bukkit.Bukkit;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ArcheologistProfileHandler {

    private Archaeology plugin;
    private DatabaseHandler databaseHandler;
    private Set<ArcheologistProfile> profiles;

    public ArcheologistProfileHandler(Archaeology plugin) {
        this.plugin = plugin;
        this.databaseHandler = plugin.getDatabaseHandler();
        this.profiles = Sets.newHashSet();
    }

    public Set<ArcheologistProfile> getProfiles() {
        return profiles;
    }

    public void load() {

        Bukkit.getOnlinePlayers().forEach(player -> load(player.getUniqueId()));
    }

    public String serializeArtifacts(ArcheologistProfile profile) {

        StringBuilder builder = new StringBuilder();
        Map<Integer, Integer> artifacts = profile.getSavedArtifacts();
        artifacts.forEach((id, amount) -> builder.append(id + ";" + amount + "+"));

        return builder.toString();
    }

    public Map<Integer, Integer> deserializeArtifacts(String serializedArtifacts) {

        plugin.getLogger().warning(serializedArtifacts);

        HashMap<Integer, Integer> artifacts = Maps.newHashMap();
        String[] splitArtifacts = serializedArtifacts.split("\\+");

        for(String string : splitArtifacts) {


            int artifactId = Integer.parseInt(string.split(";")[0]);
            int amount = Integer.parseInt(string.split(";")[1]);

            artifacts.put(artifactId, amount);
        }

        return artifacts;
    }

    public void load(UUID uuid) {

        PreparedStatement statement = databaseHandler.prepareStatement("SELECT artifacts FROM artifact_playerdata WHERE uuid = ?;");

        try {

            statement.setString(1, uuid.toString());
            ResultSet set = statement.executeQuery();


            if(set.next()) {
                profiles.add(new ArcheologistProfile(plugin, uuid, deserializeArtifacts(set.getString("artifacts"))));
            } else {
                profiles.add(new ArcheologistProfile(plugin, uuid, Maps.newHashMap()));
            }


            set.close();
            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void save() {

        PreparedStatement insert = databaseHandler.prepareStatement("INSERT INTO artifact_playerdata VALUES(?, ?) ON DUPLICATE KEY UPDATE artifacts=?;");

        try {

            for(ArcheologistProfile profile : profiles) {

                if(profile.getSavedArtifacts().size() == 0) return;

                insert.setString(1, profile.getOwner().toString());
                insert.setString(2, serializeArtifacts(profile));
                insert.setString(3, serializeArtifacts(profile));

                insert.execute();
                insert.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void save(ArcheologistProfile profile) {

        if(profile.getSavedArtifacts().size() == 0) return;

        PreparedStatement find = databaseHandler.prepareStatement("SELECT artifacts FROM artifact_playerdata WHERE uuid = ?;");
        PreparedStatement insert = databaseHandler.prepareStatement("INSERT INTO artifact_playerdata VALUES(?, ?);");

        try {

            find.setString(1, profile.getOwner().toString());
            ResultSet set = find.executeQuery();

            if(!set.next()) {

                insert.setString(1, profile.getOwner().toString());
                insert.setString(2, serializeArtifacts(profile));

                insert.execute();
                insert.close();
                set.close();

            } else {

                PreparedStatement update = databaseHandler.prepareStatement("UPDATE artifact_playerdata SET artifacts = ? WHERE uuid = ?");

                update.setString(1, serializeArtifacts(profile));
                update.setString(2, profile.getOwner().toString());

                update.execute();
                update.close();

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public ArcheologistProfile getByUUID(UUID uuid) {
        return profiles.stream().filter(archeologistProfile -> archeologistProfile.getOwner().toString().equals(uuid.toString())).findFirst().get();
    }

}
