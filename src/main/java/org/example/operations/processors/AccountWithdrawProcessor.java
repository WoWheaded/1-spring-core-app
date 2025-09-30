package org.example.operations.processors;

import org.example.account.AccountService;
import org.example.operations.ConsoleOperationType;
import org.example.operations.OperationCommandProcessor;

import java.util.Scanner;

public class AccountWithdrawProcessor implements OperationCommandProcessor {

    private final Scanner scanner;
    private final AccountService accountService;

    public AccountWithdrawProcessor(Scanner scanner, AccountService accountService) {
        this.scanner = scanner;
        this.accountService = accountService;
    }

    @Override
    public void processOperation() {
        System.out.println("Enter account id: ");
        int accountId = scanner.nextInt();
        System.out.println("Enter amount to withdraw: ");
        int amountToWithdraw = scanner.nextInt();
        accountService.withdrawFromAccount(accountId, amountToWithdraw);
        System.out.printf("Account withdraw successfully! amount=%s to accountId=%s%n",
                amountToWithdraw, accountId);

    }

    @Override
    public ConsoleOperationType getOperationType() {
        return ConsoleOperationType.ACCOUNT_WITHDRAW;
    }
}
