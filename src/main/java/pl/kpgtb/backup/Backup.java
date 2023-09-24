package pl.kpgtb.backup;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public final class Backup extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();

        new Metrics(this, 19881);
        long delay = getConfig().getLong("backupsCooldown");
        delay = delay * 60 * 60 * 20;

        new BukkitRunnable() {
            @Override
            public void run() {
                createBackup();
            }
        }.runTaskTimer(this,1,delay);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void createBackup() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "Creating backup");
        File serverFile = new File(getDataFolder() + "/../../");
        new BukkitRunnable() {
            @Override
            public void run() {
                folderToZip(serverFile, Date.from(Instant.now()).toString().replace(":", " ") + ".zip");
            }
        }.runTaskAsynchronously(this);
    }

    private File folderToZip(File folder, String name) {
        try {
            File backupsFolder = new File(getDataFolder(), "backups");
            backupsFolder.mkdirs();

            purgeBackups(backupsFolder);

            File zipFile = new File(backupsFolder, name);
            zipFile.createNewFile();

            FileOutputStream fos = new FileOutputStream(zipFile);
            ZipOutputStream zos = new ZipOutputStream(fos);

            addFolderToZip("", folder, zos);

            zos.close();
            fos.close();

            Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "Created backup");
            return zipFile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void purgeBackups(File logDir){
        File[] logFiles = logDir.listFiles();
        long oldestDate = Long.MAX_VALUE;
        File oldestFile = null;
        if( logFiles != null && logFiles.length >= getConfig().getInt("maxAmountOfBackups")){
            for(File f: logFiles){
                if(f.lastModified() < oldestDate){
                    oldestDate = f.lastModified();
                    oldestFile = f;
                }
            }

            if(oldestFile != null){
                deleteFolder(oldestFile);
            }
        }
    }

    private void addFolderToZip(String parentPath, File folder, ZipOutputStream zos) throws IOException {
        if(folder.getName().equals("backups")) {
            return;
        }

        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                String path = parentPath + file.getName() + "/";
                ZipEntry zipEntry = new ZipEntry(path);
                zos.putNextEntry(zipEntry);
                addFolderToZip(path, file, zos);
                zos.closeEntry();
            } else {
                try {
                    ZipEntry zipEntry = new ZipEntry(parentPath + file.getName());
                    zos.putNextEntry(zipEntry);

                    FileInputStream fis = new FileInputStream(file);
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = fis.read(buffer)) > 0) {
                        zos.write(buffer, 0, length);
                    }

                    fis.close();
                    zos.closeEntry();
                } catch (Exception e) {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Skipped " + file.getName() + " due to OS protection");
                }
            }
        }
    }

    private void deleteFolder(File folder) {
        if(folder == null) {
            return;
        }
        if (folder.isDirectory()) {
            // recursively delete all files and subfolders
            for (File file : folder.listFiles()) {
                deleteFolder(file);
            }
        }

        // delete the folder itself
        folder.delete();
    }
}
