package ro.tuc.ds2020.dtos.builders;


import ro.tuc.ds2020.dtos.UserIdDeviceMappingDTO;
import ro.tuc.ds2020.entities.UserIdDeviceMapping;

public class UserIdDeviceMappingBuilder {
    private UserIdDeviceMappingBuilder() {
    }

    public static UserIdDeviceMappingDTO toUserDeviceMappingDTO(UserIdDeviceMapping userDeviceMapping) {
        return new UserIdDeviceMappingDTO(userDeviceMapping.getId(),
                userDeviceMapping.getUserId(),
                userDeviceMapping.getDeviceId());
    }

    public static UserIdDeviceMapping toEntity(UserIdDeviceMappingDTO userDeviceMappingDTO) {
        return new UserIdDeviceMapping(userDeviceMappingDTO.getId(), userDeviceMappingDTO.getUserId(),
                userDeviceMappingDTO.getDeviceId());
    }
}
