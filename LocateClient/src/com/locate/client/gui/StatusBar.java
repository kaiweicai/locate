/*** Eclipse Class Decompiler plugin, copyright (c) 2012 Chao Chen (cnfree2000@hotmail.com) ***/
package com.locate.client.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

public class StatusBar extends Panel {
	private static final long serialVersionUID = 1L;
	protected Timer _timer;
	protected boolean _isFixed;
	protected Label _statusText;
	protected int _numIntervals;

	public StatusBar() {
		this("");
	}

	public StatusBar(String status) {
		this(status, true);
	}

	public StatusBar(String status, boolean initialStatusFixed) {
		this._timer = new Timer(1000, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				StatusBar.this.fade();
			}
		});
		this._timer.setRepeats(true);
		this._numIntervals = 0;

		setLayout(new FlowLayout(0));
		this._statusText = new Label();
		add(this._statusText);

		if (initialStatusFixed)
			setStatusFixed(status);
		else
			setStatusFade(status, 3);
	}

	public Dimension getPreferredSize() {
		Dimension d = new Dimension();

		d.width = (getFontMetrics(getFont()).charWidth('A') * 50);
		d.height = ((getFontMetrics(getFont()).getMaxAscent() + getFontMetrics(getFont()).getMaxDescent()) * 2);

		return d;
	}

	public void setStatusFixed(String newStatus) {
		this._isFixed = true;
		String[] stateColum = newStatus.split(",");
		if(stateColum.length>3){
			String state=stateColum[2].trim();
			String dataState = stateColum[1].trim();
			if(state.equalsIgnoreCase("NONE")&&dataState.equalsIgnoreCase("OK")){
				this._statusText.setForeground(Color.BLUE);
			}else{
				this._statusText.setForeground(Color.RED);
			}
		}
		this._statusText.setText(newStatus);
		this._timer.stop();
	}

	public void setStatusFade(String newStatus) {
		setStatusFade(newStatus, 1);
	}

	public void setStatusFade(String newStatus, int numIntervals) {
		this._isFixed = false;
		this._statusText.setText(newStatus);
		this._numIntervals = numIntervals;
		this._timer.restart();
	}

	public void clearStatus() {
		setStatusFixed("");
	}

	public void cleanUp() {
		this._timer.stop();
	}

	public void fade() {
		if ((this._numIntervals <= 0) || (this._isFixed))
			return;
		this._numIntervals -= 1;
		if (this._numIntervals == 0)
			clearStatus();
	}

	public void paint(Graphics g) {
		Rectangle bounds = getBounds();
		this._statusText.setSize(bounds.width - 8, bounds.height - 8);
		try {
			Color background = getBackground();
			g.setColor(background.darker().darker());
			g.drawRect(0, 0, bounds.width - 2, bounds.height - 2);
			g.setColor(background.brighter().brighter());
			g.drawRect(1, 1, bounds.width - 2, bounds.height - 2);
		} catch (NullPointerException npe) {
		}
	}

	public String getText() {
		return this._statusText.getText();
	}
}