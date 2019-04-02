package edu.washington.escience.myria.coordinator.catalog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import edu.washington.escience.myria.MyriaConstants;
import edu.washington.escience.myria.MyriaConstants.ProfilingMode;
import edu.washington.escience.myria.api.encoding.QueryEncoding;
import edu.washington.escience.myria.api.encoding.QueryStatusEncoding;
import edu.washington.escience.myria.parallel.SocketInfo;
import edu.washington.escience.myria.util.FSUtils;

public class CatalogTest {
  /** The logger for this class. */
  private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(CatalogTest.class);

  @Rule
  public TestRule watcher = new TestWatcher() {
    @Override
    protected void starting(final Description description) {
      LOGGER.warn("*********************************************");
      LOGGER.warn(String.format("Starting test: %s()...", description.getMethodName()));
      LOGGER.warn("*********************************************");
    };
  };

  /**
   * TODO All this test does is create an in-memory Catalog and make sure its description matches. Flesh it out.
   * 
   * @throws CatalogException if there is an error creating the Catalog.
   */
  @Test
  public void testCatalogCreation() throws CatalogException {
    /* Constants needed for this test. */
    final String DESCRIPTION = "local-worker-test";
    final String SERVER = "localhost:8001";
    final String[] WORKERS = { "localhost:9001", "localhost:9002" };

    /* Turn off SQLite logging, it's annoying. */
    Logger.getLogger("com.almworks.sqlite4java").setLevel(Level.OFF);

    MasterCatalog catalog = null;

    /* Set and test the description */
    catalog = MasterCatalog.createInMemory();
    catalog.setConfigurationValue("description", DESCRIPTION);
    assertEquals(DESCRIPTION, catalog.getDescription());

    /* Set and test the server */
    catalog.addMaster(SERVER);
    final List<SocketInfo> servers = catalog.getMasters();
    assertEquals(1, servers.size());
    assertEquals(SERVER, servers.get(0).toString());

    /* Set and test the workers */
    for (int i = 0; i < WORKERS.length; ++i) {
      catalog.addWorker(i + 1, WORKERS[i]);
    }
    final Map<Integer, SocketInfo> workers = catalog.getWorkers();
    assertEquals(WORKERS.length, workers.size());
    /* Slightly annoying equality check here */
    final Collection<SocketInfo> values = workers.values();
    for (final String worker : WORKERS) {
      assertTrue(values.contains(SocketInfo.valueOf(worker)));
    }
  }

  /**
   * Test creating a catalog using CatalogMaker functionality.
   * 
   * @throws Exception if there is an error making the Catalog.
   */
  @Test
  public void testCatalogMaker() throws Exception {
    /* Turn off SQLite logging, it's annoying. */
    Logger.getLogger("com.almworks.sqlite4java").setLevel(Level.OFF);
    Path path = Files.createTempDirectory(MyriaConstants.SYSTEM_NAME + "_CatalogTest");
    final int numWorkers = 5;
    ImmutableMap.Builder<Integer, SocketInfo> workersBuilder = ImmutableMap.<Integer, SocketInfo> builder();

    for (int id = 1; id <= numWorkers; id++) {
      workersBuilder.put(id, new SocketInfo(9000 + id));
    }

    final String masterCatalogPath = path.toString() + File.separatorChar + "master.catalog";

    CatalogMaker.makeNNodesLocalParallelCatalog(path.toFile().getAbsolutePath(), ImmutableMap
        .<Integer, SocketInfo> builder().put(MyriaConstants.MASTER_ID, new SocketInfo(8001)).build(), workersBuilder
        .build(), Collections.<String, String> emptyMap(), Collections.<String, String> emptyMap());

    MasterCatalog c = MasterCatalog.open(masterCatalogPath);
    assertEquals(numWorkers, c.getWorkers().size());
    c.close();
    FSUtils.blockingDeleteDirectory(path.toString());
  }

