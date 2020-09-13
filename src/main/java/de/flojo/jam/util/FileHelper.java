package de.flojo.jam.util;

import java.awt.FileDialog;
import java.awt.Frame;
import java.nio.file.Paths;

public class FileHelper {

    private static final String TERRAIN_SUFFIX = ".terrain";

    private FileHelper() {
        throw new UnsupportedOperationException();
    }

    public static String askForTerrainPathLoad() {
        final FileDialog loadDialog = new FileDialog(new Frame(), "Load Terrain", FileDialog.LOAD);
        loadDialog.setFilenameFilter((d, n) -> n.endsWith(TERRAIN_SUFFIX));
        loadDialog.setAlwaysOnTop(true);
        loadDialog.setMultipleMode(false);
        loadDialog.setVisible(true);
        return loadDialog.getFile() == null ? null
                : Paths.get(loadDialog.getDirectory(), loadDialog.getFile()).toAbsolutePath().toString();
    }
    public static String askForTerrainPathSave(String terrainName) {
        final FileDialog saveDialog = new FileDialog(new Frame(), "Save Terrain", FileDialog.SAVE);
        saveDialog.setFilenameFilter((d, n) -> n.endsWith(TERRAIN_SUFFIX));
        saveDialog.setAlwaysOnTop(true);
        saveDialog.setMultipleMode(false);
        saveDialog.setFile(terrainName + TERRAIN_SUFFIX);
        saveDialog.setVisible(true);
        return saveDialog.getFile() == null ? null
                : Paths.get(saveDialog.getDirectory(), saveDialog.getFile()).toAbsolutePath().toString();
    }



}
