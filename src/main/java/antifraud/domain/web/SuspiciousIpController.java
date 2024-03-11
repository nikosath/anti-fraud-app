package antifraud.domain.web;

import antifraud.common.Uri;
import antifraud.common.WebUtils;
import antifraud.domain.datastore.IIpAddressEntityDatastore;
import antifraud.domain.datastore.IpAddressEntity;
import antifraud.error.ErrorEnum;
import antifraud.error.Result;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SuspiciousIpController {

    private static final String IP_ADDRESS_REGEXP = "^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}$";
    private final IIpAddressEntityDatastore datastore;

    @PostMapping(Uri.API_ANTIFRAUD_SUSPICIOUS_IP)
    public ResponseEntity<IpAddressResponse> createIpAddress(@Valid @RequestBody IpAddressRequest req) {
        log.debug("saveSuspiciousIp for req: " + req);
        Result<ErrorEnum, IpAddressEntity> result = datastore.createIpAddress(req.ip());
        if (result.isSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(IpAddressResponse.fromIpAddressEntity(result.getSuccess()));
        }
        return WebUtils.errorToResponseEntity(result.getError());
    }

    @GetMapping(Uri.API_ANTIFRAUD_SUSPICIOUS_IP)
    public List<IpAddressResponse> getAllIpAddresses() {
        log.debug("getAllIpAddresses");
        List<IpAddressEntity> ipAddresss = datastore.getAllIpAddressesOrderById();
        return ipAddresss.stream()
                .map(ipAddressEntity -> IpAddressResponse.fromIpAddressEntity(ipAddressEntity))
                .toList();
    }

    @DeleteMapping(Uri.API_ANTIFRAUD_SUSPICIOUS_IP + Uri.IP)
    public ResponseEntity<DeleteIpResponse> deleteIpAddress(@NotBlank @Pattern(regexp = IP_ADDRESS_REGEXP) @PathVariable String ip) {
        log.debug("deleteIpAddress for ip: " + ip);
        Result<ErrorEnum, IpAddressEntity> result = datastore.deleteIpAddress(ip);
        if (result.isSuccess()) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(DeleteIpResponse.fromIpAddressEntity(result.getSuccess()));
        }
        return WebUtils.errorToResponseEntity(result.getError());
    }

    public record IpAddressRequest(@NotBlank @Pattern(regexp = IP_ADDRESS_REGEXP) String ip) {
    }

    public record IpAddressResponse(Long id, String ip) {
        public static IpAddressResponse fromIpAddressEntity(IpAddressEntity ipAddressEntity) {
            return new IpAddressResponse(ipAddressEntity.getId(), ipAddressEntity.getIp());
        }
    }

    public record DeleteIpResponse(String status) {
        static DeleteIpResponse fromIpAddressEntity(IpAddressEntity entity) {
            return new DeleteIpResponse("IP %s successfully removed!".formatted(entity.getIp()));
        }
    }

}
