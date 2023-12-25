package ro.tuc.ds2020.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import ro.tuc.ds2020.dtos.DeviceDTO;
import ro.tuc.ds2020.dtos.DeviceWithUserIdDTO;
import ro.tuc.ds2020.dtos.builders.DeviceBuilder;
import ro.tuc.ds2020.entities.Device;
import ro.tuc.ds2020.repositories.DeviceRepository;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DeviceService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceService.class);

    private final MQService mqService;
    private final DeviceRepository deviceRepository;

    @Autowired
    public DeviceService(DeviceRepository deviceRepository, MQService mqService) {
        this.deviceRepository = deviceRepository;
        this.mqService = mqService;
    }

    public List<DeviceWithUserIdDTO> findDevicesWithUserIds() {
        List<?> deviceList = deviceRepository.getDevicesWithUserId();
        if(deviceList == null || deviceList.isEmpty()) {
            LOGGER.error("No devices were found in db");
            return new ArrayList<>();
        }
        return new ArrayList<>(deviceList.stream().map(DeviceBuilder::deviceWithUserIdDTO).collect(Collectors.toList()));
    }

    public List<DeviceDTO> findAll() {
        List<Device> deviceList = deviceRepository.findAll();
        if(deviceList == null || deviceList.isEmpty()) {
            LOGGER.error("No devices were found in db");
            return new ArrayList<>();
        }
        return new ArrayList<>(deviceList.stream().map(DeviceBuilder::toDeviceDTO).collect(Collectors.toList()));
    }

    public List<DeviceDTO> findDeviceByUserId(Long id) {
        Optional<List<Device>> deviceOptional = deviceRepository.getDevicesByUserId(id);
        if (!deviceOptional.isPresent()) {
            LOGGER.error("Device with id {} was not found in db", id);
            throw new ResourceNotFoundException(Device.class.getSimpleName() + " with id: " + id);
        }
        return deviceOptional.get().stream().map(DeviceBuilder::toDeviceDTO).collect(Collectors.toList());
    }

    public DeviceDTO insert(DeviceDTO deviceDTO) {
        Long nextId = deviceRepository.getMaxId();
        nextId = nextId == null ? 1 : nextId + 1;
        deviceDTO.setId(nextId);
        Device device = DeviceBuilder.toEntity(deviceDTO);
        device = deviceRepository.save(device);
        LOGGER.debug("Device with id {} was inserted in db", device.getId());
        mqService.sendDeviceUpdate(DeviceBuilder.toDeviceDTO(device), "insert");
        return DeviceBuilder.toDeviceDTO(device);
    }

    public Long updateDevice(DeviceDTO deviceDTO) {
        Device device = DeviceBuilder.toEntity(deviceDTO);
        Optional<Device> existingDevice = deviceRepository.findById(device.getId());
        if(existingDevice.isPresent()) {
            boolean energyConsumptionChanged = !(existingDevice.get().getMaxHourlyEnergyConsumption() == device.getMaxHourlyEnergyConsumption());
            Device oldDevice = existingDevice.get();
            oldDevice.setDescription(device.getDescription());
            oldDevice.setAddress(device.getAddress());
            oldDevice.setMaxHourlyEnergyConsumption(device.getMaxHourlyEnergyConsumption());

            Device updatedDevice = deviceRepository.save(oldDevice);
            LOGGER.debug("Device with id {} was updated in db", device.getId());
            if (energyConsumptionChanged)
                mqService.sendDeviceUpdate(DeviceBuilder.toDeviceDTO(updatedDevice), "update");
            return updatedDevice.getId();
        }
        else {
            throw new EntityNotFoundException("Device with id " + device.getId() + " was not found in db");
        }
    }

    public Long deleteDevice(DeviceDTO deviceDTO) {
        Device device = DeviceBuilder.toEntity(deviceDTO);
        mqService.sendDeviceDelete(deviceDTO);
        deviceRepository.delete(device);
        LOGGER.debug("Device with id {} was deleted from the db", device.getId());
        return device.getId();
    }
}