  /**
   * Test the query search functionality in the catalog.
   * 
   * @throws CatalogException if there is an error creating the Catalog.
   */
  @Test
  public void testCatalogQuerySearch() throws CatalogException {
    /* Turn off SQLite logging, it's annoying. */
    Logger.getLogger("com.almworks.sqlite4java").setLevel(Level.OFF);

    MasterCatalog catalog = MasterCatalog.createInMemory();
    List<QueryStatusEncoding> queries;
    QueryEncoding query = new QueryEncoding();

    /* Q1 */
    query.rawQuery = "query 1 is about baseball";
    query.logicalRa = "";
    catalog.newQuery(query);

    /* Test fetching a single query in myriad ways. */
    assertEquals(1, catalog.getQueries(null, null, null, null).size());
    assertEquals(1, catalog.getQueries(null, 0L, null, null).size());
    assertEquals(1, catalog.getQueries(null, 1L, null, null).size());
    assertEquals(1, catalog.getQueries(null, 2L, null, null).size());
    assertEquals(1, catalog.getQueries(null, null, 0L, null).size());
    assertEquals(1, catalog.getQueries(null, null, 1L, null).size());
    assertEquals(0, catalog.getQueries(null, null, 2L, null).size());
    assertEquals(1, catalog.getQueries(null, 1L, 2L, null).size());
    assertEquals(0, catalog.getQueries(null, 0L, 2L, null).size());
    assertEquals(1, catalog.getQueries(0L, null, null, null).size());
    assertEquals(1, catalog.getQueries(1L, null, null, null).size());
    assertEquals(1, catalog.getQueries(10L, null, null, null).size());
    assertEquals(1, catalog.getQueries(10L, 1L, null, null).size());
    assertEquals(1, catalog.getQueries(10L, 1L, 2L, null).size());
    assertEquals(0, catalog.getQueries(10L, 0L, 2L, null).size());
    assertEquals(0, catalog.getQueries(10L, null, 2L, null).size());
    /* Test one query + simple search. */
    assertEquals(1, catalog.getQueries(null, null, null, "query").size());
    assertEquals(1, catalog.getQueries(null, null, null, "query 1").size());
    assertEquals(0, catalog.getQueries(null, null, null, "query 2").size());
    assertEquals(1, catalog.getQueries(null, null, null, "baseball").size());
    assertEquals(1, catalog.getQueries(null, null, null, "BaSEbALl").size());
    assertEquals(0, catalog.getQueries(null, null, null, "basketball").size());
    assertEquals(0, catalog.getQueries(null, null, null, "bas").size());

    /* Q2 */
    query.rawQuery = "query 2 is about basketball";
    query.logicalRa = "";
    catalog.newQuery(query);

    /* Test fetching two queries in myriad ways. */
    assertEquals(2, catalog.getQueries(null, null, null, null).size());
    assertEquals(2, catalog.getQueries(null, 0L, null, null).size());
    assertEquals(1, catalog.getQueries(null, 1L, null, null).size());
    assertEquals(2, catalog.getQueries(null, 2L, null, null).size());
    assertEquals(2, catalog.getQueries(null, null, 0L, null).size());
    assertEquals(2, catalog.getQueries(null, null, 1L, null).size());
    assertEquals(1, catalog.getQueries(null, null, 2L, null).size());
    assertEquals(0, catalog.getQueries(null, null, 3L, null).size());
    assertEquals(1, catalog.getQueries(null, 1L, 2L, null).size());
    assertEquals(1, catalog.getQueries(null, 0L, 2L, null).size());
    assertEquals(2, catalog.getQueries(0L, null, null, null).size());
    assertEquals(1, catalog.getQueries(1L, null, null, null).size());
    assertEquals(2, catalog.getQueries(10L, null, null, null).size());
    assertEquals(1, catalog.getQueries(10L, 1L, null, null).size());
    assertEquals(2, catalog.getQueries(10L, 2L, 3L, null).size());
    assertEquals(1, catalog.getQueries(10L, 0L, 2L, null).size());
    assertEquals(1, catalog.getQueries(1L, null, 1L, null).size());
    assertEquals(2, catalog.getQueries(10L, null, 1L, null).size());
    assertEquals(1, catalog.getQueries(1L, null, 2L, null).size());
    /* Test two queries + simple search. */
    assertEquals(2, catalog.getQueries(null, null, null, "quERY ").size());
    assertEquals(1, catalog.getQueries(null, null, null, "query 1").size());
    assertEquals(1, catalog.getQueries(null, null, null, "query 2").size());
    assertEquals(1, catalog.getQueries(null, null, null, "baseball").size());
    assertEquals(1, catalog.getQueries(null, null, null, "BaSEbALl").size());
    assertEquals(1, catalog.getQueries(null, null, null, "basketball").size());
    assertEquals(0, catalog.getQueries(null, null, null, "bas").size());
    assertEquals(0, catalog.getQueries(null, null, null, "ball").size());

    /* Test search + max/min/etc. */
    assertEquals(2, catalog.getQueries(0L, null, null, "query").size());
    assertEquals(1, catalog.getQueries(1L, null, null, "query").size());
    assertEquals(2, catalog.getQueries(2L, null, null, "query").size());
    assertEquals(1, catalog.getQueries(null, 3L, null, "query 1").size());
    assertEquals(0, catalog.getQueries(null, 1L, null, "query 2").size());
    assertEquals(0, catalog.getQueries(null, null, 2L, "baseball").size());
    assertEquals(1, catalog.getQueries(null, null, 1L, "baseball").size());
    assertEquals(1, catalog.getQueries(null, null, null, "BaSEbALl").size());
    assertEquals(1, catalog.getQueries(null, null, null, "basketball").size());
    assertEquals(0, catalog.getQueries(null, null, null, "bas").size());
    assertEquals(0, catalog.getQueries(null, null, null, "ball").size());

    /* Q2 */
    query.rawQuery = "query 3 is about football";
    query.logicalRa = "";
    catalog.newQuery(query);

    /* Test that the results are properly ordered. */
    queries = catalog.getQueries(null, null, null, null);
    assertEquals(3, queries.size());
    assertEquals(Long.valueOf(3L), queries.get(0).queryId);
    assertEquals(Long.valueOf(2L), queries.get(1).queryId);
    assertEquals(Long.valueOf(1L), queries.get(2).queryId);

    queries = catalog.getQueries(1L, null, null, "query");
    assertEquals(1, queries.size());
    assertEquals(Long.valueOf(3L), queries.get(0).queryId);

    queries = catalog.getQueries(1L, 1L, null, "query");
    assertEquals(1, queries.size());
    assertEquals(Long.valueOf(1L), queries.get(0).queryId);

    queries = catalog.getQueries(null, null, 1L, null);
    assertEquals(3, queries.size());
    assertEquals(Long.valueOf(3L), queries.get(0).queryId);
    assertEquals(Long.valueOf(2L), queries.get(1).queryId);
    assertEquals(Long.valueOf(1L), queries.get(2).queryId);

    // Note this interesting case -- when we query for min=2 and limit 1, we should only get query *2*. This is
    // the smallest query that meets the condition, rather than the largest query.
    queries = catalog.getQueries(1L, null, 2L, "query");
    assertEquals(1, queries.size());
    assertEquals(Long.valueOf(2L), queries.get(0).queryId);
    // in this one, since max is present, the min should be ignored.
    queries = catalog.getQueries(1L, 1L, 2L, "query");
    assertEquals(1, queries.size());
    assertEquals(Long.valueOf(1L), queries.get(0).queryId);
    // in this one, since max is present, the min should be ignored.
    queries = catalog.getQueries(1L, 10L, 2L, "query");
    assertEquals(1, queries.size());
    assertEquals(Long.valueOf(3L), queries.get(0).queryId);

    queries = catalog.getQueries(2L, null, 2L, "query");
    assertEquals(2, queries.size());
    assertEquals(Long.valueOf(3L), queries.get(0).queryId);
    assertEquals(Long.valueOf(2L), queries.get(1).queryId);
  }

