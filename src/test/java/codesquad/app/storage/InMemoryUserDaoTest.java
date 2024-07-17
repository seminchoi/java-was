package codesquad.app.storage;

import codesquad.app.model.User;
import codesquad.exception.HttpException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class InMemoryUserDaoTest {
    private InMemoryUserDao userStorage;

    private User user1;

    @BeforeEach
    void setUp() {
        userStorage = new InMemoryUserDao();

        user1 = new User("semin", "1234", "세민");
    }

    @Test
    public void 사용자를_저장하면_인메모리에_저장된다() {
        userStorage.save(user1);

        Optional<User> foundUser = userStorage.findById(user1.getUserId());
        assertThat(foundUser.isPresent()).isTrue();
        assertThat(foundUser.get().getUserId()).isEqualTo(user1.getUserId());
    }

    @Test
    public void 같은_아이디로_사용자를_저장하면_예외가_발생한다() {
        userStorage.save(user1);

        assertThatThrownBy(()-> userStorage.save(user1))
                .isInstanceOf(HttpException.class);
    }

    @Test
    public void 동일_아이디로_동시에_회원가입을_요청해도_한_번만_가입이_성곤한다() {
        //TODO: 동시성 테스트 방법 공부하기
        ExecutorService executorService = Executors.newFixedThreadPool(50);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger throwCount = new AtomicInteger(0);


        for (int i = 0; i < 50; i++) {
            executorService.execute(() -> {
                try {
                    userStorage.save(user1);
                    successCount.incrementAndGet();
                } catch (HttpException e) {
                    throwCount.incrementAndGet();
                }
            });
        }

        Assertions.assertThat(successCount.get()).isEqualTo(1);
        Assertions.assertThat(throwCount.get()).isEqualTo(49);
    }
}
