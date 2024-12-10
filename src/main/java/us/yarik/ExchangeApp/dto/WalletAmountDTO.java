package us.yarik.ExchangeApp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import us.yarik.ExchangeApp.model.Currency;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletAmountDTO {
    private Currency currency;
    private Double amount;
    private Integer userId;
}
