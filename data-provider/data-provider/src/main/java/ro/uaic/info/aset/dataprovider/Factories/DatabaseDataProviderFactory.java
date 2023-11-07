package ro.uaic.info.aset.dataprovider.Factories;

import ro.uaic.info.aset.dataprovider.DataProviders.DatabaseDataProvider;
import ro.uaic.info.aset.dataprovider.Interfaces.DataProvider;
import ro.uaic.info.aset.dataprovider.Interfaces.DataProviderFactory;

import java.util.Map;

public class DatabaseDataProviderFactory implements DataProviderFactory {
    @Override
    public DataProvider createDataProvider(Map<String, String> config) {
        String jdbcUrl = config.get("jdbcUrl");
        String username = config.get("username");
        String password = config.get("password");
        return new DatabaseDataProvider(jdbcUrl, username, password);
    }
}