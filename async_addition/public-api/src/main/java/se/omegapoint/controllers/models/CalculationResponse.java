package se.omegapoint.controllers.models;

import lombok.Builder;

@Builder
public record CalculationResponse(String asyncId, CalculationResult result) {}
