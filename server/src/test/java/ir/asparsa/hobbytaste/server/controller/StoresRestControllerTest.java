package ir.asparsa.hobbytaste.server.controller;

import ir.asparsa.common.net.dto.StoreProto;
import ir.asparsa.common.net.path.StoreServicePath;
import ir.asparsa.common.util.MapUtil;
import ir.asparsa.hobbytaste.server.database.model.AccountModel;
import ir.asparsa.hobbytaste.server.database.model.StoreLikeModel;
import ir.asparsa.hobbytaste.server.database.model.StoreModel;
import ir.asparsa.hobbytaste.server.database.repository.*;
import ir.asparsa.hobbytaste.server.security.config.WebSecurityConfig;
import ir.asparsa.hobbytaste.server.storage.StorageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MvcResult;

import java.util.*;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author hadi
 * @since 3/24/2017 AD.
 */
@RunWith(SpringRunner.class)
@WebMvcTest(StoresRestController.class)
public class StoresRestControllerTest extends BaseControllerTest {

    private final static Logger logger = LoggerFactory.getLogger(StoresRestControllerTest.class);

    @MockBean
    StoreRepository storeRepository;
    @MockBean
    StoreBannerRepository storeBannerRepository;
    @MockBean
    StoreLikeRepository storeLikeRepository;
    @MockBean
    StoreCommentRepository storeCommentRepository;
    @MockBean
    CommentLikeRepository commentLikeRepository;
    @MockBean
    StorageService storageService;

    @Test
    public void readEmptyStores() throws Exception {
        String hash = UUID.randomUUID().toString();

        String token = "token";
        jwtAuthenticationTokenFilterMock.setToken(token);

        AccountModel accountModel = new AccountModel("testUser", hash, "USER");
        jwtAuthenticationProviderMock.setParsedUser(accountModel);

        given(storeRepository.findAll())
                .willReturn(new ArrayList<>());
        given(storeLikeRepository.findByAccount(accountModel))
                .willReturn(new ArrayList<>());

        MvcResult result = this.mockMvc.perform(
                get(WebSecurityConfig.ENTRY_POINT_API + "/" + StoreServicePath.SERVICE)
                        .accept(ProtobufHttpMessageConverter.PROTOBUF))
                                       .andExpect(status().isOk())
                                       .andExpect(content().contentType(ProtobufHttpMessageConverter.PROTOBUF))
                                       .andReturn();

        StoreProto.Stores stores = StoreProto.Stores.parseFrom(result.getResponse().getContentAsByteArray());

        assertThat(stores.getStoreCount()).isEqualTo(0);
    }

    @Test
    public void readFilledStores() throws Exception {
        String hash = UUID.randomUUID().toString();

        String token = "token";
        jwtAuthenticationTokenFilterMock.setToken(token);

        AccountModel accountModel = new AccountModel("testUser", hash, "USER");
        jwtAuthenticationProviderMock.setParsedUser(accountModel);

        ArrayList<StoreModel> models = new ArrayList<>();
        StoreProto.Store store = StoreProto.Store
                .newBuilder()
                .setLat(43.234d)
                .setLon(54.34d)
                .setTitle("sdfvs")
                .setDescription("sdfvsdfv")
                .setHashCode(2345345L)
                .build();
        models.add(StoreModel.newInstance(store));
        given(storeRepository.findAll())
                .willReturn(models);
        given(storeLikeRepository.findByAccount(accountModel))
                .willReturn(new ArrayList<>());

        MvcResult result = this.mockMvc.perform(
                get(WebSecurityConfig.ENTRY_POINT_API + "/" + StoreServicePath.SERVICE)
                        .accept(ProtobufHttpMessageConverter.PROTOBUF))
                                       .andExpect(status().isOk())
                                       .andExpect(content().contentType(ProtobufHttpMessageConverter.PROTOBUF))
                                       .andReturn();

        StoreProto.Stores stores = StoreProto.Stores.parseFrom(result.getResponse().getContentAsByteArray());

        assertThat(stores.getStoreCount()).isEqualTo(1);
        assertThat(stores.getStoreList().get(0).getLat()).isEqualTo(store.getLat());
        assertThat(stores.getStoreList().get(0).getLon()).isEqualTo(store.getLon());
        assertThat(stores.getStoreList().get(0).getTitle()).isEqualTo(store.getTitle());
        assertThat(stores.getStoreList().get(0).getDescription()).isEqualTo(store.getDescription());
        assertThat(stores.getStoreList().get(0).getHashCode()).isEqualTo(store.getHashCode());
        assertThat(stores.getStoreList().get(0).getLike()).isEqualTo(false);
    }

