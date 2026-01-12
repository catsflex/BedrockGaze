package me.catsflex.bedrockgaze.runnable;

import me.catsflex.bedrockgaze.BedrockGaze;
import me.catsflex.bedrockgaze.config.PluginConfiguration;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GazeTask extends BukkitRunnable implements Listener {

	private final BedrockGaze _plugin;
	private final Map<UUID, Location> _previousLocations = new HashMap<>();
	private boolean _enabled = false;
	
	public GazeTask(BedrockGaze plugin) {
		_plugin = plugin;
	}
	
	@Override
	public void run() {
		if (!_enabled) return;
		
		for (var player : _plugin.getServer().getOnlinePlayers()) {
			var uuid = player.getUniqueId();
			
			// If a player is in ignored gamemode
			if (PluginConfiguration.ignoredGamemodes.contains(player.getGameMode())) {
				
				// Make sure to ignore player's last block they looked at
				_previousLocations.remove(uuid);
				continue;
			}
			
			// Current target block & its location (ray casting)
			int distance = Math.min(player.getWorld().getViewDistance() * 16, PluginConfiguration.maxDistance);
			var currentBlock = player.getTargetBlockExact(distance, PluginConfiguration.fluidCollisionMode);
			var currentLocation = currentBlock != null ? currentBlock.getLocation() : null;
			
			// Previous target location
			var previousLocation = _previousLocations.get(uuid);
			
			// If a player is looking at another block
			if (previousLocation != null && !previousLocation.equals(currentLocation)) {
				var blockToChange = previousLocation.getBlock();
				
				// If previous target block was NOT in ignored blocks set
				if (!PluginConfiguration.ignoredMaterials.contains(blockToChange.getType())) {
					blockToChange.setType(PluginConfiguration.replacementMaterial);
				}
				
				_previousLocations.remove(uuid);
			}
			
			// Store the current block for next tick
			if (currentLocation != null) {
				_previousLocations.put(uuid, currentLocation);
			}
		}
	}
	
	// Remove player's data after they went offline
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		_previousLocations.remove(event.getPlayer().getUniqueId());
	}
	
	public void setEnabled(boolean value) {
		_enabled = value;
		if (!_enabled) {
			_previousLocations.clear();
		}
	}
	
	public boolean isEnabled() {
		return _enabled;
	}
}
