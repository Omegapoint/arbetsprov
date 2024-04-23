package com.example.publicapi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdditionResponse {
    private String asyncId;
    private CalculationResult calculationResult;

    public AdditionResponse(AdditionRecord additionRecord){
        this.asyncId = additionRecord.getAsyncId();
        if(additionRecord.getResult() != null){
            this.calculationResult = new CalculationResult(additionRecord.getNumberOne(), additionRecord.getNumberTwo(), additionRecord.getResult());
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class CalculationResult {
        private Double numberOne;
        private Double numberTwo;
        private Double result;
    }
}
