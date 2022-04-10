package org.tosan.defi.tosancoin.cli.command;

import org.bouncycastle.util.encoders.Hex;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.tosan.defi.tosancoin.blockchain.logic.Blockchain;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@ShellComponent
public class PasswordManager {
    @Autowired
    private StringEncryptor stringEncryptor;


    @ShellMethod("sha256")
    public static   String sha256(@ShellOption({"-P", "--phrase"}) String phrase) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(phrase.getBytes(StandardCharsets.UTF_8));
            String block_sha256 = new String(Hex.encode(hash));
            return block_sha256;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
