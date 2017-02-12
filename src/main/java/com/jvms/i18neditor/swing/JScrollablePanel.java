package com.jvms.i18neditor.swing;

import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.JPanel;
import javax.swing.Scrollable;

/**
 * This class extends a default {@link JPanel} with {@link Scrollable} capabilities.
 * 
 * @author Jacob van Mourik
 */
public class JScrollablePanel extends JPanel implements Scrollable {
	private final static long serialVersionUID = -7947570506111556197L;
	private final boolean scrollableTracksViewportWidth;
	private final boolean scrollableTracksViewportHeight;
	
	/**
	 * Constructs a {@link JScrollablePanel}.
	 * 
	 * @param 	scrollableTracksViewportWidth whether to force the width of this panel to match the width of the viewport.
	 * @param 	scrollableTracksViewportHeight whether to force the height of this panel to match the height of the viewport.
	 */
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