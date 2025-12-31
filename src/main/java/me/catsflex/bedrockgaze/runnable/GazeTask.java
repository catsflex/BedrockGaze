package me.catsflex.bedrockgaze.runnable;

import me.catsflex.bedrockgaze.BedrockGaze;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class GazeTask extends BukkitRunnable implements Listener {

	private final BedrockGaze _plugin;
	private final Map<UUID, Location> _previousLocations = new HashMap<>();
	private boolean _enabled = false;
	
	// From config
	private Material _replaceMaterial;
	private final Set<Material> _ignoredMaterials = new HashSet<>();
	private int _maxDistance;
	
	public GazeTask(BedrockGaze plugin) {
		_plugin = plugin;
		loadConfigData();
	}
	
	@Override
	public void run() {
		if (!_enabled) return;
		
		for (var player : _plugin.getServer().getOnlinePlayers()) {
			var uuid = player.getUniqueId();
			
			// Current target block & its location (raytracing)
			int distance = Math.min(player.getWorld().getViewDistance() * 16, _maxDistance);
			var currentBlock = player.getTargetBlockExact(distance, FluidCollisionMode.ALWAYS);
			var currentLocation = currentBlock != null ? currentBlock.getLocation() : null;
			
			// Previous target location
			var previousLocation = _previousLocations.get(uuid);
			
			// If a player is looking at another block
			if (previousLocation != null && !previousLocation.equals(currentLocation)) {
				var blockToChange = previousLocation.getBlock();
				
				// If previous target block was NOT in ignored blocks set
				if (!_ignoredMaterials.contains(blockToChange.getType())) {
					blockToChange.setType(_replaceMaterial);
				}
				
				_previousLocations.remove(uuid);
			}
			
			// Store the current block for next tick
			if (currentLocation != null) {
				_previousLocations.put(uuid, currentLocation);
			}
		}
	}
	
	// Remove the player's data after they went offline
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		_previousLocations.remove(event.getPlayer().getUniqueId());
	}
	
	// Config logic
	public void loadConfigData() {
		var cfg = _plugin.getConfig();
		
		// Default values
		var DEFAULT_BLOCK = Material.BEDROCK;
		var DEFAULT_MAX_DISTANCE = 64;
		
		// The block
		var replaceWith = cfg.getString("replace-with", DEFAULT_BLOCK.toString());
		var matched = Material.matchMaterial(replaceWith);
		if (matched != null && matched.isBlock()) {
			_replaceMaterial = matched;
		} else {
			_plugin.getLogger().warning("'" + replaceWith + "' is not a valid block for 'replace-with'! Using " + DEFAULT_BLOCK + " instead.");
			_replaceMaterial = DEFAULT_BLOCK;
		}
		
		// List of ignored blocks
		_ignoredMaterials.clear();
		var ignoredBlocks = cfg.getStringList("ignored-blocks");
		for (var ignored : ignoredBlocks) {
			var block = Material.matchMaterial(ignored);
			if (block != null && block.isBlock()) {
				_ignoredMaterials.add(block);
			} else {
				_plugin.getLogger().warning("'" + ignored + "' is not a valid block for 'ignored-blocks'! Skipping...");
			}
		}
		
		// Max distance
		var distance = cfg.getInt("max-distance", DEFAULT_MAX_DISTANCE);
		_maxDistance = distance <= 0 ? DEFAULT_MAX_DISTANCE : distance;
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
