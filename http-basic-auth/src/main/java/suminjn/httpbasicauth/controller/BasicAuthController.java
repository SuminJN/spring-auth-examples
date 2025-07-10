package suminjn.httpbasicauth.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BasicAuthController {

    @GetMapping("/public")
    public String publicPage() {
        return "This is a public page.";
    }

    @GetMapping("/secure")
    public String securePage(Authentication authentication) {
        return "Welcome, " + authentication.getName() + "!";
    }
}
