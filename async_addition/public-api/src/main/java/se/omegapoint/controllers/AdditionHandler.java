package se.omegapoint.controllers;

import jakarta.validation.constraints.NotNull;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import se.omegapoint.controllers.models.CalculationRequest;
import se.omegapoint.controllers.models.CalculationResponse;
import se.omegapoint.controllers.models.CalculationResult;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeoutException;

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@RestController
public class AdditionHandler {
    private final ReactiveKafkaProducerTemplate<String, CalculationRequest> kafkaProducerTemplate;

    private ConcurrentMap<String, CalculationResult> results = new ConcurrentHashMap<>();

    public AdditionHandler(ReactiveKafkaProducerTemplate<String, CalculationRequest> kafkaProducerTemplate) {
        this.kafkaProducerTemplate = kafkaProducerTemplate;
    }

    @RouterOperations({
            @RouterOperation(path = "/public/addition/add", method = RequestMethod.POST, beanClass = AdditionHandler.class, beanMethod = "calculateAddition" ),
            @RouterOperation(path = "/public/addition/list-results", method = RequestMethod.GET, beanClass = AdditionHandler.class, beanMethod = "handleListResults" )
    })
    @Bean
    public RouterFunction<ServerResponse> additionHandlerRoutes(AdditionHandler handler) {
        return RouterFunctions.route()
                .POST("/public/addition/add", accept(MediaType.APPLICATION_JSON), handler::calculateAddition)
                .GET("/public/addition/list-results", accept(MediaType.APPLICATION_JSON), handler::handleListResults)
                .build();
    }

    public Mono<ServerResponse> calculateAddition(ServerRequest serverRequest) {

        Mono<CalculationRequest> calculationRequestMono = serverRequest.bodyToMono(CalculationRequest.class);
        boolean syncResult = serverRequest.queryParam("syncResult").map(Boolean::parseBoolean).orElse(false);
        return calculationRequestMono.flatMap(requestBody -> {
                    if (requestBody.numberOne() == null || requestBody.numberTwo() == null) {
                        return Mono.error(new IllegalArgumentException("Missing required attribute! Number one and number two must be present."));
                    }
                    String asyncId = UUID.randomUUID().toString();
                    return kafkaProducerTemplate.send("addition-service.results", asyncId, requestBody)
                            .then(Mono.just(asyncId))
                            .flatMap(id -> syncResult ? waitForResult(id) :
                                    Mono.just(ServerResponse.ok().bodyValue(new CalculationResponse(id, null))));
                }).flatMap(response -> ServerResponse.ok().bodyValue(response))
                .onErrorResume(IllegalArgumentException.class, ex -> ServerResponse.badRequest().bodyValue(ex.getMessage()));
    }

    public Mono<ServerResponse> handleListResults(ServerRequest request) {
        return ServerResponse.ok().bodyValue(Flux.fromIterable(results.values()));
    }

    @KafkaListener(topics = "addition-service.results", groupId = "public-addition-group")
    public void listen(CalculationResponse response) {
        results.put(response.asyncId(), response.result());
    }

    private Mono<ServerResponse> waitForResult(String asyncId) {
        return Flux.interval(Duration.ofMillis(100))
                .timeout(Duration.ofSeconds(10))
                .flatMap(tick -> Mono.justOrEmpty(results.get(asyncId)))
                .next()
                .flatMap(result -> ServerResponse.ok().bodyValue(new CalculationResponse(asyncId, result)))
                .onErrorResume(TimeoutException.class, e -> ServerResponse.status(504).build());
    }
}
