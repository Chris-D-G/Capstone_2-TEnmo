package com.techelevator.tenmo.model;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class TransferJsonObject {

    private int transferId;
    @NotNull
    private BigDecimal amount;

    private String from;
    @NotEmpty
    private String to;


    public TransferJsonObject() {
    }

    public int getTransferId() {
        return transferId;
    }

    public void setTransferId(int transferId) {
        this.transferId = transferId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    @Override
    public String toString() {
        return "TransferJsonReturn{" +
                "transferId=" + transferId +
                ", amount=" + amount +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                '}';
    }
}
