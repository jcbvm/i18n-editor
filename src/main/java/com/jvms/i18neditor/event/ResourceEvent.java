package com.jvms.i18neditor.event;

import com.jvms.i18neditor.Resource;

public class ResourceEvent {
	private final Resource resource;
	
	public ResourceEvent(Resource resource) {
		this.resource = resource;
	}
	
	public Resource getResource() {
		return resource;
	}
}
