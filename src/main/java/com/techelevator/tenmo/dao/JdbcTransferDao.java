package com.techelevator.tenmo.dao;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferJsonObject;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
@Component
public class JdbcTransferDao implements TransferDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<TransferJsonObject> getTransfersByUsername(String username) {
        /*  ---Return all transfers associated with the logged in user---
            SELECT transfer_id,amount ,t1.username AS from,t2.username AS to
            FROM transfer
            JOIN account AS a1 ON transfer.sender_account_id = a1.account_id
            JOIN account AS a2 ON transfer.receiver_account_id = a2.account_id
            JOIN tenmo_user AS t1 on a1.user_id = t1.user_id
            JOIN tenmo_user AS t2 on a2.user_id = t2.user_id
            WHERE sender_account_id=(SELECT account_id FROM account JOIN tenmo_user ON account.user_id = tenmo_user.user_id WHERE tenmo_user.username = 'chris')
            OR receiver_account_id = (SELECT account_id FROM account JOIN tenmo_user ON account.user_id = tenmo_user.user_id WHERE tenmo_user.username = 'chris')
            ORDER BY transfer_id;
         */

        String sql ="SELECT transfer_id,amount ,t1.username AS from,t2.username AS to\n" +
                    "FROM transfer\n" +
                    "JOIN account AS a1 ON transfer.sender_account_id = a1.account_id\n" +
                    "JOIN account AS a2 ON transfer.receiver_account_id = a2.account_id\n" +
                    "JOIN tenmo_user AS t1 on a1.user_id = t1.user_id\n" +
                    "JOIN tenmo_user AS t2 on a2.user_id = t2.user_id\n" +
                    "WHERE sender_account_id=(SELECT account_id FROM account JOIN tenmo_user ON account.user_id = tenmo_user.user_id WHERE tenmo_user.username = ?)\n" +
                    "OR receiver_account_id = (SELECT account_id FROM account JOIN tenmo_user ON account.user_id = tenmo_user.user_id WHERE tenmo_user.username = ?)\n" +
                    "ORDER BY transfer_id;";
        List<TransferJsonObject> returnedTransfers = new ArrayList<>();
        try{
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql,username,username);
            while(results.next()){
                returnedTransfers.add(mapSqlRowsetToJsonObject(results));
            }

        }catch (CannotGetJdbcConnectionException e){
            throw new RuntimeException("Unable to contact the database!", e);
        }catch (BadSqlGrammarException e){
            throw new RuntimeException("Bad SQL query: " + e.getSql()
                                        +"\n"+e.getSQLException(), e);
        }catch (DataIntegrityViolationException e){
            throw new RuntimeException("Database Integrity Violation", e);
        }
        return returnedTransfers;
    }

    @Override
    public TransferJsonObject getTransferByID(int id) {
        /*  -- Return transfer by specific transfer ID
            SELECT transfer_id,amount ,t1.username AS from,t2.username AS to
            FROM transfer
            JOIN account AS a1 ON transfer.sender_account_id = a1.account_id
            JOIN account AS a2 ON transfer.receiver_account_id = a2.account_id
            JOIN tenmo_user AS t1 on a1.user_id = t1.user_id
            JOIN tenmo_user AS t2 on a2.user_id = t2.user_id
            WHERE transfer_id = ?;
         */

        String sql = "SELECT transfer_id,amount ,t1.username AS from,t2.username AS to\n" +
                     "FROM transfer\n" +
                     "JOIN account AS a1 ON transfer.sender_account_id = a1.account_id\n" +
                     "JOIN account AS a2 ON transfer.receiver_account_id = a2.account_id\n" +
                     "JOIN tenmo_user AS t1 on a1.user_id = t1.user_id\n" +
                     "JOIN tenmo_user AS t2 on a2.user_id = t2.user_id\n" +
                     "WHERE transfer_id = ?;";

        //create object to return and set to null
        TransferJsonObject returnedTransfer = null;

        try{
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql,id);
            if(results.next()){
                returnedTransfer = mapSqlRowsetToJsonObject(results);
            }
        }catch (CannotGetJdbcConnectionException e){
            throw new RuntimeException("Unable to contact the database!", e);
        }catch (BadSqlGrammarException e){
            throw new RuntimeException("Bad SQL query: " + e.getSql()
                    +"\n"+e.getSQLException(), e);
        }catch (DataIntegrityViolationException e){
            throw new RuntimeException("Database Integrity Violation", e);
        }
        return returnedTransfer;
    }

    @Override
    public TransferJsonObject createTransfer(Transfer newTransfer) {
        /*
            --create transfer (easy way)
            INSERT INTO transfer(sender_account_id, receiver_account_id, approve_status, amount)
            VALUES(?,?,'*Pending*',?)RETURNING transfer_id;
         */

        TransferJsonObject createdTransfer = null;
        String sql = "INSERT INTO transfer(sender_account_id, receiver_account_id, approve_status, amount)\n" +
                "VALUES(?,?,?,?)RETURNING transfer_id;";

        try{
            int newTransferId = jdbcTemplate.queryForObject(sql,Integer.class,
                                newTransfer.getSenderAccountId(),
                                newTransfer.getReceiverAccountId(),
                                newTransfer.getStatus(),
                                newTransfer.getAmount());
            if(newTransferId>0){
                createdTransfer = getTransferByID(newTransferId);
            }
        }catch (CannotGetJdbcConnectionException e){
            throw new RuntimeException("Unable to contact the database!", e);
        }catch (BadSqlGrammarException e){
            throw new RuntimeException("Bad SQL query: " + e.getSql()
                    +"\n"+e.getSQLException(), e);
        }catch (DataIntegrityViolationException e){
            throw new RuntimeException("Database Integrity Violation", e);
        }catch (NullPointerException e){
            throw new RuntimeException("Null value returned", e);
        }
        return createdTransfer;
    }

    @Override
    public TransferJsonObject updateTransferStatus(String status, int id) {
        /*  -- update transfer Status using id
            UPDATE transfer SET approve_status = ?
            WHERE transfer_id = ?;
         */
        String sql = "UPDATE transfer SET approve_status = ?\n" +
                     "WHERE transfer_id = ?;";

        TransferJsonObject updatedTransfer = null;

        try{
            int updatedRows = jdbcTemplate.update(sql,status,id);
            if(updatedRows>0){
                updatedTransfer = getTransferByID(id);
            }
        }catch (CannotGetJdbcConnectionException e){
            throw new RuntimeException("Unable to contact the database!", e);
        }catch (BadSqlGrammarException e){
            throw new RuntimeException("Bad SQL query: " + e.getSql()
                    +"\n"+e.getSQLException(), e);
        }catch (DataIntegrityViolationException e){
            throw new RuntimeException("Database Integrity Violation", e);
        }

        return updatedTransfer;
    }

    private TransferJsonObject mapSqlRowsetToJsonObject (SqlRowSet results){
        TransferJsonObject mappedOutput = new TransferJsonObject();

        mappedOutput.setTransferId(results.getInt("transfer_id"));
        mappedOutput.setAmount(results.getBigDecimal("amount"));
        mappedOutput.setFrom(results.getString("from"));
        mappedOutput.setTo(results.getString("to"));

        return mappedOutput;
    }



