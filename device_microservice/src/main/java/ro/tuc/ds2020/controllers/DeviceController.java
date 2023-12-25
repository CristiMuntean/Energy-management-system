package ro.tuc.ds2020.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.tuc.ds2020.dtos.DeviceDTO;
import ro.tuc.ds2020.dtos.DeviceWithUserIdDTO;
import ro.tuc.ds2020.dtos.UserIdDeviceMappingDTO;
import ro.tuc.ds2020.services.DeviceService;
import ro.tuc.ds2020.services.UserIdDeviceMappingService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@CrossOrigin
@RequestMapping(value = "api/device")
public class DeviceController {

    private final DeviceService deviceService;
    private final UserIdDeviceMappingService userIdDeviceMappingService;

    @Autowired
    public DeviceController(DeviceService deviceService, UserIdDeviceMappingService userIdDeviceMappingService) {
        this.deviceService = deviceService;
        this.userIdDeviceMappingService = userIdDeviceMappingService;
    }

    @GetMapping(value = "/get_all_devices_with_owning_user")
    public ResponseEntity<List<?>> getAllDevices() {
        List<DeviceWithUserIdDTO> dtos = deviceService.findDevicesWithUserIds();
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @GetMapping(value = "/get_user_devices")
    public ResponseEntity<?> getDevice(@RequestHeader String userId) {
        Long value = Long.parseLong(userId);
        List<DeviceDTO> dto = new ArrayList<>();
        try {
            dto = deviceService.findDeviceByUserId(value);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(dto, HttpStatus.OK);
        }
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PostMapping(value = "create_device")
    public ResponseEntity<?> createDevice(@Valid @RequestBody DeviceDTO deviceDTO) {
        DeviceDTO deviceId = deviceService.insert(deviceDTO);
        return new ResponseEntity<>(deviceId, HttpStatus.CREATED);
    }

    @PostMapping("/link_device_to_user")
    public ResponseEntity<Long> insertUserIdDeviceMapping(@RequestBody UserIdDeviceMappingDTO userIdDeviceMappingDTO) {
        Long userDeviceMappingId;
        try {
            userDeviceMappingId = userIdDeviceMappingService.update(userIdDeviceMappingDTO);
        } catch (ResourceNotFoundException e) {
            userDeviceMappingId = userIdDeviceMappingService.insert(userIdDeviceMappingDTO);
        }
        return new ResponseEntity<>(userDeviceMappingId, HttpStatus.CREATED);
    }

    @PutMapping(value = "/update_device")
    public ResponseEntity<Long> updateDevice(@RequestBody DeviceDTO deviceDTO) {
        Long deviceId = deviceService.updateDevice(deviceDTO);
        return new ResponseEntity<>(deviceId, HttpStatus.OK);
    }

    @DeleteMapping(value = "/delete_device")
    public ResponseEntity<Long> deleteDevice(@RequestBody DeviceDTO deviceDTO) {
        Long deviceId = deviceService.deleteDevice(deviceDTO);
        return new ResponseEntity<>(deviceId, HttpStatus.OK);
    }

    @DeleteMapping(value = "/unlink_device_from_user")
    public ResponseEntity<Long> deleteUserIdDeviceMapping(@RequestBody UserIdDeviceMappingDTO userIdDeviceMappingDTO) {
        Long userDeviceMappingId = userIdDeviceMappingService.delete(userIdDeviceMappingDTO);
        return new ResponseEntity<>(userDeviceMappingId, HttpStatus.OK);
    }
}
