/*
 * Copyright 2017-present Open Networking Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.atomix.protocols.raft.proxy.impl;

import io.atomix.cluster.NodeId;
import io.atomix.protocols.raft.RaftError;
import io.atomix.protocols.raft.protocol.CloseSessionRequest;
import io.atomix.protocols.raft.protocol.CloseSessionResponse;
import io.atomix.protocols.raft.protocol.CommandRequest;
import io.atomix.protocols.raft.protocol.CommandResponse;
import io.atomix.protocols.raft.protocol.KeepAliveRequest;
import io.atomix.protocols.raft.protocol.KeepAliveResponse;
import io.atomix.protocols.raft.protocol.MetadataRequest;
import io.atomix.protocols.raft.protocol.MetadataResponse;
import io.atomix.protocols.raft.protocol.OpenSessionRequest;
import io.atomix.protocols.raft.protocol.OpenSessionResponse;
import io.atomix.protocols.raft.protocol.QueryRequest;
import io.atomix.protocols.raft.protocol.QueryResponse;
import io.atomix.protocols.raft.protocol.RaftClientProtocol;
import io.atomix.protocols.raft.protocol.RaftRequest;
import io.atomix.protocols.raft.protocol.RaftResponse;
import io.atomix.utils.concurrent.ThreadContext;
import io.atomix.utils.logging.ContextualLoggerFactory;
import io.atomix.utils.logging.LoggerContext;
import org.slf4j.Logger;

import java.net.ConnectException;
import java.nio.channels.ClosedChannelException;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeoutException;
import java.util.function.BiFunction;
import java.util.function.Predicate;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Client connection that recursively connects to servers in the cluster and attempts to submit requests.
 */
public class RaftProxyConnection {
  private static final Predicate<RaftResponse> COMPLETE_PREDICATE = response ->
      response.status() == RaftResponse.Status.OK
          || response.error().type() == RaftError.Type.COMMAND_FAILURE
          || response.error().type() == RaftError.Type.QUERY_FAILURE
          || response.error().type() == RaftError.Type.APPLICATION_ERROR
          || response.error().type() == RaftError.Type.UNKNOWN_CLIENT
          || response.error().type() == RaftError.Type.UNKNOWN_SESSION
          || response.error().type() == RaftError.Type.UNKNOWN_SERVICE
          || response.error().type() == RaftError.Type.PROTOCOL_ERROR;

  private final Logger log;
  private final RaftClientProtocol protocol;
  private final MemberSelector selector;
  private final ThreadContext context;
  private NodeId currentNode;

  public RaftProxyConnection(RaftClientProtocol protocol, MemberSelector selector, ThreadContext context, LoggerContext loggerContext) {
    this.protocol = checkNotNull(protocol, "protocol cannot be null");
    this.selector = checkNotNull(selector, "selector cannot be null");
    this.context = checkNotNull(context, "context cannot be null");
    this.log = ContextualLoggerFactory.getLogger(getClass(), loggerContext);
  }

  /**
   * Resets the member selector.
   */
  public void reset() {
    selector.reset();
  }

  /**
   * Resets the member selector.
   *
   * @param leader the selector leader
   * @param servers the selector servers
   */
  public void reset(NodeId leader, Collection<NodeId> servers) {
    selector.reset(leader, servers);
  }

  /**
   * Returns the current selector leader.
   *
   * @return The current selector leader.
   */
  public NodeId leader() {
    return selector.leader();
  }

  /**
   * Returns the current set of members.
   *
   * @return The current set of members.
   */
  public Collection<NodeId> members() {
    return selector.members();
  }

  /**
   * Sends an open session request to the given node.
   *
   * @param request the request to send
   * @return a future to be completed with the response
   */
  public CompletableFuture<OpenSessionResponse> openSession(OpenSessionRequest request) {
    CompletableFuture<OpenSessionResponse> future = new CompletableFuture<>();
    if (context.isCurrentContext()) {
      sendRequest(request, protocol::openSession, next(), future);
    } else {
      context.execute(() -> sendRequest(request, protocol::openSession, next(), future));
    }
    return future;
  }

  /**
   * Sends a close session request to the given node.
   *
   * @param request the request to send
   * @return a future to be completed with the response
   */
  public CompletableFuture<CloseSessionResponse> closeSession(CloseSessionRequest request) {
    CompletableFuture<CloseSessionResponse> future = new CompletableFuture<>();
    if (context.isCurrentContext()) {
      sendRequest(request, protocol::closeSession, next(), future);
    } else {
      context.execute(() -> sendRequest(request, protocol::closeSession, next(), future));
    }
    return future;
  }

