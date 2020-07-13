package demo;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.OffsetDateTime;

public class Now {
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    private OffsetDateTime time;
    public Now(){
        time=OffsetDateTime.now();
    }
    public OffsetDateTime getTime(){
        return time;
    }
}
