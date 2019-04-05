package okhttp3.internal.duplex;

import okhttp3.Headers;

public interface HeadersListener {
  void onHeaders(Headers headers);
}
