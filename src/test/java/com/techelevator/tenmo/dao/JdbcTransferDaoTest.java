package com.techelevator.tenmo.dao;

import com.techelevator.dao.BaseDaoTests;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferDTO;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class JdbcTransferDaoTest extends BaseDaoTests {

    private JdbcTransferDao sut;
    private static final Transfer TRANSFER_1 = new Transfer(3101,2101,2102, new BigDecimal("500"),"*Approved*");
    private static final Transfer TRANSFER_2 = new Transfer(3102,2104,2103, new BigDecimal("20000"), "*Approved*");
    private static final Transfer TRANSFER_3 = new Transfer(3103, 2103,2101, new BigDecimal("1531.52"), "*Pending*");
    private static final Transfer TRANSFER_4 = new Transfer(3104, 2101,2102, new BigDecimal("500"), "*Rejected*");
    private static final Account ACCOUNT_1 = new Account(2101,1101,new BigDecimal("1000"));
    private static final Account ACCOUNT_2 = new Account(2102,1102,new BigDecimal("2000")));
    private static final Account ACCOUNT_3 = new Account(2103,1103,new BigDecimal("3000"));
    private static final Account ACCOUNT_4 = new Account(2104,1104,new BigDecimal("4000"));

    private static final TransferDTO TRANSFER_DTO_1 = new TransferDTO();

    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        sut = new JdbcTransferDao(jdbcTemplate);
    }

    @Test
    public void getTransferDTOsByUsername() {

    }

    @Test
    public void getTransferDTOByID() {
    }

    @Test
    public void createTransfer() {
    }

    @Test
    public void updateTransferStatus() {
    }

    @Test
    public void completeTransaction() {
    }

    @Test
    public void getTransferByID() {
    }

    @Test
    public void getPendingDTOs() {
    }
}