  /**
   * Test the query search functionality in the catalog.
   * 
   * @throws CatalogException if there is an error creating the Catalog.
   */
  @Test
  public void testCatalogExtraFieldsList() throws CatalogException {
    /* Turn off SQLite logging, it's annoying. */
    Logger.getLogger("com.almworks.sqlite4java").setLevel(Level.OFF);

    MasterCatalog catalog = MasterCatalog.createInMemory();
    QueryEncoding query = new QueryEncoding();
    QueryStatusEncoding qs;

    /* Q1 */
    query.rawQuery = "query 1";
    query.logicalRa = "";
    query.language = "myrial";
    catalog.newQuery(query);

    qs = catalog.getQuery(1L);
    assertEquals(qs.rawQuery, query.rawQuery);
    assertEquals(qs.logicalRa, query.logicalRa);
    assertEquals(qs.profilingMode, ImmutableList.<ProfilingMode> of());
    assertEquals(qs.language, query.language);
    /* Also test the getQueries() method of getting query info. */
    qs = catalog.getQueries(1L, 1L, null, null).get(0);
    assertEquals(qs.rawQuery, query.rawQuery);
    assertEquals(null, qs.logicalRa);
    assertEquals(qs.profilingMode, ImmutableList.<ProfilingMode> of());
    assertEquals(qs.language, query.language);

    /* Q2 */
    query.rawQuery = "query 2";
    query.logicalRa = "";
    query.profilingMode = ImmutableList.of(ProfilingMode.QUERY);
    query.language = "datalog";
    catalog.newQuery(query);

    qs = catalog.getQuery(2L);
    assertEquals(qs.rawQuery, query.rawQuery);
    assertEquals(qs.logicalRa, query.logicalRa);
    assertEquals(ImmutableSet.copyOf(qs.profilingMode), ImmutableSet.copyOf(query.profilingMode));
    assertEquals(qs.language, query.language);
    /* Also test the getQueries() method of getting query info. */
    qs = catalog.getQueries(1L, 2L, null, null).get(0);
    assertEquals(qs.rawQuery, query.rawQuery);
    assertEquals(null, qs.logicalRa);
    assertEquals(ImmutableSet.copyOf(qs.profilingMode), ImmutableSet.copyOf(query.profilingMode));
    assertEquals(qs.language, query.language);

    /* Q3 */
    query.rawQuery = "query 3";
    query.logicalRa = "";
    query.language = null;
    query.profilingMode = ImmutableList.of(ProfilingMode.QUERY, ProfilingMode.RESOURCE);
    catalog.newQuery(query);

    qs = catalog.getQuery(3L);
    assertEquals(qs.rawQuery, query.rawQuery);
    assertEquals(qs.logicalRa, query.logicalRa);
    assertEquals(ImmutableSet.copyOf(qs.profilingMode), ImmutableSet.copyOf(query.profilingMode));
    assertEquals(qs.language, query.language);
    /* Also test the getQueries() method of getting query info. */
    qs = catalog.getQueries(1L, 3L, null, null).get(0);
    assertEquals(qs.rawQuery, query.rawQuery);
    assertEquals(null, qs.logicalRa);
    assertEquals(ImmutableSet.copyOf(qs.profilingMode), ImmutableSet.copyOf(query.profilingMode));
    assertEquals(qs.language, query.language);

    /* Q4 */
    query.rawQuery = "query 4";
    query.logicalRa = "";
    query.profilingMode = ImmutableList.of(ProfilingMode.RESOURCE, ProfilingMode.QUERY);
    query.language = "sql";
    catalog.newQuery(query);

    qs = catalog.getQuery(4L);
    assertEquals(qs.rawQuery, query.rawQuery);
    assertEquals(qs.logicalRa, query.logicalRa);
    assertEquals(ImmutableSet.copyOf(qs.profilingMode), ImmutableSet.copyOf(query.profilingMode));
    assertEquals(qs.language, query.language);
    /* Also test the getQueries() method of getting query info. */
    qs = catalog.getQueries(1L, 4L, null, null).get(0);
    assertEquals(qs.rawQuery, query.rawQuery);
    assertEquals(null, qs.logicalRa);
    assertEquals(ImmutableSet.copyOf(qs.profilingMode), ImmutableSet.copyOf(query.profilingMode));
    assertEquals(qs.language, query.language);
  }
}
