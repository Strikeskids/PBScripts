package org.logicail.rsbot.scripts.logartisanarmourer;


import org.logicail.rsbot.scripts.framework.LogicailGui;
import org.logicail.rsbot.scripts.framework.LogicailScript;
import org.logicail.rsbot.scripts.framework.tasks.Task;
import org.logicail.rsbot.scripts.framework.tasks.impl.AnimationMonitor;
import org.logicail.rsbot.scripts.framework.tasks.impl.AntiBan;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.DepositOre;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.MakeIngots;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.StayInArea;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.burialarmour.DepositArmour;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.burialarmour.SmithAnvil;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.respect.Ancestors;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.respect.BrokenPipes;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.swords.GetPlan;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.swords.GetTongs;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.swords.HeatIngots;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.swords.MakeSword;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.track.LayTracks;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.track.SmithTrack;
import org.logicail.rsbot.scripts.logartisanarmourer.jobs.track.TakeIngots;
import org.logicail.rsbot.scripts.logartisanarmourer.wrapper.IngotGrade;
import org.logicail.rsbot.scripts.logartisanarmourer.wrapper.IngotType;
import org.logicail.rsbot.scripts.logartisanarmourer.wrapper.Mode;
import org.logicail.rsbot.util.ErrorDialog;
import org.logicail.rsbot.util.LinkedProperties;
import org.powerbot.event.MessageEvent;
import org.powerbot.event.MessageListener;
import org.powerbot.script.Manifest;
import org.powerbot.script.methods.Skills;
import org.powerbot.script.util.SkillData;
import org.powerbot.script.util.Timer;
import org.powerbot.script.wrappers.Area;
import org.powerbot.script.wrappers.Tile;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@Manifest(
		name = "Log Artisan Workshop",
		description = "Cheap smithing xp at Artisans Workshop",
		version = 2.06,
		authors = {"Logicail"},
		topic = 1134701
)
public class LogArtisanWorkshop extends LogicailScript<LogArtisanWorkshop> implements MessageListener {
	public static final int[] INGOT_IDS = {20632, 20633, 20634, 20635, 20636,
			20637, 20638, 20639, 20640, 20641, 20642, 20643, 20644, 20645,
			20646, 20647, 20648, 20649, 20650, 20651, 20652};
	public static final int[] ARMOUR_ID_LIST = {20572, 20573, 20574, 20575,
			20576, 20577, 20578, 20579, 20580, 20581, 20582, 20583, 20584,
			20585, 20586, 20587, 20588, 20589, 20590, 20591, 20592, 20593,
			20594, 20595, 20596, 20597, 20598, 20599, 20600, 20601, 20602,
			20603, 20604, 20605, 20606, 20607, 20608, 20609, 20610, 20611,
			20612, 20613, 20614, 20615, 20616, 20617, 20618, 20619, 20620,
			20621, 20622, 20623, 20624, 20625, 20626, 20627, 20628, 20629,
			20630, 20631};
	public static final Area AREAS_ARTISAN_WORKSHOP_BURIAL = new Area(new Tile(3050, 3345, 0), new Tile(3062, 3333, 0));
	public static final Area AREAS_ARTISAN_WORKSHOP_SWORDS = new Area(new Tile(3034, 3346, 0), new Tile(3049, 3332, 0));
	public static final Area AREAS_ARTISAN_WORKSHOP_TRACKS = new Area(new Tile(3054, 9719, 0), new Tile(3080, 9704, 0));
	public static final Area AREAS_ARTISAN_WORKSHOP_LARGE = new Area(new Tile(3040, 3344, 0), new Tile(3061, 3333, 0));
	public static final int ID_MINE_CART = 24824;
	public static final int ID_SMELTER = 29395;
	public static final int ID_SMELTER_SWORDS = 29394;
	public static final int[] ANIMATION_SMITHING = {898, 11062, 15121};
	public final LogArtisanWorkshopOptions options = new LogArtisanWorkshopOptions();
	private SkillData skillData = null;
	private int currentLevel = -1;
	private int startLevel = -1;

