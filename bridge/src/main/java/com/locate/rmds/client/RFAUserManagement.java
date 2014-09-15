package com.locate.rmds.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.locate.common.constant.RFANodeconstant;
import com.locate.common.utils.RFAUtils;
import com.locate.common.utils.SystemProperties;
import com.locate.rmds.user.RFAUser;
import com.locate.rmds.user.RFAUserPermission;

public class RFAUserManagement {
	
	static Logger _logger = LoggerFactory.getLogger(RFAUserManagement.class.getName());
	
	public static Map<String,RFAUser> RFAUserList = new HashMap<String,RFAUser>();
	public static Map<String,RFAUser> RFAUserBusiness = new HashMap<String,RFAUser>();
	
	public static void init(){
		SAXReader reader=new SAXReader();
		String userFile = SystemProperties.getProperties(SystemProperties.RFA_USER_FILE,"");
		Document userData = null;
		try {
			userData = reader.read(userFile);
		} catch (DocumentException e) {
			_logger.error("Inital RFA user data error.", e);
		}
		Element rmds = userData.getRootElement();
		List<Element> userNodeList = rmds.elements();//(RFANodeconstant.SELECT_USER);
		List<Element> userPermList = null;
		RFAUser userModel = null;
		RFAUserPermission userPermisson = null;
		Map<String,RFAUserPermission> userPermSet = null;
		Element permElement ;
		String limit ;
		for(Element userNode: userNodeList){
			userModel = new RFAUser();
			userPermSet = new HashMap<String,RFAUserPermission>();
			userModel.setUserName(userNode.element(RFANodeconstant.SELECT_SINGLE_NAME).getText());
			userModel.setPassword(userNode.element(RFANodeconstant.SELECT_SINGLE_PWD).getText());
			permElement = userNode.element(RFANodeconstant.SELECT_SINGLE_PERMISSION);
			
			userPermList = permElement.elements();
			for(Element userPermNode : userPermList){
				userPermisson = new RFAUserPermission();
				userPermisson.setName(userPermNode.element(RFANodeconstant.SELECT_SINGLE_ITEM).getText().toUpperCase());
				limit = userPermNode.element(RFANodeconstant.SELECT_SINGLE_LIMIT).getText();
				try {
					limit = RFAUtils.output(limit);
				} catch (Exception e) {
					_logger.error("User config has error.");
					System.exit(0);
				}
				if(NumberUtils.isNumber(limit)){
					userPermisson.setLimit(NumberUtils.toInt(limit));
				}
//				else{
//					if(!userPermisson.getName().equalsIgnoreCase(GateWayMessageTypes.RFAMessageName.getRFAMessageName(GateWayMessageTypes.NEWS_REQUEST))){
//						_logger.error("User config has error.");
//						System.exit(0);
//					}
//					userPermisson.setFilter(limit.toUpperCase());
//				}
				userPermSet.put(userPermisson.getName(),userPermisson);
			}
			userModel.setPermission(userPermSet);
			
			RFAUserList.put(userModel.getUserName(), userModel);
		}
		_logger.info("Completed loading user config.");
	}
	
//	private	int stringToInt(String v){
//		int limit = Integer.parseInt(v);
//	}
	public static void setUserAddress(String userName,String address){
		RFAUser userModel = RFAUserList.get(userName);
		userModel.setUserIP(address);
	}
	
	public static boolean hasUser(String userName){
		if(RFAUserList.get(userName) == null){
			return false;
		}else{
			return true;
		}
	}
	
	public static boolean validateUser(String userName,String pwd){
		RFAUser userModel = RFAUserList.get(userName);
		String userRightPwd = userModel.getPassword();
		try {
			userRightPwd = RFAUtils.output(userRightPwd);
		} catch (Exception e) {
			return false;
		}
		if(userRightPwd.equals(pwd)){
			return true;
		}else{
			return false;
		}
	}
	
