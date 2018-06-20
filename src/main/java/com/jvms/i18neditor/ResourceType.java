package com.jvms.i18neditor;

/**
 * An enum describing the type of a {@link Resource}.
 * 
 * <p>A resource type additionally holds information about the filename representation.</p>
 * 
 * @author Jacob van Mourik
 */
public enum ResourceType {
	JSON(".json"), 
	ES6(".js"), 
	Properties(".properties");
	
	private final String extension;
	
	/**
	 * Gets the file extension of the resource type.
	 * 
	 * @return 	the file extension.
	 */
	public String getExtension() {
		return extension;
	}
	
	private ResourceType(String extension) {
		this.extension = extension;
	}
}