    @Test
    public void readFilledLikeStores() throws Exception {
        long hashCode = new Random().nextLong();
        String hash = UUID.randomUUID().toString();

        String token = "token";
        jwtAuthenticationTokenFilterMock.setToken(token);

        AccountModel accountModel = new AccountModel("testUser", hash, "USER");
        jwtAuthenticationProviderMock.setParsedUser(accountModel);

        ArrayList<StoreModel> models = new ArrayList<>();
        StoreProto.Store store = StoreProto.Store
                .newBuilder()
                .setLat(43.234d)
                .setLon(54.34d)
                .setTitle("sdfvs")
                .setDescription("sdfvsdfv")
                .setHashCode(2345345L)
                .build();
        StoreModel storeModel = StoreModel.newInstance(store);
        models.add(storeModel);

        given(storeRepository.findAll())
                .willReturn(models);

        ArrayList<StoreLikeModel> likeList = new ArrayList<>();
        likeList.add(new StoreLikeModel(storeModel, accountModel));

        given(storeLikeRepository.findByAccount(accountModel))
                .willReturn(likeList);

        MvcResult result = this.mockMvc.perform(
                get(WebSecurityConfig.ENTRY_POINT_API + "/" + StoreServicePath.SERVICE)
                        .accept(ProtobufHttpMessageConverter.PROTOBUF))
                                       .andExpect(status().isOk())
                                       .andExpect(content().contentType(ProtobufHttpMessageConverter.PROTOBUF))
                                       .andReturn();

        StoreProto.Stores stores = StoreProto.Stores.parseFrom(result.getResponse().getContentAsByteArray());

        assertThat(stores.getStoreCount()).isEqualTo(1);
        assertThat(stores.getStoreList().get(0).getLat()).isEqualTo(store.getLat());
        assertThat(stores.getStoreList().get(0).getLon()).isEqualTo(store.getLon());
        assertThat(stores.getStoreList().get(0).getTitle()).isEqualTo(store.getTitle());
        assertThat(stores.getStoreList().get(0).getDescription()).isEqualTo(store.getDescription());
        assertThat(stores.getStoreList().get(0).getHashCode()).isEqualTo(store.getHashCode());
        assertThat(stores.getStoreList().get(0).getLike()).isEqualTo(true);
    }

    @Test
    public void readPagedEmptyStores() throws Exception {
        String hash = UUID.randomUUID().toString();

        String token = "token";
        jwtAuthenticationTokenFilterMock.setToken(token);

        AccountModel accountModel = new AccountModel("testUser", hash, "USER");
        jwtAuthenticationProviderMock.setParsedUser(accountModel);

        given(storeRepository.findAll())
                .willReturn(new ArrayList<>());
        given(storeLikeRepository.findByAccount(accountModel))
                .willReturn(new ArrayList<>());

        MvcResult result = this.mockMvc.perform(
                post(WebSecurityConfig.ENTRY_POINT_API + "/" + StoreServicePath.SERVICE)
                        .accept(ProtobufHttpMessageConverter.PROTOBUF)
                        .param("latitude", "0")
                        .param("longitude", "0")
                        .param("page", "20")
                        .param("size", "30"))
                                       .andExpect(status().isOk())
                                       .andExpect(content().contentType(ProtobufHttpMessageConverter.PROTOBUF))
                                       .andReturn();

        StoreProto.Stores stores = StoreProto.Stores.parseFrom(result.getResponse().getContentAsByteArray());

        assertThat(stores.getStoreCount()).isEqualTo(0);
    }

