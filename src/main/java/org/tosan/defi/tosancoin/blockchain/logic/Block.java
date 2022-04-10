package org.tosan.defi.tosancoin.blockchain.logic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Block {
    private  Integer index;
    private String previous_block_hash;
    private String nonce;

    // Bitcoin concept
    private String merkle;

    private Long timestamp;
    private Transaction[] transactions ;

    @Override
    public String toString() {
        return "Block{" +
                "\n\tindex=" + index +
                "\n\t, previous_block_hash='" + previous_block_hash + '\'' +
                "\n\t, merkle='" + merkle + '\'' +
                "\n\t, nonce='" + nonce + '\'' +
                "\n\t, timestamp=" + timestamp +
                "\n\t, transactions=" + Arrays.toString(transactions) +
                "\n}";
    }
}
