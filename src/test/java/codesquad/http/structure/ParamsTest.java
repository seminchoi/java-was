package codesquad.http.structure;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ParamsTest {
    @Test
    public void 파라미터_문자열을_올바르게_파싱한다() {
        String paramString = "id=semin&name=세민";
        Params params = new Params(paramString);

        String id = params.get("id");
        String name = params.get("name");

        assertThat(id).isEqualTo("semin");
        assertThat(name).isEqualTo("세민");
    }

    @Test
    public void 파라미터가_키_값형식이_아니면_무시한다() {
        String paramString = "id&name=세민";
        Params params = new Params(paramString);

        String id = params.get("id");
        String name = params.get("name");

        assertThat(id).isNull();
        assertThat(name).isEqualTo("세민");
    }

    @Test
    public void 파라미터의_값에_equal문자가_여러개_들어가도_올바르게_파싱한다() {
        String paramString = "name=세민=세민";
        Params params = new Params(paramString);

        String name = params.get("name");

        assertThat(name).isEqualTo("세민=세민");
    }

    @Test
    public void 파라미터에_동일한_키가_여러개_있으면_최초_한_개만_저장한다() {
        String paramString = "name=세민&name=셈";
        Params params = new Params(paramString);

        String name = params.get("name");

        assertThat(name).isEqualTo("세민");
    }

    @Test
    public void 파라미터에_값이_없으면_Null을_저장한다() {
        String paramString = "name=";
        Params params = new Params(paramString);

        String name = params.get("name");

        assertThat(name).isNull();
    }
}
