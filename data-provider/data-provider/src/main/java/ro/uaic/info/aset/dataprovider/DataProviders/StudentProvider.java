package ro.uaic.info.aset.dataprovider.DataProviders;

import ro.uaic.info.aset.dataprovider.Beans.DataIdentifier;
import ro.uaic.info.aset.dataprovider.Beans.GenericData;
import ro.uaic.info.aset.dataprovider.Interfaces.DataProvider;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StudentProvider implements DataProvider {

    private final DataSource dataSource;
    String url;
    String username;
    String password;
    String driverClassName;

    public StudentProvider(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public GenericData fetchData(DataIdentifier identifier) {
        try (Connection connection = dataSource.getConnection()) {

            String sqlQuery = "SELECT * FROM student WHERE id = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
                preparedStatement.setString(1, identifier.getId()); // Assuming id is an integer, adjust accordingly

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    // Process the result set, e.g., map to GenericData object
                    if (resultSet.next()) {
                        GenericData genericData = new GenericData();
                        genericData.addField("column1", resultSet.getString("column1")   );
                        genericData.addField("column2",resultSet.getString("column2")    );

                        return genericData;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle exceptions appropriately in a real application
        }
        return null;
    }
}
