package org.una.programmingIII.utemp_app.test.services;

import org.una.programmingIII.utemp_app.services.AuthService;
import org.una.programmingIII.utemp_app.services.request.AuthRequest;
import org.una.programmingIII.utemp_app.services.responses.MessageResponse;

public class AuthServiceTest {


    public void runTests() {
        AuthService authService = new AuthService();

        System.out.println("\n\nÉxito");
        MessageResponse<String> response = authService.login(new AuthRequest("000000000", "admin"));
        print(response);
//
//        System.out.println("\n\nFracaso");
//        response = authService.login(new AuthRequest("00000000", "admin"));
//        print(response);
//
//        System.out.println("\n\nFracaso");
//        response = authService.login(new AuthRequest("000000000", "admi"));
//        print(response);
    }

    public void print(MessageResponse<String> response) {
        System.out.println("Token: " + response.getData());
        System.out.println("Mensaje: " + response.getMessage());
        if (response.isSuccess()) {
            System.out.println("Success");
        } else {
            System.out.println("Failure");
        }
        System.out.println("Código de error: " + response.getErrorCode());
    }
}

//@Getter
//@Setter
//public class AuthRequest {
//    private String identificationNumber; para prueba 000000000
//    private String password;             para prueba admin
//
//}
