package ir.asparsa.hobbytaste.server.controller;

import ir.asparsa.common.net.dto.ResponseProto;
import ir.asparsa.common.net.path.UserServicePath;
import ir.asparsa.hobbytaste.server.database.repository.AccountRepository;
import ir.asparsa.hobbytaste.server.security.config.WebSecurityConfig;
import ir.asparsa.hobbytaste.server.storage.StorageService;
import ir.asparsa.hobbytaste.server.util.JwtTokenUtil;
import ir.asparsa.hobbytaste.server.util.RequestLogUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Random;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author hadi
 * @since 3/25/2017 AD.
 */
@RunWith(SpringRunner.class)
@WebMvcTest(UserRestController.class)
public class ServiceExceptionHandlerTest extends BaseControllerTest {

    private final static Logger logger = LoggerFactory.getLogger(ServiceExceptionHandlerTest.class);

    @MockBean
    private AccountRepository accountRepository;
    @MockBean
    private RequestLogUtil requestLogUtil;
    @MockBean
    private StorageService storageService;
    @MockBean
    private JwtTokenUtil jwtTokenUtil;

    @Test
    public void withoutTokenTest() throws Exception {
        long hashCode = new Random().nextLong();

        String newUsername = "newUsername";
        MvcResult result = this.mockMvc.perform(
                post(WebSecurityConfig.ENTRY_POINT_API + "/" +
                     UserServicePath.SERVICE + UserServicePath.USERNAME.replace("{hashCode}", Long.toString(hashCode)))
                        .param("new", newUsername)
                        .accept(ProtobufHttpMessageConverter.PROTOBUF))
                                       .andExpect(status().is(HttpStatus.UNAUTHORIZED.value()))
                                       .andExpect(content().contentType(ProtobufHttpMessageConverter.PROTOBUF))
                                       .andReturn();

        ResponseProto.Response error = ResponseProto.Response
                .parseFrom(result.getResponse().getContentAsByteArray());
        logger.info("Error dto: " + error);
        assertThat(error.getStatus()).isEqualTo(ResponseProto.Response.StatusType.ERROR);
        assertThat(error.getDetailMessage())
                .isEqualTo("No JWT token found in request headers. /api/v1/user/username/" + hashCode);
        assertThat(error.getLocalizedMessage()).isEqualTo("Token is not found");
    }


    @Test
    public void withoutParsedAccountTest() throws Exception {
        long hashCode = new Random().nextLong();

        String token = "token";
        jwtAuthenticationTokenFilterMock.setToken(token);

        String newUsername = "newUsername";
        MvcResult result = this.mockMvc.perform(
                post(WebSecurityConfig.ENTRY_POINT_API + "/" +
                     UserServicePath.SERVICE + UserServicePath.USERNAME.replace("{hashCode}", Long.toString(hashCode)))
                        .param("new", newUsername)
                        .accept(ProtobufHttpMessageConverter.PROTOBUF))
                                       .andExpect(status().is(HttpStatus.UNAUTHORIZED.value()))
                                       .andExpect(content().contentType(ProtobufHttpMessageConverter.PROTOBUF))
                                       .andReturn();

        ResponseProto.Response error = ResponseProto.Response
                .parseFrom(result.getResponse().getContentAsByteArray());
        logger.info("Error dto: " + error);
        assertThat(error.getStatus()).isEqualTo(ResponseProto.Response.StatusType.ERROR);
        assertThat(error.getDetailMessage()).isEqualTo("JWT token is not valid");
        assertThat(error.getLocalizedMessage()).isEqualTo("Token is not valid");
    }
}