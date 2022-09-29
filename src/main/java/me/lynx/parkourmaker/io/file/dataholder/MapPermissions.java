package me.lynx.parkourmaker.io.file.dataholder;

import me.lynx.parkourmaker.ParkourMakerPlugin;
import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class MapPermissions {

    private Set<Permission> maps;

    public MapPermissions() {
        maps = new HashSet<>();
    }

    public void addNewMap(String mapName) {
        Permission permission = new Permission("parkour-maker.join." + mapName);
        permission.setDefault(PermissionDefault.FALSE);
        maps.add(permission);

        if (Bukkit.getPluginManager().getPermission(permission.getName()) == null) {
            permission.addParent("parkour-maker.join.*", true);
            Bukkit.getPluginManager().addPermission(permission);
        }
    }

    public void removeMap(String mapName) {
        Permission permission = Bukkit.getPluginManager().getPermission("parkour-maker.join." + mapName);
        if (permission != null) {
            maps.removeIf(perm -> perm.getName().equals(permission.getName()));
            Bukkit.getPluginManager().removePermission(permission);
        }
    }

    public void loadPermissions() {
        ParkourMakerPlugin.instance().getMapHandler().getAllMapNames().forEach(this::addNewMap);

        Set<String> mapNames = maps.stream()
            .map(Permission::getName)
            .map(map -> map.replaceAll("parkour-maker\\.join\\.", ""))
            .collect(Collectors.toSet());
        ParkourMakerPlugin.instance().getLogger().info("Permissions for " +
            mapNames.toString().replaceAll("\\[|\\]", "") + " map" +
            (maps.size() == 1 ? "" : "s") + " generated.");
    }

}