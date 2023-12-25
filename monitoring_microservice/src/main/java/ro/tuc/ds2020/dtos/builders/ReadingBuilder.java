package ro.tuc.ds2020.dtos.builders;

import ro.tuc.ds2020.dtos.ReadingDTO;
import ro.tuc.ds2020.entities.Reading;

public class ReadingBuilder {
    private ReadingBuilder() {
    }

    public static ReadingDTO toReadingDTO(Reading reading) {
        return new ReadingDTO(reading.getId(),
                reading.getDeviceId(),
                reading.getMeasurementValue(),
                reading.getTimestamp());
    }

    public static Reading toEntity(ReadingDTO readingDTO) {
        return new Reading(readingDTO.getId(),
                readingDTO.getDeviceId(),
                readingDTO.getMeasurementValue(),
                readingDTO.getTimestamp());
    }
}
