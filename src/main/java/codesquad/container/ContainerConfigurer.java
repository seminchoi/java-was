package codesquad.container;

import java.util.List;

public interface ContainerConfigurer {
    List<String> getTargetPackages();

    void addTargetPackages(String... targetPackages);
}
