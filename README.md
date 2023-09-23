# ares-backup
Minecraft backup plugin for the server in a zip fileyou can change the backup time in the config !


Backup Plugin (Ares-Backup)

Ares Backup is a plugin that creates backups of your Minecraft server in a zip format every 'x' hours.

The plugin is straightforward to configure and use. You can set a maximum number of backups and the time interval for each backup.

Simply navigate to:
plugins/Backup/config.yml :

 - maxAmountOfBackups: 5: This represents the maximum number of backups or zip files that the plugin can create. The plugin automatically deletes the oldest backups once this limit is reached.

 - backupsCooldown: 12 hours: This is the time interval between each backup. In this case, a backup will be created every 12 hours.
