package suminjn.formlogin.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import suminjn.formlogin.dto.UserRegisterDto;
import suminjn.formlogin.entity.User;
import suminjn.formlogin.repository.UserRepository;

@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new UserRegisterDto());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("user") UserRegisterDto dto) {
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            return "redirect:/register?error=username_taken";
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(dto.getRole());

        userRepository.save(user);
        return "redirect:/login?registered";
    }


    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";  // dashboard.html (Thymeleaf)
    }
}
