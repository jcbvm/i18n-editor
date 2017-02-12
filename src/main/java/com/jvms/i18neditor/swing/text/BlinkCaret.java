package com.jvms.i18neditor.swing.text;

import javax.swing.UIManager;
import javax.swing.text.DefaultCaret;

/**
 * This class extends the {@link DefaultCaret} with a default blink rate set.
 * 
 * @author Jacob van Mourik
 */
public class BlinkCaret extends DefaultCaret {
	private final static long serialVersionUID = -3365578081904749196L;
	public final static int DEFAULT_BLINK_RATE = 500;

	public BlinkCaret() {
		int blinkRate = DEFAULT_BLINK_RATE;
		Object o = UIManager.get("TextArea.caretBlinkRate");
		if (o != null && o instanceof Integer) {
			blinkRate = ((Integer) o).intValue();
		}
		setBlinkRate(blinkRate);		
	}
}
