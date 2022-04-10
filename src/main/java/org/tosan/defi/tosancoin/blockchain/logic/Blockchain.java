package org.tosan.defi.tosancoin.blockchain.logic;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.tosan.defi.tosancoin.blockchain.parameters.*;
import org.w3c.dom.NodeList;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Component
@Data
public class Blockchain {
    private List<Block> chain = new ArrayList<>();
    private List<Transaction> current_transactions = new ArrayList<>();
    private String node_identifier;
    private Set<ChainNode> neighbour_nodes = new HashSet<>();


    @Autowired
    private RestTemplate restTemplate ;

    @PostConstruct
    public void init(){
        new_block("1",null,"1");
        node_identifier = UUID.randomUUID().toString().replace("-","");
    }

    public Block new_block(String nonce,String merkle, String previous_hash){
        Transaction[] transactions = new Transaction[current_transactions.size()];
        current_transactions.toArray(transactions);

        Block block = new Block();
        block.setIndex(chain.size()+1);
        block.setTimestamp(System.currentTimeMillis());
        block.setTransactions(transactions);
        block.setMerkle(merkle);
        block.setNonce(nonce);
        block.setPrevious_block_hash(previous_hash!=null ?  previous_hash : get_block_hash(get_last_block()));

        current_transactions.clear();
        chain.add(block);
        return block;
    }
    public NewTnxRepone new_transaction(String sender, String recipient , Double amount){
        Transaction transaction = new Transaction(sender,recipient,amount);
        current_transactions.add(transaction);
        Integer lastIndex = get_last_block().getIndex()+1;
        NewTnxRepone newTnxRepone = NewTnxRepone.builder()
                .message("Transaction will be addes block:"+lastIndex)
                .build();
        return newTnxRepone;
    }

    public static String get_block_hash(Block block){
        ObjectMapper objectMapper = new ObjectMapper();
        String block_json_string =  objectMapper.convertValue(block, HashMap.class).toString();
        return get_hash(block_json_string);
    }
    public static String get_hash(String value){
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            String block_sha256 = new String(Hex.encode(hash));
            return block_sha256;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
    public Block get_last_block(){
        return chain.get(chain.size()-1);
    }

    public Integer proof_of_work(String last_proof){
        Integer nounce = 0;
        while(!valid_proof(last_proof,nounce.toString()))
            nounce+=1;
        return nounce;
    }
    public static String makeMerkle(Transaction[] transactions){
        if(transactions.length == 1 )
            return Blockchain.get_hash(transactions[0].toString());
        Integer middle = transactions.length/2;
        String left = makeMerkle(Arrays.copyOfRange(transactions,0,middle));
        String right = makeMerkle(Arrays.copyOfRange(transactions,middle,transactions.length));
        return Blockchain.get_hash(left+right);
    }

    public MineResponse block_mine(){
        if(current_transactions.isEmpty()) {
            return MineResponse.builder()
                    .message("mempool is empty")
                    .build();
        }
        //Block last_block = get_last_block();

        Transaction[] transactions = new Transaction[current_transactions.size()];
        current_transactions.toArray(transactions);
        String merkle = makeMerkle(transactions);

        Integer pow =  proof_of_work(merkle);
        new_transaction("0",node_identifier,50.0);
        String previous_hash = get_block_hash(get_last_block());
        Block block = new_block(pow.toString(),merkle,previous_hash);
        return MineResponse.builder()
                .index(block.getIndex())
                .message("New Block Constructed")
                .previous_hash(block.getPrevious_block_hash())
                .merkle(merkle)
                .nounce(block.getNonce())
                .transactions(block.getTransactions())
                //.current_hash()
                .build();
    }

    public static Boolean valid_proof(String last_proof , String proof){
        StringBuilder builder = new StringBuilder();
        builder.append(last_proof).append(proof);
        String guess_hash =  get_hash(builder.toString());
        return guess_hash.substring(guess_hash.length()-4).equals("0000");
    }

    public static Boolean validate_chain(List<Block> chain){
        Block last_block = chain.get(0);
        int current_index = 1;
        while(current_index<chain.size()){
            Block block = chain.get(current_index);
            if(!block.getPrevious_block_hash().equals(get_block_hash(last_block)))
                return Boolean.FALSE;
            if(last_block.getTransactions().length>0 && !valid_proof(last_block.getMerkle(),last_block.getNonce()))
                return Boolean.FALSE;
            last_block = block;
            current_index+=1;
        }
        return Boolean.TRUE;
    }

    public void Register_node(RegisterNodeRequest registerNodeRequest){
        for(String node : registerNodeRequest.getNodes()) {
            ChainNode chainNode = ChainNode.builder()
                    .node(node)
                    .build();
            neighbour_nodes.add(chainNode);
        }
    }

   // @Scheduled(fixedRate = 20000)
    public Boolean Resolve_confilict(){

        Integer max_length = chain.size();
        List<Block> max_blocks = null;
        for(ChainNode node : neighbour_nodes){
            ResponseEntity<ChainResponse> response = null;
            try {
                response = restTemplate.exchange(new URI(node.getNode()+"/chain"), HttpMethod.GET,null, ChainResponse.class);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            if(response.getStatusCode() == HttpStatus.OK && response.hasBody()){
                ChainResponse chainResponse = response.getBody();
                if(max_length < chainResponse.getChain_length() && validate_chain(chainResponse.getBlocks())){
                    max_length = chainResponse.getChain_length();
                    max_blocks = chainResponse.getBlocks();
                }
            }
        }
        if(max_blocks != null){
            chain = max_blocks;
            return true;
        }
        return false;
    }
    //@Scheduled(fixedRate = 30000)
    public void RefreshNodes(){
        Set<ChainNode> newNeighbour = new HashSet<>();
        for(ChainNode node : neighbour_nodes) {
            ResponseEntity<NodeListResponse> response = null;
            try {
                response = restTemplate.exchange(new URI(node.getNode() + "/links"), HttpMethod.GET, null, NodeListResponse.class);
                if(response.getStatusCode() == HttpStatus.OK && response.hasBody()){
                    for(String nd: response.getBody().getNodes()){
                        newNeighbour.add(ChainNode.builder().node(nd).build());
                    }
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        if(!newNeighbour.isEmpty())
            neighbour_nodes.addAll(newNeighbour);
    }
}
