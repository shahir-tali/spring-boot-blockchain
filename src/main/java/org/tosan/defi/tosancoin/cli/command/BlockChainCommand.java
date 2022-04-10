package org.tosan.defi.tosancoin.cli.command;

import org.bouncycastle.util.encoders.Hex;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.tosan.defi.tosancoin.blockchain.logic.Blockchain;
import org.tosan.defi.tosancoin.blockchain.parameters.NewTnxRepone;
import org.tosan.defi.tosancoin.blockchain.parameters.RegisterNodeRequest;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

@ShellComponent
public class BlockChainCommand {
    @Autowired
    private Blockchain blockchain;

    @ShellMethod("transaction")
    public String transaction(
            @ShellOption({"-S", "--sender"}) String sender,
            @ShellOption({"-R", "--recipient"}) String recipient,
            @ShellOption({"-A", "--amount"}) Double amount) {
        NewTnxRepone newTnxRepone = blockchain.new_transaction(sender,recipient,amount);
        return newTnxRepone.toString();
    }
    @ShellMethod("node")
    public void node(@ShellOption({"-N", "--nodes"}) String nodes) {
        RegisterNodeRequest registerNodeRequest = new RegisterNodeRequest();
        registerNodeRequest.setNodes(Arrays.asList(nodes.split(",")));
        blockchain.Register_node(registerNodeRequest);
    }
    @ShellMethod("chain")
    public String chain() {
        return blockchain.getChain().toString();
    }
    @ShellMethod("mine")
    public String mine() {
        return blockchain.block_mine().toString();
    }
    @ShellMethod("resolve")
    public String resolve() {
        return blockchain.Resolve_confilict().toString();
    }
    @ShellMethod("links")
    public String links() {
        return blockchain.getNeighbour_nodes().toString();
    }
}
