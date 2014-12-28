package myQuarto;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;

import org.lolhens.network.AbstractClient;
import org.lolhens.network.IConnectHandler;
import org.lolhens.network.IDisconnectHandler;
import org.lolhens.network.ProtocolProvider;
import org.lolhens.network.disconnect.DisconnectReason;
import org.lolhens.network.disconnect.Refused;
import org.lolhens.network.disconnect.Timeout;
import org.lolhens.network.nio.Client;

import myQuarto.clientpanes.PaneServerConnect;
import myQuarto.netprot.QuartoPacket;
import myQuarto.netprot.QuartoProtocol;
import static myQuarto.netprot.QuartoProtocol.quartoPacket;

public class ClientApplication extends JFrame {
    private static final long serialVersionUID = 1L;
    
    public static void main(String... args) {
        Logger.getGlobal().setLevel(Level.FINEST);
        
        new ClientApplication().setVisible(true);
    }
    
    Socket sock;
    Client<QuartoPacket> client;
    
    public ClientApplication() {
        client = new Client<>(QuartoProtocol.class);

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
                
                quartoPacket(client, "connect");
            }
        });
        
        setContentPane(new PaneServerConnect(new BiConsumer<String, Boolean>() {
            @Override
            public void accept(String t, Boolean b) {
                tryConnect(t, b.booleanValue());
            }
        }));
        setSize(400, 150);
    }
    
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
            
            ((PaneServerConnect)getContentPane()).setStatus("Joining...");
            break; 
        }
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
