package com.bmc.mii.controller;

import java.io.IOException;
import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component("scheduledTasks")
public class ScheduledTasks {

    protected static Logger logger = Logger.getLogger("Scheduler Jobs:");

    //@Scheduled(fixedDelay = 60000)
    public void reportCurrentTime() throws IOException {
        logger.info("Scheduler jobs is running from xml");

//        remedyController remedyController = new remedyController();
//        remedyController.getRemedyTicketKontrak();
//        remedyController.getRemedyTicketSRM();

    }
}
