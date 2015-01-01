package myQuarto;

import java.awt.Container;
import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.interfaces.RSAKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.JFrame;

import org.lolhens.network.AbstractClient;
import org.lolhens.network.IConnectHandler;
import org.lolhens.network.IDisconnectHandler;
import org.lolhens.network.ProtocolProvider;
import org.lolhens.network.disconnect.DisconnectReason;
import org.lolhens.network.disconnect.Refused;
import org.lolhens.network.disconnect.Timeout;
import org.lolhens.network.nio.Client;

import myQuarto.clientpanes.PaneConnecting;
import myQuarto.clientpanes.PaneNameRequest;
import myQuarto.clientpanes.PaneQuartoGame;
import myQuarto.clientpanes.PaneServerConnect;
import myQuarto.netprot.QuartoPacket;
import myQuarto.netprot.QuartoProtocol;
import static myQuarto.netprot.QuartoProtocol.quartoPacket;

public class ClientApplication extends JFrame {
    private static final long serialVersionUID = 1L;
    
    public static void main(String... args) {        
        Logger.getGlobal().setFilter(new Filter() {
            @Override
            public boolean isLoggable(LogRecord record) {
                // TODO Auto-generated method stub
                return Options.ENABLE_LOGGING;
            }
        });
        
        new ClientApplication().setVisible(true);
    }
    
    Socket sock;
    Client<QuartoPacket> client;
    String clientName = "";
    
    PaneQuartoGame gamePane = new PaneQuartoGame();
    
    @Override
    public void setContentPane(Container contentPane) {
        super.setContentPane(contentPane);
        super.validate();
    }
    
    public ClientApplication() {
        client = new Client<>(QuartoProtocol.class);
        
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        client.setReceiveHandler((c, packet) -> this.receivePacket(packet));
        
        client.setDisconnectHandler(new IDisconnectHandler<QuartoPacket>() {
            
            @Override
            public void onDisconnect(ProtocolProvider<QuartoPacket> protocolProvider, DisconnectReason r) {
                Logger.getGlobal().log(Level.INFO, "Disconnected");
                
                if(r instanceof Refused || r instanceof Timeout) {
                    ((PaneServerConnect)getContentPane()).setStatus("Can't reach server...");
                }
            }
        });
        
        client.setConnectHandler(new IConnectHandler<QuartoPacket>() {
            @Override
            public void onConnect(AbstractClient<QuartoPacket> c) {
                Logger.getGlobal().log(Level.INFO, "Connection to established");
                
                quartoPacket(client, "connect", "den", "server", "hart", new byte[] {1,2,3,4,5,6});
            }
        });
        
        setContentPane(new PaneServerConnect(new BiConsumer<String, Boolean>() {
            @Override
            public void accept(String t, Boolean b) {
                tryConnect(t, b.booleanValue());
            }
        }));
        setSize(500, 150);
    }
    
    KeyPair localKeyPair;
    
    private void receivePacket(QuartoPacket packet) {
        switch(packet.getAction()) {
        case "get_password":
            Logger.getGlobal().log(Level.INFO, "Server requesting password");
            
            quartoPacket(client, "send_password", "password", ((PaneServerConnect)getContentPane()).getEnteredPassword());
            break;
        case "deny_password":
            ((PaneServerConnect)getContentPane()).setStatus("Wrong password...");
            
            try {
                client.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            break;
        case "request_identity":
            Logger.getGlobal().log(Level.INFO, "Server requesting identity");
            
            setContentPane(new PaneConnecting());
            
            ((PaneConnecting)getContentPane()).setStatus("Loading keypair...");
            
            localKeyPair = loadKeyPair();
            
            if(localKeyPair == null) {
                ((PaneConnecting)getContentPane()).setStatus("Generating keypair...");
                
                localKeyPair = generateKeyPair();
                saveKeyPair(localKeyPair);
            }
            
            quartoPacket(client, "send_pubkey", "key", localKeyPair.getPublic());      
            
            ((PaneConnecting)getContentPane()).setStatus("Authenticating...");
            
            break; 
        case "confirm_srvkey":
            Logger.getGlobal().log(Level.INFO, "Server requesting key confirmation");
            try {
                Cipher clPubkeyCipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING");
                
                clPubkeyCipher.init(Cipher.DECRYPT_MODE, localKeyPair.getPrivate());
                
                byte[] pkDec = clPubkeyCipher.doFinal(packet.<byte[]>getObject("cipher"));
                
                quartoPacket(client, "confirm_privkey", "cdata", pkDec);
                
            } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            
            break;
        case "authentication_fail":
            Logger.getGlobal().log(Level.INFO, "RSA auth failed...");
            setContentPane(new PaneServerConnect(new BiConsumer<String, Boolean>() {
                @Override
                public void accept(String t, Boolean b) {
                    tryConnect(t, b.booleanValue());
                }
            }));
            
            ((PaneServerConnect)getContentPane()).setStatus("RSA auth failed...");
            
            
            break;
        case "request_name":
            Logger.getGlobal().log(Level.INFO, "Server requesting nickname...");
            setContentPane(new PaneNameRequest((n) -> confirmName(n, false), (n) -> confirmName(n, true)));
            
            break;
        case "deny_name_checkout":
            ((PaneNameRequest)getContentPane()).setStatus(packet.getString("message"));
            
            break;
        case "welcome":
            Logger.getGlobal().log(Level.INFO, "Logged in...");
            clientName = packet.getString("name");
            setContentPane(gamePane);
            
            break;
        case "client_join":
            if(!packet.getString("name").equals(clientName)) {
                gamePane.addOnlineClient(packet.getString("name"));
            }
            break;
        case "client_part":
            gamePane.removeOnlineClient(packet.getString("name"));
                        
            break;
        }
    }
    
    private String confirmName(String name, boolean doCheckout) {
        quartoPacket(client, "confirm_name", "name", name, "do_checkout", doCheckout);
        return !doCheckout ? "Checking..." : "Setting name...";
    }
    
    private void saveKeyPair(KeyPair pair) {
        
    }
    
    private KeyPair loadKeyPair() {
        return null;
    }
    
    private KeyPair generateKeyPair() {
        KeyPairGenerator kpg;
        try {
            kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            return kpg.genKeyPair();
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
    
    public void tryConnect(String address, boolean doHS) {
        try {
            Logger.getGlobal().log(Level.INFO, "Trying to connect to " + address);
            
            client.connect(address, 1248);
        } catch (Exception e) {
            Logger.getGlobal().log(Level.INFO, "Can't connect to " + address + ". Assuming invalid address. " + e.getMessage());
            
            ((PaneServerConnect)getContentPane()).setStatus("Invalid address...");
        }
    }
}
