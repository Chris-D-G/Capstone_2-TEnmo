package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/")
public class TransferController {

    private final TransferDao transferDao;


    public TransferController(TransferDao transferDao) {
        this.transferDao = transferDao;
    }

    @PostMapping(path = "user/transfer")
    public Transfer createTransfer(@RequestBody @Valid Transfer transfer, Principal principal) {
        return transferDao.createTransfer(transfer);
    }

    @GetMapping(path = "user/transfer/history")
    public List<Transfer> getTransfers(Principal principal) {
        String username = principal.getName();
        try{
            return transferDao.getTransfersByUsername(username);


        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Something is not working properly! " + e.getMessage());
        }
    }

    @GetMapping(path = "user/transfer/{id}")
    public Transfer getTransferById(@PathVariable int id){
        return transferDao.getTransferByID(id);
    }



}
