package antifraud;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class TestUtils {
    public static MockHttpServletRequestBuilder createPostRequest(String path,
                                                                  Object request,
                                                                  ObjectMapper mapper) throws JsonProcessingException {
        String requestAsJson = mapper.writeValueAsString(request);
        return post(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestAsJson);
    }
}
