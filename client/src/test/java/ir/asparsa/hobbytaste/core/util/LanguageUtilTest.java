package ir.asparsa.hobbytaste.core.util;

import ir.asparsa.hobbytaste.core.manager.PreferencesManager;
import ir.asparsa.hobbytaste.core.util.LanguageUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author hadi
 * @since 3/23/2017 AD.
 */
@RunWith(MockitoJUnitRunner.class)
public class LanguageUtilTest {

    @Test
    public void getLocaleFarsiTest() {
        PreferencesManager preferencesManager = Mockito.mock(PreferencesManager.class);
        when(preferencesManager.getString(eq(PreferencesManager.KEY_DEFAULT_LANGUAGE), anyString()))
                .thenReturn(LanguageUtil.LANGUAGE_FA);

        LanguageUtil.reset();
        assertEquals("", LanguageUtil.LANGUAGE_FA, LanguageUtil.getLocale(preferencesManager).getLanguage());
    }

    @Test
    public void getLocaleEnglishTest() {
        PreferencesManager preferencesManager = Mockito.mock(PreferencesManager.class);
        when(preferencesManager.getString(eq(PreferencesManager.KEY_DEFAULT_LANGUAGE), anyString()))
                .thenReturn(LanguageUtil.LANGUAGE_EN);

        LanguageUtil.reset();
        assertEquals("", LanguageUtil.LANGUAGE_EN, LanguageUtil.getLocale(preferencesManager).getLanguage());
    }

    @Test
    public void setDefaultLanguageEnglishTest() {
        PreferencesManager preferencesManager = Mockito.mock(PreferencesManager.class);
        when(preferencesManager.getString(eq(PreferencesManager.KEY_DEFAULT_LANGUAGE), anyString()))
                .thenReturn(LanguageUtil.LANGUAGE_FA)
                .thenReturn(LanguageUtil.LANGUAGE_EN);

        LanguageUtil.reset();
        assertEquals("", LanguageUtil.LANGUAGE_FA, LanguageUtil.getLocale(preferencesManager).getLanguage());
        LanguageUtil.setDefaultLanguage(preferencesManager, LanguageUtil.LANGUAGE_EN);

        assertEquals("", LanguageUtil.LANGUAGE_EN, LanguageUtil.getLocale(preferencesManager).getLanguage());
        verify(preferencesManager).put(eq(PreferencesManager.KEY_DEFAULT_LANGUAGE), eq(LanguageUtil.LANGUAGE_EN));
    }

    @Test
    public void setDefaultLanguageFarsiTest() {
        PreferencesManager preferencesManager = Mockito.mock(PreferencesManager.class);
        when(preferencesManager.getString(eq(PreferencesManager.KEY_DEFAULT_LANGUAGE), anyString()))
                .thenReturn(LanguageUtil.LANGUAGE_EN)
                .thenReturn(LanguageUtil.LANGUAGE_FA);

        LanguageUtil.reset();
        assertEquals("", LanguageUtil.LANGUAGE_EN, LanguageUtil.getLocale(preferencesManager).getLanguage());
        LanguageUtil.setDefaultLanguage(preferencesManager, LanguageUtil.LANGUAGE_FA);

        assertEquals("", LanguageUtil.LANGUAGE_FA, LanguageUtil.getLocale(preferencesManager).getLanguage());
        verify(preferencesManager).put(eq(PreferencesManager.KEY_DEFAULT_LANGUAGE), eq(LanguageUtil.LANGUAGE_FA));
    }

    @Test
    public void isRtlEnglishTest() {
        PreferencesManager preferencesManager = Mockito.mock(PreferencesManager.class);
        when(preferencesManager.getString(eq(PreferencesManager.KEY_DEFAULT_LANGUAGE), anyString()))
                .thenReturn(LanguageUtil.LANGUAGE_EN);

        LanguageUtil.reset();
        assertEquals("", LanguageUtil.LANGUAGE_EN, LanguageUtil.getLocale(preferencesManager).getLanguage());
        assertFalse("", LanguageUtil.isRTL());
    }


    @Test
    public void isRtlFarsiTest() {
        PreferencesManager preferencesManager = Mockito.mock(PreferencesManager.class);
        when(preferencesManager.getString(eq(PreferencesManager.KEY_DEFAULT_LANGUAGE), anyString()))
                .thenReturn(LanguageUtil.LANGUAGE_FA);

        LanguageUtil.reset();
        assertEquals("", LanguageUtil.LANGUAGE_FA, LanguageUtil.getLocale(preferencesManager).getLanguage());
        assertTrue("", LanguageUtil.isRTL());
    }
}
