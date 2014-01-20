package org.logicail.rsbot.scripts.testing;

import org.logicail.rsbot.scripts.loggildedaltar.LogGildedAltar;
import org.logicail.rsbot.scripts.loggildedaltar.gui.LogGildedAltarGUI;
import org.powerbot.script.Manifest;
import org.powerbot.script.PollingScript;

import javax.swing.*;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 20/01/2014
 * Time: 20:07
 */
@Manifest(name = "Logicail Gui Test", description = "Creates an instance of LogGildedAltarGUI", hidden = true)
public class GuiTest extends PollingScript {
    @Override
    public void start() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    new LogGildedAltarGUI(GuiTest.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public int poll() {
        return 1000;
    }
}
