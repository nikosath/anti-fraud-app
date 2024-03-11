package antifraud.domain.web;

import antifraud.common.Uri;
import antifraud.common.WebUtils;
import antifraud.domain.datastore.IStolenCardEntityDatastore;
import antifraud.domain.datastore.StolenCardEntity;
import antifraud.error.ErrorEnum;
import antifraud.error.Result;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.CreditCardNumber;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StolenCardController {

    private final IStolenCardEntityDatastore datastore;

    @PostMapping(Uri.API_ANTIFRAUD_STOLENCARD)
    public ResponseEntity<StolenCardResponse> createStolenCard(@Valid @RequestBody StolenCardRequest req) {
        log.debug("saveStolenCard for req: " + req);
        Result<ErrorEnum, StolenCardEntity> result = datastore.createStolenCard(req.cardNumber());
        if (result.isSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(StolenCardResponse.fromStolenCardEntity(result.getSuccess()));
        }
        return WebUtils.errorToResponseEntity(result.getError());
    }

    @GetMapping(Uri.API_ANTIFRAUD_STOLENCARD)
    public List<StolenCardResponse> getAllStolenCardes() {
        log.debug("getAllStolenCardes");
        List<StolenCardEntity> stolenCards = datastore.getAllStolenCardsOrderById();
        return stolenCards.stream()
                .map(stolenCardEntity -> StolenCardResponse.fromStolenCardEntity(stolenCardEntity))
                .toList();
    }

    @DeleteMapping(Uri.API_ANTIFRAUD_STOLENCARD + Uri.CARD_NUMBER)
    public ResponseEntity<DeleteStolenCardResponse> deleteStolenCard(@NotBlank @CreditCardNumber @PathVariable String cardNumber) {
        log.debug("deleteStolenCard for cardNumber: " + cardNumber);
        Result<ErrorEnum, StolenCardEntity> result = datastore.deleteStolenCard(cardNumber);
        if (result.isSuccess()) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(DeleteStolenCardResponse.fromStolenCardEntity(result.getSuccess()));
        }
        return WebUtils.errorToResponseEntity(result.getError());
    }

    public record StolenCardRequest(@NotBlank @CreditCardNumber String cardNumber) {
    }

    public record StolenCardResponse(Long id, String cardNumber) {
        public static StolenCardResponse fromStolenCardEntity(StolenCardEntity stolenCardEntity) {
            return new StolenCardResponse(stolenCardEntity.getId(), stolenCardEntity.getCardNumber());
        }
    }

    public record DeleteStolenCardResponse(String status) {
        static DeleteStolenCardResponse fromStolenCardEntity(StolenCardEntity entity) {
            return new DeleteStolenCardResponse("Card %s successfully removed!".formatted(entity.getCardNumber()));
        }
    }

}
