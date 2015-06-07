package com.jvms.i18neditor.event;

import com.jvms.i18neditor.Resource;

public class ResourceEvent {
	private final Resource resource;
	private final String key;
	
	public ResourceEvent(Resource resource, String key) {
		this.resource = resource;
		this.key = key;
	}
	
	public Resource getResource() {
		return resource;
	}
	
	public String getAffectedKey() {
		return key;
	}
}
