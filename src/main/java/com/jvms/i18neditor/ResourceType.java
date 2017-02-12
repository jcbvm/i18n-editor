package com.jvms.i18neditor;

/**
 * An enum describing the type of a {@link Resource}.
 * 
 * <p>A resource type additionally holds information about the filename representation.</p>
 * 
 * @author Jacob van Mourik
 */
public enum ResourceType {
	JSON(".json", false), 
	ES6(".js", false), 
	Properties(".properties", true);
	
	private final String extension;
	private final boolean embedLocale;
	
	/**
	 * Gets the file extension of the resource type.
	 * 
	 * @return 	the file extension.
	 */
	public String getExtension() {
		return extension;
	}
	
	/**
	 * Whether the locale should be embedded in the filename for this resource type.
	 * 
	 * @return 	whether the locale should be embedded in the filename.
	 */
	public boolean isEmbedLocale() {
		return embedLocale;
	}
	
	private ResourceType(String extension, boolean embedLocale) {
		this.extension = extension;
		this.embedLocale = embedLocale;
	}
}