package com.example.Account_Service.kafka.dto;


public record DownstreamFailureResponse(
        String service,
        String message
) {
}
