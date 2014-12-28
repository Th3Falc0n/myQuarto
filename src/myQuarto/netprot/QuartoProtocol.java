package myQuarto.netprot;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.lolhens.network.nio.Client;
import org.lolhens.network.protocol.AbstractBufferedProtocol;

public class QuartoProtocol extends AbstractBufferedProtocol<QuartoPacket>{

    @SuppressWarnings("unchecked")
    @Override
    protected QuartoPacket readPacket(ByteBuffer arg0) {
        arg0.rewind();
                
        QuartoPacket ret = new QuartoPacket("");
        
        try {
            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(arg0.array()));
            ret.data = (HashMap<String, Object>) in.readObject();
        } catch (ClassNotFoundException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        Logger.getGlobal().log(Level.INFO, "Decoded packet with action \"" + ret.getAction() + "\"");
        
        return ret;
    }

    @Override
    protected ByteBuffer wrapPacket(QuartoPacket arg0) {
        Logger.getGlobal().log(Level.INFO, "Wrapping packet with action \"" + arg0.getAction() + "\"");
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        try {
            ObjectOutputStream outs = new ObjectOutputStream(out);
            outs.writeObject(arg0.data);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return ByteBuffer.wrap(out.toByteArray());
    }

    public static void quartoPacket(Client<QuartoPacket> c, String action, Object... pairs) {
        
        QuartoPacket pack = new QuartoPacket(action);
        
        if(pairs.length % 2 != 0) throw new IllegalArgumentException("Objects are not in tuples");
        
        for(int i = 0; i < pairs.length; i+=2) {
            if(!(pairs[i] instanceof String)) throw new IllegalArgumentException("Key is not a string");
            if(!(pairs[i+1] instanceof Serializable)) throw new IllegalArgumentException("Value is not Serializable");
            
            pack.put((String)pairs[i], pairs[i+1]);
        }
        
        Logger.getGlobal().log(Level.INFO, "Sending packet with action \"" + action + "\"");
        
        c.send(pack);
    }
}