//    @Override
//    public List<Transfer> getTransfersByUsername(String username) {
//        List<Transfer> transfersByUser = new ArrayList<>();
//        /*
//            SELECT transfer_id, amount
//            FROM transfer
//            WHERE sender_id=(SELECT account_id FROM account JOIN tenmo_user ON account.user_id = tenmo_user.user_id WHERE tenmo_user.username = '?')
//            OR receiver_id = (SELECT account_id FROM account JOIN tenmo_user ON account.user_id = tenmo_user.user_id WHERE tenmo_user.username = '?');
//        */
//
//        String sql = "SELECT transfer_id,amount ,t1.username AS from,t2.username AS to\n" +
//                "FROM transfer\n" +
//                "JOIN account AS a1 ON transfer.sender_id = a1.account_id\n" +
//                "JOIN account AS a2 ON transfer.receiver_id = a2.account_id\n" +
//                "JOIN tenmo_user AS t1 on a1.user_id = t1.user_id\n" +
//                "JOIN tenmo_user AS t2 on a2.user_id = t2.user_id\n" +
//                "WHERE sender_id=(SELECT account_id FROM account JOIN tenmo_user ON account.user_id = tenmo_user.user_id WHERE tenmo_user.username = ?)\n" +
//                "OR receiver_id = (SELECT account_id FROM account JOIN tenmo_user ON account.user_id = tenmo_user.user_id WHERE tenmo_user.username = ?)\n" +
//                "ORDER BY transfer_id;";
//        try {
//            SqlRowSet results = jdbcTemplate.queryForRowSet(sql,username,username);
//            while(results.next()){
//                transfersByUser.add(mapRowSetToTransfer(results));
//            }
//        }catch (CannotGetJdbcConnectionException e){
//            System.out.println("Cannot connect to database!");
//        }catch (BadSqlGrammarException e){
//            System.out.println("Bad Query: " + e.getSql() +
//                    "\n"+e.getSQLException());
//        }catch (DataIntegrityViolationException e){
//            System.out.println("Data Integrity Violation: " + e.getMessage());
//        }
//
//        return transfersByUser;
//    }
//
//    @Override
//    public Transfer getTransferByID(int id) {
//        Transfer retreivedTransfer = null;
//        /*  SELECT transfer_id,sender_id,receiver_id,approve_status,amount
//            FROM transfer
//            WHERE transfer_id = ?;
//        */
//
//        String sql = "SELECT transfer_id,sender_id,receiver_id,approve_status,amount " +
//                     "FROM transfer " +
//                     "WHERE transfer_id = ?;";
//        try{
//            SqlRowSet results = jdbcTemplate.queryForRowSet(sql,id);
//            if(results.next()){
//                retreivedTransfer = mapRowSetToTransfer(results)
//                retreivedTransfer.;
//            }
//        }catch (CannotGetJdbcConnectionException e){
//            System.out.println("Cannot connect to database!");
//        }catch (BadSqlGrammarException e){
//            System.out.println("Bad Query: " + e.getSql() +
//                    "\n"+e.getSQLException());
//        }catch (DataIntegrityViolationException e){
//            System.out.println("Data Integrity Violation" + e.getMessage());
//        }
//
//
//        return retreivedTransfer;
//    }
//
//    @Override
//    public Transfer createTransfer(Transfer newTransfer) {
//        Transfer createdTransfer = null;
//
//        /*  INSERT INTO transfer (sender_id,receiver_id,approve_status,amount)
//            VALUES(?,?,?,?) RETURNING transfer_id,
//         */
//        String sql = "INSERT INTO transfer (sender_id,receiver_id,approve_status,amount) " +
//                "VALUES(?,?,?,?) RETURNING transfer_id;";
//        try{
//            int createdId = jdbcTemplate.queryForObject(sql, int.class,newTransfer.getSender_id(),newTransfer.getReceiver_id(),newTransfer.getStatus(),newTransfer.getAmount());
//            createdTransfer = getTransferByID(createdId);
//
//        }catch (CannotGetJdbcConnectionException e){
//            System.out.println("Cannot connect to database!");
//        }catch (BadSqlGrammarException e){
//            System.out.println("Bad Query: " + e.getSql() +
//                    "\n"+e.getSQLException());
//        }catch (DataIntegrityViolationException e){
//            System.out.println("Data Integrity Violation" + e.getMessage());
//        }catch (NullPointerException e){
//            System.out.println("Null value returned!");
//        }
//
//
//        return createdTransfer;
//    }
//
//    @Override
//    public Transfer updateTransferStatus(String status, int id) {
//        Transfer updatedTransfer = null;
//
//        /*  UPDATE transfer SET  approve_status = ?
//            WHERE transfer_id = ?;
//         */
//        String sql = "UPDATE transfer SET  approve_status = ? " +
//                "WHERE transfer_id = ?;";
//        try{
//            int updatedID = jdbcTemplate.update(sql,status,id);
//            updatedTransfer = getTransferByID(updatedID);
//
//
//        }catch (CannotGetJdbcConnectionException e){
//            System.out.println("Cannot connect to database!");
//        }catch (BadSqlGrammarException e){
//            System.out.println("Bad Query: " + e.getSql() +
//                    "\n"+e.getSQLException());
//        }catch (DataIntegrityViolationException e){
//            System.out.println("Data Integrity Violation" + e.getMessage());
//        }
//
//
//        return updatedTransfer;
//    }
//
//    private Transfer mapRowSetToTransfer (SqlRowSet results){
//        Transfer mappedTransfer = new Transfer();
//        mappedTransfer.setTransfer_id(results.getInt("transfer_id"));
//        mappedTransfer.setSender_id(results.getInt("sender_id"));
////        mappedTransfer.setSenderName(results.getString("t1.username"));
////        mappedTransfer.setReceiverName(results.getString("t2.username"));
//        mappedTransfer.setReceiver_id(results.getInt("receiver_id"));
//        mappedTransfer.setAmount(results.getBigDecimal("amount"));
//        mappedTransfer.setStatus(results.getString("approve_status"));
//        return  mappedTransfer;
//    }



}