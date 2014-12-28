package myQuarto.server;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import myQuarto.netprot.QuartoPacket;
import myQuarto.netprot.QuartoProtocol;

import org.lolhens.network.AbstractClient;
import org.lolhens.network.AbstractProtocol;
import org.lolhens.network.IClientFactory;
import org.lolhens.network.nio.Server;

public class QuartoServer implements IClientFactory<QuartoPacket> {
    protected String serverPassword = "";    
    
    public void run() {
        Server<QuartoPacket> server = new Server<QuartoPacket>(QuartoProtocol.class);
        
        try {
            server.bind(1248);
            
            server.setClientFactory(this);
            
            server.setReceiveHandler((client, packet) -> ((ClientConnection)client).receivePacket(packet));
            server.setDisconnectHandler((client, reason) -> ((ClientConnection)client).disconnect(reason));
            
            while(server.isAlive()) {
                Thread.yield();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public AbstractClient<QuartoPacket> newClient(Class<? extends AbstractProtocol<QuartoPacket>> protocol) {
        Logger.getGlobal().log(Level.INFO, "Client connected...");
        return new ClientConnection(protocol, this);
    }
}
