package com.evra.ocppcharging.services.command;

import com.evra.ocppcharging.domain.event.*;
import com.evra.ocppcharging.domain.lifecycle.ChargingLifeCycle;
import com.evra.ocppcharging.domain.model.ChargingSessionAggregate;
import com.evra.ocppcharging.infrastructure.eventbus.DomainEventPublisher;
import com.evra.ocppcharging.infrastructure.eventstoer.EventEnvelope;
import com.evra.ocppcharging.infrastructure.eventstoer.EventStore;
import com.evra.ocppcharging.infrastructure.idempotency.CommandResult;
import com.evra.ocppcharging.infrastructure.idempotency.IdempotencyStore;
import com.evra.ocppcharging.infrastructure.locking.LockManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ChargingCommandService {

    private static final Logger log = LoggerFactory.getLogger(ChargingCommandService.class);

    private final EventStore eventStore;
    private final IdempotencyStore idempotencyStore;
    private final LockManager lockManager;
    private final DomainEventPublisher publisher;

    public ChargingCommandService(EventStore eventStore,
                                  IdempotencyStore idempotencyStore,
                                  LockManager lockManager,
                                  DomainEventPublisher publisher) {
        this.eventStore = eventStore;
        this.idempotencyStore = idempotencyStore;
        this.lockManager = lockManager;
        this.publisher = publisher;
    }

    public CommandResult handle(ChargingLifeCycle command) {
        Optional<CommandResult> replay = idempotencyStore.find(command.getIdempotencyKey());
        if (replay.isPresent()) {
            log.info("idempotent replay detected key={} chargerId={} commandType={}",
                    command.getIdempotencyKey(), command.getChargerId(), command.getCommandType());
            return replay.get();
        }

        return lockManager.executeWithLock(command.lockKey(), () -> {
            Optional<CommandResult> secondCheck = idempotencyStore.find(command.getIdempotencyKey());
            if (secondCheck.isPresent()) {
                return secondCheck.get();
            }

            CommandResult result = switch (command.getCommandType()) {
                case BOOT_NOTIFICATION -> appendAndProject("charger:" + command.getChargerId(),
                        new ChargerBootedEvent(command.getChargerId(), command.getTimestamp(), command.getConnectorId()),
                        "ACCEPTED",
                        "BootNotification accepted");
                case AUTHORIZE -> appendAndProject("charger:" + command.getChargerId(),
                        new AuthorizationAcceptedEvent(command.getChargerId(), command.getTimestamp(), command.getAuthorizationId()),
                        "ACCEPTED",
                        "Authorize accepted");
                case STATUS_NOTIFICATION -> {
                    ChargingSessionAggregate aggregate = loadAggregate(command.getTransactionId());
                    yield appendAndProject("charger:" + command.getChargerId(),
                            aggregate.statusChanged(command),
                            "ACCEPTED",
                            "StatusNotification accepted");
                }
                case START_TRANSACTION -> {
                    ChargingSessionAggregate aggregate = loadAggregate(command.getTransactionId());
                    SessionStartedEvent event = aggregate.start(command);
                    yield appendAndProject(streamKey(command.getTransactionId()), event, "ACCEPTED", "StartTransaction accepted");
                }
                case METER_VALUES -> {
                    ChargingSessionAggregate aggregate = loadAggregate(command.getTransactionId());
                    ChargingSessionAggregate.MeterComputation computation = aggregate.recordMeterValue(command);
                    if (computation.isAccepted()) {
                        yield appendAndProject(streamKey(command.getTransactionId()),
                                computation.getRecordedEvent(),
                                "ACCEPTED",
                                "MeterValues accepted");
                    }
                    yield appendAndProject(streamKey(command.getTransactionId()),
                            computation.getAnomalyEvent(),
                            "REJECTED",
                            "MeterValues rejected: meter value decreased");
                }
                case STOP_TRANSACTION -> {
                    ChargingSessionAggregate aggregate = loadAggregate(command.getTransactionId());
                    SessionCompletedEvent event = aggregate.stop(command);
                    yield appendAndProject(streamKey(command.getTransactionId()), event, "ACCEPTED", "StopTransaction accepted");
                }
            };

            idempotencyStore.put(command.getIdempotencyKey(), result);
            return result;
        });
    }

    private ChargingSessionAggregate loadAggregate(String transactionId) {
        return ChargingSessionAggregate.rehydrate(eventStore.load(streamKey(transactionId)));
    }

    private String streamKey(String transactionId) {
        return "session:" + transactionId;
    }

    private CommandResult appendAndProject(String streamKey, DomainEvent event, String status, String message) {
        EventEnvelope envelope = eventStore.append(streamKey, event);
        publisher.publish(event);
        return new CommandResult(event.eventId(), status, message + " (seq=" + envelope.getSequence() + ")", event.transactionId());
    }
}
