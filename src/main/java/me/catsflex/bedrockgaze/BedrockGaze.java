package me.catsflex.bedrockgaze;

import me.catsflex.bedrockgaze.commands.BGCommand;
import me.catsflex.bedrockgaze.runnable.GazeTask;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class BedrockGaze extends JavaPlugin {

	private GazeTask _task;
	
	@Override
	public void onEnable() {
		
		// 1. Load/update config
		getLogger().info("Loading config...");
		saveDefaultConfig();
		getConfig().options().copyDefaults(true);
		saveConfig();
		getLogger().info("Config loaded!");
		
		// 2. Initializing tasks
		getLogger().info("Initializing tasks...");
		_task = new GazeTask(this);
		_task.runTaskTimer(this, 0L, 1L);
		getServer().getPluginManager().registerEvents(_task, this);
		getLogger().info("Tasks initialized!");
		
		// 3. Registering commands
		getLogger().info("Registering commands...");
		var bg = Objects.requireNonNull(getCommand("bg"), "Command 'bg' not found in plugin.yml!");
		var bgCmd = new BGCommand(_task, this);
		bg.setExecutor(bgCmd);
		bg.setTabCompleter(bgCmd);
		getLogger().info("Commands initialized!");
		
		getLogger().info("Enabled!");
	}

	@Override
	public void onDisable() {
		
		// 1. Cancelling tasks
		getLogger().info("Cancelling tasks...");
		if (_task != null) {
			_task.cancel();
		}
		getLogger().info("Tasks cancelled!");
		
		getLogger().info("Disabled!");
	}
}
