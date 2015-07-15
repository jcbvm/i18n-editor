package com.jvms.i18neditor.event;

import java.util.EventListener;

import com.jvms.i18neditor.Resource;

/**
 * Defines an object which listens for {@link ResourceEvent}s.
 * 
 * @author Jacob
 */
public interface ResourceListener extends EventListener {

	/**
	 * Invoked when the target {@link Resource} of the listener has changed its data.
	 * 
	 * @param 	e the resource event.
	 */
	void resourceChanged(ResourceEvent e);
}
