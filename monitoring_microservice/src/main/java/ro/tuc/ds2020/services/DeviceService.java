package ro.tuc.ds2020.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ro.tuc.ds2020.dtos.DeviceDTO;
import ro.tuc.ds2020.dtos.builders.DeviceBuilder;
import ro.tuc.ds2020.entities.Device;
import ro.tuc.ds2020.repositories.DeviceRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeviceService {
    private final DeviceRepository deviceRepository;

    @Autowired
    public DeviceService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    public void deleteDevice(Long id) {
        deviceRepository.deleteById(id);
    }


    public void insertOrUpdate(DeviceDTO deviceDTO) {
        Device device = DeviceBuilder.toEntity(deviceDTO);
        deviceRepository.save(device);
    }

    public DeviceDTO findDeviceById(Long id) {
        Device device = deviceRepository.findById(id).orElse(null);
        return device != null ? DeviceBuilder.toDeviceDTO(device) : null;
    }

    public List<DeviceDTO> findAll() {
        List<Device> deviceList = deviceRepository.findAll();
        return deviceList.stream().map(DeviceBuilder::toDeviceDTO).collect(Collectors.toList());
    }
}
