package antifraud.transactionvalidation.datastore;

import antifraud.error.ErrorEnum;
import antifraud.error.Result;

import java.util.List;

public interface IIpAddressEntityDatastore {
    Result<ErrorEnum, IpAddressEntity> createIpAddress(String ip);

    long countByIp(String ip);

    boolean existsByIp(String ip);

    List<IpAddressEntity> getAllIpAddressesOrderById();

    Result<ErrorEnum, IpAddressEntity> deleteIpAddress(String ip);

}