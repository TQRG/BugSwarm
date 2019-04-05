package retrofit;

import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.ResponseBody;
import java.io.IOException;

public final class VoidConverter implements Converter<Void> {
  @Override public Void fromBody(ResponseBody body) throws IOException {
    body.close();
    return null;
  }

  @Override public RequestBody toBody(Void value) {
    throw new UnsupportedOperationException();
  }
}
