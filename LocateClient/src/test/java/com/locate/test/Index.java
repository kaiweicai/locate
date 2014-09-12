package com.locate.test;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.skin.GeminiSkin;
import org.pushingpixels.substance.api.skin.GraphiteAquaSkin;
import org.pushingpixels.substance.api.skin.MagellanSkin;
import org.pushingpixels.substance.api.skin.MistAquaSkin;
import org.pushingpixels.substance.api.skin.ModerateSkin;
import org.pushingpixels.substance.api.skin.NebulaBrickWallSkin;
import org.pushingpixels.substance.api.skin.NebulaSkin;
import org.pushingpixels.substance.api.skin.OfficeBlue2007Skin;
import org.pushingpixels.substance.api.skin.OfficeSilver2007Skin;
import org.pushingpixels.substance.api.skin.RavenSkin;
import org.pushingpixels.substance.api.skin.SaharaSkin;

public class Index {
	public static void main(String[] args) {

		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				SubstanceLookAndFeel.setSkin(new SaharaSkin());
				try {
					final JFrame frame = new JFrame();
					frame.setTitle("SubstanceLookAndFeel");
					frame.setVisible(true);

					frame.setSize(600, 400);
					frame.setLocationRelativeTo(frame.getOwner());
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//					frame.setIconImage(Toolkit.getDefaultToolkit()
//							.createImage(JFrame.class.getResource("icon.png")));

					frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
					frame.addWindowListener(new WindowAdapter() {
						public void windowClosing(WindowEvent e) {
							int flag = JOptionPane.showConfirmDialog(frame, "Sure to close?", "Care!",
									JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
							if (JOptionPane.YES_OPTION == flag) {
								System.exit(0);
							} else {
								return;
							}
						}
					});

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
