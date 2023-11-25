package ro.uaic.info.aset.dataprovider.Factories;

import org.springframework.stereotype.Service;
import ro.uaic.info.aset.dataprovider.DataProviders.RestApiDataProvider;
import ro.uaic.info.aset.dataprovider.Interfaces.DataProvider;
import ro.uaic.info.aset.dataprovider.Interfaces.DataProviderFactory;

import java.util.Map;

public class RestApiDataProviderFactory implements DataProviderFactory {
    @Override
    public DataProvider createDataProvider(Map<String, String> config) {
        String apiToken = config.get("apiToken");
        return new RestApiDataProvider(apiToken);
    }
}
