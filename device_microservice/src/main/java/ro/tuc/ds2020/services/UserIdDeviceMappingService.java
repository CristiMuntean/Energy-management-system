package ro.tuc.ds2020.services;

import javax.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import ro.tuc.ds2020.dtos.DeviceDTO;
import ro.tuc.ds2020.dtos.DeviceWithUserIdDTO;
import ro.tuc.ds2020.dtos.UserIdDeviceMappingDTO;
import ro.tuc.ds2020.dtos.builders.UserIdDeviceMappingBuilder;
import ro.tuc.ds2020.entities.UserIdDeviceMapping;
import ro.tuc.ds2020.repositories.UserIdDeviceMappingRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserIdDeviceMappingService {
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(UserIdDeviceMappingService.class);
    private final MQService mqService;
    private final DeviceService deviceService;
    private final UserIdDeviceMappingRepository userDeviceMappingRepository;


    @Autowired
    public UserIdDeviceMappingService(UserIdDeviceMappingRepository userDeviceMappingRepository,
                                      MQService mqService,
                                      DeviceService deviceService) {
        this.userDeviceMappingRepository = userDeviceMappingRepository;
        this.mqService = mqService;
        this.deviceService = deviceService;
    }

    public List<UserIdDeviceMappingDTO> findUserDeviceMappings() {
        List<UserIdDeviceMapping> userDeviceMappingList = userDeviceMappingRepository.findAll();
        return userDeviceMappingList.stream()
                .map(UserIdDeviceMappingBuilder::toUserDeviceMappingDTO)
                .collect(java.util.stream.Collectors.toList());
    }

    public UserIdDeviceMappingDTO findUserDeviceMappingById(Long id) {
        Optional<UserIdDeviceMapping> userDeviceMapping = userDeviceMappingRepository.findById(id);
        if(!userDeviceMapping.isPresent()) {
            LOGGER.error("UserDeviceMapping with id {} was not found in db", id);
            throw new ResourceNotFoundException(UserIdDeviceMapping.class.getSimpleName() + " with id: " + id);
        }
        return UserIdDeviceMappingBuilder.toUserDeviceMappingDTO(userDeviceMapping.get());
    }

    public Long insert(UserIdDeviceMappingDTO userDeviceMappingDTO) {
        Long nextId = userDeviceMappingRepository.getMaxId();
        nextId = nextId == null ? 1 : nextId + 1;
        userDeviceMappingDTO.setId(nextId);
        UserIdDeviceMapping userDeviceMapping = UserIdDeviceMappingBuilder.toEntity(userDeviceMappingDTO);
        userDeviceMapping = userDeviceMappingRepository.save(userDeviceMapping);
        LOGGER.debug("UserDeviceMapping with id {} was inserted in db", userDeviceMapping.getId());
        DeviceDTO deviceDTO =
                deviceService.findAll()
                        .stream()
                        .filter(el -> el.getId().equals(userDeviceMappingDTO.getDeviceId()))
                        .findFirst().get();
        mqService.sendDeviceUpdate(deviceDTO, "update");
        return userDeviceMapping.getId();
    }

    public Long update(UserIdDeviceMappingDTO userIdDeviceMappingDTO) {
        Long existingId = findUserDeviceMappingByDeviceId(userIdDeviceMappingDTO.getDeviceId());
        userIdDeviceMappingDTO.setId(existingId);
        UserIdDeviceMapping newMapping = UserIdDeviceMappingBuilder.toEntity(userIdDeviceMappingDTO);
        Optional<UserIdDeviceMapping> existingUserIdDeviceMapping = userDeviceMappingRepository.findById(newMapping.getId());
        if(existingUserIdDeviceMapping.isPresent()) {
            UserIdDeviceMapping oldMapping = existingUserIdDeviceMapping.get();
            oldMapping.setUserId(newMapping.getUserId());
            oldMapping.setDeviceId(newMapping.getDeviceId());

            userDeviceMappingRepository.save(oldMapping);
            LOGGER.debug("UserIdDeviceMapping with id {} was updated in the db", newMapping.getId());
            DeviceDTO deviceDTO =
                    deviceService.findAll()
                            .stream()
                            .filter(el -> el.getId().equals(oldMapping.getDeviceId()))
                            .findFirst().get();
            mqService.sendDeviceUpdate(deviceDTO, "update");
            return newMapping.getId();
        }
        else {
            throw new EntityNotFoundException("UserIdDeviceMapping with id " + newMapping.getId() + " was not found in the db");
        }
    }

    public Long delete(UserIdDeviceMappingDTO userIdDeviceMappingDTO) {
        Long id = findUserDeviceMappingByUserIdAndDeviceId(userIdDeviceMappingDTO.getUserId(), userIdDeviceMappingDTO.getDeviceId());
        userIdDeviceMappingDTO.setId(id);
        UserIdDeviceMapping userIdDeviceMapping = UserIdDeviceMappingBuilder.toEntity(userIdDeviceMappingDTO);
        userDeviceMappingRepository.delete(userIdDeviceMapping);
        LOGGER.debug("UserIdDeviceMapping with id {} was deleted from the db", userIdDeviceMapping.getId());
        DeviceDTO deviceDTO =
                deviceService.findAll()
                        .stream()
                        .filter(el -> el.getId().equals(userIdDeviceMapping.getDeviceId()))
                        .findFirst().get();
        mqService.sendDeviceUpdate(deviceDTO, "update");
        return userIdDeviceMapping.getId();
    }

    private Long findUserDeviceMappingByUserIdAndDeviceId(Long userId, Long deviceId) {
        Long id = userDeviceMappingRepository.findUserIdDeviceMappingByDeviceIdAndUserId(deviceId, userId).getId();
        if(id == null) {
            throw new ResourceNotFoundException("UserIdDeviceMapping with userId " + userId + " and deviceId " + deviceId + " was not found in db");
        }
        return id;
    }

    private Long findUserDeviceMappingByDeviceId(Long deviceId) {
        Optional<UserIdDeviceMapping> deviceMapping = userDeviceMappingRepository.findUserIdDeviceMappingByDeviceId(deviceId);
        Long id = deviceMapping.map(UserIdDeviceMapping::getId).orElse(null);
        if(id == null) {
            throw new ResourceNotFoundException("UserIdDeviceMapping with deviceId " + deviceId + " was not found in db");
        }
        return id;
    }

    public void deleteAllByUserId(Long userId) {
        try {
            List<Long> mappingIds = userDeviceMappingRepository.findUserDeviceMappingByUserId(userId);
            for(Long id : mappingIds) {
                userDeviceMappingRepository.deleteById(id);
            }
        } catch (Exception e) {
            throw new ResourceNotFoundException("UserIdDeviceMapping with userId " + userId + " was not found in db");
        }
    }
}
