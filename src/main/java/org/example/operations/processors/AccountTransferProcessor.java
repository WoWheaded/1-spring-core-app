package org.example.operations.processors;

import org.example.account.AccountService;
import org.example.operations.ConsoleOperationType;
import org.example.operations.OperationCommandProcessor;

import java.util.Scanner;

public class AccountTransferProcessor implements OperationCommandProcessor {
    private final Scanner scanner;
    private final AccountService accountService;

    public AccountTransferProcessor(Scanner scanner, AccountService accountService) {
        this.scanner = scanner;
        this.accountService = accountService;
    }

    @Override
    public void processOperation() {
        System.out.println("Enter source account id: ");
        int fromAccountId = scanner.nextInt();
        System.out.println("Enter destination account id: ");
        int toAccountId = scanner.nextInt();
        System.out.println("Enter amount to transfer: ");
        int amountToTransfer = scanner.nextInt();

        accountService.transfer(fromAccountId, toAccountId, amountToTransfer);
        System.out.println("Transfer %s successful from account=%s to account=%s"
                .formatted(amountToTransfer, fromAccountId, toAccountId));
    }

    @Override
    public ConsoleOperationType getOperationType() {
        return ConsoleOperationType.ACCOUNT_TRANSFER;
    }
}
