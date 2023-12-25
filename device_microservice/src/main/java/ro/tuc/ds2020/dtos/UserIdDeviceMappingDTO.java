package ro.tuc.ds2020.dtos;

import org.springframework.hateoas.RepresentationModel;

import java.util.Objects;

public class UserIdDeviceMappingDTO extends RepresentationModel<UserIdDeviceMappingDTO> {
    private Long id;
    private Long userId;
    private Long deviceId;

    public UserIdDeviceMappingDTO() {
    }

    public UserIdDeviceMappingDTO(Long id, Long userId, Long deviceId) {
        this.id = id;
        this.userId = userId;
        this.deviceId = deviceId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserIdDeviceMappingDTO that = (UserIdDeviceMappingDTO) o;
        return Objects.equals(userId, that.userId)
                && Objects.equals(deviceId, that.deviceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, deviceId);
    }
}
