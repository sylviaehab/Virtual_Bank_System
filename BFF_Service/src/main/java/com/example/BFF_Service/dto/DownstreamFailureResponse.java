package com.example.BFF_Service.dto;


public record DownstreamFailureResponse(
        String service,
        String message
) {
}
