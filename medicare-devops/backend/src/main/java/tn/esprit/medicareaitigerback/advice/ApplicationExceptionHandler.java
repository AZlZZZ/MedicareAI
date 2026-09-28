package tn.esprit.medicareaitigerback.advice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tn.esprit.medicareaitigerback.exceptions.*;
import tn.esprit.medicareaitigerback.models.dto.response.ApiRespDTO;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ApplicationExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiRespDTO<Object>> handleException(MethodArgumentNotValidException ex) {
        Map<String, String> errorMap = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(fieldError -> {
            errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
        });

        return ResponseEntity.ok(new ApiRespDTO<>(400, "Bad Request", errorMap));
    }

    @ExceptionHandler(RoleInUseException.class)
    public ResponseEntity<ApiRespDTO<Object>> handleException(RoleInUseException ex) {

        return ResponseEntity.ok(new ApiRespDTO<>(3001, "RoleInUseException", ex.getMessage()));
    }

    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<ApiRespDTO<Object>> handleException(RoleNotFoundException ex) {

        return ResponseEntity.ok(new ApiRespDTO<>(3002, "RoleNotFoundException", ex.getMessage()));
    }

    @ExceptionHandler(RoleAlreadyExistException.class)
    public ResponseEntity<ApiRespDTO<Object>> handleException(RoleAlreadyExistException ex) {

        return ResponseEntity.ok(new ApiRespDTO<>(3003, "RoleAlreadyExistException", ex.getMessage()));
    }

    @ExceptionHandler(AddressDontMatchWithUserException.class)
    public ResponseEntity<ApiRespDTO<Object>> handleException(AddressDontMatchWithUserException ex) {

        return ResponseEntity.ok(new ApiRespDTO<>(3004, "AddressDontMatchWithUserException", ex.getMessage()));
    }

    @ExceptionHandler(AddressNotFoundException.class)
    public ResponseEntity<ApiRespDTO<Object>> handleException(AddressNotFoundException ex) {

        return ResponseEntity.ok(new ApiRespDTO<>(3005, "AddressNotFoundException", ex.getMessage()));
    }

    @ExceptionHandler(EmailUsedException.class)
    public ResponseEntity<ApiRespDTO<Object>> handleException(EmailUsedException ex) {

        return ResponseEntity.ok(new ApiRespDTO<>(3006, "EmailUsedException", ex.getMessage()));
    }

    @ExceptionHandler(AgentNotFoundException.class)
    public ResponseEntity<ApiRespDTO<Object>> handleException(AgentNotFoundException ex) {

        return ResponseEntity.ok(new ApiRespDTO<>(3007, "AgentNotFoundException", ex.getMessage()));
    }

    @ExceptionHandler(UserNameUsedException.class)
    public ResponseEntity<ApiRespDTO<Object>> handleException(UserNameUsedException ex) {

        return ResponseEntity.ok(new ApiRespDTO<>(3008, "UserNameUsedException", ex.getMessage()));
    }
    @ExceptionHandler(PhoneNumberUsedException.class)
    public ResponseEntity<ApiRespDTO<Object>> handleException(PhoneNumberUsedException ex) {

        return ResponseEntity.ok(new ApiRespDTO<>(3009, "PhoneNumberUsedException", ex.getMessage()));
    }
    @ExceptionHandler(PatientNotFoundException.class)
    public ResponseEntity<ApiRespDTO<Object>> handleException(PatientNotFoundException ex) {

        return ResponseEntity.ok(new ApiRespDTO<>(3010, "PatientNotFoundException", ex.getMessage()));
    }
    @ExceptionHandler(UnknownUserException.class)
    public ResponseEntity<ApiRespDTO<Object>> handleException(UnknownUserException ex) {

        return ResponseEntity.ok(new ApiRespDTO<>(3011, "UnknownUserException", ex.getMessage()));
    }
    @ExceptionHandler(TokenInvalidException.class)
    public ResponseEntity<ApiRespDTO<Object>> handleException(TokenInvalidException ex) {

        return ResponseEntity.ok(new ApiRespDTO<>(3012, "TokenInvalidException", ex.getMessage()));
    }
    @ExceptionHandler(UserNotEnabledException.class)
    public ResponseEntity<ApiRespDTO<Object>> handleException(UserNotEnabledException ex) {

        return ResponseEntity.ok(new ApiRespDTO<>(3013, "UserNotEnabledException", ex.getMessage()));
    }
    @ExceptionHandler(PasswordNotInitilizedException.class)
    public ResponseEntity<ApiRespDTO<Object>> handleException(PasswordNotInitilizedException ex) {

        return ResponseEntity.ok(new ApiRespDTO<>(3014, "PasswordNotInitilizedException", ex.getMessage()));
    }
    @ExceptionHandler(PasswordExpiredException.class)
    public ResponseEntity<ApiRespDTO<Object>> handleException(PasswordExpiredException ex) {

        return ResponseEntity.ok(new ApiRespDTO<>(3014, "PasswordExpiredException", ex.getMessage()));
    }
    @ExceptionHandler(UserDontMatchWithApplicationException.class)
    public ResponseEntity<ApiRespDTO<Object>> handleException(UserDontMatchWithApplicationException ex) {

        return ResponseEntity.ok(new ApiRespDTO<>(3014, "UserDontMatchWithApplicationException", ex.getMessage()));
    }
    @ExceptionHandler(DTOWrongException.class)
    public ResponseEntity<ApiRespDTO<Object>> handleException(DTOWrongException ex) {

        return ResponseEntity.ok(new ApiRespDTO<>(3014, "DTOWrongException", ex.getMessage()));
    }
    @ExceptionHandler(TokenNotFoundException.class)
    public ResponseEntity<ApiRespDTO<Object>> handleException(TokenNotFoundException ex) {

        return ResponseEntity.ok(new ApiRespDTO<>(3014, "TokenNotFoundException", ex.getMessage()));
    }
    @ExceptionHandler(MismatchBetweenRefreshTokenAndAccessTokenException.class)
    public ResponseEntity<ApiRespDTO<Object>> handleException(MismatchBetweenRefreshTokenAndAccessTokenException ex) {

        return ResponseEntity.ok(new ApiRespDTO<>(3014, "MismatchBetweenRefreshTokenAndAccessTokenException", ex.getMessage()));
    }
    @ExceptionHandler(RefreshTokenInvalideException.class)
    public ResponseEntity<ApiRespDTO<Object>> handleException(RefreshTokenInvalideException ex) {

        return ResponseEntity.ok(new ApiRespDTO<>(3014, "RefreshTokenInvalideException", ex.getMessage()));
    }
    @ExceptionHandler(RefreshTokenExpieredException.class)
    public ResponseEntity<ApiRespDTO<Object>> handleException(RefreshTokenExpieredException ex) {

        return ResponseEntity.ok(new ApiRespDTO<>(3014, "RefreshTokenExpieredException", ex.getMessage()));
    }
    @ExceptionHandler(TokenNotExpiredYetException.class)
    public ResponseEntity<ApiRespDTO<Object>> handleException(TokenNotExpiredYetException ex) {

        return ResponseEntity.ok(new ApiRespDTO<>(3014, "TokenNotExpiredYetException", ex.getMessage()));
    }
    @ExceptionHandler(TokenAlreadySentException.class)
    public ResponseEntity<ApiRespDTO<Object>> handleException(TokenAlreadySentException ex) {

        return ResponseEntity.ok(new ApiRespDTO<>(3014, "TokenAlreadySentException", ex.getMessage()));
    }
    @ExceptionHandler(UserConnectedFromAnotherDeviceException.class)
    public ResponseEntity<ApiRespDTO<Object>> handleException(UserConnectedFromAnotherDeviceException ex) {

        return ResponseEntity.ok(new ApiRespDTO<>(3014, "UserConnectedFromAnotherDeviceException", ex.getMessage()));
    }
    @ExceptionHandler(PasswordNotAllowedException.class)
    public ResponseEntity<ApiRespDTO<Object>> handleException(PasswordNotAllowedException ex) {

        return ResponseEntity.ok(new ApiRespDTO<>(3014, "PasswordNotAllowedException", ex.getMessage()));
    }
}

