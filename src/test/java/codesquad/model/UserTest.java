package codesquad.model;

import codesquad.exception.HttpException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class UserTest {
    @Test
    void User_객체_생성_시_name이_null이나_blank면_예외가_발생한다() {
        assertThatThrownBy(() -> new User("semin", "1234", null))
                .isInstanceOf(HttpException.class);
        assertThatThrownBy(() -> new User("semin", "1234", ""))
                .isInstanceOf(HttpException.class);
    }

    @Test
    void User_객체_생성_시_password가_null이나_blank면_예외가_발생한다() {
        assertThatThrownBy(() -> new User("semin", null, "세민"))
                .isInstanceOf(HttpException.class);
        assertThatThrownBy(() -> new User("semin", "  ", "세민"))
                .isInstanceOf(HttpException.class);
    }

    @Test
    void User_객체_생성_시_userId가_null이나_blank면_예외가_발생한다() {
        assertThatThrownBy(() -> new User(null, "1234", "세민"))
                .isInstanceOf(HttpException.class);
        assertThatThrownBy(() -> new User("  ", "1234", "세민"))
                .isInstanceOf(HttpException.class);
    }
}
