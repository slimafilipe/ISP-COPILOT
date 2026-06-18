package dev.filipe.ispcopilot.dto;

public record DiagnosticResult(
        String login,
        double opticalSignalDbm,
        double opticalSignalStatus,
        String uptime,
        boolean hasIpv6Lan,
        String ipv6Status,
        String humanMessage
) {
}
