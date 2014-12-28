package myQuarto.server;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import myQuarto.ServerApplication;
import myQuarto.netprot.QuartoPacket;

import org.lolhens.network.AbstractProtocol;
import org.lolhens.network.disconnect.DisconnectReason;
import org.lolhens.network.nio.Client;

import static myQuarto.netprot.QuartoProtocol.quartoPacket;

public class ClientConnection extends Client<QuartoPacket> {
    QuartoServer assignedServer = null;
    
    boolean authenticated = false;
    String clientName = "";
    
    Logger log = Logger.getGlobal();

    public ClientConnection(Class<? extends AbstractProtocol> protocolClazz, QuartoServer quartoServer) {
        super(protocolClazz);
        assignedServer = quartoServer;
    }

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
        }

        try {
            this.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void disconnect(DisconnectReason reason) {
        
    }

}
