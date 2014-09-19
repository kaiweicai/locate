package rmds;

import org.junit.Test;

import com.locate.rmds.QSConsumerProxy;
import com.reuters.rfa.dictionary.FidDef;
import com.reuters.rfa.dictionary.FieldDictionary;
import com.reuters.rfa.omm.OMMTypes;

public class DictTest {
	@Test
	public void dictTest() {
		FieldDictionary dictionary = QSConsumerProxy.initializeDictionary(
				"../LocateGateWay/config/RDM/RDMFieldDictionary", "../LocateGateWay/config/RDM/enumtype.def");
		// EnumTable[] emum=dictionary.getEnumTables();
		// System.out.println(emum);
		short maxId = dictionary.getMaxFieldId();
		short minId = dictionary.getMinNegFieldId();
		for (short i = minId; i < maxId; i++) {
			FidDef fidDef = dictionary.getFidDef(i);
			if (fidDef != null)
				System.out.println(fidDef.getFieldId() + ",name:" + fidDef.getName() + ",long name:"
						+ fidDef.getLongName() + ",type:" + OMMTypes.toString(fidDef.getOMMType())+",");
		}
	}

}
