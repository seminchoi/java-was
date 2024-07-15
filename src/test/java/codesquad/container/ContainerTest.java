package codesquad.container;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ContainerTest {
    private Container container;

    @Nested
    class 동일한_타입의_구현체가_여러개_일_때 {
        @Test
        public void 생성자의_매개변수로도_명확한_타입을_찾을수_없으면_예외가_발생한다() {
            ContainerConfigurer containerConfigurer =
                    createContainerConfigurer(List.of("codesquad.container.multiimpl.ambiguous"));
            container = new Container(containerConfigurer);

            assertThatThrownBy(() -> container.init())
                    .isInstanceOf(RuntimeException.class);
        }

        @Test
        public void 생성자의_매개변수로_타입을_지정하면_예외가_발생하지_않는다() {
            ContainerConfigurer containerConfigurer =
                    createContainerConfigurer(List.of("codesquad.container.multiimpl.clear"));

            container = new Container(containerConfigurer);
            container.init();

            Object clearConstructor = container.getComponent("clearConstructorInner");
            assertNotNull(clearConstructor);
        }
    }

    static ContainerConfigurer createContainerConfigurer(List<String> targetPackages) {
        return new ContainerConfigurer() {
            @Override
            public List<String> getTargets() {
                return targetPackages;
            }

            @Override
            public void addTargets(String... targets) {

            }
        };
    }
}
