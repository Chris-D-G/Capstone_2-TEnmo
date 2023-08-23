package com.techelevator.tenmo.dao;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
@Component
public class JdbcTransferDao implements TransferDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Transfer> getTransfersByUsername(String username) {
        List<Transfer> transfersByUser = new ArrayList<>();
        /*
            SELECT transfer_id, amount
            FROM transfer
            WHERE sender_id=(SELECT account_id FROM account JOIN tenmo_user ON account.user_id = tenmo_user.user_id WHERE tenmo_user.username = '?')
            OR receiver_id = (SELECT account_id FROM account JOIN tenmo_user ON account.user_id = tenmo_user.user_id WHERE tenmo_user.username = '?');
        */

        String sql = "SELECT transfer_id,amount ,t1.username AS from,t2.username AS to\n" +
                "FROM transfer\n" +
                "JOIN account AS a1 ON transfer.sender_id = a1.account_id\n" +
                "JOIN account AS a2 ON transfer.receiver_id = a2.account_id\n" +
                "JOIN tenmo_user AS t1 on a1.user_id = t1.user_id\n" +
                "JOIN tenmo_user AS t2 on a2.user_id = t2.user_id\n" +
                "WHERE sender_id=(SELECT account_id FROM account JOIN tenmo_user ON account.user_id = tenmo_user.user_id WHERE tenmo_user.username = ?)\n" +
                "OR receiver_id = (SELECT account_id FROM account JOIN tenmo_user ON account.user_id = tenmo_user.user_id WHERE tenmo_user.username = ?)\n" +
                "ORDER BY transfer_id;";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql,username,username);
            while(results.next()){
                transfersByUser.add(mapRowSetToTransfer(results));
            }
        }catch (CannotGetJdbcConnectionException e){
            System.out.println("Cannot connect to database!");
        }catch (BadSqlGrammarException e){
            System.out.println("Bad Query: " + e.getSql() +
                    "\n"+e.getSQLException());
        }catch (DataIntegrityViolationException e){
            System.out.println("Data Integrity Violation: " + e.getMessage());
        }

        return transfersByUser;
    }

    @Override
    public Transfer getTransferByID(int id) {
        Transfer retreivedTransfer = null;
        /*  SELECT transfer_id,sender_id,receiver_id,approve_status,amount
            FROM transfer
            WHERE transfer_id = ?;
        */

        String sql = "SELECT transfer_id,sender_id,receiver_id,approve_status,amount " +
                     "FROM transfer " +
                     "WHERE transfer_id = ?;";
        try{
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql,id);
            if(results.next()){
                retreivedTransfer = mapRowSetToTransfer(results)
                retreivedTransfer.;
            }
        }catch (CannotGetJdbcConnectionException e){
            System.out.println("Cannot connect to database!");
        }catch (BadSqlGrammarException e){
            System.out.println("Bad Query: " + e.getSql() +
                    "\n"+e.getSQLException());
        }catch (DataIntegrityViolationException e){
            System.out.println("Data Integrity Violation" + e.getMessage());
        }


        return retreivedTransfer;
    }

    @Override
    public Transfer createTransfer(Transfer newTransfer) {
        Transfer createdTransfer = null;

        /*  INSERT INTO transfer (sender_id,receiver_id,approve_status,amount)
            VALUES(?,?,?,?) RETURNING transfer_id,
         */
        String sql = "INSERT INTO transfer (sender_id,receiver_id,approve_status,amount) " +
                "VALUES(?,?,?,?) RETURNING transfer_id;";
        try{
            int createdId = jdbcTemplate.queryForObject(sql, int.class,newTransfer.getSender_id(),newTransfer.getReceiver_id(),newTransfer.getStatus(),newTransfer.getAmount());
            createdTransfer = getTransferByID(createdId);

        }catch (CannotGetJdbcConnectionException e){
            System.out.println("Cannot connect to database!");
        }catch (BadSqlGrammarException e){
            System.out.println("Bad Query: " + e.getSql() +
                    "\n"+e.getSQLException());
        }catch (DataIntegrityViolationException e){
            System.out.println("Data Integrity Violation" + e.getMessage());
        }catch (NullPointerException e){
            System.out.println("Null value returned!");
        }


        return createdTransfer;
    }

    @Override
    public Transfer updateTransferStatus(String status, int id) {
        Transfer updatedTransfer = null;

        /*  UPDATE transfer SET  approve_status = ?
            WHERE transfer_id = ?;
         */
        String sql = "UPDATE transfer SET  approve_status = ? " +
                "WHERE transfer_id = ?;";
        try{
            int updatedID = jdbcTemplate.update(sql,status,id);
            updatedTransfer = getTransferByID(updatedID);


        }catch (CannotGetJdbcConnectionException e){
            System.out.println("Cannot connect to database!");
        }catch (BadSqlGrammarException e){
            System.out.println("Bad Query: " + e.getSql() +
                    "\n"+e.getSQLException());
        }catch (DataIntegrityViolationException e){
            System.out.println("Data Integrity Violation" + e.getMessage());
        }


        return updatedTransfer;
    }

    private Transfer mapRowSetToTransfer (SqlRowSet results){
        Transfer mappedTransfer = new Transfer();
        mappedTransfer.setTransfer_id(results.getInt("transfer_id"));
        mappedTransfer.setSender_id(results.getInt("sender_id"));
//        mappedTransfer.setSenderName(results.getString("t1.username"));
//        mappedTransfer.setReceiverName(results.getString("t2.username"));
        mappedTransfer.setReceiver_id(results.getInt("receiver_id"));
        mappedTransfer.setAmount(results.getBigDecimal("amount"));
        mappedTransfer.setStatus(results.getString("approve_status"));
        return  mappedTransfer;
    }



}