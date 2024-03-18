package antifraud;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@RequiredArgsConstructor
public class TestHelper {

    private final ObjectMapper mapper;

    public MockHttpServletRequestBuilder createPostRequest(String path, Object request) throws JsonProcessingException {
        String requestAsJson = mapper.writeValueAsString(request);
        return post(path).contentType(MediaType.APPLICATION_JSON)
                .content(requestAsJson);
    }

    public MockHttpServletRequestBuilder createPutRequest(String path, Object request) throws JsonProcessingException {
        String requestAsJson = mapper.writeValueAsString(request);
        return put(path).contentType(MediaType.APPLICATION_JSON)
                .content(requestAsJson);
    }

    public <T> T deserializeToCollectionType(ResultActions result, TypeReference<T> type) throws Exception {
        String responseAsString = result.andReturn().getResponse().getContentAsString();
        return mapper.readValue(responseAsString, type);
    }

    public <T> T deserializeToType(ResultActions result, Class<T> type) throws Exception {
        String responseAsString = result.andReturn().getResponse().getContentAsString();
        return mapper.readValue(responseAsString, type);
    }

    public enum TestBehaviorEnum {
        SUCCEEDS, RETURNS_2_ENTITIES
    }

    public static class Constants {
        public static final String VALID_CARD_NUMBER = "4000008449433403";
        public static final String VALID_IP = "169.254.123.220";

    }
}
