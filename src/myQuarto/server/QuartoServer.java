package myQuarto.server;

import java.io.IOException;
import java.security.PublicKey;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
    
    protected List<QuartoClient> clients = new LinkedList<>();
    protected Server<QuartoPacket> server = new Server<QuartoPacket>(QuartoProtocol.class);
    
    private Connection databaseConnection;
    
    public void run() {
        
        try {
            server.bind(1248);
            
            server.setClientFactory(this);
            
            server.setReceiveHandler((client, packet) -> ((QuartoClient)client).receivePacket(packet));
            server.setDisconnectHandler((client, reason) -> ((QuartoClient)client).disconnect(reason));
            
            org.h2.Driver.load();
            try {
                String url = "jdbc:h2:quarto_server";
                String user = "sa";
                String pwds = "filepwd userpwd";
                
                databaseConnection = DriverManager.getConnection(url, user, pwds);
                
                databaseConnection.createStatement().execute("CREATE TABLE IF NOT EXISTS users(key BINARY(294) PRIMARY KEY, name VARCHAR(255));");
            } catch (SQLException e) {
                Logger.getGlobal().log(Level.SEVERE, "SQL Exception", e);
            }
            
            
            while(server.isAlive()) {
                System.out.println(System.in.read());
            }
        } catch (IOException e) {
            Logger.getGlobal().log(Level.SEVERE, "IO Exception", e);
        }
    }

    @Override
    public AbstractClient<QuartoPacket> newClient(Class<? extends AbstractProtocol<QuartoPacket>> protocol) {
        Logger.getGlobal().log(Level.INFO, "Client connected...");
        
        QuartoClient c = new QuartoClient(protocol, this);
                
        return c;
    }
    
    protected String dbGetNameForKey(PublicKey key) {
        try {
            PreparedStatement stmt = databaseConnection.prepareStatement("SELECT name FROM users WHERE key = ?");
            
            stmt.setBytes(1, key.getEncoded());
            
            ResultSet res = stmt.executeQuery();
            if(res.next()) {
                String name = res.getString("name");
                
                res.close();
                stmt.close();
                
                return name;
            }
            else
            {
                return null;
            }
        } catch (SQLException e) {
            Logger.getGlobal().log(Level.SEVERE, "SQL Exception", e);
        }
        return null;
    }

    public boolean dbIsNameTaken(String name) {
        try {
            PreparedStatement stmt = databaseConnection.prepareStatement("SELECT COUNT(*) FROM users WHERE name LIKE ?");
            
            stmt.setString(1, name);
            
            ResultSet res = stmt.executeQuery();
            res.next();
            int count = res.getInt(1);
            
            res.close();
            stmt.close();
            
            return (count != 0);
        } catch (SQLException e) {
            Logger.getGlobal().log(Level.SEVERE, "SQL Exception", e);
        }
        return true;
    }

    public void dbSaveName(PublicKey key, String name) {
        try {
            PreparedStatement stmt = databaseConnection.prepareStatement("INSERT INTO users(key, name) VALUES(?, ?)");
            
            stmt.setBytes(1, key.getEncoded());
            stmt.setString(2, name);
            
            int affected = stmt.executeUpdate();
            System.out.println(affected);
            
            stmt.close();
            
            return;
        }
        catch (SQLException e) {
            Logger.getGlobal().log(Level.SEVERE, "SQL Exception", e);
        }
    }
}
