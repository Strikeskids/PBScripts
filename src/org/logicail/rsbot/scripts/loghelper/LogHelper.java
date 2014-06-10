package org.logicail.rsbot.scripts.loghelper;

import org.powerbot.script.PollingScript;
import org.powerbot.script.Script;
import org.powerbot.script.rt6.ClientContext;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 10/06/2014
 * Time: 00:47
 */
@Script.Manifest(name = "Logicail Helper", description = "...", properties = "hidden=true")
public class LogHelper extends PollingScript<ClientContext> {
	private TrayIcon trayIcon;

	public LogHelper() {
		getExecQueue(State.START).add(new Runnable() {
			@Override
			public void run() {
				try {
					SystemTray tray = SystemTray.getSystemTray();
					ImageIcon icon = new ImageIcon(downloadImage("http://www.logicail.co.uk/resources/sounds/icon.png"));
					Image image = icon.getImage();
					trayIcon = new TrayIcon(image, "Tray Demo");
					trayIcon.setImageAutoSize(true);
					trayIcon.setToolTip("System tray icon demo");
					tray.add(trayIcon);
					trayIcon.displayMessage("Hello, World", "notification demo", TrayIcon.MessageType.INFO);
				} catch (AWTException e) {
					e.printStackTrace();
				}
			}
		});

		getExecQueue(State.STOP).add(new Runnable() {
			@Override
			public void run() {
				if (trayIcon != null) {
					SystemTray.getSystemTray().remove(trayIcon);
				}
			}
		});

		getExecQueue(State.START).add(new Runnable() {
			@Override
			public void run() {
				try {
					final File file = download("http://www.logicail.co.uk/resources/sounds/Alarm02.wav", "Alarm02.wav");
					AudioInputStream audioIn = AudioSystem.getAudioInputStream(file);
					Clip clip = AudioSystem.getClip();
					clip.open(audioIn);
					clip.start();
				} catch (UnsupportedAudioFileException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (LineUnavailableException e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void poll() {

	}
}
