package org.tosan.defi.tosancoin.blockchain.parameters;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.tosan.defi.tosancoin.blockchain.logic.Block;

import java.util.List;

@Data
@NoArgsConstructor
@ToString
public class ResolveResponse {
    private String message ;
    private List<Block> chain;
}
