package org.logicail.rsbot.scripts.framework.context.rt4;

import com.logicail.accessors.DefinitionManager;
import org.logicail.rsbot.scripts.framework.context.rt4.providers.*;
import org.powerbot.script.rt4.ClientContext;

import java.io.FileNotFoundException;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 10/07/2014
 * Time: 12:41
 */
public class IClientContext extends ClientContext {
	public final IChat chat;
	public final ICamera camera;
	public final IMovement movement;
	public final IInventory inventory;
	public final IGame game;
	public final DefinitionManager definitions;

	public IClientContext(ClientContext ctx) {
		super(ctx);

		chat = new IChat(this);
		camera = new ICamera(this);
		movement = new IMovement(this);
		inventory = new IInventory(this);
		game = new IGame(this);

		DefinitionManager manger = null;

		try {
			manger = new DefinitionManager(this);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			this.controller.stop();
		}

		this.definitions = manger;
	}
}
