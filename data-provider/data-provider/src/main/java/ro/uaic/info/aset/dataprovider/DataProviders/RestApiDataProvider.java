package ro.uaic.info.aset.dataprovider.DataProviders;

import ro.uaic.info.aset.dataprovider.Beans.DataIdentifier;
import ro.uaic.info.aset.dataprovider.Beans.GenericData;
import ro.uaic.info.aset.dataprovider.Interfaces.DataProvider;

public class RestApiDataProvider implements DataProvider {
    public RestApiDataProvider(String apiToken) {
    }

    @Override
    public GenericData fetchData(DataIdentifier identifier) {
        GenericData personData = new GenericData();

        // Adding fields for a person
        personData.addField("firstName", "Alice");
        personData.addField("lastName", "Smith");
        personData.addField("age", 30);
        personData.addField("isStudent", false);

        return personData;
    }
}
