/**
 *
 */
package edu.washington.escience.myria.systemtest;

import static org.junit.Assert.assertEquals;

<<<<<<< HEAD
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
=======
import java.util.HashMap;
>>>>>>> origin/myria-perfenforce
import java.util.Map;

import org.junit.Test;

import edu.washington.escience.myria.RelationKey;
import edu.washington.escience.myria.Schema;
import edu.washington.escience.myria.Type;
import edu.washington.escience.myria.io.AmazonS3Source;
import edu.washington.escience.myria.io.UriSource;
<<<<<<< HEAD
=======
import edu.washington.escience.myria.operator.CSVFileScanFragment;
>>>>>>> origin/myria-perfenforce
import edu.washington.escience.myria.operator.DbInsert;
import edu.washington.escience.myria.operator.DbQueryScan;
import edu.washington.escience.myria.operator.Difference;
import edu.washington.escience.myria.operator.EOSSource;
import edu.washington.escience.myria.operator.FileScan;
import edu.washington.escience.myria.operator.RootOperator;
import edu.washington.escience.myria.operator.SinkRoot;
<<<<<<< HEAD
import edu.washington.escience.myria.operator.network.GenericShuffleConsumer;
import edu.washington.escience.myria.operator.network.GenericShuffleProducer;
import edu.washington.escience.myria.operator.network.partition.RoundRobinPartitionFunction;
import edu.washington.escience.myria.operator.network.partition.WholeTupleHashPartitionFunction;
=======
import edu.washington.escience.myria.operator.network.CollectConsumer;
import edu.washington.escience.myria.operator.network.CollectProducer;
import edu.washington.escience.myria.operator.network.partition.RoundRobinPartitionFunction;
>>>>>>> origin/myria-perfenforce
import edu.washington.escience.myria.parallel.ExchangePairID;
import edu.washington.escience.myria.util.JsonAPIUtils;

/**
<<<<<<< HEAD
 *
 */
public class ParallelIngestS3Test extends SystemTestBase {

  Schema dateSchema =
      Schema.ofFields(
          "d_datekey",
          Type.LONG_TYPE,
          "d_date",
          Type.STRING_TYPE,
          "d_dayofweek",
          Type.STRING_TYPE,
          "d_month",
          Type.STRING_TYPE,
          "d_year",
          Type.LONG_TYPE,
          "d_yearmonthnum",
          Type.LONG_TYPE,
          "d_yearmonth",
          Type.STRING_TYPE,
          "d_daynuminweek",
          Type.LONG_TYPE,
          "d_daynuminmonth",
          Type.LONG_TYPE,
          "d_daynuminyear",
          Type.LONG_TYPE,
          "d_monthnuminyear",
          Type.LONG_TYPE,
          "d_weeknuminyear",
          Type.LONG_TYPE,
          "d_sellingseason",
          Type.STRING_TYPE,
          "d_lastdayinweekfl",
          Type.STRING_TYPE,
          "d_lastdayinmonthfl",
          Type.STRING_TYPE,
          "d_holidayfl",
          Type.STRING_TYPE,
          "d_weekdayfl",
          Type.STRING_TYPE);

  String dateTableAddress = "s3://myria-test/dateOUT.csv";

  @Test
  public void parallelIngestTest() throws Exception {
    RelationKey dateRelationKey = RelationKey.of("public", "adhoc", "testParallel");
    AmazonS3Source dateSource = new AmazonS3Source(dateTableAddress, null, null);
    server.parallelIngestDataset(dateRelationKey, dateSchema, '|', null, null, 0, dateSource, null);
    assertEquals(2556, server.getDatasetStatus(dateRelationKey).getNumTuples());
  }

