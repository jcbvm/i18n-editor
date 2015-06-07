package com.jvms.i18neditor.event;

import java.util.EventListener;

public interface ResourceListener extends EventListener {

	void resourceChanged(ResourceEvent e);
}
