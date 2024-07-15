package codesquad.container;

public class ContainerHolder {
    private static Container CONTAINER;

    public static void init(ContainerConfigurer containerConfigurer) {
        CONTAINER = new Container(containerConfigurer);
        CONTAINER.init();
    }

    public static Container getContainer() {
        return CONTAINER;
    }
}
