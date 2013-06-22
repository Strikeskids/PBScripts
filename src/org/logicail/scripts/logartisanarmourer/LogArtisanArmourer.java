package org.logicail.scripts.logartisanarmourer;

import org.logicail.framework.gui.GuiScript;
import org.logicail.framework.node.Job;
import org.logicail.framework.node.JobContainer;
import org.powerbot.script.Manifest;
import org.powerbot.script.PollingScript;
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
public class LogArtisanArmourer extends PollingScript implements GuiScript {

	private final JobContainer jobContainer = new JobContainer();

	@Override
	public int poll() {
		Job job = jobContainer.poll();

		if (job != null) {
			job.execute();
			return job.delay();
		}

		return Random.nextInt(200, 500);
	}

	@Override
	public void createJobs() {
		//jobContainer.submit(JOB);
	}
}
