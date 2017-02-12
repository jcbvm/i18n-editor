package com.jvms.i18neditor.editor;

import java.nio.file.Path;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.jvms.i18neditor.Resource;
import com.jvms.i18neditor.ResourceType;

/**
 * This class represents an editor project.
 * 
 * @author Jacob van Mourik
 */
public class EditorProject {
	private Path path;
	private String resourceName;
	private ResourceType resourceType;
	private List<Resource> resources = Lists.newLinkedList();
	private boolean minifyResources;
	
	public EditorProject(Path path) {
		this.path = path;
	}

	public Path getPath() {
		return path;
	}

	public void setPath(Path path) {
		this.path = path;
	}

	public ResourceType getResourceType() {
		return resourceType;
	}

	public void setResourceType(ResourceType resourceType) {
		this.resourceType = resourceType;
	}

	public List<Resource> getResources() {
		return ImmutableList.copyOf(resources);
	}

	public void setResources(List<Resource> resources) {
		this.resources = resources;
	}
	
	public void addResource(Resource resource) {
		resources.add(resource);
	}
	
	public boolean hasResources() {
		return !resources.isEmpty();
	}

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceFilename) {
		this.resourceName = resourceFilename;
	}

	public boolean isMinifyResources() {
		return minifyResources;
	}

	public void setMinifyResources(boolean minifyResources) {
		this.minifyResources = minifyResources;
	}
}
