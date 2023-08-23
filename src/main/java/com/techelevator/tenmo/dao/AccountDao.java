package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

public interface AccountDao {

    Account createAccount(Account account);
    List<Account> findByUsername(String username);
    Account findAccountById(int accountId);
    Account updateAccount(int accountId, BigDecimal balance);




    Account increaseBalance(Account account, String amount);
    Account decreaseBalance(Account account, String amount);

}
