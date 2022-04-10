package org.tosan.defi.tosancoin.blockchain.parameters;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterNodeResponse {
    private String message ;
    private Integer added_nodes;
}
