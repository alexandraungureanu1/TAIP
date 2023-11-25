package ro.uaic.info.aset.dataprovider.Services;


import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DataCache {
    private final Map<String, String> cache = new ConcurrentHashMap<>();

    public String getCachedData(String key) {
        return cache.get(key);
    }

    public void cacheData(String key, String data) {
        cache.put(key, data);
    }

    public void evictCache(String key) {
        cache.remove(key);
    }

    public void clearCache() {
        cache.clear();
    }
}
