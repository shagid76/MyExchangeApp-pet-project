package us.yarik.ExchangeApp.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import us.yarik.ExchangeApp.model.Currency;
import us.yarik.ExchangeApp.model.WalletAmount;

import java.util.List;
import java.util.Optional;

@Repository
public interface WalletAmountRepository extends JpaRepository<WalletAmount, Integer> {

    List<WalletAmount> findAllByUserId(Integer userId);

    Optional<WalletAmount> findByCurrencyAndUserId(Currency currency, Integer userId);
    @Modifying
    @Transactional
    @Query("UPDATE WalletAmount w SET w.amount = :amount WHERE w.currency = :currency AND w.user.id = :userId")
    void updateAmountByCurrencyAndUserId(@Param("amount") double amount,
                                         @Param("currency") Currency currency,
                                         @Param("userId") Integer userId);

}