	public static boolean checkMaxBusinessValiable(String userName,String businessType,List<String> itemNames){
		RFAUser userUsedBusinessNumber =RFAUserBusiness.get(userName);
		RFAUser userBuyBusinessNumber =RFAUserList.get(userName);
		
		RFAUserPermission permission = null;
		Map<String ,RFAUserPermission> userBuyPermMap = userBuyBusinessNumber.getPermission();
		int maxBusinessNumber = 0;
		boolean isTotalLimit = false;
		if(userBuyPermMap.get(RFANodeconstant.CHECK_ALL_LIMIT) != null){ // Total limit
			maxBusinessNumber = userBuyPermMap.get(RFANodeconstant.CHECK_ALL_LIMIT).getLimit();
			isTotalLimit = true;
			businessType = RFANodeconstant.CHECK_ALL_LIMIT;
		}else{ //business limit
			 maxBusinessNumber = userBuyPermMap.get(businessType).getLimit();
		}
		
		
		if(userUsedBusinessNumber == null){
			userUsedBusinessNumber = new RFAUser();
			userUsedBusinessNumber.setUserName(userName);
			Map<String,RFAUserPermission> permMap = new HashMap();
			if(maxBusinessNumber>0 && maxBusinessNumber >= itemNames.size()){
				permission = new RFAUserPermission();
				permission.setName(businessType);
				permission.setLimit(itemNames.size());
				permMap.put(businessType, permission);
				userUsedBusinessNumber.setPermission(permMap);
				RFAUserBusiness.put(userName, userUsedBusinessNumber);
				return true;
			}else{
				return false;
			}
		}else{
			Map<String,RFAUserPermission> permMap = userUsedBusinessNumber.getPermission();
			if(permMap.get(businessType) == null){
				
				if(maxBusinessNumber>0 && maxBusinessNumber >= itemNames.size()){
					permission = new RFAUserPermission();
					permission.setName(businessType);
					permission.setLimit(itemNames.size());
					permMap.put(businessType, permission);
					userUsedBusinessNumber.setPermission(permMap);
					RFAUserBusiness.put(userName, userUsedBusinessNumber);
					return true;
				}else{
					return false;
				}
			}
			int currentUsedBusinessdNumber = userUsedBusinessNumber.getPermission().get(businessType).getLimit();
			if((currentUsedBusinessdNumber+itemNames.size()) <= maxBusinessNumber ){
				userUsedBusinessNumber.getPermission().get(businessType).setLimit(currentUsedBusinessdNumber+itemNames.size());
				return true;
			}else{
				return false;
			}
		}
	}
	
	public static boolean checkNewsCodeValiable(String userName,String businessType,List<String> newsCode){
		RFAUser userUsedBusinessNumber =RFAUserBusiness.get(userName);
		RFAUser userBuyBusinessNumber =RFAUserList.get(userName);
		RFAUserPermission permission = null;
		Map<String ,RFAUserPermission> userBuyPermMap = userBuyBusinessNumber.getPermission();
		String userBuyNewsCode = "";
		if(userBuyPermMap.get(RFANodeconstant.CHECK_ALL_LIMIT) != null){ // Total limit
			return checkMaxBusinessValiable(userName,RFANodeconstant.CHECK_ALL_LIMIT,newsCode);
		}else{ //business limit
			if(userBuyBusinessNumber.getPermission().get(businessType) == null) {
				return false;
			}
			userBuyNewsCode = userBuyBusinessNumber.getPermission().get(businessType).getFilter();
			if(StringUtils.isEmpty(userBuyNewsCode)){
				return false;
			}
			userBuyNewsCode = userBuyNewsCode.toUpperCase();
			if(StringUtils.isEmpty(userBuyNewsCode)){
				return false;
			}else{
				for(String code : newsCode){
					if(! userBuyNewsCode.contains(code.toUpperCase())){
						return false;
					}
				}
			}
			return true;
		}
		
	}
}
