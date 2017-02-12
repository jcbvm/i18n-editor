package com.jvms.i18neditor.editor;

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.UIManager;

import com.jvms.i18neditor.util.Colors;

/**
 * This class represents a toggle icon for a translation tree cell.
 * 
 * @author Jacob van Mourik
 */
public class TranslationTreeToggleIcon implements Icon {
    private final static int SIZE = 10;
    private final ToggleIconType type;
    
    public enum ToggleIconType {
    	Collapsed, Expanded
    }
    
    public TranslationTreeToggleIcon(ToggleIconType type) {
        this.type = type;
    }
    
    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
    	g.setColor(UIManager.getColor("Tree.background"));
    	g.fillRect(x, y, SIZE, SIZE);
    	g.setColor(Colors.scale(UIManager.getColor("Panel.background"), .8f));
    	g.drawRect(x, y, SIZE, SIZE);
    	g.setColor(UIManager.getColor("Tree.foreground"));
    	g.drawLine(x + 2, y + SIZE/2, x + SIZE - 2, y + SIZE/2);
        if (type == ToggleIconType.Collapsed) {
        	g.drawLine(x + SIZE/2, y + 2, x + SIZE/2, y + SIZE - 2);
    	}
    }
    
    @Override
    public int getIconWidth() {
        return SIZE;
    }
    
    @Override
    public int getIconHeight() {
        return SIZE;
    }
}
