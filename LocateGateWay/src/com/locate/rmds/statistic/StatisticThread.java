package com.locate.rmds.statistic;

import com.locate.rmds.processer.ItemManager;

public class StatisticThread extends Thread  {

	public void run(){
		while (true) {
			ItemManager.runMetricsForCurrentInterval();
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}
