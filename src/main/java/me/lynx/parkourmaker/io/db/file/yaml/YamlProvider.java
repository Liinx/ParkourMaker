package me.lynx.parkourmaker.io.db.file.yaml;

import me.lynx.parkourmaker.ParkourMakerPlugin;
import me.lynx.parkourmaker.io.db.access.AccessProvider;

import me.lynx.parkourmaker.model.map.*;
import me.lynx.parkourmaker.model.runner.*;
import me.lynx.parkourmaker.model.sign.SignText;
import me.lynx.parkourmaker.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class YamlProvider implements AccessProvider {

    private final ParkourMakerPlugin plugin = ParkourMakerPlugin.instance();
    private final File yamlStorage;
    private final File userStorage;
    private final File mapStorage;
    private File lobbyLocStorage;

    public YamlProvider() {
        File pluginDir = plugin.getPluginDir();
        if (!pluginDir.exists()) pluginDir.mkdir();

        yamlStorage = makeDir(pluginDir.getAbsolutePath(), "storage");
        userStorage = makeDir(yamlStorage.getAbsolutePath(), "users");
        mapStorage = makeDir(yamlStorage.getAbsolutePath(), "maps");
        lobbyLocStorage = new File(yamlStorage.getAbsolutePath(), "lobby-location.yml");

        String loadedMaps = loadMaps();
        plugin.getLogger().info("Successfully loaded " + loadedMaps + " maps.");
        loadUsers();
    }

    private File makeDir(String parent, String fileName) {
        File dir  = new File(parent + File.separator + fileName);
        if (!dir.exists()) dir.mkdir();
        return dir;
    }

    @Override
    public void setLobbyLocation(double x, double y, double z, float yaw, float pitch, String worldName) {
        if (!yamlStorage.exists()) yamlStorage.mkdir();

        if (!lobbyLocStorage.exists()) {
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(lobbyLocStorage),
                    "UTF-8"))) {
                lobbyLocStorage.createNewFile();

                bw.write("x: " + Utils.roundToTwo(x));
                bw.newLine();
                bw.write("y: " +  Utils.roundToTwo(y));
                bw.newLine();
                bw.write("z: " +  Utils.roundToTwo(z));
                bw.newLine();
                bw.write("yaw: " +  Utils.roundToTwo(yaw));
                bw.newLine();
                bw.write("pitch: " +  Utils.roundToTwo(pitch));
                bw.newLine();
                bw.write("world: " + "'" + worldName + "'");
            } catch (IOException e) {
                plugin.getLogger().warning("Could not save lobby location!");
            }
        } else {
            YamlConfiguration lobbyLoc = YamlConfiguration.loadConfiguration(lobbyLocStorage);
            lobbyLoc.set("x", Utils.roundToTwo(x));
            lobbyLoc.set("y", Utils.roundToTwo(y));
            lobbyLoc.set("z", Utils.roundToTwo(z));
            lobbyLoc.set("yaw", Utils.roundToTwo(yaw));
            lobbyLoc.set("pitch", Utils.roundToTwo(pitch));
            lobbyLoc.set("world", worldName);
            try {
                lobbyLoc.save(lobbyLocStorage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Location getLobbyLocation() {
        if (!lobbyLocStorage.exists()) return null;
        YamlConfiguration lobbyLoc = YamlConfiguration.loadConfiguration(lobbyLocStorage);

        Location loc = new Location(
            Bukkit.getWorld(lobbyLoc.getString("world")),
            lobbyLoc.getDouble("x"),
            lobbyLoc.getDouble("y"),
            lobbyLoc.getDouble("z"),
            (float) lobbyLoc.getDouble("yaw"),
            (float) lobbyLoc.getDouble("pitch"));
        return loc;
    }

    @Override
    public void createNewMap(String name, String creator) {
        if (!yamlStorage.exists()) yamlStorage.mkdir();
        if (!mapStorage.exists()) mapStorage.mkdir();

        File mapFile = new File(mapStorage.getAbsolutePath(), name + ".yml");
        if (!mapFile.exists()) {
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(mapFile),
                    "UTF-8"))) {
                mapFile.createNewFile();

                bw.write("name: " + name);
                bw.newLine();
                bw.write("creator: " + creator);
                bw.newLine();
                bw.write("enabled: " + false);
            } catch (IOException e) {
                plugin.getLogger().warning("Could not create or write to " + name + ".yml file!");
            }
        }
    }

    @Override
    public void deleteMap(String name) {
        if (!yamlStorage.exists()) yamlStorage.mkdir();
        if (!mapStorage.exists()) mapStorage.mkdir();

        File mapFile = new File(mapStorage.getAbsolutePath(), name + ".yml");
        if (mapFile.exists()) {
            try {
                Files.delete(mapFile.getAbsoluteFile().toPath());
            } catch (IOException e) {
                plugin.getLogger().warning("Could not delete " + name + ".yml file!");
            }
        }
    }

    @Override
    public void setStartLocation(String name, Location location) {
        if (!yamlStorage.exists()) yamlStorage.mkdir();
        if (!mapStorage.exists()) mapStorage.mkdir();

        File mapFile = new File(mapStorage.getAbsolutePath(), name + ".yml");
        if (mapFile.exists()) {
            YamlConfiguration mapConfig = YamlConfiguration.loadConfiguration(mapFile);
            mapConfig.set("start-location.x", Utils.roundToTwo(location.getX()));
            mapConfig.set("start-location.y", Utils.roundToTwo(location.getY()));
            mapConfig.set("start-location.z", Utils.roundToTwo(location.getZ()));
            mapConfig.set("start-location.yaw", Utils.roundToTwo(location.getYaw()));
            mapConfig.set("start-location.pitch", Utils.roundToTwo(location.getPitch()));
            mapConfig.set("start-location.world", location.getWorld().getName());
            try {
                mapConfig.save(mapFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else ParkourMakerPlugin.instance().getLogger().warning("Could not save info to " + name + " .yml file!");
    }

    @Override
    public void setFinishLocation(String name, Selection selection) {
        if (!yamlStorage.exists()) yamlStorage.mkdir();
        if (!mapStorage.exists()) mapStorage.mkdir();

        File mapFile = new File(mapStorage.getAbsolutePath(), name + ".yml");
        if (mapFile.exists()) {
            YamlConfiguration mapConfig = YamlConfiguration.loadConfiguration(mapFile);
            mapConfig.set("finish-location.type", selection.getType().name());

            if (selection.getType() == SelectionType.SINGLE) {
                mapConfig.set("finish-location.x", Utils.roundToTwo(selection.getStartPoint().getX()));
                mapConfig.set("finish-location.y", Utils.roundToTwo(selection.getStartPoint().getY()));
                mapConfig.set("finish-location.z", Utils.roundToTwo(selection.getStartPoint().getZ()));
            } else if (selection.getType() == SelectionType.MULTI) {
                mapConfig.set("finish-location.min.x", Utils.roundToTwo(selection.getStartPoint().getX()));
                mapConfig.set("finish-location.min.y", Utils.roundToTwo(selection.getStartPoint().getY()));
                mapConfig.set("finish-location.min.z", Utils.roundToTwo(selection.getStartPoint().getZ()));
                mapConfig.set("finish-location.max.x", Utils.roundToTwo(selection.getEndPoint().getX()));
                mapConfig.set("finish-location.max.y", Utils.roundToTwo(selection.getEndPoint().getY()));
                mapConfig.set("finish-location.max.z", Utils.roundToTwo(selection.getEndPoint().getZ()));
            }
            mapConfig.set("finish-location.world", selection.getStartPoint().getWorld().getName());

            try {
                mapConfig.save(mapFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else ParkourMakerPlugin.instance().getLogger().warning("Could not save info to " + name + " .yml file!");
    }

    @Override
    public void setFinishTeleport(String name, Location location) {
        if (!yamlStorage.exists()) yamlStorage.mkdir();
        if (!mapStorage.exists()) mapStorage.mkdir();

        File mapFile = new File(mapStorage.getAbsolutePath(), name + ".yml");
        if (mapFile.exists()) {
            YamlConfiguration mapConfig = YamlConfiguration.loadConfiguration(mapFile);
            if (location == null) mapConfig.set("finish-location.teleport", null);
            else {
                mapConfig.set("finish-location.teleport.x", Utils.roundToTwo(location.getX()));
                mapConfig.set("finish-location.teleport.y", Utils.roundToTwo(location.getY()));
                mapConfig.set("finish-location.teleport.z", Utils.roundToTwo(location.getZ()));
                mapConfig.set("finish-location.teleport.yaw", Utils.roundToTwo(location.getYaw()));
                mapConfig.set("finish-location.teleport.pitch", Utils.roundToTwo(location.getPitch()));
                mapConfig.set("finish-location.teleport.world", location.getWorld().getName());
            }
            try {
                mapConfig.save(mapFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else ParkourMakerPlugin.instance().getLogger().warning("Could not save info to " + name + " .yml file!");
    }

    @Override
    public void addCheckpoint(String name, Checkpoint checkpoint) {
        if (!yamlStorage.exists()) yamlStorage.mkdir();
        if (!mapStorage.exists()) mapStorage.mkdir();

        File mapFile = new File(mapStorage.getAbsolutePath(), name + ".yml");
        if (mapFile.exists()) {
            YamlConfiguration mapConfig = YamlConfiguration.loadConfiguration(mapFile);
            int pos = checkpoint.getPosition();

            mapConfig.set("checkpoint." + pos + ".name", checkpoint.getName());
            mapConfig.set("checkpoint." + pos + ".position", pos);
            mapConfig.set("checkpoint." + pos + ".type", checkpoint.getType().name());

            if (checkpoint.getType() == SelectionType.SINGLE) {
                mapConfig.set("checkpoint." + pos + ".x", Utils.roundToTwo(checkpoint.getStartPoint().getX()));
                mapConfig.set("checkpoint." + pos + ".y", Utils.roundToTwo(checkpoint.getStartPoint().getY()));
                mapConfig.set("checkpoint." + pos + ".z", Utils.roundToTwo(checkpoint.getStartPoint().getZ()));
                mapConfig.set("checkpoint." + pos + ".yaw", Utils.roundToTwo(checkpoint.getStartPoint().getYaw()));
                mapConfig.set("checkpoint." + pos + ".pitch", Utils.roundToTwo(checkpoint.getStartPoint().getPitch()));
            } else if (checkpoint.getType() == SelectionType.MULTI) {
                mapConfig.set("checkpoint." + pos + ".min.x", Utils.roundToTwo(checkpoint.getStartPoint().getX()));
                mapConfig.set("checkpoint." + pos + ".min.y", Utils.roundToTwo(checkpoint.getStartPoint().getY()));
                mapConfig.set("checkpoint." + pos + ".min.z", Utils.roundToTwo(checkpoint.getStartPoint().getZ()));
                mapConfig.set("checkpoint." + pos + ".max.x", Utils.roundToTwo(checkpoint.getEndPoint().getX()));
                mapConfig.set("checkpoint." + pos + ".max.y", Utils.roundToTwo(checkpoint.getEndPoint().getY()));
                mapConfig.set("checkpoint." + pos + ".max.z", Utils.roundToTwo(checkpoint.getEndPoint().getZ()));

                mapConfig.set("checkpoint." + pos + ".area-teleport.x", Utils.roundToTwo(checkpoint.getTeleportLocation().getX()));
                mapConfig.set("checkpoint." + pos + ".area-teleport.y", Utils.roundToTwo(checkpoint.getTeleportLocation().getY()));
                mapConfig.set("checkpoint." + pos + ".area-teleport.z", Utils.roundToTwo(checkpoint.getTeleportLocation().getZ()));
                mapConfig.set("checkpoint." + pos + ".area-teleport.yaw", Utils.roundToTwo(checkpoint.getTeleportLocation().getYaw()));
                mapConfig.set("checkpoint." + pos + ".area-teleport.pitch", Utils.roundToTwo(checkpoint.getTeleportLocation().getPitch()));
                mapConfig.set("checkpoint." + pos + ".area-teleport.world", checkpoint.getTeleportLocation().getWorld().getName());
            }
            mapConfig.set("checkpoint." + pos + ".world", checkpoint.getStartPoint().getWorld().getName());

            try {
                mapConfig.save(mapFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else ParkourMakerPlugin.instance().getLogger().warning("Could not save info to " + name + " .yml file!");
    }

    @Override
    public void deleteCheckpoint(String name, int position) {
        if (!yamlStorage.exists()) yamlStorage.mkdir();
        if (!mapStorage.exists()) mapStorage.mkdir();

        File mapFile = new File(mapStorage.getAbsolutePath(), name + ".yml");
        if (mapFile.exists()) {
            YamlConfiguration mapConfig = YamlConfiguration.loadConfiguration(mapFile);
            mapConfig.set("checkpoint." + position, null);

            try {
                mapConfig.save(mapFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else ParkourMakerPlugin.instance().getLogger().warning("Could not save info to " + name + " .yml file!");
    }

    @Override
    public void addFallzone(String name, Fallzone fallzone) {
        if (!yamlStorage.exists()) yamlStorage.mkdir();
        if (!mapStorage.exists()) mapStorage.mkdir();

        File mapFile = new File(mapStorage.getAbsolutePath(), name + ".yml");
        if (mapFile.exists()) {
            YamlConfiguration mapConfig = YamlConfiguration.loadConfiguration(mapFile);
            String zoneName = fallzone.getName();
            mapConfig.set("fallzone." + zoneName + ".min.x", Utils.roundToTwo(fallzone.getStartPoint().getX()));
            mapConfig.set("fallzone." + zoneName + ".min.y", Utils.roundToTwo(fallzone.getStartPoint().getY()));
            mapConfig.set("fallzone." + zoneName + ".min.z", Utils.roundToTwo(fallzone.getStartPoint().getZ()));
            mapConfig.set("fallzone." + zoneName + ".max.x", Utils.roundToTwo(fallzone.getEndPoint().getX()));
            mapConfig.set("fallzone." + zoneName + ".max.y", Utils.roundToTwo(fallzone.getEndPoint().getY()));
            mapConfig.set("fallzone." + zoneName + ".max.z", Utils.roundToTwo(fallzone.getEndPoint().getZ()));
            mapConfig.set("fallzone." + zoneName + ".world", fallzone.getStartPoint().getWorld().getName());

            try {
                mapConfig.save(mapFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else ParkourMakerPlugin.instance().getLogger().warning("Could not save info to " + name + " .yml file!");
    }

    @Override
    public void deleteFallzone(String name, String zoneName) {
        if (!yamlStorage.exists()) yamlStorage.mkdir();
        if (!mapStorage.exists()) mapStorage.mkdir();

        File mapFile = new File(mapStorage.getAbsolutePath(), name + ".yml");
        if (mapFile.exists()) {
            YamlConfiguration mapConfig = YamlConfiguration.loadConfiguration(mapFile);
            mapConfig.set("fallzone." + zoneName, null);
            try {
                mapConfig.save(mapFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else ParkourMakerPlugin.instance().getLogger().warning("Could not save info to " + name + " .yml file!");
    }

    @Override
    public void addReward(String name, Reward reward) {
        if (!yamlStorage.exists()) yamlStorage.mkdir();
        if (!mapStorage.exists()) mapStorage.mkdir();

        File mapFile = new File(mapStorage.getAbsolutePath(), name + ".yml");
        if (mapFile.exists()) {
            YamlConfiguration mapConfig = YamlConfiguration.loadConfiguration(mapFile);
            mapConfig.set("reward." + reward.getId(), reward.getCommand());

            try {
                mapConfig.save(mapFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else ParkourMakerPlugin.instance().getLogger().warning("Could not save info to " + name + " .yml file!");
    }

    @Override
    public void deleteReward(String name, int id) {
        if (!yamlStorage.exists()) yamlStorage.mkdir();
        if (!mapStorage.exists()) mapStorage.mkdir();

        File mapFile = new File(mapStorage.getAbsolutePath(), name + ".yml");
        if (mapFile.exists()) {
            YamlConfiguration mapConfig = YamlConfiguration.loadConfiguration(mapFile);
            mapConfig.set("reward." + id, null);

            try {
                mapConfig.save(mapFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else ParkourMakerPlugin.instance().getLogger().warning("Could not save info to " + name + " .yml file!");
    }

    @Override
    public void setStartMessage(String name, String message) {
        if (!yamlStorage.exists()) yamlStorage.mkdir();
        if (!mapStorage.exists()) mapStorage.mkdir();

        File mapFile = new File(mapStorage.getAbsolutePath(), name + ".yml");
        if (mapFile.exists()) {
            YamlConfiguration mapConfig = YamlConfiguration.loadConfiguration(mapFile);
            mapConfig.set("message.start", message);

            try {
                mapConfig.save(mapFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else ParkourMakerPlugin.instance().getLogger().warning("Could not save info to " + name + " .yml file!");
    }

    @Override
    public void setFinishMessage(String name, String message) {
        if (!yamlStorage.exists()) yamlStorage.mkdir();
        if (!mapStorage.exists()) mapStorage.mkdir();

        File mapFile = new File(mapStorage.getAbsolutePath(), name + ".yml");
        if (mapFile.exists()) {
            YamlConfiguration mapConfig = YamlConfiguration.loadConfiguration(mapFile);
            mapConfig.set("message.finish", message);

            try {
                mapConfig.save(mapFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else ParkourMakerPlugin.instance().getLogger().warning("Could not save info to " + name + " .yml file!");
    }

    @Override
    public void addSignText(String name, int line, String message) {
        if (!yamlStorage.exists()) yamlStorage.mkdir();
        if (!mapStorage.exists()) mapStorage.mkdir();

        File mapFile = new File(mapStorage.getAbsolutePath(), name + ".yml");
        if (mapFile.exists()) {
            YamlConfiguration mapConfig = YamlConfiguration.loadConfiguration(mapFile);
            mapConfig.set("sign-text." + line, message);

            try {
                mapConfig.save(mapFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else ParkourMakerPlugin.instance().getLogger().warning("Could not save info to " + name + " .yml file!");
    }

    @Override
    public void setActivity(String name, boolean activity) {
        if (!yamlStorage.exists()) yamlStorage.mkdir();
        if (!mapStorage.exists()) mapStorage.mkdir();

        File mapFile = new File(mapStorage.getAbsolutePath(), name + ".yml");
        if (mapFile.exists()) {
            YamlConfiguration mapConfig = YamlConfiguration.loadConfiguration(mapFile);
            mapConfig.set("enabled", activity);

            try {
                mapConfig.save(mapFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else ParkourMakerPlugin.instance().getLogger().warning("Could not save info to " + name + " .yml file!");
    }

    @Override
    public void setDisplayName(String name, String displayName) {
        if (!yamlStorage.exists()) yamlStorage.mkdir();
        if (!mapStorage.exists()) mapStorage.mkdir();

        File mapFile = new File(mapStorage.getAbsolutePath(), name + ".yml");
        if (mapFile.exists()) {
            YamlConfiguration mapConfig = YamlConfiguration.loadConfiguration(mapFile);
            mapConfig.set("display-name", displayName);

            try {
                mapConfig.save(mapFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else ParkourMakerPlugin.instance().getLogger().warning("Could not save info to " + name + " .yml file!");
    }

    @Override
    public void setJoinCooldown(String name, long amount) {
        if (!yamlStorage.exists()) yamlStorage.mkdir();
        if (!mapStorage.exists()) mapStorage.mkdir();

        File mapFile = new File(mapStorage.getAbsolutePath(), name + ".yml");
        if (mapFile.exists()) {
            YamlConfiguration mapConfig = YamlConfiguration.loadConfiguration(mapFile);
            mapConfig.set("cooldown.join", amount);

            try {
                mapConfig.save(mapFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else ParkourMakerPlugin.instance().getLogger().warning("Could not save info to " + name + " .yml file!");
    }

    @Override
    public void setRewardCooldown(String name, long amount) {
        if (!yamlStorage.exists()) yamlStorage.mkdir();
        if (!mapStorage.exists()) mapStorage.mkdir();

        File mapFile = new File(mapStorage.getAbsolutePath(), name + ".yml");
        if (mapFile.exists()) {
            YamlConfiguration mapConfig = YamlConfiguration.loadConfiguration(mapFile);
            mapConfig.set("cooldown.reward", amount);

            try {
                mapConfig.save(mapFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else ParkourMakerPlugin.instance().getLogger().warning("Could not save info to " + name + " .yml file!");
    }

    @Override
    public void setRewardType(String name, RewardType type) {
        if (!yamlStorage.exists()) yamlStorage.mkdir();
        if (!mapStorage.exists()) mapStorage.mkdir();

        File mapFile = new File(mapStorage.getAbsolutePath(), name + ".yml");
        if (mapFile.exists()) {
            YamlConfiguration mapConfig = YamlConfiguration.loadConfiguration(mapFile);
            mapConfig.set("reward.type", type.name());

            try {
                mapConfig.save(mapFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else ParkourMakerPlugin.instance().getLogger().warning("Could not save info to " + name + " .yml file!");
    }

    @Override
    public void setAttempts(String name, int amount) {
        if (!yamlStorage.exists()) yamlStorage.mkdir();
        if (!mapStorage.exists()) mapStorage.mkdir();

        File mapFile = new File(mapStorage.getAbsolutePath(), name + ".yml");
        if (mapFile.exists()) {
            YamlConfiguration mapConfig = YamlConfiguration.loadConfiguration(mapFile);
            mapConfig.set("attempts", amount);

            try {
                mapConfig.save(mapFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else ParkourMakerPlugin.instance().getLogger().warning("Could not save info to " + name + " .yml file!");
    }

    private String loadMaps() {
        File[] maps = mapStorage.listFiles();

        int amountOfFiles = 0;
        int amountOfLoadedMaps = 0;

        for (File file : maps) {
            if (file.isDirectory()) continue;
            if (file.isFile()) {
                amountOfFiles++;
                ParkourMap map = constructMapFromStorage(file.getName());
                if (map == null) continue;
                ParkourMakerPlugin.instance().getMapHandler().addMapFromStorage(map);
                amountOfLoadedMaps++;
            }
        }
        return amountOfLoadedMaps + "/" + amountOfFiles;
    }

    /**
     * Safely build a map from storage it will ignore values that are no set. Only manual deletion of
     * specific values might cause problems.
     */
    @Override
    public ParkourMap constructMapFromStorage(String fileName) {
        if (!yamlStorage.exists()) yamlStorage.mkdir();
        if (!mapStorage.exists()) mapStorage.mkdir();

        File mapFile = new File(mapStorage.getAbsolutePath(), fileName);
        if (mapFile.exists()) {
            YamlConfiguration mapConfig = YamlConfiguration.loadConfiguration(mapFile);

            String name = mapConfig.getString("name");
            String displayName = mapConfig.getString("display-name");
            String creator = mapConfig.getString("creator");
            boolean enabled = mapConfig.getBoolean("enabled");

            List<Checkpoint> checkpoints = new ArrayList<>();
            ConfigurationSection cpSection = mapConfig.getConfigurationSection("checkpoint");
            if (cpSection != null) {
                cpSection.getKeys(false).forEach(cpField -> {
                    Checkpoint checkpoint = new Checkpoint();

                    checkpoint.setName(cpSection.getString(cpField + ".name"));
                    checkpoint.setPosition(Integer.parseInt(cpField));
                    checkpoint.setType(SelectionType.valueOf(cpSection.getString(cpField + ".type")));

                    if (checkpoint.getType() == SelectionType.SINGLE) {
                        double x = cpSection.getDouble(cpField + ".x");
                        double y = cpSection.getDouble(cpField + ".y");
                        double z = cpSection.getDouble(cpField + ".z");
                        double yaw = cpSection.getDouble(cpField + ".yaw");
                        double pitch = cpSection.getDouble(cpField + ".pitch");
                        String world = cpSection.getString(cpField + ".world");

                        checkpoint.setStartPoint(new Location(Bukkit.getWorld(world) , x ,y ,z ,(float) yaw, (float) pitch));
                    } else if (checkpoint.getType() == SelectionType.MULTI) {
                        double minX = cpSection.getDouble(cpField + ".min.x");
                        double minY = cpSection.getDouble(cpField + ".min.y");
                        double minZ = cpSection.getDouble(cpField + ".min.z");

                        double maxX = cpSection.getDouble(cpField + ".max.x");
                        double maxY = cpSection.getDouble(cpField + ".max.y");
                        double maxZ = cpSection.getDouble(cpField + ".max.z");
                        String areaWorld = cpSection.getString(cpField + ".world");

                        checkpoint.setStartPoint(new Location(Bukkit.getWorld(areaWorld), minX, minY, minZ));
                        checkpoint.setEndPoint(new Location(Bukkit.getWorld(areaWorld), maxX, maxY, maxZ));

                        double x = cpSection.getDouble(cpField + ".area-teleport.x");
                        double y = cpSection.getDouble(cpField + ".area-teleport.y");
                        double z = cpSection.getDouble(cpField + ".area-teleport.z");
                        double yaw = cpSection.getDouble(cpField + ".area-teleport.yaw");
                        double pitch = cpSection.getDouble(cpField + ".area-teleport.pitch");
                        String world = cpSection.getString(cpField + ".area-teleport.world");

                        checkpoint.setTeleportLocation(new Location(Bukkit.getWorld(world) , x ,y ,z ,(float) yaw, (float) pitch));
                    }
                    checkpoints.add(checkpoint);
                });
            }

            List<Fallzone> fallzones = new ArrayList<>();
            ConfigurationSection fzSection = mapConfig.getConfigurationSection("fallzone");
            if (fzSection != null) {
                fzSection.getKeys(false).forEach(fzField -> {

                    double minX = fzSection.getDouble(fzField + ".min.x");
                    double minY = fzSection.getDouble(fzField + ".min.y");
                    double minZ = fzSection.getDouble(fzField + ".min.z");

                    double maxX = fzSection.getDouble(fzField + ".max.x");
                    double maxY = fzSection.getDouble(fzField + ".max.y");
                    double maxZ = fzSection.getDouble(fzField + ".max.z");
                    String areaWorld = fzSection.getString(fzField + ".world");

                    Fallzone fallzone = new Fallzone(fzField,
                        new Location(Bukkit.getWorld(areaWorld), minX, minY, minZ),
                        new Location(Bukkit.getWorld(areaWorld), maxX, maxY, maxZ));
                    fallzones.add(fallzone);
                });
            }

            List<Reward> rewards = new ArrayList<>();
            ConfigurationSection rwSection = mapConfig.getConfigurationSection("reward");
            if (rwSection != null) {
                rwSection.getKeys(false).forEach(rwField -> {
                    if (rwField.equalsIgnoreCase("TYPE")) return;

                    String command = rwSection.getString(rwField);
                    Reward reward = new Reward(Integer.parseInt(rwField), command);
                    rewards.add(reward);
                });
            }

            Location startLocation = null;
            ConfigurationSection slSection = mapConfig.getConfigurationSection("start-location");
            if (slSection != null) {
                double x = slSection.getDouble("x");
                double y = slSection.getDouble("y");
                double z = slSection.getDouble("z");
                double yaw = slSection.getDouble("yaw");
                double pitch = slSection.getDouble("pitch");
                String world = slSection.getString("world");

                startLocation = new Location(Bukkit.getWorld(world), x, y, z, (float) yaw, (float) pitch);
            }

            Selection finishSelection = null;
            Location finishTeleport = null;
            ConfigurationSection flSection = mapConfig.getConfigurationSection("finish-location");
            if (flSection != null) {
                SelectionType finishType = SelectionType.valueOf(flSection.getString("type"));
                if (finishType == SelectionType.SINGLE) {
                    double x = flSection.getDouble("x");
                    double y = flSection.getDouble("y");
                    double z = flSection.getDouble("z");
                    String world = flSection.getString("world");

                    finishSelection = new Selection(new Location(Bukkit.getWorld(world) , x ,y ,z));
                } else {
                    double minX = flSection.getDouble("min.x");
                    double minY = flSection.getDouble("min.y");
                    double minZ = flSection.getDouble("min.z");

                    double maxX = flSection.getDouble("max.x");
                    double maxY = flSection.getDouble("max.y");
                    double maxZ = flSection.getDouble("max.z");
                    String world = flSection.getString("world");

                    finishSelection = new Selection(new Location(Bukkit.getWorld(world), minX, minY, minZ),
                            new Location(Bukkit.getWorld(world), maxX, maxY, maxZ));
                }


                ConfigurationSection fltSection = flSection.getConfigurationSection("teleport");
                if (fltSection != null) {
                    double x = fltSection.getDouble("x");
                    double y = fltSection.getDouble("y");
                    double z = fltSection.getDouble("z");
                    double yaw = fltSection.getDouble("yaw");
                    double pitch = fltSection.getDouble("pitch");
                    String world = fltSection.getString("world");

                    finishTeleport = new Location(Bukkit.getWorld(world), x, y, z, (float) yaw, (float) pitch);
                }
            }

            SignText signText = new SignText();
            ConfigurationSection stSection = mapConfig.getConfigurationSection("sign-text");
            if (stSection != null) {
                stSection.getKeys(false).forEach(stField -> {

                    String line = stSection.getString(stField);
                    signText.setLine(Integer.parseInt(stField), line, false);
                });
            }

            String startMessage = mapConfig.getString("message.start");
            String finishMessage = mapConfig.getString("message.finish");

            long joinCooldown = mapConfig.getLong("cooldown.join");
            long rewardCooldown = mapConfig.getLong("cooldown.reward");

            String rewardType = mapConfig.getString("reward.type");
            if (rewardType == null) rewardType = "ALL";
            RewardType actualRewardType = RewardType.valueOf(rewardType);

            int attempts = mapConfig.getInt("attempts");

            ParkourMap map = new ParkourMap(name, creator, checkpoints, fallzones, rewards,
                startLocation, finishSelection, finishTeleport, signText, startMessage, finishMessage, enabled,
                displayName, joinCooldown, rewardCooldown, actualRewardType, attempts);

            map.getAllCheckpoints().forEach(checkpoint -> checkpoint.setOwningMap(map));
            map.getAllRewards().forEach(reward -> reward.setOwningMap(map));
            map.getSignText().setOwningMap(map);

            return map;
        } else {
            ParkourMakerPlugin.instance().getLogger().warning("Could not load " + fileName + " file!");
            return null;
        }
    }

    @Override
    public void insertRunner(String name) {
        if (!yamlStorage.exists()) yamlStorage.mkdir();
        if (!userStorage.exists()) userStorage.mkdir();

        File userFile = new File(userStorage.getAbsolutePath(), name + ".yml");
        if (!userFile.exists()) {
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(userFile),
                    "UTF-8"))) {
                userFile.createNewFile();

                bw.write("name: " + name);
                bw.newLine();
            } catch (IOException e) {
                plugin.getLogger().warning("Could not create or write to " + name + ".yml file!");
            }
        }
    }

    @Override
    public void addCooldown(String name, Cooldown cooldown) {
        if (!yamlStorage.exists()) yamlStorage.mkdir();
        if (!userStorage.exists()) userStorage.mkdir();

        File userFile = new File(userStorage.getAbsolutePath(), name + ".yml");
        if (userFile.exists()) {
            YamlConfiguration userConfig = YamlConfiguration.loadConfiguration(userFile);
            userConfig.set("cooldown." + cooldown.getMapName() + "." + cooldown.getType() , cooldown.getStartTime());

            try {
                userConfig.save(userFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else ParkourMakerPlugin.instance().getLogger().warning("Could not save info to " + name + " .yml file!");
    }

    @Override
    public void addEnteredMap(String name, String mapName) {
        if (!yamlStorage.exists()) yamlStorage.mkdir();
        if (!userStorage.exists()) userStorage.mkdir();

        File userFile = new File(userStorage.getAbsolutePath(), name + ".yml");
        if (userFile.exists()) {
            YamlConfiguration userConfig = YamlConfiguration.loadConfiguration(userFile);
            userConfig.set("map" , mapName);

            try {
                userConfig.save(userFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else ParkourMakerPlugin.instance().getLogger().warning("Could not save info to " + name + " .yml file!");
    }

    @Override
    public void updateCheckpoint(String name, int checkpoint) {
        if (!yamlStorage.exists()) yamlStorage.mkdir();
        if (!userStorage.exists()) userStorage.mkdir();

        File userFile = new File(userStorage.getAbsolutePath(), name + ".yml");
        if (userFile.exists()) {
            YamlConfiguration userConfig = YamlConfiguration.loadConfiguration(userFile);
            userConfig.set("current-checkpoint" , checkpoint);

            try {
                userConfig.save(userFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else ParkourMakerPlugin.instance().getLogger().warning("Could not save info to " + name + " .yml file!");
    }

    @Override
    public void setRunnerAttempts(String name, int attempts) {
        if (!yamlStorage.exists()) yamlStorage.mkdir();
        if (!userStorage.exists()) userStorage.mkdir();

        File userFile = new File(userStorage.getAbsolutePath(), name + ".yml");
        if (userFile.exists()) {
            YamlConfiguration userConfig = YamlConfiguration.loadConfiguration(userFile);
            userConfig.set("attempts-left" , attempts);

            try {
                userConfig.save(userFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else ParkourMakerPlugin.instance().getLogger().warning("Could not save info to " + name + " .yml file!");
    }

    @Override
    public void setRunTimestamps(String name, RunTime runTime) {
        if (!yamlStorage.exists()) yamlStorage.mkdir();
        if (!userStorage.exists()) userStorage.mkdir();

        File userFile = new File(userStorage.getAbsolutePath(), name + ".yml");
        if (userFile.exists()) {
            YamlConfiguration userConfig = YamlConfiguration.loadConfiguration(userFile);

            if (runTime == null) userConfig.set("run-timestamp", null);
            else {
                userConfig.set("run-timestamp.start" , runTime.getStarTime());
                userConfig.set("run-timestamp.stop" , runTime.getStopTime());
                runTime.getBreaks().forEach(aBreak -> {
                    userConfig.set("run-timestamp.break." + aBreak.getId() + ".pause", aBreak.getPauseTime());
                    userConfig.set("run-timestamp.break." + aBreak.getId() + ".continue", aBreak.getContinueTime());
                });
            }
            try {
                userConfig.save(userFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else ParkourMakerPlugin.instance().getLogger().warning("Could not save info to " + name + " .yml file!");
    }

    @Override
    public void saveBestRunTime(String name, String mapName, String time) {
        if (!yamlStorage.exists()) yamlStorage.mkdir();
        if (!userStorage.exists()) userStorage.mkdir();

        File userFile = new File(userStorage.getAbsolutePath(), name + ".yml");
        if (userFile.exists()) {
            YamlConfiguration userConfig = YamlConfiguration.loadConfiguration(userFile);
            userConfig.set("best-time." + mapName , time);

            try {
                userConfig.save(userFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else ParkourMakerPlugin.instance().getLogger().warning("Could not save info to " + name + " .yml file!");
    }

    private String loadUsers() {
        File[] users = userStorage.listFiles();

        int amountOfFiles = 0;
        int amountOfLoadedUsers = 0;

        for (File file : users) {
            if (file.isDirectory()) continue;
            if (file.isFile()) {
                amountOfFiles++;
                Runner runner = constructRunnerFromStorage(file.getName());
                if (runner == null) continue;
                ParkourMakerPlugin.instance().getRunnerHandler().addFromStorageRunner(runner);
                amountOfLoadedUsers++;
            }
        }
        return amountOfLoadedUsers + "/" + amountOfFiles;
    }

    @Override
    public Runner constructRunnerFromStorage(String fileName) {
        if (!yamlStorage.exists()) yamlStorage.mkdir();
        if (!userStorage.exists()) userStorage.mkdir();

        File userFile = new File(userStorage.getAbsolutePath(), fileName);
        if (userFile.exists()) {
            YamlConfiguration userConfig = YamlConfiguration.loadConfiguration(userFile);

            String name = userConfig.getString("name");
            String enteredMap = userConfig.getString("map");
            int checkpoint = userConfig.getInt("current-checkpoint");
            int attempts = userConfig.getInt("attempts-left");

            List<Cooldown> cooldowns = new ArrayList<>();
            ConfigurationSection cdSection = userConfig.getConfigurationSection("cooldown");
            if (cdSection != null) {
                cdSection.getKeys(false).forEach(cdMap -> {
                    cdSection.getConfigurationSection(cdMap).getKeys(false).forEach(cdType -> {
                        long timestamp = cdSection.getLong(cdMap + "." + cdType);
                        cooldowns.add(new Cooldown(cdMap, CooldownType.valueOf(cdType.toUpperCase()), timestamp));
                    });
                });
            }

            RunTime runTime = null;
            ConfigurationSection rtSection = userConfig.getConfigurationSection("run-timestamp");
            if (rtSection != null) {
                long startTime = rtSection.getLong("start");
                long stopTime = rtSection.getLong("stop");

                List<TimeBreak> breaks = new ArrayList<>();
                ConfigurationSection bSection = rtSection.getConfigurationSection("break");
                if (bSection != null) {
                    bSection.getKeys(false).forEach(aBreak -> {
                        long pause = bSection.getLong(aBreak + ".pause");
                        long aContinue = bSection.getLong(aBreak + ".continue");
                        breaks.add(new TimeBreak(Integer.parseInt(aBreak), pause, aContinue));
                    });
                }
                runTime = new RunTime(name, startTime, stopTime, breaks);
            }
            if (runTime == null) runTime = new RunTime(name);
            Runner runner = new Runner(name, enteredMap, checkpoint, attempts, cooldowns ,runTime);

            runner.getAllCooldowns().forEach(cooldown -> cooldown.setOwningRunner(name));
            runner.getRunTime().getBreaks().forEach(aBreak -> aBreak.setOwningTime(runner.getRunTime()));
            return runner;
        } else {
            ParkourMakerPlugin.instance().getLogger().warning("Could not load " + fileName + " file!");
            return null;
        }
    }

    @Override
    public void deleteCooldownForAll(String name) {
        if (!yamlStorage.exists()) yamlStorage.mkdir();
        if (!userStorage.exists()) userStorage.mkdir();

        File[] users = userStorage.listFiles();
        for (File file : users) {
            if (file.isDirectory()) continue;
            if (file.isFile()) {
                YamlConfiguration userConfig = YamlConfiguration.loadConfiguration(file);
                userConfig.set("cooldown." + name, null);

                try {
                    userConfig.save(file);
                } catch (IOException e) {
                    ParkourMakerPlugin.instance().getLogger().warning("Could not save info to " + name + " .yml file!");
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public String getBestTime(String playerName, String mapName) {
        if (!yamlStorage.exists()) yamlStorage.mkdir();
        if (!userStorage.exists()) userStorage.mkdir();

        File userFile = new File(userStorage.getAbsolutePath(), playerName + ".yml");
        if (userFile.exists()) {
            YamlConfiguration userConfig = YamlConfiguration.loadConfiguration(userFile);
            return userConfig.getString("best-time." + mapName);
        } else {
            ParkourMakerPlugin.instance().getLogger().warning("Could not read " + playerName + ".yml file!");
            return null;
        }
    }

    @Override
    public Map<String,String> getEveryoneBestTimes(String mapName) {
        if (!yamlStorage.exists()) yamlStorage.mkdir();
        if (!userStorage.exists()) userStorage.mkdir();

        Map<String,String> leaderboard = new HashMap<>();
        File[] users = userStorage.listFiles();
        for (File file : users) {
            if (file.isDirectory()) continue;
            if (file.isFile()) {
                YamlConfiguration userConfig = YamlConfiguration.loadConfiguration(file);
                String name = userConfig.getString("name");
                String bestTime = userConfig.getString("best-time." + mapName);
                leaderboard.put(name, bestTime);
            }
        }

        return leaderboard;
    }

    @Override
    public Map<String,String> getAllBestTimes(String playerName) {
        if (!yamlStorage.exists()) yamlStorage.mkdir();
        if (!userStorage.exists()) userStorage.mkdir();

        File userFile = new File(userStorage.getAbsolutePath(), playerName + ".yml");
        if (userFile.exists()) {
            YamlConfiguration userConfig = YamlConfiguration.loadConfiguration(userFile);
            Map<String,String> leaderboard = new HashMap<>();

            ConfigurationSection btSection = userConfig.getConfigurationSection("best-time");
            if (btSection != null) {
                btSection.getKeys(false).forEach(mapName -> {
                    String bestTime = btSection.getString(mapName);
                    leaderboard.put(mapName, bestTime);
                });
            }

            return leaderboard;
        } else {
            ParkourMakerPlugin.instance().getLogger().warning("Could not read " + playerName + ".yml file!");
            return null;
        }
    }

    @Override
    public void onReload() {
        String loadedMaps = loadMaps();
        plugin.getLogger().info("Successfully reloaded " + loadedMaps + " parkour maps.");
    }

}