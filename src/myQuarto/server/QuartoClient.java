package myQuarto.server;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import myQuarto.ServerApplication;
import myQuarto.netprot.QuartoPacket;

import org.lolhens.network.AbstractProtocol;
import org.lolhens.network.disconnect.DisconnectReason;
import org.lolhens.network.nio.Client;

import static myQuarto.netprot.QuartoProtocol.quartoPacket;
import static myQuarto.netprot.QuartoProtocol.quartoBroadcast;

public class QuartoClient extends Client<QuartoPacket> {
    QuartoServer assignedServer = null;
    
    boolean authenticated = false;
    String clientName = "";
    PublicKey clientPubKey;
    
    Logger log = Logger.getGlobal();

    public QuartoClient(Class<? extends AbstractProtocol> protocolClazz, QuartoServer quartoServer) {
        super(protocolClazz);
        assignedServer = quartoServer;
    }
    
    byte[] checkdata = null;

    public void receivePacket(QuartoPacket packet) {
        switch(packet.getAction()) {
        case "connect":
            Logger.getGlobal().log(Level.INFO, "connect packet received");
            
            if(!assignedServer.serverPassword.isEmpty()) {
                quartoPacket(this, "get_password");
            }
            else
            {
                quartoPacket(this, "request_identity");
            }
            return;
        case "send_password":
            Logger.getGlobal().log(Level.INFO, "password received");
            
            if(packet.getString("password").equals(assignedServer.serverPassword)) {
                quartoPacket(this, "request_identity");
                authenticated = true;
            }
            else
            {
                quartoPacket(this, "deny_password");
            }
            return;
        case "send_pubkey":
            Logger.getGlobal().log(Level.INFO, "pubkey received. generating checkdata");
            
            try {
                clientPubKey = packet.<PublicKey>getObject("key");
                
                Cipher clCDCipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING");
                
                clCDCipher.init(Cipher.ENCRYPT_MODE, clientPubKey);
                
                checkdata = new byte[256 - 11];
                Random rnd = new Random();
                
                rnd.nextBytes(checkdata);
                
                Logger.getGlobal().log(Level.INFO, "encrypting");
                
                byte[] pkEnc = clCDCipher.doFinal(checkdata);
                
                Logger.getGlobal().log(Level.INFO, "sending");
                
                quartoPacket(this, "confirm_srvkey", "cipher", pkEnc);
                
            } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            
            
            return;
        case "confirm_privkey":
            if(Arrays.equals(packet.<byte[]>getObject("cdata"), checkdata)) {
                Logger.getGlobal().log(Level.INFO, "client confirmed key");
                
                String name = assignedServer.dbGetNameForKey(clientPubKey);
                              
                if(name == null) {                
                    quartoPacket(this, "request_name");
                } else
                {
                    welcome(name);
                }
            }
            else
            {
                Logger.getGlobal().log(Level.INFO, "RSA auth failed...");
                quartoPacket(this, "authentication_fail");
            }
            return;
        
        case "confirm_name":
            if(packet.getString("name").length() < 3) {
                quartoPacket(this, "deny_name_checkout", "message", "Name has to be at least 3 characters long...");
                return;
            }
            
            if(assignedServer.dbIsNameTaken(packet.getString("name"))) {
                quartoPacket(this, "deny_name_checkout", "message", "Name already taken...");
                return;
            }
            
            if(packet.getString("name").equals("penis")) {
                quartoPacket(this, "deny_name_checkout", "message", "Nein Dominik so darfst du nicht heißen");
                return;
            }
            
            if(packet.getBoolean("do_checkout")) {
                Logger.getGlobal().log(Level.INFO, "Client requesting username: " + packet.getString("name"));
                
                assignedServer.dbSaveName(clientPubKey, packet.getString("name"));
                welcome(assignedServer.dbGetNameForKey(clientPubKey));
            }
            else
            {
                quartoPacket(this, "deny_name_checkout", "message", "Available!");
            }
            
            return;
        
        }

        try {
            this.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    private void welcome(String name) {
        quartoPacket(this, "welcome", "name", name);
        
        clientName = name;
        assignedServer.clients.add(this);
               
        quartoBroadcast(assignedServer.server, "client_join", "key", clientPubKey, "name", name);
        
        for(QuartoClient client : assignedServer.clients) {
            if(client != this && client.clientName != "") {
                //quartoPacket(this, "client_join", "key", client.clientPubKey, "name", client.clientName);
            }
        }
    }

    public void disconnect(DisconnectReason reason) {
        if(clientName != "") {
            quartoBroadcast(assignedServer.server, "client_part", "key", clientPubKey, "name", clientName);
        }
    }

}
