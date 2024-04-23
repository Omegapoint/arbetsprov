package com.example.publicapi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdditionRecord {
    private String asyncId;
    private Double numberOne;
    private Double numberTwo;
    private Double result;
}
