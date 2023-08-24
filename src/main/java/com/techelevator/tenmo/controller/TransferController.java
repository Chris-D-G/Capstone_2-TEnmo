package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.JdbcUserDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferJsonObject;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
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



    //Create constructor for Rest Controller
    public TransferController(TransferDao transferDao, UserDao userDao) {
        this.transferDao = transferDao;
        this.userDao = userDao;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "user/transfer")
    public TransferJsonObject createTransfer(@RequestBody @Valid TransferJsonObject transfer, Principal principal) {
        try{
            //Create a new transfer object
            Transfer newTransfer = new Transfer();
            //Set the sender account id by using the user dao method and principal
            newTransfer.setSenderAccountId(userDao.findIdByUsername(principal.getName()));
            //Set the receiver account id by using the user dao method and the transfer json object receiver name
            newTransfer.setReceiverAccountId(userDao.findIdByUsername(transfer.getTo()));
            //Set the transfer amount by using the transfer json object amount
            newTransfer.setAmount(transfer.getAmount());
            return transferDao.createTransfer(newTransfer);

        }catch (RuntimeException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Unable to Create Transfer!");
        }
    }

    @GetMapping(path = "user/transfer/history")
    public List<TransferJsonObject> getTransfers(Principal principal) {
        String username = principal.getName();
        try{
            return transferDao.getTransfersByUsername(username);
        }catch (RuntimeException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Unable to find transfer history!");
        }
    }

    @GetMapping(path = "user/transfer/{id}")
    public TransferJsonObject getTransferById(@PathVariable int id){
        try{
            return transferDao.getTransferByID(id);
        }catch (RuntimeException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Unable to find specified transfer!");
        }
    }



}
