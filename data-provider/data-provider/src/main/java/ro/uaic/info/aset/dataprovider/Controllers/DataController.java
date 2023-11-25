package ro.uaic.info.aset.dataprovider.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.uaic.info.aset.dataprovider.Beans.DataIdentifier;
import ro.uaic.info.aset.dataprovider.Enums.DataSourceType;
import ro.uaic.info.aset.dataprovider.Factories.DatabaseDataProviderFactory;
import ro.uaic.info.aset.dataprovider.Factories.RestApiDataProviderFactory;
import ro.uaic.info.aset.dataprovider.Interfaces.DataProvider;
import ro.uaic.info.aset.dataprovider.Interfaces.DataProviderFactory;
import ro.uaic.info.aset.dataprovider.Services.ConfigurationService;
import ro.uaic.info.aset.dataprovider.Services.DataCache;

import java.util.Map;

@RestController
@RequestMapping("/data")
public class DataController {
    private final ConfigurationService configurationService;
    private final DataCache dataCache;

    @Autowired
    public DataController(ConfigurationService configurationService, DataCache dataCache) {
        this.configurationService = configurationService;
        this.dataCache = dataCache;
    }

    @PostMapping("/requestData")
    public ResponseEntity<String> getData(@RequestParam DataSourceType source, @RequestBody DataIdentifier dataIdentifier) {
        DataProviderFactory dataProviderFactory;
        Map<String, String> config = configurationService.getConfigForSource(source);

        String cachedData = dataCache.getCachedData(getCacheKey(source, dataIdentifier));
        if (cachedData != null) {
            return ResponseEntity.ok(cachedData);
        }
        
        switch (source) {
            case REST_API:
                dataProviderFactory = new RestApiDataProviderFactory();
                break;
            case DATABASE:
                dataProviderFactory = new DatabaseDataProviderFactory();
                break;
            default:
                // Handle unknown source or provide a default factory
                dataProviderFactory = null;
        }

        DataProvider dataProvider = dataProviderFactory.createDataProvider(config);


        try {
            var genericData = dataProvider.fetchData(dataIdentifier);

            // Check if data is found
            if (genericData != null && genericData.hasData()) {
                String json = genericData.toJson();
                dataCache.cacheData(getCacheKey(source, dataIdentifier),json);
                return ResponseEntity.ok(json);
            } else {
                // Handle case where no data is found
                return ResponseEntity.notFound().build();
            }
        } catch (UnsupportedOperationException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unsupported operation: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving data: " + e.getMessage());
        }
    }

    private String getCacheKey(DataSourceType source, DataIdentifier dataIdentifier) {
        // Assuming DataIdentifier has a meaningful toString() representation
        String identifierString = dataIdentifier.toString();

        // Customize this as needed based on your requirements for generating cache keys
        return source.name() + "_" + identifierString;
    }

}