    @Test
    public void readPagedFilledStores() throws Exception {
        String hash = UUID.randomUUID().toString();

        String token = "token";
        jwtAuthenticationTokenFilterMock.setToken(token);

        AccountModel accountModel = new AccountModel("testUser", hash, "USER");
        jwtAuthenticationProviderMock.setParsedUser(accountModel);

        ArrayList<StoreModel> models = new ArrayList<>();
        StoreProto.Store store = StoreProto.Store
                .newBuilder()
                .setLat(43.234d)
                .setLon(54.34d)
                .setTitle("sdfvs")
                .setDescription("sdfvsdfv")
                .setHashCode(2345345L)
                .build();
        models.add(StoreModel.newInstance(store));
        given(storeRepository.findAll())
                .willReturn(models);
        given(storeLikeRepository.findByAccount(accountModel))
                .willReturn(new ArrayList<>());

        MvcResult result = this.mockMvc.perform(
                post(WebSecurityConfig.ENTRY_POINT_API + "/" + StoreServicePath.SERVICE)
                        .accept(ProtobufHttpMessageConverter.PROTOBUF)
                        .param("latitude", "0")
                        .param("longitude", "0")
                        .param("page", "0")
                        .param("size", "30"))
                                       .andExpect(status().isOk())
                                       .andExpect(content().contentType(ProtobufHttpMessageConverter.PROTOBUF))
                                       .andReturn();

        StoreProto.Stores stores = StoreProto.Stores.parseFrom(result.getResponse().getContentAsByteArray());

        assertThat(stores.getStoreCount()).isEqualTo(1);
        assertThat(stores.getStoreList().get(0).getLat()).isEqualTo(store.getLat());
        assertThat(stores.getStoreList().get(0).getLon()).isEqualTo(store.getLon());
        assertThat(stores.getStoreList().get(0).getTitle()).isEqualTo(store.getTitle());
        assertThat(stores.getStoreList().get(0).getDescription()).isEqualTo(store.getDescription());
        assertThat(stores.getStoreList().get(0).getHashCode()).isEqualTo(store.getHashCode());
        assertThat(stores.getStoreList().get(0).getLike()).isEqualTo(false);
    }

    @Test
    public void readPagedFilledLikeStores() throws Exception {
        long hashCode = new Random().nextLong();
        String hash = UUID.randomUUID().toString();

        String token = "token";
        jwtAuthenticationTokenFilterMock.setToken(token);

        AccountModel accountModel = new AccountModel("testUser", hash, "USER");
        jwtAuthenticationProviderMock.setParsedUser(accountModel);

        ArrayList<StoreModel> models = new ArrayList<>();
        StoreProto.Store store = StoreProto.Store
                .newBuilder()
                .setLat(43.234d)
                .setLon(54.34d)
                .setTitle("sdfvs")
                .setDescription("sdfvsdfv")
                .setHashCode(2345345L)
                .build();
        StoreModel storeModel = StoreModel.newInstance(store);
        models.add(storeModel);

        given(storeRepository.findAll())
                .willReturn(models);

        ArrayList<StoreLikeModel> likeList = new ArrayList<>();
        likeList.add(new StoreLikeModel(storeModel, accountModel));

        given(storeLikeRepository.findByAccount(accountModel))
                .willReturn(likeList);

        MvcResult result = this.mockMvc.perform(
                post(WebSecurityConfig.ENTRY_POINT_API + "/" + StoreServicePath.SERVICE)
                        .accept(ProtobufHttpMessageConverter.PROTOBUF)
                        .param("latitude", "0")
                        .param("longitude", "0")
                        .param("page", "0")
                        .param("size", "30"))
                                       .andExpect(status().isOk())
                                       .andExpect(content().contentType(ProtobufHttpMessageConverter.PROTOBUF))
                                       .andReturn();

        StoreProto.Stores stores = StoreProto.Stores.parseFrom(result.getResponse().getContentAsByteArray());

        assertThat(stores.getStoreCount()).isEqualTo(1);
        assertThat(stores.getStoreList().get(0).getLat()).isEqualTo(store.getLat());
        assertThat(stores.getStoreList().get(0).getLon()).isEqualTo(store.getLon());
        assertThat(stores.getStoreList().get(0).getTitle()).isEqualTo(store.getTitle());
        assertThat(stores.getStoreList().get(0).getDescription()).isEqualTo(store.getDescription());
        assertThat(stores.getStoreList().get(0).getHashCode()).isEqualTo(store.getHashCode());
        assertThat(stores.getStoreList().get(0).getLike()).isEqualTo(true);
    }

