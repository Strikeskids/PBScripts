package org.logicail.scripts;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.FormLayout;

import javax.swing.*;
import java.awt.*;

public class BasicComponentFactoryExample extends JPanel {
	public static void main(String[] a) {
		JFrame f = new JFrame("Basic Component Factory Example");
		f.setMinimumSize(new Dimension(250, 200));
		f.setDefaultCloseOperation(2);
		f.add(new BasicComponentFactoryExample().createPanel());
		f.pack();
		f.setVisible(true);
	}

	public JPanel createPanel() {
		DefaultFormBuilder defaultFormBuilder =
				new DefaultFormBuilder(new FormLayout("p, 2dlu, p:g"));
		defaultFormBuilder.border(Borders.DIALOG);
		ValueModel longModel = new ValueHolder();
		ValueModel dateModel = new ValueHolder();
		ValueModel stringModel = new ValueHolder();
		defaultFormBuilder.append("Integer Field:",
				BasicComponentFactory.createIntegerField(longModel, 3));
		defaultFormBuilder.append("Long Field:",
				BasicComponentFactory.createLongField(longModel, 2));
		defaultFormBuilder.append("Date Field:",
				BasicComponentFactory.createDateField(dateModel));
		defaultFormBuilder.nextLine();
		defaultFormBuilder.append("Text Field:",
				BasicComponentFactory.createTextField(stringModel, true));
		defaultFormBuilder.append("Password Field:",
				BasicComponentFactory.createPasswordField(stringModel, false));
		defaultFormBuilder.append("Text Area:",
				BasicComponentFactory.createTextArea(stringModel));
		return defaultFormBuilder.getPanel();
	}
}
