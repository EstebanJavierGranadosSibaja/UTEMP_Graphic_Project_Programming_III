package org.una.programmingIII.utemp_app.responses;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.una.programmingIII.utemp_app.dtos.UserDTO;

@Getter
@Setter
@ToString
public class TokenResponse {
    @NotNull(message = "Password must not be null")
    String token;
    @NotNull(message = "Password must not be null")
    String tokenType;
    @NotNull(message = "Password must not be null")
    UserDTO user;

    int expiresIn;

    public TokenResponse() {
    }

}
