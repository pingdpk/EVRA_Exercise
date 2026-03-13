package com.evra.ocppcharging.api.query;

import com.evra.ocppcharging.query.model.ChargerStatsView;
import com.evra.ocppcharging.query.model.SessionView;
import com.evra.ocppcharging.services.query.ChargingQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class QueryController {

    private final ChargingQueryService chargingQueryService;

    public QueryController(ChargingQueryService chargingQueryService) {
        this.chargingQueryService = chargingQueryService;
    }

    @GetMapping("/sessions")
    public List<SessionView> sessions() {
        return chargingQueryService.listSessions();
    }

    @GetMapping("/sessions/{transactionId}")
    public SessionView session(@PathVariable String transactionId) {
        return chargingQueryService.getSession(transactionId);
    }

    @GetMapping("/sessions/{transactionId}/events")
    public List<EventEnvelopeResponse> events(@PathVariable String transactionId) {
        return chargingQueryService.getSessionEvents(transactionId).stream()
                .map(EventEnvelopeResponse::from)
                .toList();
    }

    @GetMapping("/chargers")
    public List<ChargerStatsView> chargers() {
        return chargingQueryService.listChargers();
    }

    @GetMapping("/chargers/{chargerId}/stats")
    public ChargerStatsView chargerStats(@PathVariable String chargerId) {
        return chargingQueryService.getChargerStats(chargerId);
    }
}
