package org.una.programmingIII.utemp_app.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AuthRequest {
    private String identificationNumber;
    private String password;
}
