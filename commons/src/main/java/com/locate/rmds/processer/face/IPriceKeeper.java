package com.locate.rmds.processer.face;

import net.sf.json.JSON;

public interface IPriceKeeper {
	public void persistentThePrice(JSON jsonObjectt);

	public void init(String pItemName);
}
