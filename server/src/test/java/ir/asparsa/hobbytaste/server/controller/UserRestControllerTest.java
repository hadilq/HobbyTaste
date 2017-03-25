package ir.asparsa.hobbytaste.server.controller;

import ir.asparsa.common.net.dto.AuthenticateProto;
import ir.asparsa.common.net.path.UserServicePath;
import ir.asparsa.hobbytaste.server.database.model.AccountModel;
import ir.asparsa.hobbytaste.server.database.repository.AccountRepository;
import ir.asparsa.hobbytaste.server.security.config.WebSecurityConfig;
import ir.asparsa.hobbytaste.server.storage.StorageService;
import ir.asparsa.hobbytaste.server.util.JwtTokenUtil;
import ir.asparsa.hobbytaste.server.util.RequestLogUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;
import java.util.Random;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author hadi
 * @since 3/22/2017 AD.
 */
@RunWith(SpringRunner.class)
@WebMvcTest(UserRestController.class)
public class UserRestControllerTest extends BaseControllerTest {

    @MockBean
    private AccountRepository accountRepository;
    @MockBean
    private RequestLogUtil requestLogUtil;
    @MockBean
    private StorageService storageService;
    @MockBean
    private JwtTokenUtil jwtTokenUtil;

    @Test
    public void authenticationTest() throws Exception {
        long hashCode = new Random().nextLong();

        String token = "token";

        AccountModel accountModel = new AccountModel("testUser", hashCode, "USER");
        given(accountRepository.findByHashCode(hashCode))
                .willReturn(Optional.of(accountModel));
        given(jwtTokenUtil.generateToken(accountModel))
                .willReturn(token);

        AuthenticateProto.Request request = AuthenticateProto.Request
                .newBuilder()
                .setHashCode(hashCode)
                .build();

        MvcResult result = this.mockMvc.perform(
                post(WebSecurityConfig.ENTRY_POINT_API + "/" +
                     UserServicePath.SERVICE + UserServicePath.AUTHENTICATE)
                        .accept(ProtobufHttpMessageConverter.PROTOBUF)
                        .contentType(ProtobufHttpMessageConverter.PROTOBUF)
                        .content(request.toByteArray()))
                                       .andExpect(status().isOk())
                                       .andExpect(content().contentType(ProtobufHttpMessageConverter.PROTOBUF))
                                       .andReturn();

        AuthenticateProto.Authenticate authenticateDto = AuthenticateProto.Authenticate
                .parseFrom(result.getResponse().getContentAsByteArray());
        assertThat(authenticateDto.getToken()).isEqualTo(token);
        assertThat(authenticateDto.getUsername()).isEqualTo(accountModel.getUsername());
    }

    @Test
    public void changeUsernameTest() throws Exception {
        long hashCode = new Random().nextLong();

        String token = "token";
        jwtAuthenticationTokenFilterMock.setToken(token);

        AccountModel accountModel = new AccountModel("testUser", hashCode, "USER");
        jwtAuthenticationProviderMock.setParsedUser(accountModel);

        given(jwtTokenUtil.generateToken(accountModel))
                .willReturn(token);

        String newUsername = "newUsername";
        MvcResult result = this.mockMvc.perform(
                post(WebSecurityConfig.ENTRY_POINT_API + "/" +
                     UserServicePath.SERVICE + UserServicePath.USERNAME.replace("{hashCode}", Long.toString(hashCode)))
                        .param("new", newUsername)
                        .accept(ProtobufHttpMessageConverter.PROTOBUF))
                                       .andExpect(status().isOk())
                                       .andExpect(content().contentType(ProtobufHttpMessageConverter.PROTOBUF))
                                       .andReturn();

        AuthenticateProto.Authenticate authenticateDto = AuthenticateProto.Authenticate
                .parseFrom(result.getResponse().getContentAsByteArray());
        assertThat(authenticateDto.getToken()).isEqualTo(token);
        assertThat(authenticateDto.getUsername()).isEqualTo(newUsername);
    }
}
