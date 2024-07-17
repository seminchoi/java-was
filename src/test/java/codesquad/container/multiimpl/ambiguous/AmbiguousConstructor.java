package codesquad.container.multiimpl.ambiguous;

import codesquad.container.Component;
import codesquad.container.multiimpl.clear.ClearConstructor;

public class AmbiguousConstructor {
    @Component
    static class AmbiguousConstructorInner {
        private final MultiImplInterface multiImplInterface;

        public AmbiguousConstructorInner(MultiImplInterface multiImplInterface) {
            this.multiImplInterface = multiImplInterface;
        }
    }

    public interface MultiImplInterface {
    }

    @Component
    public static class MultiImplInterfaceImpl1 implements ClearConstructor.MultiImplInterface {
    }

    @Component
    public static class MultiImplInterfaceImpl2 implements ClearConstructor.MultiImplInterface {
    }
}
