package antifraud.domain.datastore;

import antifraud.error.ErrorEnum;
import antifraud.error.FailedPreconditionException;
import antifraud.error.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static antifraud.error.ErrorEnum.*;

@Repository
@RequiredArgsConstructor
public class IpAddressEntityDatastore implements IIpAddressEntityDatastore {

    private final IIpAddressEntityRepo repo;

    @Override
    public Result<ErrorEnum, IpAddressEntity> createIpAddress(String ip) {
        if (repo.existsByIp(ip)) {
            return Result.error(ENTITY_ALREADY_EXISTS);
        }
        IpAddressEntity saved = repo.save(IpAddressEntity.withIp(ip));
        return Result.success(saved);
    }

    @Override
    public long countByIp(String ip) {
        return repo.countByIp(ip);
    }

    @Override
    public boolean existsByIp(String ip) {
        return repo.existsByIp(ip);
    }

    @Override
    public List<IpAddressEntity> getAllIpAddressesOrderById() {
        return repo.findAllByOrderByIdAsc();
    }

    @Override
    public Result<ErrorEnum, IpAddressEntity> deleteIpAddress(String ip) {
        long countByIp = repo.countByIp(ip);
        if (countByIp > 1) {
            throw new FailedPreconditionException(MULTIPLE_ENTITIES_FOUND);
        }
        if (countByIp == 0) {
            return Result.error(ENTITY_NOT_FOUND);
        }
        IpAddressEntity deleted = repo.deleteByIp(ip).get(0);
        return Result.success(deleted);
    }
}
