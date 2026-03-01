package com.evra.ocppcharging.services.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.xml.transform.Result;
import java.util.Optional;

@Service
public class ChargingCommandService {

    private static final Logger log = LoggerFactory.getLogger(ChargingCommandService.class);

    public ChargingCommandService() {
    }

    public Result handle(Enum command) { //Life cycle TODO
        return null;
    }




}
