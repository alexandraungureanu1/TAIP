package ro.uaic.info.aset.dataprovider.Interfaces;

import ro.uaic.info.aset.dataprovider.Beans.DataIdentifier;
import ro.uaic.info.aset.dataprovider.Beans.GenericData;

public interface  DataProvider {

    // Define methods for data retrieval
    GenericData fetchData(DataIdentifier identifier);
}
