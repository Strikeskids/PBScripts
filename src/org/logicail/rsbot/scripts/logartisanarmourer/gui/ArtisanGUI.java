package org.logicail.rsbot.scripts.logartisanarmourer.gui;

import org.logicail.rsbot.scripts.framework.LogicailGui;
import org.logicail.rsbot.scripts.framework.tasks.Task;
import org.logicail.rsbot.scripts.logartisanarmourer.LogArtisanWorkshop;
import org.logicail.rsbot.scripts.logartisanarmourer.LogArtisanWorkshopOptions;
import org.logicail.rsbot.scripts.logartisanarmourer.wrapper.IngotGrade;
import org.logicail.rsbot.scripts.logartisanarmourer.wrapper.IngotType;
import org.logicail.rsbot.scripts.logartisanarmourer.wrapper.Mode;
import org.logicail.rsbot.util.ErrorDialog;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 11/11/12
 * Time: 12:20
 */
public class ArtisanGUI extends LogicailGui<LogArtisanWorkshop> {
	// Burial armour
	private final JComboBox<IngotGrade> burialIngotGrade = new JComboBox<IngotGrade>(new IngotGrade[]{IngotGrade.ONE, IngotGrade.TWO, IngotGrade.THREE});
	private final JCheckBox burialRespectPipes = new JCheckBox("Repair pipes");
	private final JCheckBox followInstructions = new JCheckBox("Follow instructions (XP/H often quicker when unticked but slightly move expensive)", true);
	private final JComboBox<IngotType> burialIngotType = new JComboBox<IngotType>(new IngotType[]{IngotType.IRON, IngotType.STEEL, IngotType.MITHRIL, IngotType.ADAMANT, IngotType.RUNE});
	// Track
	private final JComboBox<IngotType> trackIngotType = new JComboBox<IngotType>(new IngotType[]{IngotType.BRONZE, IngotType.IRON, IngotType.STEEL});
	// Sword
	private final JComboBox<IngotType> swordIngotType = new JComboBox<IngotType>(new IngotType[]{IngotType.IRON, IngotType.STEEL, IngotType.MITHRIL, IngotType.ADAMANT, IngotType.RUNE});
	private final JCheckBox swordRespectPipes = new JCheckBox("Repair pipes");
	private boolean startPressed;
	private JComponent burialArmourTab;
	/*private JComponent getModeTab() {
	    final JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(new EmptyBorder(5, 5, 5, 5));
		final GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.NORTH;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.weighty = 1;
		constraints.weightx = 1;
		{
			final JPanel innerPanel = new JPanel(new BorderLayout());
			innerPanel.setBorder(new CompoundBorder(new TitledBorder("Mode"), new EmptyBorder(5, 5, 5, 5)));
			innerPanel.add(comboBoxMode);
			panel.add(innerPanel, constraints);
		}

		return panel;
	}*/
	private JComponent swordsTab;
	private JComponent trackTab;
	// Variable declaration
	private JTabbedPane tabbedPane;
	//private JComponent modeTab;
	// Mode
	//private JComboBox<Mode> comboBoxMode;
	private JCheckBox respectKill;
	private JButton startButton;

