package org.tosan.defi.tosancoin.blockchain.parameters;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.tosan.defi.tosancoin.blockchain.logic.Transaction;

@Data
@Builder
@ToString
public class MineResponse {
    private String previous_hash;
    private Integer index;
    private String merkle;
    private String message;
    private String nounce;
    private Transaction[] transactions;
    private String current_hash;
}
