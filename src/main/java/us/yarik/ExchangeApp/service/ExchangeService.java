package us.yarik.ExchangeApp.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import us.yarik.ExchangeApp.dto.ExchangeRequestDTO;
import us.yarik.ExchangeApp.model.Currency;
import us.yarik.ExchangeApp.model.WalletAmount;
import us.yarik.ExchangeApp.repository.WalletAmountRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExchangeService {
    private static final Map<String, Double> convert;
    private final WalletAmountRepository walletAmountRepository;
    private final UserService userService;

    static {
        convert = new HashMap<>();
        convert.putAll(Map.of(
                "USD-EUR", 0.95153,
                "USD-YEN", 150.033,
                "USD-GBP", 0.78888,
                "USD-FRN", 0.88558,
                "EUR-USD", 1.05078,
                "EUR-YEN", 157.662,
                "EUR-GBP", 0.829,
                "EUR-FRN", 0.93062,
                "YEN-USD", 0.00666,
                "YEN-EUR", 0.00634
        ));
        convert.putAll(Map.of(
                "YEN-GBP", 0.00526,
                "YEN-FRN", 0.0059,
                "GBP-USD", 1.26741,
                "GBP-EUR", 1.20604,
                "GBP-YEN", 190.162,
                "GBP-FRN", 1.12244,
                "FRN-USD", 1.12896,
                "FRN-EUR", 1.07434,
                "FRN-YEN", 169.388,
                "FRN-GBP", 0.89067
        ));
    }

    public List<WalletAmount> findAllByUserId(Integer userId) {
        return walletAmountRepository.findAllByUserId(userId);
    }

    @Transactional
    public void addReplenishment(int userId, Currency currency, double amount) {
        Optional<WalletAmount> walletAmount = walletAmountRepository.findByCurrencyAndUserId(currency, userId);
        if (walletAmount.isPresent()) {
            walletAmount.get().setAmount(walletAmount.get().getAmount() + amount);
            walletAmountRepository.save(walletAmount.get());
        } else {
            WalletAmount newCurrency = new WalletAmount();
            newCurrency.setCurrency(currency);
            newCurrency.setAmount(amount);
            newCurrency.setUser(userService.findById(userId));
            walletAmountRepository.save(newCurrency);
        }
    }

    @Transactional
    public void exchange(ExchangeRequestDTO exchangeRequestDTO, Integer userId) {
        WalletAmount walletAmount = walletAmountRepository.findByCurrencyAndUserId(exchangeRequestDTO.getFromCurrency(), userId)
                .orElseThrow(() -> new IllegalArgumentException("Insufficient funds or wallet not found for currency: " + exchangeRequestDTO.getFromCurrency()));
        String key = exchangeRequestDTO.getFromCurrency() + "-" + exchangeRequestDTO.getToCurrency();
        validateExchangeRequest(exchangeRequestDTO, walletAmount);
        double exchangeAmount = exchangeRequestDTO.getAmount() * convert.get(key);
        walletAmountRepository.updateAmountByCurrencyAndUserId(walletAmount.getAmount() - exchangeRequestDTO.getAmount(), exchangeRequestDTO.getFromCurrency(), userId);
        addReplenishment(userId, exchangeRequestDTO.getToCurrency(), exchangeAmount);
    }

    private void validateExchangeRequest(ExchangeRequestDTO exchangeRequestDTO, WalletAmount walletAmount) {
        if (walletAmount.getAmount() < exchangeRequestDTO.getAmount()) {
            throw new IllegalArgumentException("You are trying to exchange more currencies than you have.");
        }
        if (exchangeRequestDTO.getToCurrency().equals(exchangeRequestDTO.getFromCurrency())) {
            throw new IllegalArgumentException("You can't exchange same currencies.");
        }
    }

}
