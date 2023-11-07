package ro.uaic.info.aset.dataprovider.Beans;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DataIdentifier {
    private String id;

    @JsonCreator
    public DataIdentifier(@JsonProperty("id") String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}