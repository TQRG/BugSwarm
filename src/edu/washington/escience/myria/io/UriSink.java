package edu.washington.escience.myria.io;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Objects;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import com.fasterxml.jackson.annotation.JsonProperty;

import edu.washington.escience.myria.coordinator.CatalogException;

public class UriSink implements DataSink {
  /** Required for Java serialization. */
  private static final long serialVersionUID = 1L;

  @JsonProperty
  private final URI uri;

  public UriSink(@JsonProperty(value = "uri", required = true) final String uri) throws CatalogException {
    this.uri = URI.create(Objects.requireNonNull(uri, "Parameter uri cannot be null"));
    if (!this.uri.getScheme().equals("hdfs")) {
      throw new CatalogException("URI must be an HDFS URI");
    }
  }

  @Override
  public OutputStream getOutputStream() throws IOException {
    Configuration conf = new Configuration();

    FileSystem fs = FileSystem.get(uri, conf);
    Path rootPath = new Path(uri);
    return fs.create(rootPath);
  }
}
