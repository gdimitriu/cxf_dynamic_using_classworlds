package application.routes;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.beans.Transient;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class JacksonTest {
    private String name;
    private String message;

    public JacksonTest() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public JacksonTest(String name, String message) {
        this.name = name;
        this.message = message;
    }
}
