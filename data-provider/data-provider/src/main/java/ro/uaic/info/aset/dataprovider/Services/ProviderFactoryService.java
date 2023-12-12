package ro.uaic.info.aset.dataprovider.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Service;
import ro.uaic.info.aset.dataprovider.DataProviders.StudentProvider;
import ro.uaic.info.aset.dataprovider.Interfaces.DataProvider;

import javax.sql.DataSource;
import java.util.Map;

@Service
public class ProviderFactoryService  {

    private DataSource dataSource;

    @Autowired
    public ProviderFactoryService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public DataProvider createStudentProvider(Map<String, String> config) {
        String url = config.get("url");
        String username = config.get("username");
        String password = config.get("password");
        String driverClassName = config.get("driverClassName");

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName(driverClassName);

        return new StudentProvider(dataSource);
    }
}