package us.yarik.ExchangeApp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import us.yarik.ExchangeApp.model.WalletAmount;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private String name;
    private String password;
    private List<WalletAmount> currency;
}
