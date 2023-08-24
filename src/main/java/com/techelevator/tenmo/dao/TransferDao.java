package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferJsonObject;

import java.util.List;

public interface TransferDao {

    List<TransferJsonObject> getTransfersByUsername(String username);
    TransferJsonObject getTransferByID(int id);

    TransferJsonObject createTransfer(Transfer newTransfer,int senderUserId, int receiverUserId);

    TransferJsonObject updateTransferStatus(String status, int id);



}


