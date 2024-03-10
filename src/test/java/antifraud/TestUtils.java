package antifraud;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

public class TestUtils {
    public static MockHttpServletRequestBuilder createPostRequest(String path,
                                                                  Object request,
                                                                  ObjectMapper mapper) throws JsonProcessingException {
        String requestAsJson = mapper.writeValueAsString(request);
        return post(path).contentType(MediaType.APPLICATION_JSON)
                .content(requestAsJson);
    }

    public static MockHttpServletRequestBuilder createPutRequest(String path,
                                                                 Object request,
                                                                 ObjectMapper mapper) throws JsonProcessingException {
        String requestAsJson = mapper.writeValueAsString(request);
        return put(path).contentType(MediaType.APPLICATION_JSON)
                .content(requestAsJson);
    }

    public static <T> T deserializeToCollectionType(ResultActions result, ObjectMapper mapper, TypeReference<T> type) throws Exception {
        String responseAsString = result.andReturn().getResponse().getContentAsString();
        return mapper.readValue(responseAsString, type);
    }

    public static <T> T deserializeToType(ResultActions result, Class<T> type, ObjectMapper mapper) throws Exception {
        String responseAsString = result.andReturn().getResponse().getContentAsString();
        return mapper.readValue(responseAsString, type);
    }

}