    @Test
    public void readPagingFilledStores() throws Exception {
        String hash = UUID.randomUUID().toString();

        String token = "token";
        jwtAuthenticationTokenFilterMock.setToken(token);

        AccountModel accountModel = new AccountModel("testUser", hash, "USER");
        jwtAuthenticationProviderMock.setParsedUser(accountModel);

        ArrayList<StoreModel> models = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 50; i++) {
            StoreProto.Store store = StoreProto.Store
                    .newBuilder()
                    .setLat((random.nextDouble() - .5) * 80)
                    .setLon((random.nextDouble() - .5) * 80)
                    .setTitle(UUID.randomUUID().toString())
                    .setDescription(UUID.randomUUID().toString())
                    .setHashCode(random.nextLong())
                    .build();
            models.add(StoreModel.newInstance(store));
        }
        given(storeRepository.findAll())
                .willReturn(models);
        given(storeLikeRepository.findByAccount(accountModel))
                .willReturn(new ArrayList<>());
        int page = 4;
        int size = 5;
        float latitude = (random.nextFloat() - .5f) * 80;
        float longitude = (random.nextFloat() - .5f) * 80;

        MvcResult result = this.mockMvc.perform(
                post(WebSecurityConfig.ENTRY_POINT_API + "/" + StoreServicePath.SERVICE)
                        .accept(ProtobufHttpMessageConverter.PROTOBUF)
                        .param("latitude", Float.toString(latitude))
                        .param("longitude", Float.toString(longitude))
                        .param("page", Integer.toString(page))
                        .param("size", Integer.toString(size)))
                                       .andExpect(status().isOk())
                                       .andExpect(content().contentType(ProtobufHttpMessageConverter.PROTOBUF))
                                       .andReturn();

        StoreProto.Stores stores = StoreProto.Stores.parseFrom(result.getResponse().getContentAsByteArray());
        logger.info("stores " + stores);
        logger.info("models " + models);

        assertThat(stores.getStoreCount()).isEqualTo(size);
        assertThat(stores.getTotalElements()).isEqualTo(models.size());
        for (StoreProto.Store store : stores.getStoreList()) {
            StoreModel storeModel = StoreModel.newInstance(store);
            for (StoreModel model : models) {
                if (model.equals(storeModel)) {
                    assertThat(model.getLat()).isEqualTo(store.getLat());
                    assertThat(model.getLon()).isEqualTo(store.getLon());
                    assertThat(model.getTitle()).isEqualTo(store.getTitle());
                    assertThat(model.getDescription()).isEqualTo(store.getDescription());
                    assertThat(model.getHashCode()).isEqualTo(store.getHashCode());
                    assertThat(false).isEqualTo(store.getLike());
                    break;
                }
            }
        }

        float minDistance = MapUtil
                .distFrom(latitude, longitude, stores.getStore(0).getLat(), stores.getStore(0).getLon());
        int count = 0;
        for (StoreModel model : models) {
            if (MapUtil.distFrom(latitude, longitude, model.getLat(), model.getLon()) < minDistance) {
                count++;
            }
        }