  public void diffHelperMethod(
      final RelationKey relationKeyParallelIngest,
      final RelationKey relationKeyCoordinatorIngest,
      final Schema schema)
      throws Exception {

    /* WholeTupleHashPartition the tuples from the coordinator ingest */
    DbQueryScan scanCoordinatorIngest = new DbQueryScan(relationKeyCoordinatorIngest, schema);
    ExchangePairID receiveCoordinatorIngest = ExchangePairID.newID();
    GenericShuffleProducer sendToWorkerCoordinatorIngest =
        new GenericShuffleProducer(
            scanCoordinatorIngest,
            receiveCoordinatorIngest,
            new int[] {workerIDs[0], workerIDs[1]},
            new WholeTupleHashPartitionFunction(workerIDs.length));
    GenericShuffleConsumer workerConsumerCoordinatorIngest =
        new GenericShuffleConsumer(
            schema, receiveCoordinatorIngest, new int[] {workerIDs[0], workerIDs[1]});
    DbInsert workerCoordinatorIngest =
        new DbInsert(workerConsumerCoordinatorIngest, relationKeyCoordinatorIngest, true);
    Map<Integer, RootOperator[]> workerPlansHashCoordinatorIngest =
        new HashMap<Integer, RootOperator[]>();
    for (int workerID : workerIDs) {
      workerPlansHashCoordinatorIngest.put(
          workerID, new RootOperator[] {sendToWorkerCoordinatorIngest, workerCoordinatorIngest});
    }
    server.submitQueryPlan(new SinkRoot(new EOSSource()), workerPlansHashCoordinatorIngest).get();

    /* WholeTupleHashPartition the tuples from the parallel ingest */
    DbQueryScan scanParallelIngest = new DbQueryScan(relationKeyParallelIngest, schema);
    ExchangePairID receiveParallelIngest = ExchangePairID.newID();
    GenericShuffleProducer sendToWorkerParallelIngest =
        new GenericShuffleProducer(
            scanParallelIngest,
            receiveParallelIngest,
            new int[] {workerIDs[0], workerIDs[1]},
            new WholeTupleHashPartitionFunction(workerIDs.length));
    GenericShuffleConsumer workerConsumerParallelIngest =
        new GenericShuffleConsumer(
            schema, receiveParallelIngest, new int[] {workerIDs[0], workerIDs[1]});
    DbInsert workerIngest =
        new DbInsert(workerConsumerParallelIngest, relationKeyParallelIngest, true);
    Map<Integer, RootOperator[]> workerPlansHashParallelIngest =
        new HashMap<Integer, RootOperator[]>();
    for (int workerID : workerIDs) {
      workerPlansHashParallelIngest.put(
          workerID, new RootOperator[] {sendToWorkerParallelIngest, workerIngest});
    }
    server.submitQueryPlan(new SinkRoot(new EOSSource()), workerPlansHashParallelIngest).get();

    /* Run the diff at each worker */
    RelationKey diffRelationKey = new RelationKey("public", "adhoc", "diffResult");
    Difference diff = new Difference(scanParallelIngest, scanCoordinatorIngest);
    DbInsert diffResult = new DbInsert(diff, diffRelationKey, true);
    final Map<Integer, RootOperator[]> workerPlansDiff = new HashMap<Integer, RootOperator[]>();
    for (int workerID : workerIDs) {
      workerPlansDiff.put(workerID, new RootOperator[] {diffResult});
    }
    server.submitQueryPlan(new SinkRoot(new EOSSource()), workerPlansDiff).get();

    String data =
        JsonAPIUtils.download(
            "localhost",
            masterDaemonPort,
            diffRelationKey.getUserName(),
            diffRelationKey.getProgramName(),
            diffRelationKey.getRelationName(),
            "json");
=======
 * 
 */
public class ParallelIngestS3Test extends SystemTestBase {

  Schema dateSchema = Schema.ofFields("d_datekey", Type.LONG_TYPE, "d_date", Type.STRING_TYPE, "d_dayofweek",
      Type.STRING_TYPE, "d_month", Type.STRING_TYPE, "d_year", Type.LONG_TYPE, "d_yearmonthnum", Type.LONG_TYPE,
      "d_yearmonth", Type.STRING_TYPE, "d_daynuminweek", Type.LONG_TYPE, "d_daynuminmonth", Type.LONG_TYPE,
      "d_daynuminyear", Type.LONG_TYPE, "d_monthnuminyear", Type.LONG_TYPE, "d_weeknuminyear", Type.LONG_TYPE,
      "d_sellingseason", Type.STRING_TYPE, "d_lastdayinweekfl", Type.STRING_TYPE, "d_lastdayinmonthfl",
      Type.STRING_TYPE, "d_holidayfl", Type.STRING_TYPE, "d_weekdayfl", Type.STRING_TYPE);

  Schema customerSchema = Schema.ofFields("c_custkey", Type.LONG_TYPE, "c_name", Type.STRING_TYPE, "c_address",
      Type.STRING_TYPE, "c_city", Type.STRING_TYPE, "c_nation_prefix", Type.STRING_TYPE, "c_nation", Type.STRING_TYPE,
      "c_region", Type.STRING_TYPE, "c_phone", Type.STRING_TYPE, "c_mktsegment", Type.STRING_TYPE);

  String dateTableAddress = "s3://myria-test/dateOUT.csv";
  String customerTableAddress = "s3://myria-test/customerOUT.txt";

  @Test
  public void parallelIngestTest() throws Exception {
    RelationKey relationKey = RelationKey.of("public", "adhoc", "testParallel");

    Map<Integer, RootOperator[]> workerPlansParallelIngest = new HashMap<Integer, RootOperator[]>();
    int workerCounterID = 1;
    for (int workerID : workerIDs) {
      AmazonS3Source s3Source = new AmazonS3Source(dateTableAddress);
      CSVFileScanFragment scanFragment =
          new CSVFileScanFragment(s3Source, dateSchema, workerCounterID, workerIDs.length, '|', null, null, 0);
      workerPlansParallelIngest.put(workerID, new RootOperator[] { new DbInsert(scanFragment, relationKey, true) });
      workerCounterID++;
    }
    server.submitQueryPlan(new SinkRoot(new EOSSource()), workerPlansParallelIngest).get();
    assertEquals(2556, server.getDatasetStatus(relationKey).getNumTuples());
  }

  @Test
  public void diffParallelIngestTest() throws Exception {

    /* Ingest in parallel */
    RelationKey relationKeyParallelIngest = RelationKey.of("public", "adhoc", "ingestParallel");
    Map<Integer, RootOperator[]> workerPlansParallelIngest = new HashMap<Integer, RootOperator[]>();
    int workerCounterID = 1;
    for (int workerID : workerIDs) {
      AmazonS3Source s3Source = new AmazonS3Source(customerTableAddress);
      CSVFileScanFragment scanFragment =
          new CSVFileScanFragment(s3Source, customerSchema, workerCounterID, workerIDs.length, ',', null, null, 0);
      workerPlansParallelIngest.put(workerID, new RootOperator[] { new DbInsert(scanFragment,
          relationKeyParallelIngest, true) });
      workerCounterID++;
    }
    server.submitQueryPlan(new SinkRoot(new EOSSource()), workerPlansParallelIngest).get();
    assertEquals(300000, server.getDatasetStatus(relationKeyParallelIngest).getNumTuples());

    /* Ingest the through the coordinator */
    RelationKey relationKeyCoordinatorIngest = RelationKey.of("public", "adhoc", "ingestCoordinator");
    server.ingestDataset(relationKeyCoordinatorIngest, server.getAliveWorkers(), null, new FileScan(new UriSource(
        customerTableAddress), customerSchema, ',', null, null, 0), new RoundRobinPartitionFunction(workerIDs.length));
    assertEquals(300000, server.getDatasetStatus(relationKeyCoordinatorIngest).getNumTuples());

    /* do the diff at the first worker */
    final Map<Integer, RootOperator[]> workerPlansDiff = new HashMap<Integer, RootOperator[]>();

    DbQueryScan scanParallelIngest = new DbQueryScan(relationKeyParallelIngest, customerSchema);
    ExchangePairID receiveParallelIngest = ExchangePairID.newID();
    CollectProducer sendToWorkerParallelIngest =
        new CollectProducer(scanParallelIngest, receiveParallelIngest, workerIDs[0]);

    DbQueryScan scanCoordinatorIngest = new DbQueryScan(relationKeyCoordinatorIngest, customerSchema);
    ExchangePairID receiveCoordinatorIngest = ExchangePairID.newID();
    CollectProducer sendToWorkerCoordinatorIngest =
        new CollectProducer(scanCoordinatorIngest, receiveCoordinatorIngest, workerIDs[0]);

    CollectConsumer workerConsumerParallelIngest =
        new CollectConsumer(customerSchema, receiveParallelIngest, workerIDs);
    CollectConsumer workerConsumerCoordinatorIngest =
        new CollectConsumer(customerSchema, receiveCoordinatorIngest, workerIDs);

    RelationKey diffRelationKey = new RelationKey("public", "adhoc", "diffResult");
    Difference diff = new Difference(workerConsumerParallelIngest, workerConsumerCoordinatorIngest);
    DbInsert workerIngest = new DbInsert(diff, diffRelationKey, true);

    workerPlansDiff.put(workerIDs[0], new RootOperator[] {
        sendToWorkerParallelIngest, sendToWorkerCoordinatorIngest, workerIngest });
    workerPlansDiff.put(workerIDs[1], new RootOperator[] { sendToWorkerParallelIngest, sendToWorkerCoordinatorIngest });

    server.submitQueryPlan(new SinkRoot(new EOSSource()), workerPlansDiff).get();

    String data =
        JsonAPIUtils.download("localhost", masterDaemonPort, diffRelationKey.getUserName(), diffRelationKey
            .getProgramName(), diffRelationKey.getRelationName(), "json");
>>>>>>> origin/myria-perfenforce

    assertEquals("[]", data);
  }

<<<<<<< HEAD
  public void parallelIngestSimpleDiff() throws Exception {
    AmazonS3Source dateSource = new AmazonS3Source("s3://myria-test/dateOUT.csv", null, null);
    /* Ingest in parallel */
    RelationKey relationKeyParallelIngest = RelationKey.of("public", "adhoc", "ingestParallel");
    server.parallelIngestDataset(
        relationKeyParallelIngest,
        dateSchema,
        '|',
        null,
        null,
        0,
        dateSource,
        server.getAliveWorkers());
    assertEquals(2556, server.getDatasetStatus(relationKeyParallelIngest).getNumTuples());

    /* Ingest the through the coordinator and WholeTupleHashPartition the result */
    RelationKey relationKeyCoordinatorIngest =
        RelationKey.of("public", "adhoc", "ingestCoordinator");
    server.ingestDataset(
        relationKeyCoordinatorIngest,
        server.getAliveWorkers(),
        null,
        new FileScan(new UriSource(dateTableAddress), dateSchema, '|', null, null, 0),
        new RoundRobinPartitionFunction(workerIDs.length));
    assertEquals(2556, server.getDatasetStatus(relationKeyCoordinatorIngest).getNumTuples());

    diffHelperMethod(relationKeyParallelIngest, relationKeyCoordinatorIngest, dateSchema);
  }

=======
>>>>>>> origin/myria-perfenforce
  @Test
  public void oneTupleTest() throws Exception {
    String oneTupleAddress = "s3://myria-test/sample-parallel-one-tuple.txt";
    Schema oneTupleSchema =
<<<<<<< HEAD
        Schema.ofFields(
            "w",
            Type.INT_TYPE,
            "x",
            Type.INT_TYPE,
            "y",
            Type.INT_TYPE,
            "z",
            Type.INT_TYPE,
            "a",
            Type.INT_TYPE);

    RelationKey relationKey = RelationKey.of("public", "adhoc", "testParallelOneTuple");
    server.parallelIngestDataset(
        relationKey,
        oneTupleSchema,
        ',',
        null,
        null,
        0,
        new AmazonS3Source(oneTupleAddress, null, null),
        null);
    assertEquals(1, server.getDatasetStatus(relationKey).getNumTuples());
  }

  @Test
  public void oneTupleTestAllWorkers() throws Exception {
    String oneTupleAddress = "s3://myria-test/sample-parallel-one-tuple.txt";
    Schema oneTupleSchema =
        Schema.ofFields(
            "w",
            Type.INT_TYPE,
            "x",
            Type.INT_TYPE,
            "y",
            Type.INT_TYPE,
            "z",
            Type.INT_TYPE,
            "a",
            Type.INT_TYPE);

    RelationKey relationKey = RelationKey.of("public", "adhoc", "testParallelOneTuple");
    server.parallelIngestDataset(
        relationKey,
        oneTupleSchema,
        ',',
        null,
        null,
        0,
        new AmazonS3Source(oneTupleAddress, null, null),
        server.getAliveWorkers());
    assertEquals(1, server.getDatasetStatus(relationKey).getNumTuples());
  }

  @Test
  /**
   * With two workers, this covers the case where Worker #2 has a long string on the first row -- which should be discarded by Worker#2
   **/
  public void truncatedBeginningFragmentTest() throws Exception {
    String beginningTrailAddress = "s3://myria-test/TestLongBeginningTrail.txt";
    Schema beginningTrailSchema =
        Schema.ofFields(
            "w",
            Type.STRING_TYPE,
            "x",
            Type.INT_TYPE,
            "y",
            Type.INT_TYPE,
            "z",
            Type.INT_TYPE,
            "a",
            Type.INT_TYPE);

    /* Ingest in parallel */
    RelationKey relationKeyParallelIngest = RelationKey.of("public", "adhoc", "beginningParallel");
    server.parallelIngestDataset(
        relationKeyParallelIngest,
        beginningTrailSchema,
        ',',
        null,
        null,
        0,
        new AmazonS3Source(beginningTrailAddress, null, null),
        server.getAliveWorkers());
    assertEquals(4, server.getDatasetStatus(relationKeyParallelIngest).getNumTuples());

    /* Ingest the through the coordinator */
    RelationKey relationKeyCoordinatorIngest =
        RelationKey.of("public", "adhoc", "beginningCoordinator");
    server.ingestDataset(
        relationKeyCoordinatorIngest,
        new HashSet<Integer>(Arrays.asList(workerIDs[0], workerIDs[1])),
        null,
        new FileScan(
            new UriSource(beginningTrailAddress), beginningTrailSchema, ',', null, null, 0),
        new RoundRobinPartitionFunction(workerIDs.length));
    assertEquals(4, server.getDatasetStatus(relationKeyCoordinatorIngest).getNumTuples());
    diffHelperMethod(relationKeyParallelIngest, relationKeyCoordinatorIngest, beginningTrailSchema);
  }

  @Test
  /**
   * With two workers, this covers the case where Worker #1 has a long string on the last row
   **/
  public void truncatedEndFragmentTestLF() throws Exception {
    String endTrailAddress = "s3://myria-test/TestLongEndTrail_nlines.txt";
    Schema endTrailSchema =
        Schema.ofFields(
            "x",
            Type.INT_TYPE,
            "y",
            Type.INT_TYPE,
            "z",
            Type.INT_TYPE,
            "a",
            Type.INT_TYPE,
            "w",
            Type.STRING_TYPE);

    /* Ingest in parallel */
    RelationKey relationKeyParallelIngest = RelationKey.of("public", "adhoc", "endParallel_nlines");
    server.parallelIngestDataset(
        relationKeyParallelIngest,
        endTrailSchema,
        ',',
        null,
        null,
        0,
        new AmazonS3Source(endTrailAddress, null, null),
        server.getAliveWorkers());
    assertEquals(4, server.getDatasetStatus(relationKeyParallelIngest).getNumTuples());

    /* Ingest the through the coordinator */
    RelationKey relationKeyCoordinatorIngest =
        RelationKey.of("public", "adhoc", "endCoordinator_nlines");
    server.ingestDataset(
        relationKeyCoordinatorIngest,
        server.getAliveWorkers(),
        null,
        new FileScan(new UriSource(endTrailAddress), endTrailSchema, ',', null, null, 0),
        new RoundRobinPartitionFunction(workerIDs.length));
    assertEquals(4, server.getDatasetStatus(relationKeyCoordinatorIngest).getNumTuples());
    diffHelperMethod(relationKeyParallelIngest, relationKeyCoordinatorIngest, endTrailSchema);
  }

  @Test
  /**
   * Test for truncated end for carriage return
   **/
  public void truncatedEndFragmentCR() throws Exception {
    String fileAddress = "s3://myria-test/TestLongEndTrail_rlines.txt";
    Schema fileSchema =
        Schema.ofFields(
            "x",
            Type.INT_TYPE,
            "y",
            Type.INT_TYPE,
            "z",
            Type.INT_TYPE,
            "a",
            Type.INT_TYPE,
            "w",
            Type.STRING_TYPE);

    /* Ingest in parallel */
    RelationKey relationKeyParallelIngest = RelationKey.of("public", "adhoc", "endParallel_rlines");
    server.parallelIngestDataset(
        relationKeyParallelIngest,
        fileSchema,
        ',',
        null,
        null,
        0,
        new AmazonS3Source(fileAddress, null, null),
        server.getAliveWorkers());
    assertEquals(4, server.getDatasetStatus(relationKeyParallelIngest).getNumTuples());

    /* Ingest the through the coordinator */
    RelationKey relationKeyCoordinatorIngest =
        RelationKey.of("public", "adhoc", "endCoordinator_rlines");
    server.ingestDataset(
        relationKeyCoordinatorIngest,
        server.getAliveWorkers(),
        null,
        new FileScan(new UriSource(fileAddress), fileSchema, ',', null, null, 0),
        new RoundRobinPartitionFunction(workerIDs.length));
    assertEquals(4, server.getDatasetStatus(relationKeyCoordinatorIngest).getNumTuples());
    diffHelperMethod(relationKeyParallelIngest, relationKeyCoordinatorIngest, fileSchema);
  }

  @Test
  /**
   * Test for truncated end for carriage return and line feed
   **/
  public void truncatedEndFragmentCRLF() throws Exception {
    String fileAddress = "s3://myria-test/TestLongEndTrail_rnlines.txt";
    Schema fileSchema =
        Schema.ofFields(
            "x",
            Type.INT_TYPE,
            "y",
            Type.INT_TYPE,
            "z",
            Type.INT_TYPE,
            "a",
            Type.INT_TYPE,
            "w",
            Type.STRING_TYPE);

    /* Ingest in parallel */
    RelationKey relationKeyParallelIngest =
        RelationKey.of("public", "adhoc", "endParallel_rnlines");
    server.parallelIngestDataset(
        relationKeyParallelIngest,
        fileSchema,
        ',',
        null,
        null,
        0,
        new AmazonS3Source(fileAddress, null, null),
        server.getAliveWorkers());
    assertEquals(4, server.getDatasetStatus(relationKeyParallelIngest).getNumTuples());

    /* Ingest the through the coordinator */
    RelationKey relationKeyCoordinatorIngest =
        RelationKey.of("public", "adhoc", "endCoordinator_rnlines");
    server.ingestDataset(
        relationKeyCoordinatorIngest,
        server.getAliveWorkers(),
        null,
        new FileScan(new UriSource(fileAddress), fileSchema, ',', null, null, 0),
        new RoundRobinPartitionFunction(workerIDs.length));
    assertEquals(4, server.getDatasetStatus(relationKeyCoordinatorIngest).getNumTuples());
    diffHelperMethod(relationKeyParallelIngest, relationKeyCoordinatorIngest, fileSchema);
  }

  @Test
  /**
   * Testing a perfect split case
   **/
  public void perfectRowSplitLF() throws Exception {
    String perfectSplitAddress = "s3://myria-test/PerfectSplit_nlines.txt";
    Schema perfectSplitSchema = Schema.ofFields("x", Type.INT_TYPE, "w", Type.STRING_TYPE);

    /* Ingest in parallel */
    RelationKey relationKeyParallelIngest =
        RelationKey.of("public", "adhoc", "perfectParallel_nlines");
    server.parallelIngestDataset(
        relationKeyParallelIngest,
        perfectSplitSchema,
        ',',
        null,
        null,
        0,
        new AmazonS3Source(perfectSplitAddress, null, null),
        server.getAliveWorkers());
    assertEquals(4, server.getDatasetStatus(relationKeyParallelIngest).getNumTuples());

    /* Ingest the through the coordinator */
    RelationKey relationKeyCoordinatorIngest =
        RelationKey.of("public", "adhoc", "perfectCoordinator_nlines");
    server.ingestDataset(
        relationKeyCoordinatorIngest,
        server.getAliveWorkers(),
        null,
        new FileScan(new UriSource(perfectSplitAddress), perfectSplitSchema, ',', null, null, 0),
        new RoundRobinPartitionFunction(workerIDs.length));
    assertEquals(4, server.getDatasetStatus(relationKeyCoordinatorIngest).getNumTuples());
    diffHelperMethod(relationKeyParallelIngest, relationKeyCoordinatorIngest, perfectSplitSchema);
  }

  @Test
  public void perfectRowSplitCR() throws Exception {
    String fileAddress = "s3://myria-test/PerfectSplit_rlines.txt";
    Schema fileSchema = Schema.ofFields("x", Type.INT_TYPE, "w", Type.STRING_TYPE);

    /* Ingest in parallel */
    RelationKey relationKeyParallelIngest =
        RelationKey.of("public", "adhoc", "perfectParallel_rlines");
    server.parallelIngestDataset(
        relationKeyParallelIngest,
        fileSchema,
        ',',
        null,
        null,
        0,
        new AmazonS3Source(fileAddress, null, null),
        server.getAliveWorkers());
    assertEquals(4, server.getDatasetStatus(relationKeyParallelIngest).getNumTuples());

    /* Ingest the through the coordinator */
    RelationKey relationKeyCoordinatorIngest =
        RelationKey.of("public", "adhoc", "perfectCoordinator_rlines");
    server.ingestDataset(
        relationKeyCoordinatorIngest,
        server.getAliveWorkers(),
        null,
        new FileScan(new UriSource(fileAddress), fileSchema, ',', null, null, 0),
        new RoundRobinPartitionFunction(workerIDs.length));
    assertEquals(4, server.getDatasetStatus(relationKeyCoordinatorIngest).getNumTuples());
    diffHelperMethod(relationKeyParallelIngest, relationKeyCoordinatorIngest, fileSchema);
  }

  @Test
  public void perfectRowSplitCRLF() throws Exception {
    String fileAddress = "s3://myria-test/PerfectSplit_rnlines.txt";
    Schema fileSchema = Schema.ofFields("x", Type.INT_TYPE, "w", Type.STRING_TYPE);

    /* Ingest in parallel */
    RelationKey relationKeyParallelIngest =
        RelationKey.of("public", "adhoc", "perfectParallel_rnlines");
    server.parallelIngestDataset(
        relationKeyParallelIngest,
        fileSchema,
        ',',
        null,
        null,
        0,
        new AmazonS3Source(fileAddress, null, null),
        server.getAliveWorkers());
    assertEquals(4, server.getDatasetStatus(relationKeyParallelIngest).getNumTuples());

    /* Ingest the through the coordinator */
    RelationKey relationKeyCoordinatorIngest =
        RelationKey.of("public", "adhoc", "perfectCoordinator_rnlines");
    server.ingestDataset(
        relationKeyCoordinatorIngest,
        server.getAliveWorkers(),
        null,
        new FileScan(new UriSource(fileAddress), fileSchema, ',', null, null, 0),
        new RoundRobinPartitionFunction(workerIDs.length));
    assertEquals(4, server.getDatasetStatus(relationKeyCoordinatorIngest).getNumTuples());
    diffHelperMethod(relationKeyParallelIngest, relationKeyCoordinatorIngest, fileSchema);
  }

  @Test
  public void multiByteCharacterSequenceTest() throws Exception {
    String fileAddress = "s3://myria-test/multibyteCharacterSequence.txt";
    Schema fileSchema = Schema.ofFields("w", Type.STRING_TYPE);

    /* Ingest in parallel */
    RelationKey relationKeyParallelIngest =
        RelationKey.of("public", "adhoc", "multibyteCharacterSequenceParallel");
    server.parallelIngestDataset(
        relationKeyParallelIngest,
        fileSchema,
        ',',
        null,
        null,
        0,
        new AmazonS3Source(fileAddress, null, null),
        server.getAliveWorkers());
    assertEquals(3, server.getDatasetStatus(relationKeyParallelIngest).getNumTuples());

    /* Ingest the through the coordinator */
    RelationKey relationKeyCoordinatorIngest =
        RelationKey.of("public", "adhoc", "multibyteCharacterSequenceCoordinator");
    server.ingestDataset(
        relationKeyCoordinatorIngest,
        server.getAliveWorkers(),
        null,
        new FileScan(new UriSource(fileAddress), fileSchema, ',', null, null, 0),
        new RoundRobinPartitionFunction(workerIDs.length));
    assertEquals(3, server.getDatasetStatus(relationKeyCoordinatorIngest).getNumTuples());
    diffHelperMethod(relationKeyParallelIngest, relationKeyCoordinatorIngest, fileSchema);
  }

  @Test
  public void CRLFMiddleSplitTest() throws Exception {
    String fileAddress = "s3://myria-test/PerfectSplit_rnlines_middle.txt";
    Schema fileSchema = Schema.ofFields("x", Type.INT_TYPE, "w", Type.STRING_TYPE);

    /* Ingest in parallel */
    RelationKey relationKeyParallelIngest =
        RelationKey.of("public", "adhoc", "perfectParallel_rnlines_middle");
    server.parallelIngestDataset(
        relationKeyParallelIngest,
        fileSchema,
        ',',
        null,
        null,
        0,
        new AmazonS3Source(fileAddress, null, null),
        server.getAliveWorkers());
    assertEquals(2, server.getDatasetStatus(relationKeyParallelIngest).getNumTuples());

    /* Ingest the through the coordinator */
    RelationKey relationKeyCoordinatorIngest =
        RelationKey.of("public", "adhoc", "perfectCoordinator_rnlines_middle");
    server.ingestDataset(
        relationKeyCoordinatorIngest,
        server.getAliveWorkers(),
        null,
        new FileScan(new UriSource(fileAddress), fileSchema, ',', null, null, 0),
        new RoundRobinPartitionFunction(workerIDs.length));
    assertEquals(2, server.getDatasetStatus(relationKeyCoordinatorIngest).getNumTuples());
    diffHelperMethod(relationKeyParallelIngest, relationKeyCoordinatorIngest, fileSchema);
  }
=======
        Schema.ofFields("w", Type.INT_TYPE, "x", Type.INT_TYPE, "y", Type.INT_TYPE, "z", Type.INT_TYPE, "a",
            Type.INT_TYPE);

    RelationKey relationKey = RelationKey.of("public", "adhoc", "testParallelOneTuple");

    Map<Integer, RootOperator[]> workerPlansParallelIngest = new HashMap<Integer, RootOperator[]>();
    int workerCounterID = 1;
    for (int workerID : workerIDs) {
      AmazonS3Source s3Source = new AmazonS3Source(oneTupleAddress);
      CSVFileScanFragment scanFragment =
          new CSVFileScanFragment(s3Source, oneTupleSchema, workerCounterID, workerIDs.length, ',', null, null, 0);
      workerPlansParallelIngest.put(workerID, new RootOperator[] { new DbInsert(scanFragment, relationKey, true) });
      workerCounterID++;
    }
    server.submitQueryPlan(new SinkRoot(new EOSSource()), workerPlansParallelIngest).get();
    assertEquals(1, server.getDatasetStatus(relationKey).getNumTuples());
  }

>>>>>>> origin/myria-perfenforce
}
