package org.logicail.scripts.logartisanarmourer.tasks.modes;

import org.logicail.api.methods.LogicailMethodContext;
import org.logicail.framework.script.state.BranchOnce;
import org.logicail.scripts.logartisanarmourer.LogArtisanArmourer;
import org.logicail.scripts.logartisanarmourer.LogArtisanArmourerOptions;
import org.logicail.scripts.logartisanarmourer.tasks.track.LayTracks;
import org.logicail.scripts.logartisanarmourer.tasks.track.SmithTrack;
import org.logicail.scripts.logartisanarmourer.tasks.track.TakeIngots;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 31/07/13
 * Time: 12:04
 */
public class Tracks extends BranchOnce {
	public static final int[] RAILS = {20506, 20515, 20520};
	public static final int[] BASE_PLATE = {20507, 20516, 20521};
	public static final int[] TRACK_40 = {20511, 20525, 20529};
	public static final int[] SPIKES = {20508, 20517, 20522};
	public static final int[] TRACK_60 = {20512, 20526, 20530};
	public static final int[] JOINT = {20509, 20518, 20523};
	public static final int[] TRACK_80 = {20513, 20527, 20531};
	public static final int[] TIE = {20510, 20519, 20524};
	public static final int[] TRACK_100 = {20514, 20528, 20532};
	public SmithTrack smithTrack;
	LogArtisanArmourerOptions options;

	public Tracks(LogicailMethodContext ctx) {
		super(ctx);

		options = ((LogArtisanArmourer) ctx.script).options;

		nodes.add(new TakeIngots(this));
		nodes.add(smithTrack = new SmithTrack(this));
		nodes.add(new LayTracks(ctx));
	}

	@Override
	public boolean branch() {
		return true;
	}

	public int getRails() {
		return RAILS[options.ingotType.ordinal()];
	}

	public int getBasePlate() {
		return BASE_PLATE[options.ingotType.ordinal()];
	}

	public int getTrack40() {
		return TRACK_40[options.ingotType.ordinal()];
	}

	public int getSpikes() {
		return SPIKES[options.ingotType.ordinal()];
	}

	public int getTrack60() {
		return TRACK_60[options.ingotType.ordinal()];
	}

	public int getJoint() {
		return JOINT[options.ingotType.ordinal()];
	}

	public int getTrack80() {
		return TRACK_80[options.ingotType.ordinal()];
	}

	public int getTie() {
		return TIE[options.ingotType.ordinal()];
	}

	public int getTrack100() {
		return TRACK_100[options.ingotType.ordinal()];
	}

	public int getIngotId() {
		switch (options.ingotType) {
			case BRONZE:
				return 20502;
			case IRON:
				return 20503;
			case STEEL:
				return 20504;
		}

		return 20502;
	}
}
