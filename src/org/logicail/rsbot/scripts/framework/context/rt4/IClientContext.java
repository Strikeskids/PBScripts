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
	public final IChat chat;
	public final ICamera camera;
	public final IMovement movement;
	public final IInventory inventory;
	public final DefinitionManager definitions;

	public IClientContext(IClientContext o) {
		super(o);

		System.out.println("Constructor IClientContext with IClientContext " + o.getClass().getCanonicalName());

		chat = new IChat(this);
		camera = new ICamera(this);
		movement = new IMovement(this);
		inventory = new IInventory(this);

		DefinitionManager manger = null;

		try {
			manger = new DefinitionManager(this);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			this.controller.stop();
		}

		this.definitions = manger;
	}

//	public IClientContext(ClientContext ctx) {
//		super(ctx);
//
//		System.out.println("Constructor IClientContext with " + ctx.getClass().getCanonicalName());
//
//		chat = new IChat(ctx);
//		camera = new ICamera(ctx);
//		movement = new IMovement(ctx);
//		inventory = new IInventory(ctx);
//		try {
//			this.definitions = new DefinitionManager(ctx);
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//			ctx.controller.stop();
//		}
//	}
}
