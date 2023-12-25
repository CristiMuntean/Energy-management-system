package ro.tuc.ds2020.dtos.builders;

import ro.tuc.ds2020.dtos.DeviceDTO;
import ro.tuc.ds2020.entities.Device;

public class DeviceBuilder {
    private DeviceBuilder() {
    }

    public static DeviceDTO toDeviceDTO(Device device) {
        return new DeviceDTO(device.getDeviceId(),
                device.getMaxHourlyEnergyConsumption(),
                device.getUserId());
    }

    public static Device toEntity(DeviceDTO deviceDTO) {
        return new Device(deviceDTO.getDeviceId(),
                deviceDTO.getMaxHourlyEnergyConsumption(),
                deviceDTO.getUserId());
    }
}
