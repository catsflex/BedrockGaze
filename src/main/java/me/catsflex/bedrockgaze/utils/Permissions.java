package me.catsflex.bedrockgaze.utils;

public enum Permissions {

	ON("bedrockgaze.on"),
	OFF("bedrockgaze.off"),
	RELOAD("bedrockgaze.reload");
	
	private final String _permission;
	
	Permissions(String permission) {
		_permission = permission;
	}
	
	@Override
	public String toString() {
		return _permission;
	}
}
