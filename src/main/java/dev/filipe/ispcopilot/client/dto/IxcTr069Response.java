package dev.filipe.ispcopilot.client.dto;

public record IxcTr069Response(
        String login,
        Integer rxPower,
        Long uptimeSeconds,
        String ipv6Lan
) {}
