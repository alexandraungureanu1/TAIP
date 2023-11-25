package ro.uaic.info.aset.dataprovider.DataProviders;

import ro.uaic.info.aset.dataprovider.Beans.DataIdentifier;
import ro.uaic.info.aset.dataprovider.Beans.GenericData;
import ro.uaic.info.aset.dataprovider.Interfaces.DataProvider;

public class DatabaseDataProvider implements DataProvider {
    public DatabaseDataProvider(String jdbcUrl, String username, String password) {
    }

    @Override
    public GenericData fetchData(DataIdentifier identifier) {
        // Creating a GenericData instance
        GenericData data = new GenericData();

        // Adding fields
        data.addField("name", "John Doe");
        data.addField("age", 25);

        return data;
    }
}
