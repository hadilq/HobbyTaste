package ir.asparsa.hobbytaste.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.asparsa.common.net.dto.StoreProto;
import ir.asparsa.common.net.path.StoreServicePath;
import ir.asparsa.hobbytaste.server.Application;
import ir.asparsa.hobbytaste.server.database.model.AccountModel;
import ir.asparsa.hobbytaste.server.database.model.StoreLikeModel;
import ir.asparsa.hobbytaste.server.database.model.StoreModel;
import ir.asparsa.hobbytaste.server.database.repository.*;
import ir.asparsa.hobbytaste.server.security.*;
import ir.asparsa.hobbytaste.server.security.config.WebSecurityConfig;
import ir.asparsa.hobbytaste.server.security.config.WebSecurityConfigMock;
import ir.asparsa.hobbytaste.server.storage.StorageService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.Random;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author hadi
 * @since 3/24/2017 AD.
 */
@RunWith(SpringRunner.class)
@WebMvcTest(StoresRestController.class)
@EnableAutoConfiguration(exclude = {
        WebSecurityConfig.class,
        JwtAuthenticationProvider.class
})
@ContextConfiguration(classes = {
        Application.class,
        WebSecurityConfigMock.class,
        JwtAuthenticationEntryPoint.class,
        JwtAuthenticationProviderMock.class,
        JwtAuthenticationFailureHandler.class
})
@WebAppConfiguration
public class StoresRestControllerTest {

    @Autowired
    private WebApplicationContext context;
    @Autowired
    private FilterChainProxy filterChainProxy;
    @Autowired
    private JwtAuthenticationProviderMock jwtAuthenticationProviderMock;
    @Autowired
    private JwtAuthenticationTokenFilterMock jwtAuthenticationTokenFilterMock;

    private MockMvc mockMvc;

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

    @Before
    public void setup() {
        ObjectMapper objectMapper = new ObjectMapper();
        // Possibly configure the mapper
        JacksonTester.initFields(this, objectMapper);


        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .dispatchOptions(true)
                .addFilter(filterChainProxy, "/*")
                .apply(springSecurity())
                .build();
    }

    @Test
    public void readEmptyStores() throws Exception {
        long hashCode = new Random().nextLong();

        String token = "token";
        jwtAuthenticationTokenFilterMock.setToken(token);

        AccountModel accountModel = new AccountModel("testUser", hashCode, "USER");
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
        long hashCode = new Random().nextLong();

        String token = "token";
        jwtAuthenticationTokenFilterMock.setToken(token);

        AccountModel accountModel = new AccountModel("testUser", hashCode, "USER");
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
    }

}