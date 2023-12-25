package ro.tuc.ds2020.dtos;

public class LiveReadingDTO {
    private Long id;
    private Integer hour;
    private Integer day;
    private Integer month;
    private Integer year;
    private Double reading;

    public LiveReadingDTO() {
    }

    public LiveReadingDTO(Long id, Integer hour, Integer day, Integer month, Integer year, Double reading) {
        this.id = id;
        this.hour = hour;
        this.day = day;
        this.month = month;
        this.year = year;
        this.reading = reading;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getHour() {
        return hour;
    }

    public void setHour(Integer hour) {
        this.hour = hour;
    }

    public Double getReading() {
        return reading;
    }

    public void setReading(Double reading) {
        this.reading = reading;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }
}
