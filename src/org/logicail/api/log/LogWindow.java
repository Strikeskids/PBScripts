package org.logicail.api.log;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.Closeable;

/**
 * Created with IntelliJ IDEA.
 * User: Michael
 * Date: 09/07/13
 * Time: 21:18
 */
public class LogWindow extends JFrame implements WindowListener, ComponentListener {
	private final JTextArea textArea;
	private final JScrollPane pane;
	private int height;

	public LogWindow(String title, int height) {
		super(title);
		this.height = height;

		setUndecorated(true);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		Window window = getBotWindow();
		if (window != null) {
			for (WindowListener windowListener : window.getWindowListeners()) {
				if (windowListener instanceof LogWindow) {
					window.removeWindowListener(windowListener);
				}
			}
			for (ComponentListener windowListener : window.getComponentListeners()) {
				if (windowListener instanceof LogWindow) {
					window.removeComponentListener(windowListener);
				}
			}
			window.addWindowListener(this);
			window.addComponentListener(this);
			resetPosition();
		}
		textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setFont(new Font("Monospaces", Font.PLAIN, 10));
		DefaultCaret caret = (DefaultCaret) textArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		pane = new JScrollPane(textArea);
		getContentPane().add(pane);
		setVisible(true);
	}

	/**
	 * This method appends the data to the text area.
	 *
	 * @param data the Logging information data
	 */
	public void showInfo(String data) {
		textArea.append(data);
		this.getContentPane().validate();
	}

	public Window getBotWindow() {
		for (Window window : JFrame.getWindows()) {
			if (window instanceof JFrame && window instanceof Closeable) {
				return window;
			}
		}
		return null;
	}

	@Override
	public void componentResized(ComponentEvent e) {
		resetPosition();
	}

	@Override
	public void componentMoved(ComponentEvent e) {
		resetPosition();
	}

	@Override
	public void componentShown(ComponentEvent e) {
	}

	@Override
	public void componentHidden(ComponentEvent e) {
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
		setLogVisible(false);
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		setLogVisible(true);
	}

	@Override
	public void windowActivated(WindowEvent e) {
		setLogVisible(true);
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

	public void resetPosition() {
		// TODO: Get client frame
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				Window window = getBotWindow();
				setLocation(window.getX(), window.getY() + window.getHeight());
				setSize(window.getWidth(), height);
			}
		});
	}

	private void setLogVisible(final boolean visible) {
		if (isVisible() != visible) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					setVisible(visible);
				}
			});
		}
	}
}

