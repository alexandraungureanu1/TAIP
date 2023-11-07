package ro.uaic.info.aset.dataprovider.Interfaces;

import ro.uaic.info.aset.dataprovider.Beans.DataIdentifier;

public interface  DataProvider {

    // Define methods for data retrieval
    String fetchData(DataIdentifier identifier);
}
