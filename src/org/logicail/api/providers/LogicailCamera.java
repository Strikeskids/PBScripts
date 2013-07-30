package org.logicail.api.providers;

import org.logicail.api.methods.LogicailMethodContext;
import org.logicail.api.methods.LogicailMethodProvider;
import org.powerbot.script.lang.Locatable;
import org.powerbot.script.methods.Camera;
import org.powerbot.script.wrappers.Interactive;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 25/07/13
 * Time: 17:54
 */
public class LogicailCamera extends LogicailMethodProvider {
	private Camera camera;

	public LogicailCamera(LogicailMethodContext arg0) {
		super(arg0);
		camera = new Camera(arg0);
	}

	public void setYaw(char direction) {
		camera.setYaw(direction);
	}

	public void turnTo(org.powerbot.script.lang.Locatable locatable, int deviation) {
		camera.turnTo(locatable, deviation);
	}

	public int getAngleTo(int angle) {
		return camera.getAngleTo(angle);
	}

	public int getYaw() {
		return camera.getYaw();
	}

	public void setYaw(int yaw) {
		camera.setYaw(yaw);
	}

	/**
	 * Turn camera to locatable
	 *
	 * @param locatable
	 * @return if the locatable isOnScreen
	 */
	public boolean turnTo(Locatable locatable) {
		Interactive interactive = (Interactive) locatable;
		if (interactive.isOnScreen()) {
			return true;
		}

		camera.turnTo(locatable);
		sleep(200, 1000);

		return interactive.isOnScreen();
	}
}
