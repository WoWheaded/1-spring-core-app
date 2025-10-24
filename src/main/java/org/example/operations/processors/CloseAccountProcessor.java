package org.example.operations.processors;

import org.example.account.AccountService;
import org.example.operations.ConsoleOperationType;
import org.example.operations.OperationCommandProcessor;
import org.example.user.UserService;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class CloseAccountProcessor implements OperationCommandProcessor {

    private final Scanner scanner;
    private final AccountService accountService;
    private final UserService userService;

    public CloseAccountProcessor(Scanner scanner, AccountService accountService, UserService userService) {
        this.scanner = scanner;
        this.accountService = accountService;
        this.userService = userService;
    }

    @Override
    public void processOperation() {
        System.out.println("Enter account id to close: ");
        Long accountId = Long.parseLong(scanner.nextLine());
        accountService.closeAccount(accountId);
        System.out.println("Account successfully closed with id=s%n".formatted(accountId));
    }

    @Override
    public ConsoleOperationType getOperationType() {
        return ConsoleOperationType.ACCOUNT_CLOSE;
    }
}
