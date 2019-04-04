package net.bytebuddy.android.test;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.android.AndroidClassLoadingStrategy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.utility.RandomString;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.Callable;

import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * This activity allows to run a code generation on an Android device.
 */
public class TestActivity extends Activity {

    /**
     * A sample String to be returned by an instrumented {@link Object#toString()} method.
     */
    private static final String FOO = "foo";

    /**
     * The tag to be used for Android's log messages.
     */
    private static final String BYTE_BUDDY_TAG = "net.bytebuddy";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        TextView mavenInfo = (TextView) findViewById(R.id.maven_info);
        String version = "n/a";
        try {
            InputStream inputStream = TestActivity.class.getClassLoader().getResourceAsStream("maven.properties");
            if (inputStream != null) {
                try {
                    Properties properties = new Properties();
                    properties.load(inputStream);
                    version = properties.getProperty("version", version);
                } finally {
                    inputStream.close();
                }
            }
        } catch (Exception exception) {
            Log.i(BYTE_BUDDY_TAG, "Could not read version", exception);
            Toast.makeText(TestActivity.this, "Warning: Could not read version property. (" + exception.getMessage() + ")", Toast.LENGTH_SHORT).show();
        }
        mavenInfo.setText(getResources().getString(R.string.version_info, version));
        Button runTest = (Button) findViewById(R.id.run_test);
        runTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ByteBuddy byteBuddy;
                try {
                    byteBuddy = new ByteBuddy();
                } catch (Throwable throwable) {
                    Log.w(BYTE_BUDDY_TAG, throwable);
                    Toast.makeText(TestActivity.this, "Failure: Could not create Byte Buddy instance. (" + throwable.getMessage() + ")", Toast.LENGTH_LONG).show();
                    return;
                }
                try {
                    File file = TestActivity.this.getDir(RandomString.make(), Context.MODE_PRIVATE);
                    if (!file.isDirectory()) {
                        throw new IOException("Not a directory: " + file);
                    }
                    DynamicType.Loaded<?> dynamicType;
                    try {
                        dynamicType = byteBuddy.subclass(Object.class)
                                .method(named("toString")).intercept(MethodDelegation.to(Interceptor.class))
                                .make()
                                .load(TestActivity.class.getClassLoader(), new AndroidClassLoadingStrategy(file));
                    } catch (Throwable throwable) {
                        Log.w(BYTE_BUDDY_TAG, throwable);
                        Toast.makeText(TestActivity.this, "Failure: Could not load dynamic type. (" + throwable.getMessage() + ")", Toast.LENGTH_LONG).show();
                        return;
                    }
                    try {
                        String value = dynamicType.getLoaded().newInstance().toString();
                        Toast.makeText(TestActivity.this,
                                FOO.equals(value)
                                        ? "Success: Created type and verified instrumentation."
                                        : "Failure: Expected different value by instrumented method. (was: " + value + ")",
                                Toast.LENGTH_LONG).show();
                    } catch (Throwable throwable) {
                        Log.w(BYTE_BUDDY_TAG, throwable);
                        Toast.makeText(TestActivity.this, "Failure: Could create dynamic instance. (" + throwable.getMessage() + ")", Toast.LENGTH_LONG).show();
                    }
                } catch (Throwable throwable) {
                    Log.w(BYTE_BUDDY_TAG, throwable);
                    Toast.makeText(TestActivity.this, "Failure: Could not create temporary file. (" + throwable.getMessage() + ")", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * An interceptor to be used in the instrumentation of the {@link Object#toString()} method. Of course, this
     * could also be achieved by using a {@link net.bytebuddy.implementation.FixedValue} instrumentation. However,
     * the instrumentation should generate an {@link net.bytebuddy.implementation.auxiliary.AuxiliaryType}
     * to validate their functionality.
     */
    public static class Interceptor {

        /**
         * The interception method to be applied.
         *
         * @param zuper A proxy to call the super method to validate the functioning og creating an auxiliary type.
         * @return The value to be returned by the instrumented {@link Object#toString()} method.
         * @throws Exception If an exception occurs.
         */
        public static String intercept(@SuperCall Callable<String> zuper) throws Exception {
            String toString = zuper.call();
            if (toString.equals(FOO)) {
                throw new IllegalStateException("Super call proxy invocation did not derive in its value");
            }
            return FOO;
        }
    }
}

