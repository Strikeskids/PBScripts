package org.logicail.rsbot.scripts.rt4.osvarphelper;

import javax.swing.*;
import java.awt.*;

public class FadeLabel extends JLabel {

	private float alpha;

	public FadeLabel() {
		setAlpha(1f);
	}

	public void setAlpha(float value) {
		if (alpha != value) {
			float old = alpha;
			alpha = value;
			firePropertyChange("alpha", old, alpha);
			repaint();
		}
	}

	public float getAlpha() {
		return alpha;
	}

	@Override
	public void paint(Graphics g) {
		// This is one of the few times I would directly override paint
		// This makes sure that the entire paint chain is now using
		// the alpha composite, including borders and child components
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getAlpha()));
		super.paint(g2d);
		g2d.dispose();
	}
}
