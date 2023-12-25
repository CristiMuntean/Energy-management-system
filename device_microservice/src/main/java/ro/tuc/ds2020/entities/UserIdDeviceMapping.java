package ro.tuc.ds2020.entities;

import javax.persistence.*;

@Entity
public class UserIdDeviceMapping {

    @Id
    @Column(name="id", columnDefinition = "bigint")
    private Long id;

    @Column(name="userId", nullable = false)
    private Long userId;

    @Column(name="deviceId", nullable = false)
    private Long deviceId;

    public UserIdDeviceMapping() {
    }

    public UserIdDeviceMapping(Long id, Long userId, Long deviceId) {
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
}
