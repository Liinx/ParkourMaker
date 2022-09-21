package me.lynx.parkourmaker.model.map;

import me.lynx.parkourmaker.ParkourMakerPlugin;
import me.lynx.parkourmaker.io.file.ProcessedConfigValue;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MapHandler {

    private final Set<ParkourMap> parkourMaps;

    public MapHandler() {
        parkourMaps = new HashSet<>();
    }

    public void addMapFromStorage(ParkourMap map) {
       parkourMaps.add(map);
    }

    public void createMap(String name, String creator) {
        parkourMaps.add(new ParkourMap(name, creator));
        ProcessedConfigValue.of().mapPermissions().addNewMap(name);
        ParkourMakerPlugin.instance().getStorage().createNewMap(name, creator);
    }

    public Set<String> getAllMapNames() {
        return parkourMaps.stream().map(ParkourMap::getName).collect(Collectors.toSet());
    }

    public ParkourMap getEditedMap(String editor) { /* Not case-sensitive */
        Supplier<Stream<ParkourMap>> supplier = () -> parkourMaps.stream()
            .filter(map -> map.getEditors().contains(editor));
        if (supplier.get().findAny().isEmpty()) return null;
        return supplier.get().findFirst().get();
    }

    public ParkourMap getByName(String mapName) {
        Supplier<Stream<ParkourMap>> supplier = () -> parkourMaps.stream()
                .filter(map -> map.getName().equalsIgnoreCase(mapName));
        if (supplier.get().findAny().isEmpty()) return null;
        return supplier.get().findFirst().get();
    }

}