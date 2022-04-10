package org.tosan.defi.tosancoin.blockchain.parameters;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
public class NodeListResponse {
    private Set<String> nodes;
}
