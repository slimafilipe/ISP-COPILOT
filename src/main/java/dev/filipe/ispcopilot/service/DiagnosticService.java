package dev.filipe.ispcopilot.service;

import dev.filipe.ispcopilot.client.dto.IxcClient;
import dev.filipe.ispcopilot.client.dto.IxcTr069Response;
import dev.filipe.ispcopilot.dto.DiagnosticResult;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class DiagnosticService {

    private final IxcClient ixcClient;

    public DiagnosticService(IxcClient client, IxcClient ixcClient) {
        this.ixcClient = ixcClient;
    }

    public DiagnosticResult  getDiagnostic(String login) {
        IxcTr069Response tr069Data = ixcClient.getParameterTr069(login)
                .orElseThrow(() -> new RuntimeException("Login não encontrado no concentrador"));

        double rxPowerDbm = tr069Data.rxPower() != null ? tr069Data.rxPower() / 100.0 : 0.0;
        boolean signalIsBad = rxPowerDbm < -25.0 || rxPowerDbm > -8.0;
        String signalStatus = signalIsBad ? "ATENUADO/RUIM" : "SINAL BOM";

        Duration uptimeDuration = Duration.ofSeconds(tr069Data.uptimeSeconds() != null ? tr069Data.uptimeSeconds() : 0);
        String formattedUptime = String.format("%d dias e %d horas",
                uptimeDuration.toDays(),
                uptimeDuration.toHoursPart());

        boolean hasGlobalIpv6 = tr069Data.ipv6Lan() != null &&
                (tr069Data.ipv6Lan().startsWith("2804:") || tr069Data.ipv6Lan().startsWith("2001:"));
        String ipv6Status = hasGlobalIpv6 ? "Navegando em IPV6" : "IPv4 apenas";

        String humanMessage = buildHumanReadableMessage(rxPowerDbm, signalIsBad, formattedUptime, hasGlobalIpv6);

        return new DiagnosticResult(
                login,
                rxPowerDbm,
                signalStatus,
                formattedUptime,
                hasGlobalIpv6,
                ipv6Status,
                humanMessage
        );
    }

    private String buildHumanReadableMessage(double rxPowerDbm, boolean signalIsBad, String uptime, boolean hasIpv6) {
        StringBuilder msg = new StringBuilder();

        msg.append("RESUMO DO DIAGNOSTICO:\n\n");
        if (signalIsBad) {
            msg.append("ALERTA DE FIBRA: O sinal do cliente esta em ").append(rxPowerDbm).append(" dbm.");
            msg.append("Isso indica atenuação severa ou rompimento parcial. Necessita de visita técnica para revisar conectores ou o drop óptico.\n");
        } else {
            msg.append("FIBRA OK: O sinal está dento padrão (").append(rxPowerDbm).append(" dbm.)\n");
        }
        msg.append("TEMPO CONECTADO: O roteador está ligado e autenticado há ").append(uptime).append("\n");

        if (!hasIpv6) {
            msg.append("ALERTA DE NAVEGAÇÃO: O roteador não pegou IPV6.");
            msg.append("O cliente pode reclamar de lentidão em jogos ou streaming.");
            msg.append("Tente reiniciar a sessão PPPoE para forçar uma nova negociação.");
            msg.append("Caso persistir o problema, consultar o NOC.");

        } else {
            msg.append("NAVEGAÇÃO OK: Cliente possui IPv6 válido");
        }
        return msg.toString();

    }
}
