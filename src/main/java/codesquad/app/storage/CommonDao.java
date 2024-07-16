package codesquad.app.storage;

import java.util.List;
import java.util.Optional;

public interface CommonDao <T, P> {
    void save(T t);
    Optional<T> findById(P id);
    List<T> findAll();
}
