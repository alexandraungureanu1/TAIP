package ro.uaic.info.aset.dataprovider.Services;

import org.springframework.stereotype.Service;
import ro.uaic.info.aset.dataprovider.Enums.DataSourceType;

import java.util.Map;

@Service
public class ConfigurationService {
    public Map<String, String> getConfigForSource(DataSourceType source) {

        return Map.of("username", "me");
    }
}