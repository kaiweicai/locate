package com.locate.test;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

public class Dao extends JFrame {
	int x1=0;
	int y1=0;
	int x2=0;
	int y2=0;
	public Dao() {
		super("Dao!");
		setSize(480, 250);

		setVisible(true);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		setVisible(true);
	}

//	public void paint(Graphics g) {
//		super.paint(g);
//		g.setColor(Color.red);
//		g.drawLine(x1, y1, x2, y2);
//	}
	
	public void paintMy(int x1,int y1,int x2,int y2){
		this.x1=x1;
		this.y1=y1;
		this.x2=x2;
		this.y2=y2;
	}

	public static void main(String args[]) {
		Dao dao = new Dao();
		dao.getGraphics().drawLine(12, 21, 54, 99);
	}
}
