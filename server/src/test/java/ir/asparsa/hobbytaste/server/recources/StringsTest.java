package ir.asparsa.hobbytaste.server.recources;

import ir.asparsa.hobbytaste.server.resources.Strings;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.util.Locale;

import static org.springframework.test.util.AssertionErrors.assertEquals;

/**
 * @author hadi
 * @since 3/22/2017 AD.
 */
public class StringsTest {

    private ReloadableResourceBundleMessageSource resourceBundle;

    @Before
    public void setup() {
        resourceBundle = new ReloadableResourceBundleMessageSource();
        resourceBundle.setBasename("classpath:locale/messages");
        resourceBundle.setCacheSeconds(100); //reload messages every 100 seconds
    }

    @Test
    public void english() {
        assertEquals(
                "English", resourceBundle.getMessage(Strings.ACCOUNT_NOT_FOUND, null, new Locale("en")),
                "Account is not found");
    }

    @Test
    public void farsi() {
        assertEquals(
                "Farsi", resourceBundle.getMessage(Strings.ACCOUNT_NOT_FOUND, null, new Locale("fa")),
                "\u062d\u0633\u0627\u0628\u200c\u06a9\u0627\u0631\u0628\u0631\u06cc \u067e\u06cc\u062f\u0627 \u0646\u0634\u062f");
    }
}
