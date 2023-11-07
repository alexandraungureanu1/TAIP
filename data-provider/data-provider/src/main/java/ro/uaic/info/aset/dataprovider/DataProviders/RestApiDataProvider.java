package ro.uaic.info.aset.dataprovider.DataProviders;

import ro.uaic.info.aset.dataprovider.Beans.DataIdentifier;
import ro.uaic.info.aset.dataprovider.Interfaces.DataProvider;

public class RestApiDataProvider implements DataProvider {
    public RestApiDataProvider(String apiToken) {
    }

    @Override
    public String fetchData(DataIdentifier identifier) {
        return "Data from REST API for ID: " + identifier.getId();
    }
}
