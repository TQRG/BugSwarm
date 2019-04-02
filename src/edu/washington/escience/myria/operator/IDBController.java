package edu.washington.escience.myria.operator;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import edu.washington.escience.myria.DbException;
import edu.washington.escience.myria.MyriaConstants;
import edu.washington.escience.myria.Schema;
import edu.washington.escience.myria.Type;
import edu.washington.escience.myria.operator.network.Consumer;
import edu.washington.escience.myria.parallel.ExchangePairID;
import edu.washington.escience.myria.parallel.LocalFragmentResourceManager;
import edu.washington.escience.myria.parallel.ipc.StreamOutputChannel;
import edu.washington.escience.myria.storage.TupleBatch;
import edu.washington.escience.myria.storage.TupleBatchBuffer;

/**
 * Together with the EOSController, the IDBController controls what to serve into an iteration and when to stop an
 * iteration.
 * */
public class IDBController extends Operator implements StreamingStateful {

  /** Required for Java serialization. */
  private static final long serialVersionUID = 1L;

  /** The logger for this class. */
  private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(IDBController.class);

  /**
   * Initial IDB input.
   * */
  private Operator initialIDBInput;

  /**
   * input from iteration.
   * */
  private Operator iterationInput;

  /**
   * the Consumer who is responsible for receiving EOS notification from the EOSController.
   * */
  private Consumer eosControllerInput;

  /**
   * The workerID where the EOSController is running.
   * */
  private final int controllerWorkerID;

  /**
   * The operator ID to which the EOI report should be sent.
   * */
  private final ExchangePairID controllerOpID;

  /**
   * The index of this IDBController. This is to differentiate the IDBController operators in the same worker. Note that
   * this number is the index, it must start from 0 and to (The number of IDBController operators in a worker -1)
   * */
  private final int selfIDBIdx;

  /**
   * Indicating if the initial input is ended.
   * */
  private transient boolean initialInputEnded;

  /**
   * Indicating if the number of tuples received from either the initialInput child or the iteration input child is 0
   * since last EOI.
   * */
  private transient boolean emptyDelta;
  /**
   * For IPC communication. Specifically, for doing EOI report.
   * */
  private transient LocalFragmentResourceManager resourceManager;
  /**
   * The IPC channel for EOI report.
   * */
  private transient StreamOutputChannel<TupleBatch> eoiReportChannel;

  /** The state. */
  private StreamingState state;

  /** the buffered tuples came from the previous iteration. */
  private ArrayList<TupleBatch> bufferedIterTBs;

  /** delta tuples of the previous iteration. */

  private LinkedList<TupleBatch> deltaTuples;

  /** if the buffer of previous iteration tuples has been cleaned. */
  private boolean bufferCleared = false;

  /** if this IDBController uses sync mode. */
  private final boolean sync;

  /**
   * The index of the initialIDBInput in children array.
   * */
  public static final int CHILDREN_IDX_INITIAL_IDB_INPUT = 0;

  /**
   * The index of the iterationInput in children array.
   * */
  public static final int CHILDREN_IDX_ITERATION_INPUT = 1;

  /**
   * The index of the eosControllerInput in children array.
   * */
  public static final int CHILDREN_IDX_EOS_CONTROLLER_INPUT = 2;

  /**
   * @param selfIDBIdx see the corresponding field comment.
   * @param controllerOpID see the corresponding field comment.
   * @param controllerWorkerID see the corresponding field comment.
   * @param initialIDBInput see the corresponding field comment.
   * @param iterationInput see the corresponding field comment.
   * @param eosControllerInput see the corresponding field comment.
   * @param state the internal state.
   * */
  public IDBController(final int selfIDBIdx, final ExchangePairID controllerOpID, final int controllerWorkerID,
      final Operator initialIDBInput, final Operator iterationInput, final Consumer eosControllerInput,
      final StreamingState state) {
    this(selfIDBIdx, controllerOpID, controllerWorkerID, initialIDBInput, iterationInput, eosControllerInput, state,
        false);
  }

  /**
   * @param selfIDBIdx see the corresponding field comment.
   * @param controllerOpID see the corresponding field comment.
   * @param controllerWorkerID see the corresponding field comment.
   * @param initialIDBInput see the corresponding field comment.
   * @param iterationInput see the corresponding field comment.
   * @param eosControllerInput see the corresponding field comment.
   * @param state the internal state.
   * @param sync if it's sync or async.
   * */
  public IDBController(final int selfIDBIdx, final ExchangePairID controllerOpID, final int controllerWorkerID,
      final Operator initialIDBInput, final Operator iterationInput, final Consumer eosControllerInput,
      final StreamingState state, final Boolean sync) {
    Preconditions.checkNotNull(selfIDBIdx);
    Preconditions.checkNotNull(controllerOpID);
    Preconditions.checkNotNull(controllerWorkerID);

    this.selfIDBIdx = selfIDBIdx;
    this.controllerOpID = controllerOpID;
    this.controllerWorkerID = controllerWorkerID;
    this.initialIDBInput = initialIDBInput;
    this.iterationInput = iterationInput;
    this.eosControllerInput = eosControllerInput;
    this.state = state;
    this.state.setAttachedOperator(this);
    this.sync = sync;
  }

  @Override
  public final TupleBatch fetchNextReady() throws DbException {
    if (sync) {
      return fetchNextReadySync();
    }
    TupleBatch tb;
    if (!initialInputEnded) {
      while ((tb = initialIDBInput.nextReady()) != null) {
        tb = state.update(tb);
        if (tb != null && tb.numTuples() > 0) {
          emptyDelta = false;
          return tb;
        }
      }
      return null;
    }

    while ((tb = iterationInput.nextReady()) != null) {
      tb = state.update(tb);
      if (tb != null && tb.numTuples() > 0) {
        emptyDelta = false;
        return tb;
      }
    }

    return null;
  }

