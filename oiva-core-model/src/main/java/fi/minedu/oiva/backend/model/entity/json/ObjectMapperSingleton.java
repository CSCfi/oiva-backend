package fi.minedu.oiva.backend.model.entity.json;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectMapperSingleton {
    public static final ObjectMapper mapper = new ObjectMapper();
}
