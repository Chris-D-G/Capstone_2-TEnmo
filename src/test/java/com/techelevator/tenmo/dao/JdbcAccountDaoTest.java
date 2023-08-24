package com.techelevator.tenmo.dao;

import com.techelevator.dao.BaseDaoTests;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class JdbcAccountDaoTest extends BaseDaoTests {

    private JdbcAccountDao sut;

    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        sut = new JdbcAccountDao(jdbcTemplate);
    }

    @Test
    public void createAccountTest () {
        Account account1 = new Account();
        BigDecimal balance1 = new BigDecimal("1000.00");
        account1.setBalance(balance1);
        account1.setUserId(1333);
        account1.setAccountId(2333);
        Account actualResult = sut.createAccount(account1);

        Assert.assertEquals(account1, actualResult);
    }

    @Test
    public void findByUsernameTest() {
        User user1 = new User();
        user1.setUsername("user1");
        user1.setPassword("pw1");
        user1.setActivated(true);
        user1.setId(1333);

        Account account1 = new Account();
        BigDecimal balance1 = new BigDecimal("1000.00");
        account1.setBalance(balance1);
        account1.setUserId(1333);
        account1.setAccountId(2333);

        Account account2 = new Account();
        BigDecimal balance2 = new BigDecimal("1000.00");
        account2.setBalance(balance1);
        account2.setUserId(1333);
        account2.setAccountId(2444);

        List<Account> expectedResult = new ArrayList<>();
        expectedResult.add(account1);
        expectedResult.add(account2);

        List<Account> actualResult = sut.findByUsername("user1");
        Assert.assertEquals(expectedResult, actualResult);
    }


}