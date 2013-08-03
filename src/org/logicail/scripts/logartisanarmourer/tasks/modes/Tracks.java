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
	private final int rails;
	public SmithTrack smithTrack;
	private int basePlate;
	private int joint;
	private int tie;
	private int track40;
	private int track60;
	private int track80;
	private int track100;
	private int spikes;
	private int ingotId;

	public Tracks(LogicailMethodContext ctx) {
		super(ctx);

		LogArtisanArmourerOptions options = ((LogArtisanArmourer) ctx.script).options;

		rails = RAILS[options.ingotType.ordinal()];
		basePlate = BASE_PLATE[options.ingotType.ordinal()];
		spikes = SPIKES[options.ingotType.ordinal()];
		joint = JOINT[options.ingotType.ordinal()];
		tie = TIE[options.ingotType.ordinal()];
		track40 = TRACK_40[options.ingotType.ordinal()];
		track60 = TRACK_60[options.ingotType.ordinal()];
		track80 = TRACK_80[options.ingotType.ordinal()];
		track100 = TRACK_100[options.ingotType.ordinal()];

		switch (options.ingotType) {
			case BRONZE:
				ingotId = 20502;
			case IRON:
				ingotId = 20503;
			case STEEL:
				ingotId = 20504;
		}

		nodes.add(new TakeIngots(this));
		nodes.add(smithTrack = new SmithTrack(this));
		nodes.add(new LayTracks(ctx));
	}

	@Override
	public boolean branch() {
		return true;
	}

	public int getRails() {
		return rails;
	}

	public int getBasePlate() {
		return basePlate;
	}

	public int getSpikes() {
		return spikes;
	}

	public int getJoint() {
		return joint;
	}

	public int getTie() {
		return tie;
	}

	public int getTrack40() {
		return track40;
	}

	public int getTrack60() {
		return track60;
	}

	public int getTrack80() {
		return track80;
	}

	public int getTrack100() {
		return track100;
	}

	public int getIngotId() {
		return ingotId;
	}
}
