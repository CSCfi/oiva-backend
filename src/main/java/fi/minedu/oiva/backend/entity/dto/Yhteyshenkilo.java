package fi.minedu.oiva.backend.entity.dto;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import fi.minedu.oiva.backend.entity.json.ObjectMapperSingleton;

import java.util.Objects;
import java.util.Set;

public class Yhteyshenkilo {
    String name;
    String title;
    String phoneNumber;
    String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public static Set<Yhteyshenkilo> fromJson(JsonNode node) {
        return ObjectMapperSingleton.mapper
            .convertValue(node,
                new TypeReference<Set<Yhteyshenkilo>>() {});
    }

    public static JsonNode toJson(Set<Yhteyshenkilo> yhteyshenkilot) {
        return ObjectMapperSingleton.mapper
            .convertValue(yhteyshenkilot, JsonNode.class);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Yhteyshenkilo that = (Yhteyshenkilo) o;
        return Objects.equals(name, that.name) &&
            Objects.equals(title, that.title) &&
            Objects.equals(phoneNumber, that.phoneNumber) &&
            Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, title, phoneNumber, email);
    }
}