  /**
   * Synchronous mode of IDBController fetchNextReady.
   * 
   * @return next ready output TupleBatch.
   * @throws DbException if any error occurs
   */
  public final TupleBatch fetchNextReadySync() throws DbException {
    // 1. keeps all the incoming tuples in a buffer without passing them through the streaming state.
    // 2. receives an EOI from iterationInput.
    // 3. generating delta tuples of the previous iteration by passing buffered tuples through the streaming state.
    // 4. feeding the delta tuples to the downstream operator as input.

    TupleBatch tb;
    if (!initialInputEnded) {
      while ((tb = initialIDBInput.nextReady()) != null) {
        tb = state.update(tb);
        if (tb != null && tb.numTuples() > 0) {
          emptyDelta = false;
          return tb;
        }
      }
      return null;
    }

    while ((tb = iterationInput.nextReady()) != null) {
      bufferedIterTBs.add(tb);
    }
    if (iterationInput.eoi() && !bufferCleared) {
      StreamingState tmpState = state.newInstanceFromMyself();
      tmpState.setAttachedOperator(this);
      tmpState.init(null);
      for (TupleBatch tb1 : bufferedIterTBs) {
        tmpState.update(tb1);
      }
      List<TupleBatch> tmp = tmpState.exportState();
      Preconditions.checkArgument(deltaTuples.size() == 0);
      for (TupleBatch tb1 : tmp) {
        tb = state.update(tb1);
        if (tb != null) {
          deltaTuples.add(tb);
        }
      }
      emptyDelta = (deltaTuples.size() == 0);
      bufferedIterTBs.clear();
      bufferCleared = true;
    }
    if (deltaTuples.size() > 0) {
      TupleBatch tmp = deltaTuples.pop();
      return tmp;
    }
    return null;
  }

  @Override
  public final void checkEOSAndEOI() {
    if (!initialInputEnded) {
      if (initialIDBInput.eos()) {
        setEOI(true);
        emptyDelta = true;
        initialInputEnded = true;
      }
    } else {
      try {
        if (eosControllerInput.hasNext()) {
          eosControllerInput.nextReady();
        }

        if (eosControllerInput.eos()) {
          setEOS();
          eoiReportChannel.release();
          // notify the EOSController to end.
        } else if (iterationInput.eoi()) {
          iterationInput.setEOI(false);
          setEOI(true);
          bufferCleared = false;
          final TupleBatchBuffer buffer = new TupleBatchBuffer(EOI_REPORT_SCHEMA);
          buffer.putInt(0, selfIDBIdx);
          buffer.putBoolean(1, emptyDelta);
          eoiReportChannel.write(buffer.popAny());
          emptyDelta = true;
        }
      } catch (DbException e) {
        if (LOGGER.isErrorEnabled()) {
          LOGGER.error("Unknown error. ", e);
        }
      }
    }
  }

  @Override
  public final Operator[] getChildren() {
    Operator[] result = new Operator[3];
    result[CHILDREN_IDX_INITIAL_IDB_INPUT] = initialIDBInput;
    result[CHILDREN_IDX_ITERATION_INPUT] = iterationInput;
    result[CHILDREN_IDX_EOS_CONTROLLER_INPUT] = eosControllerInput;
    return result;
  }

  @Override
  public final Schema generateSchema() {
    return initialIDBInput.getSchema();
  }

  /**
   * the schema of EOI report.
   * */
  public static final Schema EOI_REPORT_SCHEMA;

  static {
    final ImmutableList<Type> types = ImmutableList.of(Type.INT_TYPE, Type.BOOLEAN_TYPE);
    final ImmutableList<String> columnNames = ImmutableList.of("idbID", "isDeltaEmpty");
    final Schema schema = new Schema(types, columnNames);
    EOI_REPORT_SCHEMA = schema;
  }

  @Override
  public final void init(final ImmutableMap<String, Object> execEnvVars) throws DbException {
    initialInputEnded = false;
    emptyDelta = true;
    resourceManager =
        (LocalFragmentResourceManager) execEnvVars.get(MyriaConstants.EXEC_ENV_VAR_FRAGMENT_RESOURCE_MANAGER);
    eoiReportChannel = resourceManager.startAStream(controllerWorkerID, controllerOpID);
    state.init(execEnvVars);
    deltaTuples = new LinkedList<TupleBatch>();
    bufferedIterTBs = new ArrayList<TupleBatch>();
  }

  @Override
  public final void setChildren(final Operator[] children) {
    Preconditions.checkArgument(children.length == 3);
    Preconditions.checkNotNull(children[0]);
    Preconditions.checkNotNull(children[1]);
    Preconditions.checkNotNull(children[2]);
    initialIDBInput = children[CHILDREN_IDX_INITIAL_IDB_INPUT];
    iterationInput = children[CHILDREN_IDX_ITERATION_INPUT];
    eosControllerInput = (Consumer) children[CHILDREN_IDX_EOS_CONTROLLER_INPUT];
  }

  @Override
  protected final void cleanup() throws DbException {
    eoiReportChannel.release();
    eoiReportChannel = null;
    resourceManager = null;
    state.cleanup();
  }

  /**
   * @return the operator ID of the EOI receiving Consumer of the EOSController.
   * */
  public final ExchangePairID getControllerOperatorID() {
    return controllerOpID;
  }

  /**
   * @return the workerID where the EOSController is running.
   * */
  public final int getControllerWorkerID() {
    return controllerWorkerID;
  }

  @Override
  public void setStreamingState(final StreamingState state) {
    this.state = state;
  }

  @Override
  public StreamingState getStreamingState() {
    return state;
  }
}
