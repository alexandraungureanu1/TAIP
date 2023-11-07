package ro.uaic.info.aset.dataprovider.DataProviders;

import ro.uaic.info.aset.dataprovider.Beans.DataIdentifier;
import ro.uaic.info.aset.dataprovider.Interfaces.DataProvider;

public class DatabaseDataProvider implements DataProvider {
    public DatabaseDataProvider(String jdbcUrl, String username, String password) {
    }

    @Override
    public String fetchData(DataIdentifier identifier) {
        return "Data from DATABASE for ID: " + identifier.getId();
    }
}
