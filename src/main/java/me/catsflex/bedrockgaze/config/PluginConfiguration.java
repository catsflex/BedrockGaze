package me.catsflex.bedrockgaze.config;

import me.catsflex.bedrockgaze.BedrockGaze;
import org.bukkit.FluidCollisionMode;
import org.bukkit.GameMode;
import org.bukkit.Material;

import java.util.LinkedHashSet;
import java.util.Set;

public class PluginConfiguration {
	
	// Config keys
	public static final String KEY_REPLACEMENT_MATERIAL = "replacement-material";
	public static final String KEY_IGNORED_MATERIALS = "ignored-materials";
	public static final String KEY_MAX_DISTANCE = "max-distance";
	public static final String KEY_FLUID_COLLISION_MODE = "fluid-collision-mode";
	public static final String KEY_IGNORED_GAMEMODES = "ignored-gamemodes";
	
	// Default values
	public static final Material DEF_REPLACEMENT_MATERIAL = Material.BEDROCK;
	public static final int DEF_MAX_DISTANCE = 64;
	public static final FluidCollisionMode DEF_FLUID_COLLISION_MODE = FluidCollisionMode.ALWAYS;
	
	// Current values
	public static Material replacementMaterial = DEF_REPLACEMENT_MATERIAL;
	public static final Set<Material> ignoredMaterials = new LinkedHashSet<>();
	public static int maxDistance = DEF_MAX_DISTANCE;
	public static FluidCollisionMode fluidCollisionMode = DEF_FLUID_COLLISION_MODE;
	public static final Set<GameMode> ignoredGamemodes = new LinkedHashSet<>();
	
	public static boolean load(BedrockGaze plugin) {
		plugin.reloadConfig();
		var cfg = plugin.getConfig();
		var logger = plugin.getLogger();
		boolean isSuccessful = true;
		
		// Main replacement material
		var rmString = cfg.getString(KEY_REPLACEMENT_MATERIAL);
		var rmMaterial = rmString != null ? Material.matchMaterial(rmString) : null;
		if (rmMaterial != null && rmMaterial.isBlock()) {
			replacementMaterial = rmMaterial;
		} else {
			replacementMaterial = DEF_REPLACEMENT_MATERIAL;
			isSuccessful = false;
			logger.warning(String.format(
				"'%s': Could not parse '%s', using '%s' instead.",
				KEY_REPLACEMENT_MATERIAL, rmString, DEF_REPLACEMENT_MATERIAL.getKey().toString()
			));
		}
		
		// Set of ignored materials
		ignoredMaterials.clear();
		for (var imString : cfg.getStringList(KEY_IGNORED_MATERIALS)) {
			var imMaterial = Material.matchMaterial(imString);
			if (imMaterial != null && imMaterial.isBlock()) {
				ignoredMaterials.add(imMaterial);
			} else {
				isSuccessful = false;
				logger.warning(String.format(
					"'%s': Could not parse '%s', skipping...",
					KEY_IGNORED_MATERIALS, imString
				));
			}
		}
		
		// Distance
		int md = cfg.getInt(KEY_MAX_DISTANCE);
		if (md > 0) {
			maxDistance = md;
		} else {
			isSuccessful = false;
			maxDistance = DEF_MAX_DISTANCE;
			logger.warning(String.format(
				"'%s': Invalid value, using '%d' instead.",
				KEY_MAX_DISTANCE, DEF_MAX_DISTANCE
			));
		}
		
		// Fluid collision mode
		var fcm = cfg.getString(KEY_FLUID_COLLISION_MODE);
		if (fcm == null) {
			fluidCollisionMode = DEF_FLUID_COLLISION_MODE;
			isSuccessful = false;
			logger.warning(String.format(
				"'%s': Value is missing, using '%s' instead.",
				KEY_FLUID_COLLISION_MODE, DEF_FLUID_COLLISION_MODE.name()
			));
		} else {
			try {
				fluidCollisionMode = FluidCollisionMode.valueOf(fcm.toUpperCase());
			} catch (IllegalArgumentException e) {
				fluidCollisionMode = DEF_FLUID_COLLISION_MODE;
				isSuccessful = false;
				logger.warning(String.format(
					"'%s': Could not parse '%s', using '%s' instead.",
					KEY_FLUID_COLLISION_MODE, fcm, DEF_FLUID_COLLISION_MODE.name()
				));
			}
		}
		
		// Set of ignored gamemodes
		ignoredGamemodes.clear();
		for (var igString : cfg.getStringList(KEY_IGNORED_GAMEMODES)) {
			try {
				ignoredGamemodes.add(GameMode.valueOf(igString.toUpperCase()));
			}
			catch (IllegalArgumentException e) {
				isSuccessful = false;
				logger.warning(String.format(
					"'%s': Could not parse '%s', skipping...",
					KEY_IGNORED_GAMEMODES, igString
				));
			}
		}
		
		save(plugin);
		return isSuccessful;
	}
	
	public static void save(BedrockGaze plugin) {
		var cfg = plugin.getConfig();
		cfg.set(KEY_REPLACEMENT_MATERIAL, replacementMaterial.getKey().toString());
		cfg.set(KEY_IGNORED_MATERIALS, ignoredMaterials.stream().map(m -> m.getKey().toString()).toList());
		cfg.set(KEY_MAX_DISTANCE, maxDistance);
		cfg.set(KEY_FLUID_COLLISION_MODE, fluidCollisionMode.name());
		cfg.set(KEY_IGNORED_GAMEMODES, ignoredGamemodes.stream().map(GameMode::name).toList());
		plugin.saveConfig();
	}
}
