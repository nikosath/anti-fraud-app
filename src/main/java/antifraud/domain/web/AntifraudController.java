package antifraud.domain.web;

import antifraud.common.Uri;
import antifraud.domain.datastore.IIpAddressEntityDatastore;
import antifraud.domain.datastore.IpAddressEntity;
import antifraud.domain.service.TransactionValidation;
import antifraud.domain.service.TransactionValidation.ValidationResultEnum;
import antifraud.error.ErrorEnum;
import io.vavr.control.Either;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static antifraud.common.WebUtils.toResponseEntity;
import static antifraud.domain.service.TransactionValidation.ValidationResultEnum.INVALID_AMOUNT;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AntifraudController {

    public static final String IP_ADDRESS_REGEXP = "^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}$";
//    private final IAntifraudService service;
    private final IIpAddressEntityDatastore datastore;

    @PostMapping(Uri.API_ANTIFRAUD_TRANSACTION)
    public ResponseEntity<ValidateTransactionResponse> validateTransaction(@RequestBody ValidateTransactionRequest request) {
        log.debug("validateTransaction for request: " + request);
        var validationResult = TransactionValidation.validateTransaction(request.amount());
        if (INVALID_AMOUNT.equals(validationResult)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(new ValidateTransactionResponse(validationResult));
    }


    @PostMapping(Uri.API_ANTIFRAUD_SUSPICIOUS_IP)
    public ResponseEntity<IpAddressResponse> createIpAddress(@Valid @RequestBody IpAddressRequest req) {
        log.debug("saveSuspiciousIp for req: " + req);
        Either<ErrorEnum, IpAddressEntity> either = datastore.createIpAddress(req.ip());
        return either.map(ipAddressEntity -> IpAddressResponse.from(ipAddressEntity))
                .map(ipAddressResponse -> ResponseEntity.status(HttpStatus.CREATED).body(ipAddressResponse))
                .getOrElseGet(error -> toResponseEntity(error));
    }

    @GetMapping(Uri.API_ANTIFRAUD_SUSPICIOUS_IP)
    public List<IpAddressResponse> getAllIpAddresses() {
        log.debug("getAllIpAddresses");
        List<IpAddressEntity> ipAddresss = datastore.getAllIpAddresses();
        return ipAddresss.stream()
                .map(ipAddressEntity -> IpAddressResponse.from(ipAddressEntity))
                .toList();
    }

    @DeleteMapping("/api/antifraud/suspicious-ip/{ip}")
    public ResponseEntity<IpAddressResponse> deleteIpAddress(@Pattern(regexp = IP_ADDRESS_REGEXP) @PathVariable String ip) {
        log.debug("deleteIpAddress for ip: " + ip);
        Either<ErrorEnum, IpAddressEntity> either = datastore.deleteIpAddress(ip);
        return either.map(ipAddressEntity -> IpAddressResponse.from(ipAddressEntity))
                .map(ipAddressResponse -> ResponseEntity.status(HttpStatus.CREATED).body(ipAddressResponse))
                .getOrElseGet(error -> toResponseEntity(error));
    }

    public record IpAddressRequest(@Pattern(regexp = IP_ADDRESS_REGEXP) String ip) {
    }

    public record IpAddressResponse(Long id, String ip) {
        static IpAddressResponse from(IpAddressEntity ipAddressEntity) {
            return new IpAddressResponse(ipAddressEntity.getId(), ipAddressEntity.getIp());
        }
    }

    public record ValidateTransactionRequest(long amount) {
    }

    public record ValidateTransactionResponse(ValidationResultEnum result) {
    }
}
