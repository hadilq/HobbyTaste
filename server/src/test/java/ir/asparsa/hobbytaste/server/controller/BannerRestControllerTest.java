package ir.asparsa.hobbytaste.server.controller;

import ir.asparsa.common.net.path.BannerServicePath;
import ir.asparsa.hobbytaste.server.resources.Strings;
import ir.asparsa.hobbytaste.server.security.config.WebSecurityConfig;
import ir.asparsa.hobbytaste.server.storage.StorageService;
import ir.asparsa.hobbytaste.server.util.ImageUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.ByteArrayInputStream;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author hadi
 * @since 4/24/2017 AD.
 */
@RunWith(SpringRunner.class)
@WebMvcTest(BannerRestController.class)
public class BannerRestControllerTest extends BaseControllerTest {

    private final static Logger logger = LoggerFactory.getLogger(StoresRestControllerTest.class);

    @MockBean
    StorageService storageService;
    @MockBean
    ImageUtil imageUtil;

    @Test
    public void serveFileTest() throws Exception {
        String fileName = "slkdfjnv";
        Resource resource = Mockito.mock(Resource.class);
        when(resource.getInputStream())
                .thenReturn(new ByteArrayInputStream("xncskdf".getBytes()));
        given(storageService.loadAsResource(fileName, Strings.DEFAULT_LOCALE))
                .willReturn(resource);

        // Test no authentication needed
        this.mockMvc.perform(
                get(WebSecurityConfig.ENTRY_POINT_API + "/" + BannerServicePath.SERVICE +
                    BannerServicePath.IMAGE.replace("{filename:.+}", fileName)))
                    .andExpect(status().isOk());

    }
}