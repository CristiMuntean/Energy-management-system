package ro.tuc.ds2020.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.tuc.ds2020.security.TokenValidator;
import ro.tuc.ds2020.services.ReadingService;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@RestController
@CrossOrigin("*")
@RequestMapping("api/monitoring")
public class MonitoringController {
    private final ReadingService readingService;
    private final TokenValidator tokenValidator;
    @Autowired
    public MonitoringController(ReadingService readingService, TokenValidator tokenValidator) {
        this.readingService = readingService;
        this.tokenValidator = tokenValidator;
    }

    @GetMapping("/get_readings_for_day")
    public ResponseEntity<?> getReadingsForDay(@RequestParam String date, @RequestParam String deviceIds, @RequestHeader("Authorization") String jwtToken) {
        if(!tokenValidator.validate(jwtToken)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        List<Long> ids = Stream.of(deviceIds.split(" "))
                .map(Long::parseLong)
                .collect(Collectors.toList());

        return new ResponseEntity<>(readingService.getReadingsForDay(date, ids), HttpStatus.OK);
    }
}
