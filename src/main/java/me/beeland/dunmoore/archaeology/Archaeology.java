package me.beeland.dunmoore.archaeology;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import dev.dbassett.skullcreator.SkullCreator;
import me.beeland.dunmoore.archaeology.commands.ArtifactAdminCommand;
import me.beeland.dunmoore.archaeology.commands.ArtifactCommand;
import me.beeland.dunmoore.archaeology.handler.ArcheologistProfileHandler;
import me.beeland.dunmoore.archaeology.handler.ArtifactCategoryProfileHandler;
import me.beeland.dunmoore.archaeology.handler.ArtifactProfileHandler;
import me.beeland.dunmoore.archaeology.handler.DatabaseHandler;
import me.beeland.dunmoore.archaeology.listener.ArtifactBookInventoryClickListener;
import me.beeland.dunmoore.archaeology.listener.BlockBreakListener;
import me.beeland.dunmoore.archaeology.listener.PlayerJoinListener;
import me.beeland.dunmoore.archaeology.listener.PlayerQuitListener;
import me.beeland.dunmoore.archaeology.profile.ArcheologistProfile;
import me.beeland.dunmoore.archaeology.profile.ArtifactCategoryProfile;
import me.beeland.dunmoore.archaeology.profile.ArtifactProfile;
import me.beeland.dunmoore.archaeology.util.InventoryBuilder;
import me.clip.placeholderapi.PlaceholderAPI;
import net.andreinc.mockneat.MockNeat;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.plugin.java.JavaPlugin;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Archaeology extends JavaPlugin implements Listener {

    private PluginConfiguration pluginConfig;
    private File artifactDirectory;
    private File imageDirectory;
    private PluginConfiguration biomeGroupConfig;
    private PluginConfiguration blockGroupConfig;
    private DatabaseHandler databaseHandler;
    private HashMap<String, List<Biome>> biomeGroups;
    private HashMap<String, List<Material>> blockGroups;
    private HashMap<String, BufferedImage> mapImages;
    private ArtifactCategoryProfileHandler artifactCategoryProfileHandler;
    private ArtifactProfileHandler artifactProfileHandler;
    private ArcheologistProfileHandler archeologistProfileHandler;
    private MockNeat mockNeat;
    private boolean rgbEnabled;
    private Pattern hexPattern;

    @Override
    public void onEnable() {

        if(!Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            getLogger().severe("You must install PlaceholderAPI to use this plugin!");
            Bukkit.getPluginManager().disablePlugin(this);
        }

        this.pluginConfig = new PluginConfiguration(this, getDataFolder(), "config.yml", true);
        this.biomeGroupConfig = new PluginConfiguration(this, getDataFolder(), "biomes.yml", true);
        this.blockGroupConfig = new PluginConfiguration(this, getDataFolder(), "blocks.yml", true);
        this.artifactDirectory = new File(getDataFolder(), "artifacts");
        this.imageDirectory = new File(getDataFolder(), "images");
        this.databaseHandler = new DatabaseHandler(this);

        if(!this.artifactDirectory.exists()) this.artifactDirectory.mkdir();
        if(!this.imageDirectory.exists()) this.imageDirectory.mkdir();

        this.biomeGroups = Maps.newHashMap();
        this.blockGroups = Maps.newHashMap();
        this.mapImages = Maps.newHashMap();

        this.biomeGroupConfig.getConfig().getKeys(false).forEach(section -> {

            List<String> biomeString = biomeGroupConfig.getConfig().getStringList(section);
            List<Biome> biomes = Lists.newArrayList();

            biomeString.forEach(biome -> {
                biomes.add(Biome.valueOf(biome));
            });

            biomeGroups.put(section, biomes);

        });

        this.blockGroupConfig.getConfig().getKeys(false).forEach(section -> {

            List<String> materialString = blockGroupConfig.getConfig().getStringList(section);
            List<Material> materials = Lists.newArrayList();

            materialString.forEach(material -> {
                materials.add(Material.valueOf(material));
            });

            blockGroups.put(section, materials);

        });

        this.pluginConfig.getConfig().getStringList("Images").forEach(fileName -> {

            File file = new File(imageDirectory, fileName);

            if(!file.exists()) {
                getLogger().warning("Attempted to load a file that doesn't exist!");
                return;
            }

            try {

                mapImages.put(fileName, ImageIO.read(file));
                getLogger().info("Loaded Image: " + fileName);

            } catch (IOException e) {
                e.printStackTrace();
            }
        });


        try {


            PreparedStatement initStatement = databaseHandler.prepareStatement("CREATE TABLE IF NOT EXISTS artifact_playerdata(uuid VARCHAR(36) PRIMARY KEY NOT NULL, artifacts VARCHAR(128) NOT NULL);");

            initStatement.execute();
            initStatement.closeOnCompletion();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        this.artifactCategoryProfileHandler = new ArtifactCategoryProfileHandler(this);
        this.artifactProfileHandler = new ArtifactProfileHandler(this);
        this.archeologistProfileHandler = new ArcheologistProfileHandler(this);

        this.mockNeat = MockNeat.threadLocal();
        this.rgbEnabled = pluginConfig.getBoolean("Options.RGB-Enabled");
        this.hexPattern = Pattern.compile("&#[a-fA-F0-9]{6}");

        this.artifactCategoryProfileHandler.load();
        this.archeologistProfileHandler.load();

        getServer().getPluginManager().registerEvents(new BlockBreakListener(this), this);
        getServer().getPluginManager().registerEvents(new ArtifactBookInventoryClickListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(this), this);

        getCommand("artifact").setExecutor(new ArtifactCommand(this));
        getCommand("artifactadmin").setExecutor(new ArtifactAdminCommand(this));
    }

    @Override
    public void onDisable() {

        if(archeologistProfileHandler != null) {
            this.archeologistProfileHandler.save();
            this.databaseHandler.closeConnection();
        }

        this.getLogger().warning("Plugin shut down, see you in a moment <3");
    }

    public PluginConfiguration getPluginConfig() {
        return pluginConfig;
    }

    public PluginConfiguration getBiomeGroupConfig() {
        return biomeGroupConfig;
    }

    public PluginConfiguration getBlockGroupConfig() {
        return blockGroupConfig;
    }

    public File getArtifactDirectory() {
        return artifactDirectory;
    }

    public DatabaseHandler getDatabaseHandler() {
        return databaseHandler;
    }

    public HashMap<String, List<Biome>> getBiomeGroups() {
        return biomeGroups;
    }

    public List<Biome> getBiomeGroupByName(String name) {

        for(Map.Entry<String, List<Biome>> entry : biomeGroups.entrySet()) {
            if(entry.getKey().equalsIgnoreCase(name)) return entry.getValue();
        }

        return Lists.newArrayList();
    }

    public HashMap<String, List<Material>> getBlockGroups() {
        return blockGroups;
    }

    public List<Material> getMaterialGroupByName(String name) {

        for(Map.Entry<String, List<Material>> entry : blockGroups.entrySet()) {
            if(entry.getKey().equalsIgnoreCase(name)) return entry.getValue();
        }
        return Lists.newArrayList();
    }

    public ArtifactCategoryProfileHandler getArtifactCategoryProfileHandler() {
        return artifactCategoryProfileHandler;
    }

    public ArcheologistProfileHandler getArcheologistProfileHandler() {
        return archeologistProfileHandler;
    }

    public ArtifactProfileHandler getArtifactProfileHandler() {
        return artifactProfileHandler;
    }

    public MockNeat getMockNeat() {
        return mockNeat;
    }

    public boolean isCreativeDisabled() {
        return pluginConfig.getBoolean("Options.Disable-Creative");
    }

    public String withColor(String message) {

        if(rgbEnabled) {

            Matcher matcher = hexPattern.matcher(message);

            while(matcher.find()) {

                ChatColor hexColor = ChatColor.of(matcher.group().substring(1));
                String before = message.substring(0, matcher.start());
                String after = message.substring(matcher.end());

                message = before + hexColor + after;
                matcher = hexPattern.matcher(message);
            }
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public List<String> asColorizedList(List<String> list) {

        List<String> colorized = Lists.newArrayList();
        list.forEach(line -> colorized.add(withColor(line)));
        return colorized;
    }

    public String getMessage(String path) {
        return withColor(pluginConfig.getString("Lang." + path));
    }

    public String getMessageWithPlaceholders(Player player, String message) {
        return PlaceholderAPI.setPlaceholders(player, withColor(message));
    }

    public ItemStack deserializeItem(ConfigurationSection section) {

        ItemStack item = new ItemStack(Material.valueOf(section.getString("Material")));

        if(section.contains("Amount")) item.setAmount(section.getInt("Amount"));

        ItemMeta meta = item.getItemMeta();

        if(section.contains("Display-Name")) meta.setDisplayName(withColor(section.getString("Display-Name")));
        if(section.contains("Lore")) meta.setLore(asColorizedList(section.getStringList("Lore")));

        if(section.contains("Enchants")) {

            section.getStringList("Enchants").forEach(enchantLine -> {

                String[] split = enchantLine.split(";");

                Enchantment enchant = Enchantment.getByName(split[0]);
                int level = Integer.parseInt(split[1]);

                meta.addEnchant(enchant, level, true);
            });
        }

        if(section.contains("Flags")) {
            section.getStringList("Flags").forEach(flag -> meta.addItemFlags(ItemFlag.valueOf(flag)));
        }

        item.setItemMeta(meta);

        if(item.getType() == Material.WRITTEN_BOOK) {

            ConfigurationSection bookSection = section.getConfigurationSection("Book");
            BookMeta bookMeta = (BookMeta) item.getItemMeta();
            List<String> pages = Lists.newArrayList();

            bookSection.getStringList("Pages").forEach(page -> pages.add(withColor(page)));

            bookMeta.setAuthor(withColor(bookSection.getString("Author")));
            bookMeta.setTitle(withColor(bookSection.getString("Title")));
            bookMeta.setPages(pages);

        }

        if(item.getType() == Material.PLAYER_HEAD) {

            SkullMeta parent = (SkullMeta) meta;
            SkullMeta child = (SkullMeta) SkullCreator.itemFromBase64(section.getString("Skull")).getItemMeta();

            parent.setOwnerProfile(child.getOwnerProfile());
            item.setItemMeta(parent);
        }

        if(item.getType() == Material.FILLED_MAP) {

            MapMeta mapMeta = (MapMeta) meta;

            MapView mapView = Bukkit.createMap(Bukkit.getWorlds().stream().findFirst().get());
            mapView.getRenderers().clear();

            mapView.addRenderer(new MapRenderer() {
                @Override
                public void render(MapView map, MapCanvas canvas, Player player) {
                    canvas.drawImage(0, 0, mapImages.get(section.getString("Map")));
                }
            });

            mapMeta.setMapView(mapView);
            item.setItemMeta(mapMeta);
        }

        return item;
    }

    public Inventory getArtifactBookMenu(Player player) {

        ItemStack fillItem = deserializeItem(pluginConfig.getConfigSection("Artifact-Book.Filler-Item"));
        ItemStack nextPageItem = deserializeItem(pluginConfig.getConfigSection("Artifact-Book.Next-Page"));
        ItemStack previousPageItem = deserializeItem(pluginConfig.getConfigSection("Artifact-Book.Previous-Page"));

        String artifactBookTitle = getMessage("Artifact-Book-Title");

        Inventory inventory = new InventoryBuilder(54, getMessageWithPlaceholders(player, artifactBookTitle))
                .setItem(fillItem, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53)
                .setItem(nextPageItem, 45)
                .setItem(previousPageItem, 53)
                .build();

        for(ArtifactCategoryProfile categoryProfile : artifactCategoryProfileHandler.getCategoryProfiles()) {
            inventory.addItem(categoryProfile.getItem());
        }
        return inventory;
    }

    public Inventory getArtifactMenu(Player player, ArtifactCategoryProfile categoryProfile) {

        ArcheologistProfile archeologistProfile = archeologistProfileHandler.getByUUID(player.getUniqueId());
        List<ArtifactProfile> allArtifacts = categoryProfile.getProfiles();
        List<ArtifactProfile> foundArtifacts = Lists.newArrayList();

        for(Map.Entry<Integer, Integer> entry : archeologistProfile.getSavedArtifacts().entrySet()) {
            foundArtifacts.add(artifactProfileHandler.getById(entry.getKey()));
        }

        String artifactBookTitle = getMessage("Artifact-Category-Title");
        ItemStack fillerItem = deserializeItem(pluginConfig.getConfigSection("Artifact-Book.Filler-Item"));
        ItemStack blackSkull = deserializeItem(pluginConfig.getConfigSection("Artifact-Book.Unknown-Artifact"));
        ItemStack previousPageItem = deserializeItem(pluginConfig.getConfigSection("Artifact-Book.Previous-Page"));


        Inventory inventory = new InventoryBuilder(54, getMessageWithPlaceholders(player, artifactBookTitle))
                .setItem(fillerItem, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53)
                .setItem(previousPageItem, 53)
                .build();

        for(ArtifactProfile profile : foundArtifacts) {

            ItemStack artifact = profile.getArtifact(player);
            artifact.setAmount(archeologistProfile.getSavedArtifacts().get(profile.getId()));

            inventory.addItem(artifact);
        }

        for(int i = 1; i <= (allArtifacts.size() - foundArtifacts.size()); i++) {
            inventory.setItem(inventory.firstEmpty(), blackSkull);
        }

        return inventory;
    }

}
