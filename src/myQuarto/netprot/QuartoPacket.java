package myQuarto.netprot;

import java.io.Serializable;
import java.util.HashMap;

public class QuartoPacket {
    protected HashMap<String, Object> data = new HashMap<>();
    
    public QuartoPacket(String action) {
        data.put("action", action);
    }
    
    public void put(String key, Object obj) {
        data.put(key, obj);
    }
    
    public<T> T getObject(String key) {
        return (T)data.get(key);
    }
    
    public int getInteger(String key) {
        return (Integer)data.get(key);
    }
    
    public byte getByte(String key) {
        return (Byte)data.get(key);
    }
    
    public String getString(String key) {
        return (String)data.get(key);
    }
    
    public boolean hasKey(String key) {
        return data.containsKey(key);
    }
    
    public boolean getBoolean(String key) {
        return (Boolean)data.get(key);
    }
    
    public String getAction() {
        return getString("action");
    }
}