	protected LogArtisanWorkshop() {
		super();

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					gui = new ArtisanGUI();
					gui.setScript(LogArtisanWorkshop.this);
				} catch (Exception exception) {
					exception.printStackTrace();
					JOptionPane.showMessageDialog(new JFrame(), exception, "Dialog", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
	}

	private int getRequiredLevel() {
		int requiredLevel = 30;
		switch (options.mode) {
			case BURIAL_ARMOUR:
				switch (options.ingotType) {
					case IRON:
						requiredLevel = 30;
						break;
					case STEEL:
						requiredLevel = 45;
						break;
					case MITHRIL:
						requiredLevel = 60;
						break;
					case ADAMANT:
						requiredLevel = 70;
						break;
					case RUNE:
						requiredLevel = 90;
						break;
				}
				break;
			case CEREMONIAL_SWORDS:
				switch (options.ingotType) {
					case IRON:
						requiredLevel = 70;
						break;
					case STEEL:
						requiredLevel = 75;
						break;
					case MITHRIL:
						requiredLevel = 80;
						break;
					case ADAMANT:
						requiredLevel = 85;
						break;
					case RUNE:
						requiredLevel = 90;
						break;
				}
			case REPAIR_TRACK:
				switch (options.ingotType) {
					case BRONZE:
						requiredLevel = 12;
						break;
					case IRON:
						requiredLevel = 35;
						break;
					case STEEL:
						requiredLevel = 60;
						break;
				}
		}
		return requiredLevel;
	}

	public void createTree() {
		// Check have required level
		try {
			if (ctx.skills.getRealLevel(Skills.SMITHING) < getRequiredLevel()) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						try {
							new ErrorDialog("Error", "You do not have the required level (" + getRequiredLevel() + ")");
						} catch (Exception exception) {
							exception.printStackTrace();
						}
					}
				});
				getController().stop();
				return;
			}
		} catch (NullPointerException ignored) {
		}

		ctx.submit(new AnimationMonitor<LogArtisanWorkshop>(this));
		ctx.submit(new AntiBan<LogArtisanWorkshop>(this));

		//tree.add(new LogoutIdle());

		tree.add(new StayInArea(this));

		if (options.respectRepairPipes) {
			tree.add(new BrokenPipes(this));
		}

		final SmithAnvil smithAnvil = new SmithAnvil(this);
		final MakeSword makeSword = new MakeSword(this, smithAnvil);
		smithAnvil.setMakeSword(makeSword);
		switch (options.mode) {
			case BURIAL_ARMOUR:
				if (options.respectAncestors) {
					//LogHandler.print("Killing ancestors disabled - not updated for eoc");
					//logHandler.print("Add ancestors");
					tree.add(new Ancestors(this));
				}

				tree.add(new DepositOre(this));
				tree.add(new DepositArmour(this));
				tree.add(new MakeIngots(this, smithAnvil));
				tree.add(smithAnvil);
				break;
			case CEREMONIAL_SWORDS:
				tree.add(new DepositOre(this));
				tree.add(new MakeIngots(this, smithAnvil));
				tree.add(new GetTongs(this));
				tree.add(new GetPlan(this, makeSword));
				tree.add(new HeatIngots(this, makeSword));
				tree.add(makeSword);
				break;
			case REPAIR_TRACK:
				tree.add(new TakeIngots(this));
				tree.add(new SmithTrack(this, makeSword));
				tree.add(new LayTracks(this));
				break;
		}
	}

	@Override
	public LinkedProperties getPaintInfo() {
		LinkedProperties properties = new LinkedProperties();

		if (ctx.game.isLoggedIn()) {
			if (skillData == null) {
				skillData = new SkillData(ctx);
			}
			currentLevel = ctx.skills.getRealLevel(Skills.SMITHING);
			if (startLevel == -1) {
				startLevel = currentLevel;
			}
		}

		final long runtime = getRuntime();

		properties.put("Status", options.status);
		properties.put("Time Running", Timer.format(runtime));

		if (skillData != null) {
			properties.put("TTL", Timer.format(skillData.timeToLevel(SkillData.Rate.HOUR, Skills.SMITHING)));
			properties.put("Level", String.format("%d (+%d)", currentLevel, currentLevel - startLevel));
			properties.put("XP Gained", String.format("%,d", skillData.experience(Skills.SMITHING)));
			properties.put("XP Hour", String.format("%,d", skillData.experience(SkillData.Rate.HOUR, Skills.SMITHING)));
		}

		final float time = runtime / 3600000f;

		switch (options.mode) {
			case BURIAL_ARMOUR:
				properties.put("Ingots Smithed", String.format("%,d (%,d/h)", options.ingotsSmithed, (int) (options.ingotsSmithed / time)));
				break;
			case CEREMONIAL_SWORDS:
				properties.put("Swords Smithed", String.format("%,d (%,d/h)", options.swordsSmithed, (int) (options.swordsSmithed / time)));
				properties.put("Perfect Swords", String.format("Perfect Swords: %,d (%,d/h)", options.perfectSwords, (int) (options.perfectSwords / time)));
				properties.put("Broken Swords", String.format("Broken Swords: %,d (%,d/h)", options.brokenSwords, (int) (options.brokenSwords / time)));
				break;
			case REPAIR_TRACK:
				properties.put("Completed Tracks", String.format("%,d (%,d/h)", options.completedTracks, (int) (options.completedTracks / time)));
				break;
		}

		//properties.add("SkillingQuanitity: " + ctx.skillingInterface.getQuantity());
		//properties.add("TimeAnim: " + AnimationMonitor.timeSinceAnimation(LogArtisanWorkshop.ANIMATION_SMITHING));
		//properties.put("SKCat", ctx.skillingInterface.getCategory());
		//properties.put("Iron3", IngotType.IRON.getID(IngotGrade.THREE));
		//properties.put("Iron4", IngotType.IRON.getID(IngotGrade.FOUR));
		//properties.put("Steel3", IngotType.STEEL.getID(IngotGrade.THREE));

		/*final List<String> categorys = ctx.skillingInterface.getCategorys();
		if (categorys.size() > 0) {
			StringBuilder stringBuilder = new StringBuilder("\"");
			for (String s : categorys) {
				stringBuilder.append(s).append("\", ");
			}
			final String s = stringBuilder.toString();
			if (!s.isEmpty()) {
				properties.put("Categorys", s.substring(0, s.length() - 2));
			}
		}*/

		return properties;
	}

	@Override
	public void messaged(MessageEvent e) {
		// TODO: Need message for when smithing level not high enough

		if (tree.any()) {
		    /*if (e.getId() == 0) {
		        if (e.getMessage().contains("You don't have enough")) {
                    ++failedConsecutiveWithdrawals;
                    if (failedConsecutiveWithdrawals >= 3) {
                        log.log(Level.SEVERE, "Ran out of ore... stopping!");
                        shutdown();
                    }
                } else if (e.getMessage().contains("You collect the"))
                    failedConsecutiveWithdrawals = 0;
            } else */
			String s = e.getMessage();
			switch (e.getId()) {
				case 0:
					if (s.equals("You need plans to make a sword.")) {
						options.gotPlan = false;
					} else if (s.equals("This sword is too cool to work. Ask Egil or Abel to rate it.") || s.equals("This sword has cooled and you can no longer work it.")) {
						options.finishedSword = true;
						options.gotPlan = false;
					} else if (s.equals("You broke the sword! You'll need to get another set of plans from Egil.")) {
						options.brokenSwords++;
						options.finishedSword = true;
						options.gotPlan = false;
					} else if (s.equals("This sword is now perfect and requires no more work.") || s.equals("This sword is perfect. Ask Egil or Abel to rate it.")) {
						options.finishedSword = true;
						options.gotPlan = false;
					} else if (s.equals("For producing a perfect sword, you are awarded 120% of the normal experience. Excellent work!")) {
						options.perfectSwords++;
						options.swordsSmithed++;
					} else if (s.startsWith("Your sword is awarded")) {
						options.swordsSmithed++;
						options.finishedSword = true;
						options.gotPlan = false;
					}
					break;
				case 109:
					if (s.contains("You make a")) {
						options.ingotsSmithed++;
					} else if (s.endsWith("track 100%.")) {
						options.completedTracks++;
					}
					break;
			}
		}
	}

//	@Override
//	public void repaint(Graphics g) {
//		super.repaint(g);
//
//		// Changed to using settings
//		// Debug incase repairing pipes doeesn't work (Haven't been able to test yet)
////		if (options.mode != Mode.REPAIR_TRACK && options.respectRepairPipes) {
////			for (GameObject pipe : ctx.objects.select().id(BrokenPipes.BROKEN_PIPE)) {
////				if (pipe.isOnScreen()) {
////					final AbstractModel abstractModel = pipe.getInternal().getModel();
////					if (abstractModel == null) {
////						continue;
////					}
////					final Point point = pipe.getCenterPoint();
////					final String s = String.format("Pipe: %d", BrokenPipes.getNumFaces(abstractModel));
////					g.setColor(Color.BLACK);
////					g.drawString(s, point.x + 1, point.y + 1);
////					g.setColor(Color.WHITE);
////					g.drawString(s, point.x, point.y);
////				}
////			}
////		}
//	}

	class ArtisanGUI extends LogicailGui<LogArtisanWorkshop> {
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
}
