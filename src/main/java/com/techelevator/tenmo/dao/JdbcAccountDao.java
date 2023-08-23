package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcAccountDao implements AccountDao {
    private JdbcTemplate jdbcTemplate;
    public JdbcAccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Account createAccount(Account account) {
        String sql = "INSERT INTO account (balance, user_id) " +
                "VALUES (?, ?) RETURNING account_id;";

        int accountId = jdbcTemplate.queryForObject(sql, int.class, account.getBalance(), account.getUserId());

        return findAccountById(accountId);
    }

    @Override
    public List<Account> findByUserId(String username){
        List<Account> accountList = new ArrayList<>();
//        String sql = "SELECT account_id, balance, user_id FROM account " +
//                    "   JOIN tenmo_user ON tenmo_user.user_id = account.user_id " +
//                    "   WHERE user_id = (SELECT user_id FROM tenmo_user WHERE username = ?);";
        String sql = "SELECT account_id, balance, user_id FROM account " +
                "   JOIN tenmo_user ON tenmo_user.user_id = account.user_id " +
                "   WHERE username = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, username);
        while(results.next()) {
            Account account = mapRowToAccount(results);
            accountList.add(account);
        }
        return accountList;
    }

    @Override
    public Account findAccountById(int accountId) {
        Account account = null;
        String sql = "SELECT account_id, user_id, balance FROM account WHERE account_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accountId);
        if(results.next()) {
            account = mapRowToAccount(results);
        }
        return account;
    }

    @Override
    public Account updateAccount(int accountId, BigDecimal balance) {
        Account account = null;
        String sql = "UPDATE account SET balance = ? WHERE account_id = ?;";
        jdbcTemplate.update(sql, balance, accountId);

        account = findAccountById(accountId);
        return account;
    }


    @Override
    public Account increaseBalance(Account account, String amount) {
        String sql = "UPDATE account SET balance = ? WHERE account_id = ?;";
        BigDecimal currentBalance = account.getBalance();
        BigDecimal transferAmount = new BigDecimal(amount);

        BigDecimal newBalance = currentBalance.add(transferAmount);
        jdbcTemplate.update(sql, newBalance, account.getAccountId());

        return findAccountById(account.getAccountId());
    }

    @Override
    public Account decreaseBalance(Account account, String amount) {
        String sql = "UPDATE account SET balance = ? WHERE account_id = ?;";
        BigDecimal currentBalance = account.getBalance();
        BigDecimal transferAmount = new BigDecimal(amount);

        BigDecimal newBalance = currentBalance.subtract(transferAmount);
        jdbcTemplate.update(sql, newBalance, account.getAccountId());

        return findAccountById(account.getAccountId());
    }

    public Account mapRowToAccount(SqlRowSet row) {
        Account account = new Account();
        account.setAccountId(row.getInt("account-id"));
        account.setUserId(row.getInt("user_id"));
        account.setBalance(row.getBigDecimal("balance"));

        return account;
    }


}
