package codesquad.util;

import codesquad.container.ContainerConfigurer;

import java.util.ArrayList;
import java.util.List;

public class ContainerUtils {
    public static ContainerConfigurer createContainerConfigurer(String... targets) {
        List<String> configTargets = new ArrayList<>();

        return new ContainerConfigurer() {
            @Override
            public List<String> getTargets() {
                for (String target : targets) {
                    configTargets.add(target);
                }
                return configTargets;
            }

            @Override
            public void addTargets(String... targets) {

            }
        };
    }
}
