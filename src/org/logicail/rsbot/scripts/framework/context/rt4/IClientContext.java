package org.logicail.rsbot.scripts.framework.context.rt4;

import com.logicail.accessors.DefinitionManager;
import org.powerbot.script.rt4.ClientContext;

import java.io.FileNotFoundException;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 10/07/2014
 * Time: 12:41
 */
public class IClientContext extends ClientContext {
	public IChat chat;
	public ICamera camera;
	public IMovement movement;
	public IInventory inventory;
	public DefinitionManager definitions;

	public IClientContext(ClientContext ctx) {
		super(ctx);
		chat = new IChat(ctx);
		camera = new ICamera(ctx);
		movement = new IMovement(ctx);
		inventory = new IInventory(ctx);
		try {
			this.definitions = new DefinitionManager(ctx);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			ctx.controller.stop();
		}
	}
}
