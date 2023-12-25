package ro.tuc.ds2020.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ro.tuc.ds2020.controllers.handlers.exceptions.model.EntityExistsException;
import ro.tuc.ds2020.controllers.handlers.exceptions.model.MicroserviceException;
import ro.tuc.ds2020.controllers.handlers.exceptions.model.ResourceNotFoundException;
import ro.tuc.ds2020.dtos.UserDTO;
import ro.tuc.ds2020.dtos.builders.UserBuilder;
import ro.tuc.ds2020.entities.User;
import ro.tuc.ds2020.repositories.UserRepository;
import ro.tuc.ds2020.security.services.UserDetailsImpl;
import ro.tuc.ds2020.utils.API_URLS;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    private final MQService mqService;

    private final UserRepository userRepository;

    @Autowired
    public UserService(MQService mqService, UserRepository userRepository) {
        this.mqService = mqService;
        this.userRepository = userRepository;
    }

    public List<UserDTO> findUsers() {
        List<User> userList = userRepository.findAll();
        return userList.stream()
                .map(UserBuilder::toUserDTO)
                .collect(Collectors.toList());
    }

    public UserDTO findUserById(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (!userOptional.isPresent()) {
            LOGGER.error("User with id {} was not found in db", id);
            throw new ResourceNotFoundException(User.class.getSimpleName() + " with id: " + id);
        }
        return UserBuilder.toUserDTO(userOptional.get());
    }

    public Long insert(UserDTO userDTO, String jwtToken) {
        User user = UserBuilder.toEntity(userDTO);
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        Long nextId = userRepository.getMaxId();
        nextId = nextId == null ? 1 : nextId + 1;
        user.setId(nextId);

        Optional<User> optionalUser = userRepository.findUserByUsername(user.getUsername());
        if(optionalUser.isPresent()) {
            throw new EntityExistsException("User with username " + user.getUsername() + " already exists in db");
        }

        if(!insertUserIdInDeviceMicroservice(user, jwtToken)) {
            throw new MicroserviceException("User with id " + user.getId() + " failed to be inserted in device microservice db");
        }

        user = userRepository.save(user);
        LOGGER.debug("User with id {} was inserted in db", user.getId());
        mqService.sendUserUpdate(UserBuilder.toUserDTO(user),"insert");
        return user.getId();
    }

    public Long insert(UserDTO userDTO) {
        User user = UserBuilder.toEntity(userDTO);
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        Long nextId = userRepository.getMaxId();
        nextId = nextId == null ? 1 : nextId + 1;
        user.setId(nextId);

        Optional<User> optionalUser = userRepository.findUserByUsername(user.getUsername());
        if(optionalUser.isPresent()) {
            throw new EntityExistsException("User with username " + user.getUsername() + " already exists in db");
        }

        user = userRepository.save(user);
        LOGGER.debug("User with id {} was inserted in db", user.getId());
        mqService.sendUserUpdate(UserBuilder.toUserDTO(user),"insert");
        return user.getId();
    }

    public void sendInsertToDeviceMicroservice(String jwtToken, UserDTO userDTO) {
        insertUserIdInDeviceMicroservice(UserBuilder.toEntity(userDTO), jwtToken);
    }

    public boolean login(String username, String password) {
        Optional<User> optionalUser = userRepository.findUserByUsernameAndPassword(username, password);
        if(optionalUser.isPresent()) {
            User user = optionalUser.get();
            return true;
        }
        else return false;
    }

    private boolean insertUserIdInDeviceMicroservice(User user, String jwtToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", jwtToken);
        String body = "{ \"userId\":" + user.getId() + "}";
        HttpEntity<String> requestHttp = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity( API_URLS.DEVICE_MICROSERVICE.getUrl() + "userId", requestHttp, String.class);
        return response.getStatusCode().is2xxSuccessful();
    }

    public Long updateUser(UserDTO userDTO) {
        User user = UserBuilder.toEntity(userDTO);
        Optional<User> existingUserWithUsername = userRepository.findUserByUsername(user.getUsername());
        if(existingUserWithUsername.isPresent()) {
            throw new EntityExistsException("Username is already taken: " + user.getUsername());
        }
        Optional<User> existingUser = userRepository.findById(user.getId());
        if(existingUser.isPresent()) {
            User oldUser = existingUser.get();
            oldUser.setUsername(user.getUsername());
            oldUser.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
            oldUser.setRole(user.getRole().toString());

            User updatedUser = userRepository.save(oldUser);
            LOGGER.debug("User with id {} was updated in db", user.getId());
            mqService.sendUserUpdate(UserBuilder.toUserDTO(user),"update");
            return updatedUser.getId();
        }
        else {
            throw new ResourceNotFoundException("User with id " + user.getId() + " was not found in db");
        }
    }

    public Long deleteUser(UserDTO userDTO, String jwtToken) {
        User user = UserBuilder.toEntity(userDTO);
        Optional<User> optionalUser = userRepository.findUserByUsername(user.getUsername());
        if(!optionalUser.isPresent()){
            throw new ResourceNotFoundException("User with username " + userDTO.getUsername() + " does not exist in the db");
        }
        user = optionalUser.get();

        if(!deleteUserFromDeviceMicroservice(user, jwtToken)) {
            throw new MicroserviceException("Failed to delete user with id " + user.getId() + " from device microservice");
        }

        userRepository.delete(user);
        LOGGER.debug("User with id {} was deleted in db", user.getId());
        mqService.sendUserUpdate(UserBuilder.toUserDTO(user),"delete");
        return user.getId();
    }

    private boolean deleteUserFromDeviceMicroservice(User user, String jwtToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", jwtToken);
        String body = "{ \"userId\":" + user.getId() + "}";
        HttpEntity<String> requestHttp = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.exchange(API_URLS.DEVICE_MICROSERVICE.getUrl() + "userId", HttpMethod.DELETE, requestHttp, String.class);
        return response.getStatusCode().is2xxSuccessful();
    }

    public UserDetails findUserByUsernameSecurity(String username, String password) {
//        String encodedPassword = new BCryptPasswordEncoder().encode(password);
        Optional<User> optionalUser = userRepository.findUserByUsernameAndPassword(username, password);
        if(!optionalUser.isPresent()) {
            throw new ResourceNotFoundException("User with username " + username + " does not exist in the db");
        }
        return UserDetailsImpl.build(optionalUser.get());
    }

    public UserDTO findUserByUsername(String username) {
        Optional<User> optionalUser = userRepository.findUserByUsername(username);
        if(!optionalUser.isPresent()) {
            throw new ResourceNotFoundException("User with username " + username + " does not exist in the db");
        }
        return UserBuilder.toUserDTO(optionalUser.get());
    }
}
