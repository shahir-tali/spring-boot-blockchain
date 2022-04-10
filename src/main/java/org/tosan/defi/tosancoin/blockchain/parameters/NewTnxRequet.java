package org.tosan.defi.tosancoin.blockchain.parameters;

import lombok.Data;

@Data
public class NewTnxRequet {
    private String sender;
    private String recipient ;
    private Double amount;
}
