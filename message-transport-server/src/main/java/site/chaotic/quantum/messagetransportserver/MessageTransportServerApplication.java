package site.chaotic.quantum.messagetransportserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MessageTransportServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MessageTransportServerApplication.class, args);
    }

}
