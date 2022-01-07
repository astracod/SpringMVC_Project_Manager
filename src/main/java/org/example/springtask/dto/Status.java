package org.example.springtask.dto;

import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Data
public class Status {
    private String status;
    private Map<String,String> auxiliaryField = new HashMap<>();
}
