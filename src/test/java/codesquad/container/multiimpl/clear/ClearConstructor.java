package codesquad.container.multiimpl.clear;

import codesquad.container.Component;

public class ClearConstructor {
    @Component
    static class ClearConstructorInner {
        private final MultiImplInterface multiImplInterface;

        public ClearConstructorInner(MultiImplInterface multiImplInterfaceImpl1) {
            this.multiImplInterface = multiImplInterfaceImpl1;
        }
    }

    public interface MultiImplInterface {
    }

    @Component
    public static class MultiImplInterfaceImpl1 implements MultiImplInterface {
    }

    @Component
    public static class MultiImplInterfaceImpl2 implements MultiImplInterface {
    }
}
