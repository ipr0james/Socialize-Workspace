package net.thenova.socialize.entities.modules;

import java.util.HashMap;
import java.util.Map;

public final class EntityValidation {

    //TODO PLS COMMENT THIS

    private final Map<String, Object> content = new HashMap<>();

    public boolean contains(String key){
        return content.containsKey(key);
    }

    public Object get(String key){
        return this.content.get(key.toLowerCase());
    }

    public <T> T get(String key, Class<T> clazz) {
        return clazz.cast(this.content.get(key));
    }

    public void set(String key, Object data){
        content.put(key.toLowerCase(), data);
    }

    public void add(String key){
        content.put(key, null);
    }

    public void unset(String key){
        content.remove(key.toLowerCase());
    }
}
