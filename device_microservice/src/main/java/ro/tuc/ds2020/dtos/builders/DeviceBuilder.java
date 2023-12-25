package ro.tuc.ds2020.dtos.builders;

import ro.tuc.ds2020.dtos.DeviceDTO;
import ro.tuc.ds2020.dtos.DeviceWithUserIdDTO;
import ro.tuc.ds2020.entities.Device;

import java.math.BigInteger;

public class DeviceBuilder {

    private DeviceBuilder() {
    }

    public static DeviceDTO toDeviceDTO(Device device) {
        return new DeviceDTO(device.getId(),
                device.getDescription(),
                device.getAddress(),
                device.getMaxHourlyEnergyConsumption());
    }

    public static Device toEntity(DeviceDTO deviceDTO) {
        return new Device(deviceDTO.getId(), deviceDTO.getDescription(),
                deviceDTO.getAddress(),
                deviceDTO.getMaxHourlyEnergyConsumption());
    }

    public static DeviceWithUserIdDTO deviceWithUserIdDTO(Object object) {
        Object[] obj = (Object[]) object;
        return obj[4] == null ?
                new DeviceWithUserIdDTO(((BigInteger) obj[0]).longValue(), (String) obj[1], (String) obj[2], (int) obj[3], null) :
                new DeviceWithUserIdDTO(((BigInteger) obj[0]).longValue(), (String) obj[1], (String) obj[2], (int) obj[3], ((BigInteger) obj[4]).longValue());
    }
}
