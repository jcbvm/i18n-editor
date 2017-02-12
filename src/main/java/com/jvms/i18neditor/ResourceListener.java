package com.jvms.i18neditor;

import java.util.EventListener;

/**
 * Defines an object which listens for {@link ResourceEvent}s.
 * 
 * @author Jacob van Mourik
 */
public interface ResourceListener extends EventListener {

	/**
	 * Invoked when the target {@link Resource} of the listener has changed its data.
	 * 
	 * @param 	e the resource event.
	 */
	void resourceChanged(ResourceEvent e);
}
