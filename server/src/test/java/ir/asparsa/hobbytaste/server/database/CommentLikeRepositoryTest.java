package ir.asparsa.hobbytaste.server.database;

import ir.asparsa.common.net.dto.StoreProto;
import ir.asparsa.hobbytaste.server.ApplicationContextTest;
import ir.asparsa.hobbytaste.server.database.model.*;
import ir.asparsa.hobbytaste.server.database.repository.CommentLikeRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;
import java.util.UUID;

import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertTrue;

/**
 * @author hadi
 * @since 3/22/2017 AD.
 */
@RunWith(SpringRunner.class)
@DataJpaTest
@WebAppConfiguration
@SpringBootTest(classes = {ApplicationContextTest.class})
public class CommentLikeRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private CommentLikeRepository commentLikeRepository;

    @Test
    public void findByAccountAndStoreTest() {
        entityManager.persist(new AccountModel("Foo", UUID.randomUUID().toString(), "USER"));
        StoreModel storeModel = StoreModel
                .newInstance(StoreProto.Store
                                     .newBuilder()
                                     .setLat(23.23d)
                                     .setLon(42.34d)
                                     .setTitle("sdfv")
                                     .setDescription("sdfvzxcv")
                                     .setHashCode(2934756L)
                                     .build(), "");
        entityManager.persist(storeModel);
        entityManager.persist(new BannerModel("test", "thumbnailTest", storeModel));

        AccountModel commenter = new AccountModel("Commenter", UUID.randomUUID().toString(), "USER");
        entityManager.persist(commenter);
        AccountModel liker = new AccountModel("Liker", UUID.randomUUID().toString(), "USER");
        entityManager.persist(liker);

        CommentModel commentModel = new CommentModel("lsfdjnvlsdf", 7649128374L, storeModel, commenter);
        entityManager.persist(commentModel);
        entityManager.persist(new CommentLikeModel(commentModel, liker));

        List<CommentLikeModel> notExist = commentLikeRepository.findByAccountAndStore(commenter, storeModel);
        assertTrue("not exist", notExist.size() == 0);

        List<CommentLikeModel> exist = commentLikeRepository.findByAccountAndStore(liker, storeModel);
        assertTrue("exist", exist.size() == 1);
        assertEquals("Check comment", exist.get(0).getComment(), commentModel);

    }

}
