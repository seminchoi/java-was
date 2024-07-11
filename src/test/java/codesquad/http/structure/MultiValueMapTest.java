package codesquad.http.structure;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class MultiValueMapTest {
    @Test
    void getFirst는_맵에_값을_저장하고_첫번째_값을_반환_받을_수_있다() {
        MultiValueMap<String, String> multiValueMap = new MultiValueMap<>();
        multiValueMap.put("key", "value1");
        multiValueMap.put("key", "value2");
        assertThat(multiValueMap.getFirst("key")).isEqualTo("value1");
    }

    @Test
    void getFirst는_맵에_값이_없을때_null을_반환한다() {
        MultiValueMap<String, String> multiValueMap = new MultiValueMap<>();
        assertThat(multiValueMap.getFirst("key")).isNull();
    }

    @Test
    void get은_맵에_값이_없을때_반환받으면_null을_반환한다() {
        MultiValueMap<String, String> multiValueMap = new MultiValueMap<>();
        assertThat(multiValueMap.get("key")).isEmpty();
    }

    @Test
    void 맵에_여러개의_값을_저장하고_받아올_수_있다() {
        MultiValueMap<String, String> multiValueMap = new MultiValueMap<>();
        multiValueMap.put("key", "value1");
        multiValueMap.put("key", "value2");
        assertThat(multiValueMap.get("key")).isEqualTo(List.of("value1", "value2"));
    }
}
