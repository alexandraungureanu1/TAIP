package ro.uaic.info.aset.dataprovider.Services;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ConfigurationService {
    public Map<String, String> getConfigForSource(String source) {

        return Map.of("username", "me");
    }
}