package org.logicail.rsbot.scripts.framework.context;

import org.powerbot.script.AbstractQuery;
import org.powerbot.script.Filter;
import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.Component;
import org.powerbot.script.rt6.Widget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ComponentQuery extends AbstractQuery<ComponentQuery, Component, ClientContext> {
	public ComponentQuery(ClientContext ctx) {
		super(ctx);
	}

	@Override
	protected List<Component> get() {
		List<Component> components = new ArrayList<Component>();
		for (Widget w : ctx.widgets.select()) {
			for (Component c : w.components()) {
				if (c.components().length > 0) {
					components.addAll(Arrays.asList(c.components()));
				}
			}
			components.addAll(Arrays.asList(w.components()));
		}
		return components;
	}

	@Override
	protected ComponentQuery getThis() {
		return this;
	}

	public ComponentQuery inViewport() {
		return select(new Filter<Component>() {
			@Override
			public boolean accept(Component component) {
				return component.inViewport();
			}
		});
	}

	@Override
	public Component nil() {
		return ctx.widgets.component(-1, -1);
	}

	public ComponentQuery text(final String... text) {
		return select(new Filter<Component>() {
			@Override
			public boolean accept(Component component) {
				for (String s : text) {
					if (s.equalsIgnoreCase(component.text().trim())) {
						return true;
					}
				}
				return false;
			}
		});
	}

	public ComponentQuery textContains(final String... text) {
		return select(new Filter<Component>() {
			@Override
			public boolean accept(Component component) {
				for (String s : text) {
					if (component.text().toLowerCase().contains(s.toLowerCase())) {
						return true;
					}
				}
				return false;
			}
		});
	}

	public ComponentQuery texture(final int... texture) {
		return select(new Filter<Component>() {
			@Override
			public boolean accept(Component component) {
				for (int i : texture) {
					if (component.textureId() == i) {
						return true;
					}
				}
				return false;
			}
		});
	}

	public ComponentQuery visible() {
		return select(new Filter<Component>() {
			@Override
			public boolean accept(Component component) {
				return component.visible();
			}
		});
	}
}
