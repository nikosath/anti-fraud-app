package antifraud.error;

import java.util.NoSuchElementException;

/**
 * Stripped down version of Either type from Vavr libary
 *
 * @param <E> the error type when the Result is an Error
 * @param <S> the actual value type when the Result is a Success
 */
public interface Result<E, S> {

    static <E, S> Result<E, S> success(S object) {
        return new Success<>(object);
    }

    static <E, S> Result<E, S> error(E object) {
        return new Error<>(object);
    }

    boolean isSuccess();

    boolean isError();

    S getSuccess();

    E getError();

    static class Success<E, S> implements Result<E, S> {

        private final S value;

        private Success(S object) {
            this.value = object;
        }

        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        public boolean isError() {
            return false;
        }

        @Override
        public S getSuccess() {
            return value;
        }

        @Override
        public E getError() {
            throw new NoSuchElementException("getError() on Success");
        }
    }

    static class Error<E, S> implements Result<E, S> {

        private final E value;

        private Error(E object) {
            this.value = object;
        }

        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        public boolean isError() {
            return true;
        }

        @Override
        public S getSuccess() {
            throw new NoSuchElementException("getSuccess() on Error");
        }

        @Override
        public E getError() {
            return value;
        }
    }

}
