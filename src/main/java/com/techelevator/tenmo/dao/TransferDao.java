package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.util.List;

public interface TransferDao {

    List<Transfer> getTransfersByUsername(String username);
    Transfer getTransferByID(int id);

    Transfer createTransfer(Transfer newTransfer);

    Transfer updateTransferStatus(String status, int id);



}


