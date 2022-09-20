package com.bmc.mii.controller;

import org.apache.log4j.Logger;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.bmc.arsys.api.ARServerUser;
import com.bmc.mii.domain.ConfigFile;
import com.bmc.mii.domain.ConfigurationValue;
import com.bmc.mii.remedy.RemedyConnection;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@Controller
public class ConnectionTesting {

    protected static Logger logger = Logger.getLogger("ConnectionTesting");

    @RequestMapping(value = "/connectiontesting", method = RequestMethod.GET)
    public String getConnectionResult(Model model) {

        ApplicationContext context = new AnnotationConfigApplicationContext(ConfigFile.class);
        ConfigurationValue configValue = context.getBean(ConfigurationValue.class);
        model.addAttribute("connectionResult1", configValue.getRemedyUsername());
        logger.info("entering connection testing...." + configValue.getRemedyUsername());

        //Connection testing
        RemedyConnection remedyConnection = new RemedyConnection();
        ARServerUser remedyServer = remedyConnection.connectToRemedy(configValue);
        model.addAttribute("connectionResult", "Connected to AR server: " + configValue.getRemedyUsername());
        remedyServer.logout();

        return "connectiontest";
    }
}
