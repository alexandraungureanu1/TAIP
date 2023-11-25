package ro.uaic.info.aset.dataprovider.Beans;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

public class GenericData {
    private final Map<String, Object> fields = new HashMap<>();

    public void addField(String key, Object value) {
        fields.put(key, value);
    }

    public Object getField(String key) {
        return fields.get(key);
    }

    public String toJson() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(fields);
    }

    public static GenericData fromJson(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        GenericData genericData = new GenericData();
        JsonNode jsonNode = objectMapper.readTree(json);

        // Convert JSON fields to a map
        Map<String, Object> fields = objectMapper.convertValue(jsonNode, Map.class);
        genericData.fields.putAll(fields);

        return genericData;
    }

    public boolean hasData() {
        return !fields.isEmpty();
    }
}
