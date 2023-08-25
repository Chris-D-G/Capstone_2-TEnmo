package com.techelevator.tenmo.model;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

public class TransferApprovalDTO {

    private int transferId;
    @NotNull
    @Positive
    private BigDecimal amount;

    private String from;
    @NotEmpty
    private String to;
    @NotEmpty
    private boolean approve;

    private String status;


}
