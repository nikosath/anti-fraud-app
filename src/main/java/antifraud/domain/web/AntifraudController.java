package antifraud.domain.web;

import antifraud.common.Uri;
import antifraud.common.WebUtils;
import antifraud.domain.datastore.IIpAddressEntityDatastore;
import antifraud.domain.datastore.IpAddressEntity;
import antifraud.domain.service.TransactionValidation;
import antifraud.domain.service.TransactionValidation.ValidationResultEnum;
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

    public record ValidateTransactionRequest(long amount) {
    }

    public record ValidateTransactionResponse(ValidationResultEnum result) {
    }
}
