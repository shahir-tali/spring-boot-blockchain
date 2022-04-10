package org.tosan.defi.tosancoin.blockchain.logic;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    private String sender;
    private String recipient;
    private Double amont;
}
