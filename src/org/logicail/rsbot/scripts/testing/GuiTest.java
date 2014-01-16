package org.logicail.rsbot.scripts.testing;

import org.logicail.rsbot.scripts.logartisanarmourer.wrapper.IngotGrade;
import org.logicail.rsbot.scripts.logartisanarmourer.wrapper.IngotType;
import org.powerbot.script.AbstractScript;
import org.powerbot.script.Manifest;
import org.powerbot.script.PollingScript;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 16/01/14
 * Time: 17:00
 */
@Manifest(name = "Test GUI on Mac", description = "testing", hidden = true)
public class GuiTest extends PollingScript {
	private GUI gui;

	public GuiTest() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				gui = new GUI(GuiTest.this);
			}
		});
	}

	@Override
	public void stop() {
		if (gui != null) {
			gui.dispose();
		}
	}

	@Override
	public int poll() {
		return 1000;
	}

	class GUI extends JFrame {
		private final AbstractScript script;
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
		private JComponent swordsTab;
		private JComponent trackTab;
		// Variable declaration
		private JTabbedPane tabbedPane;
		//private JComponent modeTab;
		// Mode
		//private JComboBox<Mode> comboBoxMode;
		private JCheckBox respectKill;
		private JButton startButton;

		public GUI(AbstractScript script) {
			this.script = script;
			initComponents();

			setTitle(script.getName() + " v" + script.getVersion());
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			setMinimumSize(new Dimension(500, 360));

			final JPanel contentPane = (JPanel) getContentPane();
			contentPane.setLayout(new BorderLayout());
			contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));

//			contentPane.add(getTopPanel(), BorderLayout.NORTH);
//			contentPane.add(getCenterPanel(), BorderLayout.CENTER);
//			contentPane.add(getBottomPanel(), BorderLayout.SOUTH);

			pack();
			setVisible(true);
			setLocationRelativeTo(null);
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
			addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosed(WindowEvent e) {
					if (!startPressed) {
						script.getController().stop();
					}
				}
			});

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
			script.getController().stop();
			dispose();
		}
	}

}
