package antifraud.domain.datastore;

import antifraud.error.ErrorEnum;
import antifraud.error.Result;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FakeIpAddressEntityDatastore implements IIpAddressEntityDatastore {

    long idSequence = 0L;
    private Map<Long, IpAddressEntity> idToIpAddressEntity = new HashMap<>();
    private Map<String, IpAddressEntity> ipToIpAddressEntity = new HashMap<>();

    @Override
    public synchronized Result<ErrorEnum, IpAddressEntity> createIpAddress(String ip) {
        IpAddressEntity entity = IpAddressEntity.withIp(ip);
        idSequence += 1;
        entity.setId(idSequence);
        idToIpAddressEntity.put(entity.getId(), entity);
        ipToIpAddressEntity.put(entity.getIp(), entity);
        return Result.success(entity);
    }

    @Override
    public synchronized long countByIp(String ip) {
        return existsByIp(ip) ? 0 : 1;
    }

    @Override
    public synchronized boolean existsByIp(String ip) {
        return ipToIpAddressEntity.containsKey(ip);
    }

    @Override
    public synchronized List<IpAddressEntity> getAllIpAddressesOrderById() {
        return ipToIpAddressEntity.values().stream().sorted().toList();
    }

    @Override
    public synchronized Result<ErrorEnum, IpAddressEntity> deleteIpAddress(String ip) {
        IpAddressEntity entity = ipToIpAddressEntity.get(ip);
        if (entity == null) {
            return Result.error(ErrorEnum.ENTITY_NOT_FOUND);
        }
        ipToIpAddressEntity.remove(ip);
        idToIpAddressEntity.remove(entity.getId());
        return Result.success(entity);
    }

}
