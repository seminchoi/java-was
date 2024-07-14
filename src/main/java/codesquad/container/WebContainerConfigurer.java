package codesquad.container;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WebContainerConfigurer implements ContainerConfigurer {
    private static final String DEFAULT_PACKAGE = "codesquad";

    private final List<String> targetPackages = new ArrayList<>();

    public WebContainerConfigurer() {
        targetPackages.add(DEFAULT_PACKAGE);
    }

    @Override
    public List<String> getTargetPackages() {
        return targetPackages;
    }

    @Override
    public void addTargetPackages(String... targetPackages) {
        this.targetPackages.addAll(Arrays.asList(targetPackages));
    }
}
