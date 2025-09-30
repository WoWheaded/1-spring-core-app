package org.example.account;

import org.example.user.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AccountService {

    private final Map<Integer, Account> accountMap;
    private int idCounter;


    private final int defaultAccountAmount;
    private final double transferCommission;

    public AccountService(int defaultAccountAmount, double transferCommission) {
        this.defaultAccountAmount = defaultAccountAmount;
        this.transferCommission = transferCommission;
        this.accountMap = new HashMap<>();
        this.idCounter = 0;
    }


    public Account createAccount(User user) {
        idCounter++;
        Account account = new Account(idCounter, user.getId(), defaultAccountAmount);
        accountMap.put(account.getId(), account);
        return account;
    }

    public Optional<Account> findAccountById(int id) {
        return Optional.ofNullable(accountMap.get(id));
    }

    public List<Account> findAllUserAccounts(int userId) {
        return accountMap.values().stream()
                .filter(account -> account.getUserId() == userId)
                .toList();
    }

    public void depositAccount(int id, int amount) {
        var account = findAccountById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account with id " + id + " not found"));
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0. amount: %s".formatted(amount));
        }

        account.setMoneyAmount(account.getMoneyAmount() + amount);
    }

    public void withdrawFromAccount(int accountId, int amountToWithdraw) {
        var account = findAccountById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot withdraw not positive amount: amount=%s"
                        .formatted(amountToWithdraw)));

        if (amountToWithdraw <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0. amount: %s"
                    .formatted(amountToWithdraw));
        }
        if (account.getMoneyAmount() < amountToWithdraw) {
            throw new IllegalArgumentException("Cannot withdraw from account: id=%s, moneyAmount=%s, attemptedWithdraw=%s"
                    .formatted(account.getId(), account.getMoneyAmount(), amountToWithdraw));
        }

        account.setMoneyAmount(account.getMoneyAmount() - amountToWithdraw);
    }

    public Account closeAccount(int id) {
        var accountToRemove = findAccountById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account with id=%s not found".formatted(id)));
        List<Account> accountList = findAllUserAccounts(accountToRemove.getUserId());
        if (accountList.size() == 1) {
            throw new IllegalStateException("Cannot close the only one account");
        }
        Account accountToDeposit = accountList.stream()
                .filter(it -> it.getId() != id)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Account with id=%s not found".formatted(id)));
        accountToDeposit.setMoneyAmount(accountToDeposit.getMoneyAmount() + accountToRemove.getMoneyAmount());
        accountMap.remove(id);
        return accountToRemove;
    }

    public void transfer(int fromAccountId, int toAccountId, int amountToTransfer) {
        var accountFrom = findAccountById(fromAccountId)
                .orElseThrow(() -> new IllegalArgumentException("Account with id=%s not found".formatted(fromAccountId)));
        var accountTo = findAccountById(toAccountId)
                .orElseThrow(() -> new IllegalArgumentException("Account with id=%s not found".formatted(toAccountId)));
        if (amountToTransfer <= 0) {
            throw new IllegalArgumentException("Cannot transfer not positive amount. amount: %s"
                    .formatted(amountToTransfer));
        }

        if (accountFrom.getMoneyAmount() < amountToTransfer) {
            throw new IllegalArgumentException("Cannot transfer from account: id=%s, moneyAmount=%s, attemptedTransfer=%s"
                    .formatted(fromAccountId, accountFrom.getMoneyAmount(), amountToTransfer));
        }

        int totalAmountToDeposit = accountTo.getUserId() != accountFrom.getUserId()
                ? (int) (amountToTransfer - amountToTransfer * transferCommission)
                : amountToTransfer;
        accountFrom.setMoneyAmount(accountFrom.getMoneyAmount() - amountToTransfer);
        accountTo.setMoneyAmount(accountTo.getMoneyAmount() + totalAmountToDeposit);
    }
}
