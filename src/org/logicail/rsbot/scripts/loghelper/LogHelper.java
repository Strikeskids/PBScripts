package org.logicail.rsbot.scripts.loghelper;

import org.powerbot.script.PollingScript;
import org.powerbot.script.Script;
import org.powerbot.script.rt6.ClientContext;

import javax.sound.sampled.*;
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
	@Override
	public void start() {
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

	@Override
	public void poll() {

	}
}
