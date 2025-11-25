package bg.softuni.myrabbitry.payment.client.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ProfitReportResponse {

    private UUID id;

    private BigDecimal amount;

    private LocalDateTime createdOn;

    private long numberOfTransactions;
}
