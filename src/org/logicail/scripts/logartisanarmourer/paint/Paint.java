package org.logicail.scripts.logartisanarmourer.paint;

import org.logicail.api.methods.LogicailMethodContext;
import org.logicail.api.methods.SimplePaint;
import org.logicail.scripts.logartisanarmourer.LogArtisanArmourer;
import org.logicail.scripts.logartisanarmourer.LogArtisanArmourerOptions;
import org.powerbot.event.MessageEvent;
import org.powerbot.event.MessageListener;
import org.powerbot.script.methods.Skills;
import org.powerbot.script.util.SkillData;
import org.powerbot.script.util.Timer;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 30/07/13
 * Time: 13:40
 */
public class Paint extends SimplePaint implements MessageListener {
	private LogArtisanArmourer script;
	private LogArtisanArmourerOptions options;
	private SkillData skillData;
	// Burial/Track
	private int ingots = 0;
	// Swords
	private int swordsSmithed;
	private int perfectSwords;
	private int brokenSwords;
	private int xpGained = 0;

	public Paint(LogicailMethodContext ctx) {
		super(ctx);
		this.script = (LogArtisanArmourer) ctx.script;
		this.options = script.options;
		skillData = new SkillData(ctx);

		contents.put("Time", "");
		contents.put("SPACE_1", "");
		contents.put("Level", "");
		contents.put("TTL", "");
		contents.put("XP Gained", "");
		contents.put("XP Hour", "");
		contents.put("SPACE_2", "");
	}

	@Override
	public int loop() {
		if (!ctx.game.isLoggedIn()) {
			skillData = new SkillData(ctx);
		}

		long totalRuntime = script.getTotalRuntime();

		contents.put("Time", Timer.format(totalRuntime));
		contents.put("Level", ctx.skills.getLevel(Skills.SMITHING) + " (" + skillData.level(Skills.SMITHING) + ")");
		contents.put("TTL", Timer.format(skillData.timeToLevel(SkillData.Rate.HOUR, Skills.SMITHING)));
		contents.put("XP Gained", String.format("%,d", xpGained));
		contents.put("XP Hour", String.format("%,d", skillData.experience(SkillData.Rate.HOUR, Skills.SMITHING)));

		switch (options.mode) {
			case BURIAL_ARMOUR:
				contents.put("Ingots Smithed", String.format("%,d (%,d/h)", ingots, (int) (ingots / (totalRuntime / 3600000f))));
				break;
			case CEREMONIAL_SWORDS:
				contents.put("Swords Smithed", String.format("%,d (%,d/h)", swordsSmithed, (int) (swordsSmithed / (totalRuntime / 3600000f))));
				contents.put("Perfect Swords", String.format("%,d (%,d/h)", perfectSwords, (int) (perfectSwords / (totalRuntime / 3600000f))));
				contents.put("Broken Swords", String.format("%,d (%,d/h)", brokenSwords, (int) (brokenSwords / (totalRuntime / 3600000f))));
				break;
		}

		return 250;
	}

	@Override
	public void messaged(MessageEvent e) {
		if (ctx.game.isLoggedIn()) {
			int before = ctx.skills.getExperience(Skills.SMITHING);
			switch (e.getId()) {
				case 0:
					switch (e.getMessage()) {
						case "You need plans to make a sword.":
							options.gotPlan = false;
							break;
						case "This sword is too cool to work. Ask Egil or Abel to rate it.":
						case "This sword has cooled and you can no longer work it.":
							options.finishedSword = true;
							options.gotPlan = false;
							break;
						case "You broke the sword! You'll need to get another set of plans from Egil.":
							brokenSwords++;
							options.finishedSword = true;
							options.gotPlan = false;
							break;
						case "This sword is now perfect and requires no more work.":
						case "This sword is perfect. Ask Egil or Abel to rate it.":
							options.finishedSword = true;
							options.gotPlan = false;
							break;
						case "For producing a perfect sword, you are awarded 120% of the normal experience. Excellent work!":
							perfectSwords++;
							swordsSmithed++;
							break;
						default:
							if (e.getMessage().startsWith("Your sword is awarded")) {
								swordsSmithed++;
								options.finishedSword = true;
								options.gotPlan = false;
							}
							break;
					}
					break;
				case 109:
					if (e.getMessage().contains("You make a")) {
						ingots++;
					}
					break;
			}
			xpGained += ctx.skills.getExperience(Skills.SMITHING) - before;
		}
	}
}
