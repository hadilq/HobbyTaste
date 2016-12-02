package ir.asparsa.hobbytaste.server;

/**
 * @author hadi
 * @since 11/30/2016 AD
 */

import ir.asparsa.hobbytaste.server.database.model.StoreModel;
import ir.asparsa.hobbytaste.server.database.repository.AccountRepository;
import ir.asparsa.hobbytaste.server.database.repository.StoreRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

@SpringBootApplication
public class Application {

    private Double lat = 35.6942119d;
    private Double lon = 51.4062329d;
    private Double delta = 0.0004d;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    CommandLineRunner init(
            AccountRepository accountRepository,
            StoreRepository storeRepository) {
        if (storeRepository.findAll().size() == 0) {

            return (evt) -> Arrays.asList(
                    "Marryam,Hadi,Habiba,Mohamad".split(","))
                    .forEach(
                            a -> {
                                StoreModel store = new StoreModel(lat, lon, a);
                                lat -= delta;
                                storeRepository.save(store);
                            });
        } else {
            return null;
        }
    }
}