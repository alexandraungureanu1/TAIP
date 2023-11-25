package ro.uaic.info.aset.dataprovider.Interfaces;

import java.util.Map;

public interface DataProviderFactory {
    DataProvider createDataProvider(Map<String, String> config);
}