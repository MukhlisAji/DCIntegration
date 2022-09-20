package com.bmc.mii.remedy;

import org.apache.log4j.Logger;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.annotation.AnnotationConfigApplicationContext;
//import org.springframework.context.support.AbstractApplicationContext;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.ARServerUser;
//import com.bmc.mii.domain.ConfigFile;
import com.bmc.mii.domain.ConfigurationValue;

public class RemedyConnection {
	protected static Logger logger = Logger.getLogger("RemedyConnection: ");
	
	public ARServerUser connectToRemedy(ConfigurationValue configValue) {
		ARServerUser remedyConnection = new ARServerUser();
		
		//Get configuration value from srtconfig.properties
		//ApplicationContext context = new AnnotationConfigApplicationContext(ConfigFile.class);
		//ConfigurationValue configValue = context.getBean(ConfigurationValue.class);
		
		//Setting remedy connection properties
		remedyConnection.setServer(configValue.getRemedyServer());
		remedyConnection.setUser(configValue.getRemedyUsername());
		remedyConnection.setPassword(configValue.getRemedyPassword());
		remedyConnection.setPort(Integer.parseInt(configValue.getRemedyPort()));
		
		try {
			remedyConnection.verifyUser();
			logger.info("Connected to BMC Remedy successfully, server address:"+remedyConnection.getServer());
		}catch (ARException e) {
			logger.info("Error on connection to Remedy: "+e.toString());
		}
		
		//closing ApplicationContext to avoid memory leak
		//((AbstractApplicationContext) context).close();
		return remedyConnection;
	}
}
