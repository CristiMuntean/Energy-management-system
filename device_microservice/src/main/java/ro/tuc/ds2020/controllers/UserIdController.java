package ro.tuc.ds2020.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.tuc.ds2020.dtos.UserIdDTO;
import ro.tuc.ds2020.services.DeviceService;
import ro.tuc.ds2020.services.UserIdDeviceMappingService;
import ro.tuc.ds2020.services.UserIdService;

@RestController
@CrossOrigin
@RequestMapping(value = "api/userId")
public class UserIdController {
    private final UserIdService userIdService;
    private final UserIdDeviceMappingService userIdDeviceMappingService;

    @Autowired
    public UserIdController(UserIdService userIdService, UserIdDeviceMappingService userIdDeviceMappingService) {
        this.userIdService = userIdService;
        this.userIdDeviceMappingService = userIdDeviceMappingService;
    }

    @PostMapping()
    public ResponseEntity<Long> insert(@RequestBody UserIdDTO userIdDTO) {
        Long insertedId = userIdService.insert(userIdDTO);
        return new ResponseEntity<>(insertedId, HttpStatus.OK);
    }

    @DeleteMapping()
    public ResponseEntity<?> delete(@RequestBody UserIdDTO userIdDTO) {
        try {
            userIdDeviceMappingService.deleteAllByUserId(userIdDTO.getUserId());
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.EXPECTATION_FAILED);
        }
        Long deletedId = userIdService.delete(userIdDTO);
        return new ResponseEntity<>(deletedId, HttpStatus.OK);
    }
}
