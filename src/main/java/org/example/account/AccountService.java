package org.example.account;

import org.example.hibernate.TransactionHelper;
import org.example.user.User;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class AccountService {

    private final AccountProperties accountProperties;
    private final SessionFactory sessionFactory;
    private final TransactionHelper transactionHelper;

    public AccountService(AccountProperties accountProperties, SessionFactory sessionFactory, TransactionHelper transactionHelper) {
        this.accountProperties = accountProperties;
        this.sessionFactory = sessionFactory;
        this.transactionHelper = transactionHelper;
    }


    public Account createAccount(User user) {
        return transactionHelper.executeInTransaction(() -> {
            Account account = new Account(null, user, accountProperties.getDefaultAccountAmount());
            sessionFactory.getCurrentSession().persist(account);
            return account;
        });
    }

    public Optional<Account> findAccountById(Long id) {
        Account account = sessionFactory.getCurrentSession().get(Account.class, id);
        return Optional.ofNullable(account);
    }

    public void depositAccount(Long id, int amount) {
        transactionHelper.executeInTransaction(() -> {
            Account account = findAccountById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Account with id " + id + " not found"));
            if (amount <= 0) {
                throw new IllegalArgumentException("Amount must be greater than 0. amount: %s".formatted(amount));
            }
            account.setMoneyAmount(account.getMoneyAmount() + amount);
            return 0;
        });
    }

    public void withdrawFromAccount(Long accountId, int amountToWithdraw) {
        transactionHelper.executeInTransaction(() -> {
            Account account = findAccountById(accountId)
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
            return 0;
        });
    }

    public Account closeAccount(Long accountId) {
        return transactionHelper.executeInTransaction(() -> {
            Account accountToRemove = findAccountById(accountId)
                    .orElseThrow(() -> new IllegalArgumentException("Account with id=%s not found".formatted(accountId)));
            List<Account> accountList = accountToRemove.getUser().getAccountList();
            if (accountList.size() == 1) {
                throw new IllegalStateException("Cannot close the only one account");
            }

            Account accountToDeposit = accountList.stream()
                    .filter(it -> !Objects.equals(it.getId(), accountId))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Account with id=%s not found".formatted(accountId)));

            accountToDeposit.setMoneyAmount(accountToDeposit.getMoneyAmount() + accountToRemove.getMoneyAmount());
            sessionFactory.getCurrentSession().remove(accountToRemove);
            return accountToRemove;
        });
    }

    public void transfer(Long fromAccountId, Long toAccountId, int amountToTransfer) {
        if (amountToTransfer <= 0) {
            throw new IllegalArgumentException("Cannot transfer not positive amount. amount: %s"
                    .formatted(amountToTransfer));
        }
        transactionHelper.executeInTransaction(() -> {
            Account accountFrom = findAccountById(fromAccountId)
                    .orElseThrow(() -> new IllegalArgumentException("Account with id=%s not found".formatted(fromAccountId)));
            Account accountTo = findAccountById(toAccountId)
                    .orElseThrow(() -> new IllegalArgumentException("Account with id=%s not found".formatted(toAccountId)));

            if (accountFrom.getMoneyAmount() < amountToTransfer) {
                throw new IllegalArgumentException("Cannot transfer from account: id=%s, moneyAmount=%s, attemptedTransfer=%s"
                        .formatted(fromAccountId, accountFrom.getMoneyAmount(), amountToTransfer));
            }

            int totalAmountToDeposit = !accountTo.getUser().getId().equals(accountFrom.getUser().getId())
                    ? (int) (amountToTransfer * (1 - accountProperties.getTransferCommission()))
                    : amountToTransfer;
            accountFrom.setMoneyAmount(accountFrom.getMoneyAmount() - amountToTransfer);
            accountTo.setMoneyAmount(accountTo.getMoneyAmount() + totalAmountToDeposit);
            return 0;
        });
    }
}
