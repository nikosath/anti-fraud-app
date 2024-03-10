package antifraud.domain.datastore;

import antifraud.error.ErrorEnum;
import io.vavr.control.Either;

import java.util.List;

public interface IIpAddressEntityDatastore {
    Either<ErrorEnum, IpAddressEntity> createIpAddress(String ip);

    long countByIp(String ip);

    boolean existsByIp(String ip);

    List<IpAddressEntity> getAllIpAddressesOrderById();

    Either<ErrorEnum, IpAddressEntity> deleteIpAddress(String ip);

}