import codesquad.template.DynamicHtml;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

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
        String template = "{dh:if=\"${condition}\"}True content{end-if} {dh:else}False content{end-else}";

        String result = dynamicHtml.process(template);
        assertEquals("False content", result);
    }

    @Test
    void If_블록이_올바르게_작동하는지_확인한다() {
        String template = "{dh:if=\"${condition}\"}True content{end-if} {dh:else}False content{end-else}";

        dynamicHtml.setArg("condition", true);
        String result = dynamicHtml.process(template);
        assertEquals("True content", result);

        dynamicHtml.setArg("condition", false);
        result = dynamicHtml.process(template);
        assertEquals("False content", result);
    }

    @Test
    void text_블록이_올바르게_작동하는지_확인한다() {
        String template = "Hello, {dh:text=\"${person.name}\"}!";

        dynamicHtml.setArg("person", woowa);

        String result = dynamicHtml.process(template);
        assertEquals("Hello, woowa!", result);
    }

    @Test
    void each_블록에_인자를_등록하지_않으면_빈문자열로_대체한다() {
        String template = "People: {dh:each=\"person : ${people}\"}${person.name}, {end-each}";

        String result = dynamicHtml.process(template);

        assertEquals("People: ", result);
    }

    @Test
    void each_블록이_올바르게_작동하는지_확인한다() {
        String template = "People: {dh:each=\"person : ${people}\"}${person.name}, {end-each}";

        dynamicHtml.setArg("people", people);

        String result = dynamicHtml.process(template);
        assertEquals("People: woowa, semin, name, ", result);
    }

    @Test
    void 여러_블록이_섞여있을_때_올바르게_작동하는지_확인한다() {
        String template =
                "{dh:if=\"${showPeople}\"}" +
                        "People: {dh:each=\"person : ${people}\"}${person.name}, {end-each}" +
                        "{end-if} {dh:else}No people to display{end-else}";

        dynamicHtml.setArg("showPeople", true);
        dynamicHtml.setArg("people", people);

        String result = dynamicHtml.process(template);
        assertEquals("People: woowa, semin, name, ", result);

        dynamicHtml.setArg("showPeople", false);
        result = dynamicHtml.process(template);
        assertEquals("No people to display", result);
    }

    private static class Simple {
        private String name;

        Simple(String name) {
            this.name = name;
        }
    }
}