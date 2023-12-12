package ro.uaic.info.aset.dataprovider.Services;

import org.springframework.stereotype.Service;
import ro.uaic.info.aset.dataprovider.Enums.DataSourceType;

import java.util.HashMap;
import java.util.Map;

@Service
public class ConfigurationService {
    // Set your actual values or obtain them dynamically
    private String dbUrl = "jdbc:postgresql://localhost:5432/student_db";
    private String dbUsername = "postgres";
    private String dbPassword = "admin";
    private String dbDriverClassName = "org.postgresql.Driver";

    public Map<String, String> getConfigForStudent() {

        Map<String, String> properties = new HashMap<>();
        properties.put("url", dbUrl);
        properties.put("username", dbUsername);
        properties.put("password", dbPassword);
        properties.put("driverClassName", dbDriverClassName);
        return properties;
    }
}