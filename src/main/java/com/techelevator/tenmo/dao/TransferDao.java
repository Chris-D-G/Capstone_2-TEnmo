package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferDTO;

import java.math.BigDecimal;
import java.util.List;

public interface TransferDao {

    List<TransferDTO> getTransferDTOsByUsername(String username);
    TransferDTO getTransferDTOByID(int id);

    Transfer getTransferByID(int id);

    TransferDTO createTransfer(Transfer newTransfer, int senderUserId, int receiverUserId);

    TransferDTO updateTransferStatus(String status, int id);

    void completeTransaction(BigDecimal amount, int senderAccountId, int receiverAccountId);

    List<TransferDTO> getPendingDTOs (String username);



}


