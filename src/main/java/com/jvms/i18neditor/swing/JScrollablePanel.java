package com.jvms.i18neditor.swing;

import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.JPanel;
import javax.swing.Scrollable;

public class JScrollablePanel extends JPanel implements Scrollable {
	private static final long serialVersionUID = -7947570506111556197L;
	
	private final boolean scrollableTracksViewportWidth;
	private final boolean scrollableTracksViewportHeight;
	
	public JScrollablePanel(boolean scrollableTracksViewportWidth, boolean scrollableTracksViewportHeight) {
		super();
		this.scrollableTracksViewportWidth = scrollableTracksViewportWidth;
		this.scrollableTracksViewportHeight = scrollableTracksViewportHeight;
	}
    
    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }
    
    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 10;
    }
    
    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 100;
    }
    
    @Override
    public boolean getScrollableTracksViewportWidth() {
        return scrollableTracksViewportWidth;
    }
    
    @Override
    public boolean getScrollableTracksViewportHeight() {
        return scrollableTracksViewportHeight;
    }
}