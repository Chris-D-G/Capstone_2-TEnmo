package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferDTO;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping("/api/v1/")
public class TransferController {

    // instantiate the transfer DAO
    private final TransferDao transferDao;
    //instantiate the User DAO
    private final UserDao userDao;

    private final AccountDao accountDao;



    //Create constructor for Rest Controller
    public TransferController(TransferDao transferDao, UserDao userDao, AccountDao accountDao) {
        this.transferDao = transferDao;
        this.userDao = userDao;
        this.accountDao=accountDao;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "user/transfer")
    public TransferDTO createTransfer(@RequestBody @Valid TransferDTO transfer, Principal principal) {
        try{
            int senderUserId;
            int receiverUserId;
            if(!transfer.getTo().equals(principal.getName())){
                //This is the user sending money to another user
                //Set the sender account id by using the user dao method and principal
                senderUserId = userDao.findIdByUsername(principal.getName());
                //Set the receiver account id by using the user dao method and the transfer json object receiver name
                receiverUserId = userDao.findIdByUsername(transfer.getTo());
            }else{
                // This the user making a request from another user
                //Set the sender account id by using the user dao method and the transfer json object receiver name
                senderUserId  = userDao.findIdByUsername(transfer.getTo());
                //Set the receiver account id by using the user dao method and principal
                receiverUserId= userDao.findIdByUsername(principal.getName());
            }
            //Create a new transfer object
            Transfer newTransfer = new Transfer();
            //Check to see if the sender and receiver IDs match
            if(senderUserId!=receiverUserId){
                //Set the transfer amount by using the transfer json object amount
                newTransfer.setAmount(transfer.getAmount());
                return transferDao.createTransfer(newTransfer,senderUserId,receiverUserId);
            }else{
                //This exception will never be seen as it is thrown to the catch statement
                throw new ResponseStatusException(HttpStatus.I_AM_A_TEAPOT,"TEST: Cannot create a transfer to the same username");
            }
        }catch (ResponseStatusException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Cannot create a transfer to the same username");
        }catch (RuntimeException e){
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Unable to Create Transfer!");
        }
    }

    @GetMapping(path = "user/transfer/history")
    public List<TransferDTO> getAllTransfers(Principal principal) {
        //Extract username from logged in user
        String username = principal.getName();
        try{
            //returns a list of the JSON formatted object
            return transferDao.getTransferDTOsByUsername(username);
        }catch (RuntimeException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Unable to find transfer history!");
        }
    }
    @GetMapping(path = "user/transfer/pending")
    public List<TransferDTO> getAllPendingTransfers(Principal principal){
        //Extract username from logged in user
        String username = principal.getName();
        try{
            //returns a list of the JSON formatted object
            return transferDao.getPendingDTOs(username);
        }catch (RuntimeException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"No pending transfers found!");
        }
    }


    @GetMapping(path = "user/transfer/{id}")
    public TransferDTO getTransferJsonObjectById(@PathVariable int id){
        try{
            //return a JSON object with the necessary information
            return transferDao.getTransferDTOByID(id);
        }catch (RuntimeException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Unable to find specified transfer!");
        }
    }

    @PutMapping(path = "user/transfer/{id}")
    public void attemptTransaction(@PathVariable int transferId,String senderApproval, @RequestBody @Valid TransferDTO transfer, Principal principal){
         /*For an approved request it must check the requirements of
            The receiver's account balance is increased by the amount of the transfer.
            The sender's account balance is decreased by the amount of the transfer.
            I can't send more TE Bucks than I have in my account.
            I can't send a zero or negative amount.*/
        String senderName = transfer.getFrom();

        if(principal.getName().equals(senderName) && senderApproval.equals("Approve")){
            //pull the pending transfer from the database
            Transfer pendingTransfer = transferDao.getTransferByID(transferId);
            // get the sender account id from the transfer
            int senderAccountId = pendingTransfer.getSenderAccountId();
            // get the receiver account id from the transfer
            int receiverAccountID = pendingTransfer.getReceiverAccountId();
            // get the pending transfer amount from the transfer
            BigDecimal pendingTransferAmount = pendingTransfer.getAmount();
            // get the balance of the sender by looking up the account id
            BigDecimal senderBalance = accountDao.findAccountById(senderAccountId).getBalance();
            // check senderBalance >0
            int checkPositiveBalance = pendingTransferAmount.compareTo(new BigDecimal("0"));
            // check pending amount is less than sender balance
            int checkTransferLessThanBalance = pendingTransferAmount.compareTo(senderBalance);
            if(checkPositiveBalance > 0 && checkTransferLessThanBalance <= 0){ //ADD approval from user
                //proceed by creating a transaction that adds money to receiver and subtracts from sender
                //BigDecimal amount, int senderAccountId, int receiverAccountId
                transferDao.completeTransaction(pendingTransferAmount,senderAccountId,receiverAccountID);
                transferDao.updateTransferStatus("*Approved",transferId);

            }else{
                // if the conditions are not met
                transferDao.updateTransferStatus("*Rejected",transferId);

                //error message
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request");

            }
        }




    }



}
