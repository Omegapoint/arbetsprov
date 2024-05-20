package se.omegapoint.controllers.models;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CalculationRequest(@NotNull Double numberOne, @NotNull Double numberTwo) {
}