	public ArtisanGUI() {
		initComponents();

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setMinimumSize(new Dimension(500, 360));

		final JPanel contentPane = (JPanel) getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));

		contentPane.add(getTopPanel(), BorderLayout.NORTH);
		contentPane.add(getCenterPanel(), BorderLayout.CENTER);
		contentPane.add(getBottomPanel(), BorderLayout.SOUTH);

		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private JComponent getBottomPanel() {
		final JPanel inner = new JPanel(new GridBagLayout());
		final GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		gbc.gridx = 0;
		gbc.gridy = 0;

		inner.add(startButton, gbc);

		return inner;
	}

	private JComponent getCenterPanel() {
		final JPanel inner = new JPanel(new BorderLayout());
		inner.setBorder(new EmptyBorder(10, 0, 10, 0));

		//tabbedPane.addTab("General", modeTab);
		tabbedPane.addTab("Burial Armour", burialArmourTab);
		tabbedPane.addTab("Ceremonial Swords", swordsTab);
		tabbedPane.addTab("Track room", trackTab);

		inner.add(tabbedPane, BorderLayout.CENTER);
		return inner;
	}

	private JComponent getTopPanel() {
		final JPanel inner = new JPanel(new BorderLayout());
		inner.setBorder(new CompoundBorder(new EtchedBorder(), new EmptyBorder(5, 5, 5, 5)));

		final JLabel title = new JLabel(getTitle(), SwingConstants.CENTER);
		title.setFont(new Font("Tahoma", Font.BOLD, 24));

		inner.add(title, BorderLayout.CENTER);

		return inner;
	}

	private void initComponents() {
//		addWindowListener(new WindowAdapter() {
//			@Override
//			public void windowClosed(WindowEvent e) {
//				if (!startPressed) {
//					script.getController().stop();
//				}
//			}
//		});

		// Bottom
		startButton = new JButton("Start Script");
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startButtonActionPerformed();
			}
		});

		// Mode
		/*comboBoxMode = new JComboBox<>(Mode.values());
		comboBoxMode.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				LogArtisanWorkshop.mode = (Mode) comboBoxMode.getSelectedItem();
			}
		});*/

		// Respect
		respectKill = new JCheckBox("Kill Ancestors");
		//respectKill.setEnabled(false);

		tabbedPane = new JTabbedPane();
		//modeTab = getModeTab();
		burialArmourTab = getBurialArmourTab();
		trackTab = getTrackTab();
		swordsTab = getSwordsTab();
	}

	JComponent getBurialArmourTab() {
		final JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(new EmptyBorder(5, 5, 5, 5));
		final GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.NORTH;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.weightx = 1;
		{
			final JPanel ingots = new JPanel(new GridBagLayout());
			ingots.setBorder(new CompoundBorder(new TitledBorder("Ingot"), new EmptyBorder(5, 5, 5, 5)));

			final GridBagConstraints innerConstraints = new GridBagConstraints();
			innerConstraints.fill = GridBagConstraints.HORIZONTAL;
			innerConstraints.anchor = GridBagConstraints.NORTH;
			{
				innerConstraints.gridx = 0;
				innerConstraints.gridy = 0;
				innerConstraints.weightx = 0.1;
				ingots.add(new JLabel("Type:"), innerConstraints);
				innerConstraints.gridx++;
				innerConstraints.weightx = 1;

				ingots.add(burialIngotType, innerConstraints);
			}
			innerConstraints.insets = new Insets(5, 0, 0, 0);
			{
				innerConstraints.gridx = 0;
				innerConstraints.gridy++;
				innerConstraints.weightx = 0.1;
				ingots.add(new JLabel("Grade:"), innerConstraints);
				innerConstraints.gridx++;
				innerConstraints.weightx = 1;
				ingots.add(burialIngotGrade, innerConstraints);
			}
			{
				innerConstraints.gridx = 0;
				innerConstraints.gridy++;
				innerConstraints.gridwidth = 2;
				innerConstraints.weightx = 1;
				ingots.add(followInstructions, innerConstraints);
			}
			panel.add(ingots, constraints);
		}

		constraints.weighty = 1;
		constraints.gridy++;
		{
			final JPanel respect = new JPanel(new GridBagLayout());
			respect.setBorder(new CompoundBorder(new TitledBorder("Respect"), new EmptyBorder(5, 5, 5, 5)));

			final GridBagConstraints innerConstraints = new GridBagConstraints();
			innerConstraints.fill = GridBagConstraints.HORIZONTAL;
			innerConstraints.anchor = GridBagConstraints.NORTH;
			innerConstraints.gridx = 0;
			innerConstraints.gridy = 0;
			innerConstraints.weightx = 1;


			respect.add(respectKill, innerConstraints);
			innerConstraints.gridy++;
			respect.add(burialRespectPipes, innerConstraints);
			panel.add(respect, constraints);
		}

		return panel;
	}

	JComponent getSwordsTab() {
		final JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(new EmptyBorder(5, 5, 5, 5));
		final GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.NORTH;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.weightx = 1;
		{
			final JPanel ingots = new JPanel(new GridBagLayout());
			ingots.setBorder(new CompoundBorder(new TitledBorder("Ingot"), new EmptyBorder(5, 5, 5, 5)));

			final GridBagConstraints innerConstraints = new GridBagConstraints();
			innerConstraints.fill = GridBagConstraints.HORIZONTAL;
			innerConstraints.anchor = GridBagConstraints.NORTH;
			innerConstraints.gridx = 0;
			innerConstraints.gridy = 0;
			innerConstraints.weightx = 0.1;
			{
				ingots.add(new JLabel("Type:"), innerConstraints);
				innerConstraints.gridx++;
				innerConstraints.weightx = 1;
				ingots.add(swordIngotType, innerConstraints);
			}
			panel.add(ingots, constraints);
		}

		constraints.weighty = 1;
		constraints.gridy++;
		{
			final JPanel respect = new JPanel(new GridBagLayout());
			respect.setBorder(new CompoundBorder(new TitledBorder("Respect"), new EmptyBorder(5, 5, 5, 5)));

			final GridBagConstraints innerConstraints = new GridBagConstraints();
			innerConstraints.fill = GridBagConstraints.HORIZONTAL;
			innerConstraints.anchor = GridBagConstraints.NORTH;
			innerConstraints.gridx = 0;
			innerConstraints.gridy = 0;
			innerConstraints.weightx = 1;

			/*respect.add(respectKill, innerConstraints);
			innerConstraints.gridy++;*/
			respect.add(swordRespectPipes, innerConstraints);
			panel.add(respect, constraints);
		}

		return panel;
	}

	JComponent getTrackTab() {
		final JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(new EmptyBorder(5, 5, 5, 5));
		final GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.NORTH;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.weightx = 1;
		{
			final JPanel ingots = new JPanel(new GridBagLayout());
			ingots.setBorder(new CompoundBorder(new TitledBorder("Ingot"), new EmptyBorder(5, 5, 5, 5)));

			final GridBagConstraints innerConstraints = new GridBagConstraints();
			innerConstraints.fill = GridBagConstraints.HORIZONTAL;
			innerConstraints.anchor = GridBagConstraints.NORTH;
			innerConstraints.gridx = 0;
			innerConstraints.gridy = 0;
			innerConstraints.weightx = 0.1;
			constraints.weighty = 1;
			{
				ingots.add(new JLabel("Type:"), innerConstraints);
				innerConstraints.gridx++;
				innerConstraints.weightx = 1;

				ingots.add(trackIngotType, innerConstraints);
			}
			panel.add(ingots, constraints);
		}


		return panel;
	}

	private void startButtonActionPerformed() {
		startPressed = true;
		final LogArtisanWorkshopOptions options = script.options;
		options.status = "Setup finished";

		//Context.get().getScriptHandler().log.info("Mode: " + tabbedPane.getTitleAt(tabbedPane.getSelectedIndex()));
		String s = tabbedPane.getTitleAt(tabbedPane.getSelectedIndex());
		if (s.equals("Burial Armour")) {
			options.mode = Mode.BURIAL_ARMOUR;
			options.ingotType = (IngotType) burialIngotType.getSelectedItem();
			options.ingotGrade = (IngotGrade) burialIngotGrade.getSelectedItem();
			options.respectRepairPipes = burialRespectPipes.isSelected();
			options.respectAncestors = respectKill.isSelected();
			options.followInstructions = followInstructions.isSelected();
		} else if (s.equals("Ceremonial Swords")) {
			options.mode = Mode.CEREMONIAL_SWORDS;
			options.ingotType = (IngotType) swordIngotType.getSelectedItem();
			options.ingotGrade = IngotGrade.FOUR;
			options.respectRepairPipes = swordRespectPipes.isSelected();
		} else if (s.equals("Track room")) {
			options.mode = Mode.REPAIR_TRACK;
			options.ingotType = (IngotType) trackIngotType.getSelectedItem();
			script.getController().getExecutor().offer(new Task<LogArtisanWorkshop>(script) {
				@Override
				public void run() {
					if (!ctx.backpack.select().isEmpty()) {
						EventQueue.invokeLater(new Runnable() {
							@Override
							public void run() {
								try {
									new ErrorDialog("Backpack not empty", "It is recommended to start the script with an empty backpack, if the script messes up restart with an empty backpack");
								} catch (Exception ignored) {
								}
							}
						});
					}
				}
			});
		} else {
			script.getController().stop();
		}

		script.createTree();

		dispose();
	}
}
