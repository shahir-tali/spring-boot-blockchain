package org.tosan.defi.tosancoin.blockchain.parameters;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class RegisterNodeRequest {
    private List<String> nodes;
}
