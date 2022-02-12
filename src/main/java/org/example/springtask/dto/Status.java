package org.example.springtask.dto;

import lombok.Data;

import java.util.Map;

@Data
public class Status {
    private String status;
    private Map<String, String> auxiliaryField;

}
