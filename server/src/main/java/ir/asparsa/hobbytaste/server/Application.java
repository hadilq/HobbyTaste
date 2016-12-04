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

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class Application {

    private Double lat = 35.7952119d;
    private Double lon = 51.4062329d;
    private Double delta = 0.01d;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    CommandLineRunner init(
            AccountRepository accountRepository,
            StoreRepository storeRepository) {
        if (storeRepository.findAll().size() == 0) {

            return (evt) -> {
                List<StoreModel> list = new ArrayList<>();
//                Arrays.asList(
//                        "Marryam,Hadi,Habiba,Mohamad".split(","))
//                        .forEach(a -> {
//                            StoreModel store = new StoreModel(lat, lon, a);
//                            lat -= delta;
//                            storeRepository.save(store);
//                        });

                list.add(new StoreModel(
                        lat, lon, "Marryam", "Maryam description", 5.0f,
                        "https://www.gravatar.com/avatar/7c27855c6a5e53fb3ec289e0f152b230?s=328&d=identicon&r=PG",
                        "https://www.gravatar.com/avatar/7c27855c6a5e53fb3ec289e0f152b230?s=32&d=identicon&r=PG"
                ));
                lat -= delta;
                list.add(new StoreModel(
                        lat, lon, "Hadi", "Hadi description", 4.5f,
                        "https://www.gravatar.com/avatar/430a6af757fa56045c6bdf2d1a6931db?s=48&d=identicon&r=PG",
                        "https://www.gravatar.com/avatar/430a6af757fa56045c6bdf2d1a6931db?s=48&d=identicon&r=PG"
                ));
                lat -= delta;
                list.add(new StoreModel(
                        lat, lon, "Habiba", "Habiba description", 4.0f,
                        "https://www.gravatar.com/avatar/0263c4c9036955a2d9768bf6a35a8345?s=32&d=identicon&r=PG",
                        "https://www.gravatar.com/avatar/0263c4c9036955a2d9768bf6a35a8345?s=32&d=identicon&r=PG"
                ));
                lat -= delta;
                list.add(new StoreModel(
                        lat, lon, "Mohammad", "Mohammad description", 3.5f,
                        "https://www.gravatar.com/avatar/500f5fef2e950bb076c66bc570c6f4f9?s=32&d=identicon&r=PG&f=1",
                        "https://www.gravatar.com/avatar/500f5fef2e950bb076c66bc570c6f4f9?s=32&d=identicon&r=PG&f=1"
                ));

                for (StoreModel storeModel : list) {
                    storeRepository.save(storeModel);
                }

            };

        } else {
            return null;
        }
    }
}