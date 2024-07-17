package codesquad.container;

import java.util.List;

public interface ContainerConfigurer {
    List<String> getTargets();

    void addTargets(String... targets);
}
