package antifraud.error;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;

/**
 * Stripped down version of Either type from Vavr library
 *
 * @param <E> the error type when the Result is an Error
 * @param <S> the actual value type when the Result is a Success
 */
public abstract class Result<E, S> {

    public static <E, S> Result<E, S> success(S object) {
        return new Success<>(object);
    }

    public static <E, S> Result<E, S> error(E object) {
        return new Error<>(object);
    }

    public abstract boolean isSuccess();

    public abstract boolean isError();

    public abstract S getSuccess();

    public abstract E getError();

    public <U> Result<E, U> map(Function<S, U> mapper) {
//    public <U> Result<E, U> map(Function<? super S, ? extends U> mapper) {
        Objects.requireNonNull(mapper, "mapper is null");
        if (isSuccess()) {
            return Result.success(mapper.apply(getSuccess()));
        } else {
            return (Result<E, U>) this;
        }
    }

    static class Success<E, S> extends Result<E, S> {

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

    static class Error<E, S> extends Result<E, S> {

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