  /**
   * Sends a keep alive request to the given node.
   *
   * @param request the request to send
   * @return a future to be completed with the response
   */
  public CompletableFuture<KeepAliveResponse> keepAlive(KeepAliveRequest request) {
    CompletableFuture<KeepAliveResponse> future = new CompletableFuture<>();
    if (context.isCurrentContext()) {
      sendRequest(request, protocol::keepAlive, next(), future);
    } else {
      context.execute(() -> sendRequest(request, protocol::keepAlive, next(), future));
    }
    return future;
  }

  /**
   * Sends a query request to the given node.
   *
   * @param request the request to send
   * @return a future to be completed with the response
   */
  public CompletableFuture<QueryResponse> query(QueryRequest request) {
    CompletableFuture<QueryResponse> future = new CompletableFuture<>();
    if (context.isCurrentContext()) {
      sendRequest(request, protocol::query, next(), future);
    } else {
      context.execute(() -> sendRequest(request, protocol::query, next(), future));
    }
    return future;
  }

  /**
   * Sends a command request to the given node.
   *
   * @param request the request to send
   * @return a future to be completed with the response
   */
  public CompletableFuture<CommandResponse> command(CommandRequest request) {
    CompletableFuture<CommandResponse> future = new CompletableFuture<>();
    if (context.isCurrentContext()) {
      sendRequest(request, protocol::command, next(), future);
    } else {
      context.execute(() -> sendRequest(request, protocol::command, next(), future));
    }
    return future;
  }

  /**
   * Sends a metadata request to the given node.
   *
   * @param request the request to send
   * @return a future to be completed with the response
   */
  public CompletableFuture<MetadataResponse> metadata(MetadataRequest request) {
    CompletableFuture<MetadataResponse> future = new CompletableFuture<>();
    if (context.isCurrentContext()) {
      sendRequest(request, protocol::metadata, next(), future);
    } else {
      context.execute(() -> sendRequest(request, protocol::metadata, next(), future));
    }
    return future;
  }

  /**
   * Sends the given request attempt to the cluster.
   */
  protected <T extends RaftRequest, U extends RaftResponse> void sendRequest(T request, BiFunction<NodeId, T, CompletableFuture<U>> sender, NodeId member, CompletableFuture<U> future) {
    if (member != null) {
      log.trace("Sending {} to {}", request, member);
      sender.apply(member, request).whenCompleteAsync((r, e) -> {
        if (e != null || r != null) {
          handleResponse(request, sender, member, r, e, future);
        } else {
          future.complete(null);
        }
      }, context);
    } else {
      future.completeExceptionally(new ConnectException("Failed to connect to the cluster"));
    }
  }

  /**
   * Resends a request due to a request failure, resetting the connection if necessary.
   */
  @SuppressWarnings("unchecked")
  protected <T extends RaftRequest> void retryRequest(Throwable cause, T request, BiFunction sender, NodeId member, CompletableFuture future) {
    // If the connection has not changed, reset it and connect to the next server.
    if (this.currentNode == member) {
      log.trace("Resetting connection. Reason: {}", cause.getMessage());
      this.currentNode = null;
    }

    // Attempt to send the request again.
    sendRequest(request, sender, next(), future);
  }

  /**
   * Handles a response from the cluster.
   */
  @SuppressWarnings("unchecked")
  protected <T extends RaftRequest> void handleResponse(T request, BiFunction sender, NodeId member, RaftResponse response, Throwable error, CompletableFuture future) {
    if (error == null) {
      log.trace("Received {} from {}", response, member);
      if (COMPLETE_PREDICATE.test(response)) {
        future.complete(response);
        selector.reset();
      } else {
        retryRequest(response.error().createException(), request, sender, member, future);
      }
    } else {
      if (error instanceof CompletionException) {
        error = error.getCause();
      }
      log.debug("{} failed! Reason: {}", request, error);
      if (error instanceof ConnectException || error instanceof TimeoutException || error instanceof ClosedChannelException) {
        retryRequest(error, request, sender, member, future);
      } else {
        future.completeExceptionally(error);
      }
    }
  }

  /**
   * Connects to the cluster.
   */
  protected NodeId next() {
    // If a connection was already established then use that connection.
    if (currentNode != null) {
      return currentNode;
    }

    if (!selector.hasNext()) {
      log.debug("Failed to connect to the cluster");
      selector.reset();
      return null;
    } else {
      this.currentNode = selector.next();
      return currentNode;
    }
  }
}
