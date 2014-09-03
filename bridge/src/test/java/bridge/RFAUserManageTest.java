package bridge;

import org.junit.Test;

import com.locate.common.utils.SystemProperties;
import com.locate.rmds.client.RFAUserManagement;

public class RFAUserManageTest {
	@Test
	public void testInit(){
		SystemProperties.init("C:\\Users\\Administrator\\git\\locate\\LocateGateWay\\config\\rfaConfig.properties");
//		RFAUserManagement.init();
	}
}
