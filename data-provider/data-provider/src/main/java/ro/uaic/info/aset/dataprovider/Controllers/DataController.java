package ro.uaic.info.aset.dataprovider.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ro.uaic.info.aset.dataprovider.Beans.DataIdentifier;
import ro.uaic.info.aset.dataprovider.Factories.DatabaseDataProviderFactory;
import ro.uaic.info.aset.dataprovider.Factories.RestApiDataProviderFactory;
import ro.uaic.info.aset.dataprovider.Interfaces.DataProvider;
import ro.uaic.info.aset.dataprovider.Interfaces.DataProviderFactory;
import ro.uaic.info.aset.dataprovider.Services.ConfigurationService;

import java.util.Map;

@RestController
@RequestMapping("/data")
public class DataController {
    private final ConfigurationService configurationService;

    @Autowired
    public DataController(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @PostMapping("/requestData")
    public String getData(@RequestParam String source, @RequestBody DataIdentifier dataIdentifier) {
        DataProviderFactory dataProviderFactory;
        Map<String, String> config = configurationService.getConfigForSource(source);

        switch (source) {
            case "restapi":
                dataProviderFactory = new RestApiDataProviderFactory();
                break;
            case "database":
                dataProviderFactory = new DatabaseDataProviderFactory();
                break;
            default:
                // Handle unknown source or provide a default factory
                dataProviderFactory = null;
        }

        DataProvider dataProvider = dataProviderFactory.createDataProvider(config);
        return dataProvider.fetchData(dataIdentifier);
    }

}