package dev.filipe.ispcopilot.client.dto;

public record IxcSearchRequest(
        String qtype,
        String query,
        String oper
) {}
