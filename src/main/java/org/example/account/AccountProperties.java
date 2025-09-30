package org.example.account;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AccountProperties {
    private final int defaultAccountAmount;
    private final double transferCommission;

    public AccountProperties(
            @Value("${account.default-amount}") int defaultAmount,
            @Value("${account.transfer-commission}") double transferCommission
    ) {
        this.defaultAccountAmount = defaultAmount;
        this.transferCommission = transferCommission;
    }

    public int getDefaultAccountAmount() {
        return defaultAccountAmount;
    }

    public double getTransferCommission() {
        return transferCommission;
    }
}
