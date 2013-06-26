package org.logicail.scripts.logartisanarmourer;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.stage.Stage;
import org.logicail.framework.script.ActiveScript;
import org.logicail.framework.script.job.state.Node;
import org.logicail.framework.script.job.state.Tree;
import org.logicail.scripts.logartisanarmourer.gui.ArtisanArmourerInterface;
import org.powerbot.script.Manifest;
import org.powerbot.script.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: Michael
 * Date: 19/06/13
 * Time: 17:25
 */
@Manifest(
		name = "LogArtisanArmourer",
		description = "Cheap smithing xp at Artisans Workshop",
		version = 2.0,
		authors = {"Logicail"},
		instances = 99,
		website = "http://www.powerbot.org/community/topic/704413-logartisan-artisan-armourer-cheap-smither/")
public class LogArtisanArmourer extends ActiveScript {

	private Tree tree = new Tree(new Node[]{new MouseMover(ctx)});

	// TODO: Hook paused etc to disable container

	@Override
	public void start() {
		super.start();
		try {
			new JFXPanel();
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					new ArtisanArmourerInterface().start(new Stage());
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void suspend() {
		super.suspend();
	}

	@Override
	public int poll() {
		Node state = tree.state();
		if (state != null) {
			submit(state);
			state.join();
		}
		return Random.nextInt(200, 500);
	}
}
