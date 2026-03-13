package com.evra.ocppcharging.api.command;

import com.evra.ocppcharging.services.command.ChargingCommandService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/commands")
public class SimulationCommandController {

    private final ChargingCommandService chargingCommandService;

    public SimulationCommandController(ChargingCommandService chargingCommandService) {
        this.chargingCommandService = chargingCommandService;
    }

    @PostMapping
    public RestCommandResponse command(@Valid @RequestBody RestCommandRequest request) {
        return RestCommandResponse.from(chargingCommandService.handle(request.toCommand()));
    }
}