        assertThat(count).isEqualTo(page * size);
    }

    @Test
    public void saveStore() throws Exception {
        String hash = UUID.randomUUID().toString();
        long hashCode = new Random().nextLong();

        String token = "token";
        jwtAuthenticationTokenFilterMock.setToken(token);

        AccountModel accountModel = new AccountModel("testUser", hash, "USER");
        jwtAuthenticationProviderMock.setParsedUser(accountModel);

        StoreProto.Store store = StoreProto.Store
                .newBuilder()
                .setLat(43.234d)
                .setLon(54.34d)
                .setTitle("sdfvs")
                .setDescription("sdfvsdfv")
                .setHashCode(hashCode)
                .build();

        given(storeRepository.findByHashCode(eq(hashCode)))
                .willReturn(Optional.empty());
        given(storeRepository.save(any(StoreModel.class)))
                .willReturn(StoreModel.newInstance(store));

        MvcResult result = this.mockMvc.perform(
                put(WebSecurityConfig.ENTRY_POINT_API + "/" + StoreServicePath.SERVICE)
                        .accept(ProtobufHttpMessageConverter.PROTOBUF)
                        .contentType(ProtobufHttpMessageConverter.PROTOBUF)
                        .content(store.toByteArray()))
                                       .andExpect(status().isOk())
                                       .andExpect(content().contentType(ProtobufHttpMessageConverter.PROTOBUF))
                                       .andReturn();

        store = StoreProto.Store.parseFrom(result.getResponse().getContentAsByteArray());

        assertThat(store.getLat()).isEqualTo(store.getLat());
        assertThat(store.getLon()).isEqualTo(store.getLon());
        assertThat(store.getTitle()).isEqualTo(store.getTitle());
        assertThat(store.getDescription()).isEqualTo(store.getDescription());
        assertThat(store.getHashCode()).isEqualTo(store.getHashCode());
    }


    @Test
    public void savedStore() throws Exception {
        String hash = UUID.randomUUID().toString();
        long hashCode = new Random().nextLong();

        String token = "token";
        jwtAuthenticationTokenFilterMock.setToken(token);

        AccountModel accountModel = new AccountModel("testUser", hash, "USER");
        jwtAuthenticationProviderMock.setParsedUser(accountModel);

        StoreProto.Store store = StoreProto.Store
                .newBuilder()
                .setLat(43.234d)
                .setLon(54.34d)
                .setTitle("sdfvs")
                .setDescription("sdfvsdfv")
                .setHashCode(hashCode)
                .build();

        given(storeRepository.findByHashCode(eq(hashCode)))
                .willReturn(Optional.of(StoreModel.newInstance(store)));

        given(storeLikeRepository.findByAccount(accountModel))
                .willReturn(new ArrayList<>());

        MvcResult result = this.mockMvc.perform(
                put(WebSecurityConfig.ENTRY_POINT_API + "/" + StoreServicePath.SERVICE)
                        .accept(ProtobufHttpMessageConverter.PROTOBUF)
                        .contentType(ProtobufHttpMessageConverter.PROTOBUF)
                        .content(store.toByteArray()))
                                       .andExpect(status().isOk())
                                       .andExpect(content().contentType(ProtobufHttpMessageConverter.PROTOBUF))
                                       .andReturn();

        store = StoreProto.Store.parseFrom(result.getResponse().getContentAsByteArray());

        assertThat(store.getLat()).isEqualTo(store.getLat());
        assertThat(store.getLon()).isEqualTo(store.getLon());
        assertThat(store.getTitle()).isEqualTo(store.getTitle());
        assertThat(store.getDescription()).isEqualTo(store.getDescription());
        assertThat(store.getHashCode()).isEqualTo(store.getHashCode());
        assertThat(store.getLike()).isEqualTo(false);
    }

    @Test
    public void savedLikedStore() throws Exception {
        String hash = UUID.randomUUID().toString();
        long hashCode = new Random().nextLong();

        String token = "token";
        jwtAuthenticationTokenFilterMock.setToken(token);

        AccountModel accountModel = new AccountModel("testUser", hash, "USER");
        jwtAuthenticationProviderMock.setParsedUser(accountModel);

        StoreProto.Store store = StoreProto.Store
                .newBuilder()
                .setLat(43.234d)
                .setLon(54.34d)
                .setTitle("sdfvs")
                .setDescription("sdfvsdfv")
                .setHashCode(hashCode)
                .build();

        StoreModel storeModel = StoreModel.newInstance(store);
        given(storeRepository.findByHashCode(eq(hashCode)))
                .willReturn(Optional.of(storeModel));

        List<StoreLikeModel> likeList = new ArrayList<>();
        likeList.add(new StoreLikeModel(storeModel, accountModel));
        given(storeLikeRepository.findByAccount(accountModel))
                .willReturn(likeList);

        MvcResult result = this.mockMvc.perform(
                put(WebSecurityConfig.ENTRY_POINT_API + "/" + StoreServicePath.SERVICE)
                        .accept(ProtobufHttpMessageConverter.PROTOBUF)
                        .contentType(ProtobufHttpMessageConverter.PROTOBUF)
                        .content(store.toByteArray()))
                                       .andExpect(status().isOk())
                                       .andExpect(content().contentType(ProtobufHttpMessageConverter.PROTOBUF))
                                       .andReturn();

        store = StoreProto.Store.parseFrom(result.getResponse().getContentAsByteArray());

        assertThat(store.getLat()).isEqualTo(store.getLat());
        assertThat(store.getLon()).isEqualTo(store.getLon());
        assertThat(store.getTitle()).isEqualTo(store.getTitle());
        assertThat(store.getDescription()).isEqualTo(store.getDescription());
        assertThat(store.getHashCode()).isEqualTo(store.getHashCode());
        assertThat(store.getLike()).isEqualTo(true);
    }

    @Test
    public void storeViewed() throws Exception {

    }
}