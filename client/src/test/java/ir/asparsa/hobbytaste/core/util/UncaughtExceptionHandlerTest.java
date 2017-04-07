package ir.asparsa.hobbytaste.core.util;

import android.content.Context;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

/**
 * @author hadi
 * @since 3/23/2017 AD.
 */
public class UncaughtExceptionHandlerTest {

    @Test
    public void notDebugTest() {
        UncaughtExceptionHandler.UncaughtExceptionController controller = Mockito
                .mock(UncaughtExceptionHandler.UncaughtExceptionController.class);
        when(controller.isDebug()).thenReturn(false);
        when(controller.isAppDebug()).thenReturn(false);
        when(controller.isCrashReportActivityIsRunning()).thenReturn(false);

        UncaughtExceptionHandler handler = new UncaughtExceptionHandler(controller, null);
        handler.uncaughtException(Thread.currentThread(), new RuntimeException());

        verify(controller, times(1)).launchCrashReportActivity(isNull(Context.class), any(RuntimeException.class));
        verify(controller, times(1)).uncaughtException(any(Thread.class), any(RuntimeException.class));
        verify(controller, never()).debugMode(any(Thread.class), any(RuntimeException.class));
    }

    @Test
    public void debugTest() {
        UncaughtExceptionHandler.UncaughtExceptionController controller = Mockito
                .mock(UncaughtExceptionHandler.UncaughtExceptionController.class);
        when(controller.isDebug()).thenReturn(true);
        when(controller.isAppDebug()).thenReturn(true);
        when(controller.isCrashReportActivityIsRunning()).thenReturn(false);

        UncaughtExceptionHandler handler = new UncaughtExceptionHandler(controller, null);
        handler.uncaughtException(Thread.currentThread(), new RuntimeException());

        verify(controller, times(1)).launchCrashReportActivity(isNull(Context.class), any(RuntimeException.class));
        verify(controller, times(1)).uncaughtException(any(Thread.class), any(RuntimeException.class));
        verify(controller, times(1)).debugMode(any(Thread.class), any(RuntimeException.class));
    }

    @Test
    public void appDebugTest() {
        UncaughtExceptionHandler.UncaughtExceptionController controller = Mockito
                .mock(UncaughtExceptionHandler.UncaughtExceptionController.class);
        when(controller.isDebug()).thenReturn(false);
        when(controller.isAppDebug()).thenReturn(true);
        when(controller.isCrashReportActivityIsRunning()).thenReturn(false);

        UncaughtExceptionHandler handler = new UncaughtExceptionHandler(controller, null);
        handler.uncaughtException(Thread.currentThread(), new RuntimeException());

        verify(controller, never()).launchCrashReportActivity(isNull(Context.class), any(RuntimeException.class));
        verify(controller, never()).uncaughtException(any(Thread.class), any(RuntimeException.class));
        verify(controller, times(1)).debugMode(any(Thread.class), any(RuntimeException.class));
    }

    @Test
    public void appDebugCrashReportTest() {
        UncaughtExceptionHandler.UncaughtExceptionController controller = Mockito
                .mock(UncaughtExceptionHandler.UncaughtExceptionController.class);
        when(controller.isDebug()).thenReturn(false);
        when(controller.isAppDebug()).thenReturn(false);
        when(controller.isCrashReportActivityIsRunning()).thenReturn(true);

        UncaughtExceptionHandler handler = new UncaughtExceptionHandler(controller, null);
        handler.uncaughtException(Thread.currentThread(), new RuntimeException());

        verify(controller, never()).launchCrashReportActivity(isNull(Context.class), any(RuntimeException.class));
        verify(controller, times(1)).uncaughtException(any(Thread.class), any(RuntimeException.class));
        verify(controller, never()).debugMode(any(Thread.class), any(RuntimeException.class));
    }

    @Test
    public void cannotLaunchTest() {
        UncaughtExceptionHandler.UncaughtExceptionController controller = Mockito
                .mock(UncaughtExceptionHandler.UncaughtExceptionController.class);
        when(controller.isDebug()).thenReturn(false);
        when(controller.isAppDebug()).thenReturn(false);
        when(controller.isCrashReportActivityIsRunning()).thenReturn(false);
        doThrow(new RuntimeException()).when(controller)
                                       .launchCrashReportActivity(any(Context.class), any(Throwable.class));

        UncaughtExceptionHandler handler = new UncaughtExceptionHandler(controller, null);
        handler.uncaughtException(Thread.currentThread(), new RuntimeException());

        verify(controller, times(1)).launchCrashReportActivity(isNull(Context.class), any(RuntimeException.class));
        verify(controller, times(1)).uncaughtException(any(Thread.class), any(RuntimeException.class));
        verify(controller, never()).debugMode(any(Thread.class), any(RuntimeException.class));
    }
}