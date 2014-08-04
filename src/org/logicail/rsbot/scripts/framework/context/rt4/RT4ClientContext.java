package org.logicail.rsbot.scripts.framework.context.rt4;

import com.logicail.accessors.RT4DefinitionManager;
import org.logicail.rsbot.scripts.framework.context.rt4.providers.*;
import org.powerbot.script.rt4.ClientContext;

import java.io.FileNotFoundException;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 10/07/2014
 * Time: 12:41
 */
public class RT4ClientContext extends ClientContext {
	public final IChat chat;
	public final ICamera camera;
	public final IMovement movement;
	public final IInventory inventory;
	public final IGame game;
	public final RT4DefinitionManager definitions;

	public RT4ClientContext(ClientContext ctx) {
		super(ctx);

		chat = new IChat(this);
		camera = new ICamera(this);
		movement = new IMovement(this);
		inventory = new IInventory(this);
		game = new IGame(this);

		RT4DefinitionManager manger = null;

		try {
			manger = new RT4DefinitionManager(this);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			this.controller.stop();
		}

		this.definitions = manger;
	}
}
