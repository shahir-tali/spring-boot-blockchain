package org.tosan.defi.tosancoin.blockchain.parameters;

import lombok.Data;
import lombok.ToString;
import org.tosan.defi.tosancoin.blockchain.logic.Block;

import java.util.List;

@Data
@ToString
public class ChainResponse {
    List<Block>  blocks;
    Integer chain_length;
}
