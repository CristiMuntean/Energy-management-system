package ro.tuc.ds2020.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.tuc.ds2020.controllers.handlers.exceptions.model.EntityExistsException;
import ro.tuc.ds2020.controllers.handlers.exceptions.model.ResourceNotFoundException;
import ro.tuc.ds2020.dtos.UserDTO;
import ro.tuc.ds2020.security.jwt.JWTConfig;
import ro.tuc.ds2020.services.UserService;


@RestController
@CrossOrigin(value = "*")
@RequestMapping( "api/user")
public class UserController {
    private final UserService userService;

    private final JWTConfig jwtConfig;

    @Autowired
    public UserController(UserService userService, JWTConfig jwtConfig) {
        this.userService = userService;
        this.jwtConfig = jwtConfig;
    }

    @GetMapping("/current_user")
    public ResponseEntity<?> getUsers(@RequestHeader("Authorization") String jwtToken) {
        jwtToken = jwtToken.substring(7);
        String username = jwtConfig.getUsernameFromToken(jwtToken);

        UserDTO dto = userService.findUserByUsername(username);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping("/get_all_users")
    public ResponseEntity<?> getAllUsers() {
        return new ResponseEntity<>(userService.findUsers(), HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<String> insertUser(@RequestBody UserDTO userDTO, @RequestHeader("Authorization") String jwtToken) {
            Long userId = userService.insert(userDTO, jwtToken);
            return new ResponseEntity<>(userId.toString(), HttpStatus.CREATED);
    }

    @PutMapping("/update_user")
    public ResponseEntity<?> updateUser(@RequestBody UserDTO userDTO) {
        try{
            Long userId = userService.updateUser(userDTO);
            return new ResponseEntity<>(userId, HttpStatus.OK);
        } catch (EntityExistsException e) {
            return new ResponseEntity<>("Username is already taken", HttpStatus.CONFLICT);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>("Selected user not found", HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(value = "/delete_user")
    public ResponseEntity<?> deleteUser(@RequestBody UserDTO userDTO, @RequestHeader("Authorization") String jwtToken) {
        try {
            Long userId = userService.deleteUser(userDTO, jwtToken);
            return new ResponseEntity<>(userId, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>("Selected user not found", HttpStatus.NOT_FOUND);
        }
    }
}
