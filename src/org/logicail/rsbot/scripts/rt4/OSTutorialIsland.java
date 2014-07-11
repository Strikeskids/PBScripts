package org.logicail.rsbot.scripts.rt4;

import org.logicail.rsbot.scripts.framework.GraphScript;
import org.logicail.rsbot.scripts.framework.context.rt4.IClientContext;
import org.logicail.rsbot.scripts.rt4.stages.*;
import org.powerbot.script.PaintListener;
import org.powerbot.script.Script;

import java.awt.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 10/07/2014
 * Time: 11:39
 */
@Script.Manifest(name = "OS Tutorial Island", description = "Completes tutorial island", properties = "topic=1198289;client=4")
public class OSTutorialIsland extends GraphScript<IClientContext> implements PaintListener {
	private final BasicStroke mouseStroke = new BasicStroke(2f);

	public static int[] BOUNDS_DOOR_EW = {96, 128, -240, 0, 0, 120};
	public static int[] BOUNDS_DOOR_E = {0, 32, -240, 0, 0, 120};
	public static int[] BOUNDS_DOOR_N = {0, 120, -240, 0, 0, 32};
	public static int[] BOUNDS_DOOR_S = {0, 120, -240, 0, 108, 108 + BOUNDS_DOOR_N[5]};
	public static int[] BOUNDS_CHAIN_GATE_EW = {115, 140, -210, 0, 6, 140};
	public static int[] BOUNDS_CHAIN_GATE_NS = {0, 32, -210, 0, 6, 125};

	public AtomicReference<String> task = new AtomicReference<String>("");

//	public static AtomicReference<Interactive> interactive = new AtomicReference<Interactive>();

	private long lastSet = 0;

	@Override
	public void repaint(Graphics graphics) {
		Graphics2D g2d = (Graphics2D) graphics;

		graphics.drawString(getName(), 25, 125);
		final Action<IClientContext> action = current.get();
		if (action != null) {
			final String s = action.toString();
			if (!s.equals("")) {
				task.set(s);
			}
			lastSet = System.currentTimeMillis();
		}

		if (System.currentTimeMillis() > lastSet + 1000) {
			task.set("unknown");
		}

		graphics.drawString("Last action: " + task.get(), 25, 140);

//		final Interactive target = interactive.get();
//		if (target != null && target.valid()) {
//			if (target instanceof GameObject) {
//				try {
//					final BoundingModel model = BoundsUtil.getBoundingModel(target);
//					if (model != null) {
//						model.drawWireFrame(graphics);
//					} else {
//						target.draw(g2d);
//					}
//				} catch (IllegalAccessException e) {
//					e.printStackTrace();
//				} catch (NoSuchFieldException e) {
//					e.printStackTrace();
//				}
//			} else {
//				target.draw(graphics);
//			}
//		}

//		for (GameObject rocks : ctx.objects.select().name("Rocks")) {
//			final ObjectDefinition definition = ctx.definitions.get(rocks);
//			if (definition != null) {
//				final Point point = rocks.centerPoint();
//				graphics.drawString(String.valueOf(rocks.id()), point.x - 25, point.y - 15);
//				graphics.drawString("original: " + Arrays.toString(definition.recolorOriginal), point.x - 25, point.y);
//				graphics.drawString("target: " + Arrays.toString(definition.recolorTarget), point.x - 25, point.y + 15);
//			}
//		}

		g2d.setStroke(mouseStroke);
		g2d.setColor(ctx.input.getPressWhen() > System.currentTimeMillis() - 500 ? Color.RED : Color.WHITE);

		final Point mouse = ctx.input.getLocation();
		g2d.drawLine(mouse.x - 5, mouse.y - 5, mouse.x + 5, mouse.y + 5);
		g2d.drawLine(mouse.x - 5, mouse.y + 5, mouse.x + 5, mouse.y - 5);
	}


	@Override
	public void start() {
		LoggedIn loggedIn = new LoggedIn(ctx);

		final RandomCharacter randomCharacter = new RandomCharacter(ctx);
		loggedIn.add(randomCharacter);

		final Action.Validator randomCharacterValid = new Action.Validator() {
			@Override
			public boolean valid() {
				return !randomCharacter.valid();
			}
		};

		final RunescapeGuide runescapeGuide = new RunescapeGuide(ctx);
		runescapeGuide.push(randomCharacterValid);
		loggedIn.add(runescapeGuide);

		final SetRun run = new SetRun(ctx);
		run.push(randomCharacterValid);
		loggedIn.add(run);

		loggedIn.add(new SurvivalExpert(ctx));
		loggedIn.add(new MasterChef(ctx));
		loggedIn.add(new Emotes(ctx));
		loggedIn.add(new Running(ctx));
		loggedIn.add(new QuestGuide(ctx));
		loggedIn.add(new MiningInstructor(ctx));
		loggedIn.add(new CombatExpert(ctx));
		loggedIn.add(new Banker(ctx));
		loggedIn.add(new FinancialAdvisor(ctx));
		loggedIn.add(new BrotherBrace(ctx));
		loggedIn.add(new MagicInstructor(ctx));
		loggedIn.add(new Complete(ctx));

		chain.add(loggedIn);
	}
}
