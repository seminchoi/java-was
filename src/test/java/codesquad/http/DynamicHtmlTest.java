import codesquad.http.HttpResponse;
import codesquad.template.DynamicHtml;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

class DynamicHtmlTest {
    private DynamicHtml dynamicHtml;
    private Simple woowa;
    private Simple semin;
    private Simple name;
    private List<Simple> people;

    @BeforeEach
    void setUp() {
        dynamicHtml = new DynamicHtml();
        woowa = new Simple("woowa");
        semin = new Simple("semin");
        name = new Simple("name");
        people = Arrays.asList(woowa, semin, name);
    }

    @Test
    void If_블록의_인자를_등록하지_않으면_false로_간주한다() {
        dynamicHtml.setTemplate("/if_template.html");
        HttpResponse httpResponse = dynamicHtml.process();

        String result = new String(httpResponse.makeResponse());
        Assertions.assertThat(result).contains("False content");
    }

    @Test
    void If_블록이_올바르게_작동하는지_확인한다() {
        dynamicHtml.setTemplate("/if_template.html");
        dynamicHtml.setArg("condition", true);
        HttpResponse httpResponse = dynamicHtml.process();
        String result = new String(httpResponse.makeResponse());
        Assertions.assertThat(result).contains("True content");


        dynamicHtml.setArg("condition", false);
        httpResponse = dynamicHtml.process();
        result = new String(httpResponse.makeResponse());
        Assertions.assertThat(result).contains("False content");
    }

    @Test
    void text_블록이_올바르게_작동하는지_확인한다() {
        dynamicHtml.setTemplate("/text_template.html");
        dynamicHtml.setArg("person", woowa);

        HttpResponse httpResponse = dynamicHtml.process();
        String result = new String(httpResponse.makeResponse());

        Assertions.assertThat(result).contains("Hello, woowa!");
    }

    @Test
    void each_블록에_인자를_등록하지_않으면_빈문자열로_대체한다() {
        dynamicHtml.setTemplate("/each_template.html");

        HttpResponse httpResponse = dynamicHtml.process();
        String result = new String(httpResponse.makeResponse());

        Assertions.assertThat(result).contains("People: ");
    }

    @Test
    void each_블록이_올바르게_작동하는지_확인한다() {
        dynamicHtml.setTemplate("/each_template.html");
        dynamicHtml.setArg("people", people);

        HttpResponse httpResponse = dynamicHtml.process();
        String result = new String(httpResponse.makeResponse());

        Assertions.assertThat(result).contains("People: woowa, semin, name, ");
    }

    @Test
    void 여러_블록이_섞여있을_때_올바르게_작동하는지_확인한다() {
        dynamicHtml.setTemplate("/complex_template.html");
        dynamicHtml.setArg("showPeople", true);
        dynamicHtml.setArg("people", people);

        HttpResponse httpResponse = dynamicHtml.process();
        String result = new String(httpResponse.makeResponse());
        Assertions.assertThat(result).contains("People: woowa, semin, name, ");

        dynamicHtml.setArg("showPeople", false);
        httpResponse = dynamicHtml.process();
        result = new String(httpResponse.makeResponse());
        Assertions.assertThat(result).contains("No people to display");
    }

    private static class Simple {
        private String name;

        Simple(String name) {
            this.name = name;
        }
    }
}