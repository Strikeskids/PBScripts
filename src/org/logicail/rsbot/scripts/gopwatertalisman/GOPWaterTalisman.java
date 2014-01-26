package org.logicail.rsbot.scripts.gopwatertalisman;

import org.logicail.rsbot.scripts.framework.LogicailScript;
import org.logicail.rsbot.scripts.framework.tasks.impl.AntiBan;
import org.logicail.rsbot.scripts.gopwatertalisman.tasks.BankingTask;
import org.logicail.rsbot.scripts.gopwatertalisman.tasks.ExchangeTask;
import org.logicail.rsbot.util.ErrorDialog;
import org.logicail.rsbot.util.LinkedProperties;
import org.powerbot.event.MessageEvent;
import org.powerbot.event.MessageListener;
import org.powerbot.script.Manifest;
import org.powerbot.script.util.GeItem;
import org.powerbot.script.util.Timer;

import java.awt.*;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 26/01/14
 * Time: 19:26
 */
@Manifest(name = "GOP Exchanger", description = "Exchanges tokens for water talismans", hidden = true)
public class GOPWaterTalisman extends LogicailScript<GOPWaterTalisman> implements MessageListener {
	public static final int ELRISS = 8032;
	public static final int TOKENS = 13650;
	public static final int DEPOSIT = 24995;
	public static final int WATER_TALISMAN = 1444;
	public AtomicInteger banked = new AtomicInteger();
	public AtomicInteger priceWaterTalisman = new AtomicInteger();

	public GOPWaterTalisman() {
		getController().getExecutor().offer(new Runnable() {
			@Override
			public void run() {
				priceWaterTalisman.set(GeItem.getPrice(WATER_TALISMAN));
			}
		});
	}

	@Override
	public LinkedProperties getPaintInfo() {
		final LinkedProperties properties = new LinkedProperties();

		final long runtime = getRuntime();
		final float time = runtime / 3600000f;
		final int banked = this.banked.get();
		final int profit = banked * priceWaterTalisman.get();

		properties.put("Time Running", Timer.format(runtime));
		properties.put("Water Talisman Price", priceWaterTalisman.get());
		properties.put("Profit", String.format("%,d (%,d/h)", profit, profit > 0 ? (int) (profit / time) : 0));
		properties.put("Banked", String.format("%,d (%,d/h)", banked, banked > 0 ? (int) (banked / time) : 0));

		return properties;
	}

	@Override
	public void start() {
		tree.add(new ExchangeTask(this));
		tree.add(new BankingTask(this));
		ctx.submit(new AntiBan<GOPWaterTalisman>(this));
	}

	@Override
	public void messaged(MessageEvent messageEvent) {
		if (messageEvent.getId() == 0 && messageEvent.getMessage().equals("You do not have enough tokens to buy that item.")) {
			try {
				final StringBuilder sb = new StringBuilder();
				final LinkedProperties properties = getPaintInfo();
				final Enumeration<Object> keys = properties.keys();
				while (keys.hasMoreElements()) {
					final Object element = keys.nextElement();
					sb.append(String.format("  %s: %s\n", element, properties.get(element)));
				}

				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						try {
							new ErrorDialog(getName(), "Stopped because out of tokens\n" + sb.toString());
						} catch (Exception exception) {
							exception.printStackTrace();
						}
					}
				});
			} finally {
				getController().stop();
			}
		}
	}
}
