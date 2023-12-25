package ro.tuc.ds2020.dtos;

public class WarningMessageDTO {
    private Long deviceId;
    private String message;

    public WarningMessageDTO() {
    }

    public WarningMessageDTO(Long deviceId, String message) {
        this.deviceId = deviceId;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }
}
