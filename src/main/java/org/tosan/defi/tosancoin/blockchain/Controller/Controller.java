package org.tosan.defi.tosancoin.blockchain.Controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.tosan.defi.tosancoin.blockchain.logic.Blockchain;
import org.tosan.defi.tosancoin.blockchain.logic.ChainNode;
import org.tosan.defi.tosancoin.blockchain.parameters.*;

import java.util.List;
import java.util.Set;

@RestController
@Slf4j
public class Controller {

    @Autowired
    Blockchain blockchain;

    @GetMapping("/mine")
    public @ResponseBody
    MineResponse mine() {
        return blockchain.block_mine();
    }

    @GetMapping("/links")
    public @ResponseBody
    Set<ChainNode> getLinks() {
        return blockchain.getNeighbour_nodes();
    }

    @GetMapping("/nodes/resolve")
    public @ResponseBody
    ResolveResponse Resolve() {
        ResolveResponse resolveResponse = new ResolveResponse();

        if(blockchain.Resolve_confilict())
            resolveResponse.setMessage("Our chain was replaced");

        else
            resolveResponse.setMessage("Our chain is authoritative");
        resolveResponse.setChain(blockchain.getChain());
        return resolveResponse;
    }

    @PostMapping("/transactions/new")
    public @ResponseBody
    NewTnxRepone new_transaction(@RequestBody NewTnxRequet newTnxRequet) {
        return blockchain.new_transaction(newTnxRequet.getSender(),newTnxRequet.getRecipient(),newTnxRequet.getAmount());
    }

    @PostMapping("/nodes/register")
    public @ResponseBody
    RegisterNodeResponse register_nodes(@RequestBody RegisterNodeRequest registerNodeRequest) {
        RegisterNodeResponse registerNodeResponse = RegisterNodeResponse.builder()
                .message("Error: Please supply a valid list of nodes")
                .build();
        if(registerNodeRequest == null || registerNodeRequest.getNodes().size()==0)
            return registerNodeResponse;
        blockchain.Register_node(registerNodeRequest);
        registerNodeResponse.setMessage("New nodes have been added");
        registerNodeResponse.setAdded_nodes(blockchain.getNeighbour_nodes().size());
        return registerNodeResponse;
    }
    @GetMapping("/chain")
    public @ResponseBody
    ChainResponse chain() {
        ChainResponse chainResponse = new ChainResponse();
        chainResponse.setBlocks(blockchain.getChain());
        chainResponse.setChain_length(blockchain.getChain().size());
        return chainResponse;
    }
}
