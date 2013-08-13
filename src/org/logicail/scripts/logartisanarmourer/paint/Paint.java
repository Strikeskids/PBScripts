package org.logicail.scripts.logartisanarmourer.paint;

import org.logicail.api.methods.LogicailMethodContext;
import org.logicail.api.methods.SimplePaint;
import org.logicail.scripts.logartisanarmourer.LogArtisanArmourer;
import org.logicail.scripts.logartisanarmourer.LogArtisanArmourerOptions;
import org.powerbot.script.methods.Skills;
import org.powerbot.script.util.SkillData;
import org.powerbot.script.util.Timer;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 30/07/13
 * Time: 13:40
 */
public class Paint extends SimplePaint {
	// Burial/Track
	public int ingots = 0;
	// Swords
	public int swordsSmithed;
	public int perfectSwords;
	public int brokenSwords;
	public int xpGained = 0;
	private LogArtisanArmourer script;
	private LogArtisanArmourerOptions options;
	private SkillData skillData = null;

	public Paint(LogicailMethodContext ctx) {
		super(ctx);
		this.script = (LogArtisanArmourer) ctx.script;
		this.options = script.options;

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
		if (skillData == null && ctx.game.isLoggedIn()) {
			skillData = new SkillData(ctx);
		}

		long totalRuntime = script.getTotalRuntime();

		contents.put("Time", Timer.format(totalRuntime));
		if (skillData != null) {
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
		}

		return 250;
	}
}
