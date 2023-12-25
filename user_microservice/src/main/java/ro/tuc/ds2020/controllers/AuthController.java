package ro.tuc.ds2020.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ro.tuc.ds2020.dtos.UserDTO;
import ro.tuc.ds2020.security.jwt.JWTConfig;
import ro.tuc.ds2020.security.services.UserDetailsImpl;
import ro.tuc.ds2020.services.UserService;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final PasswordEncoder encoder;
    private final JWTConfig jwtConfig;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, UserService userService, JWTConfig jwtConfig) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.encoder = new BCryptPasswordEncoder();
        this.jwtConfig = jwtConfig;
    }

    @PostMapping(value = "/login")
    public ResponseEntity<?> login(@RequestBody UserDTO userDTO) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userDTO.getUsername(), userDTO.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = "{\"accessToken\": \"" + jwtConfig.generateToken(authentication) + "\"}";
            UserDetailsImpl user = (UserDetailsImpl) authentication.getPrincipal();

            return new ResponseEntity<>(jwt, HttpStatus.OK);
        } catch (AuthenticationException e ) {
            System.out.println("Invalid credentials");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @PostMapping(value = "/register")
    public ResponseEntity<String> insertUser(@RequestBody UserDTO userDTO) {
            Long userId = userService.insert(userDTO);
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userDTO.getUsername(), userDTO.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = "{\"accessToken\": \"" + jwtConfig.generateToken(authentication) + "\"}";
            UserDetailsImpl user = (UserDetailsImpl) authentication.getPrincipal();
            userService.sendInsertToDeviceMicroservice(jwt, userDTO);
            return new ResponseEntity<>(jwt, HttpStatus.CREATED);
    }
}
