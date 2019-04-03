package com.hubspot.singularity.auth;

import static com.google.common.collect.ImmutableSet.copyOf;
import static com.hubspot.singularity.WebExceptions.badRequest;
import static com.hubspot.singularity.WebExceptions.checkForbidden;
import static com.hubspot.singularity.WebExceptions.checkUnauthorized;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.hubspot.mesos.JavaUtils;
import com.hubspot.singularity.InvalidSingularityTaskIdException;
import com.hubspot.singularity.SingularityRequest;
import com.hubspot.singularity.SingularityRequestWithState;
import com.hubspot.singularity.SingularityTaskId;
import com.hubspot.singularity.SingularityUser;
import com.hubspot.singularity.config.SingularityConfiguration;
import com.hubspot.singularity.data.RequestManager;

@Singleton
public class SingularityAuthorizationHelper {
  private final RequestManager requestManager;
  private final ImmutableSet<String> adminGroups;
  private final ImmutableSet<String> requiredGroups;
  private final ImmutableSet<String> jitaGroups;
  private final boolean authEnabled;

  @Inject
  public SingularityAuthorizationHelper(RequestManager requestManager, SingularityConfiguration configuration) {
    this.requestManager = requestManager;
    this.adminGroups = copyOf(configuration.getAuthConfiguration().getAdminGroups());
    this.requiredGroups = copyOf(configuration.getAuthConfiguration().getRequiredGroups());
    this.jitaGroups = copyOf(configuration.getAuthConfiguration().getJitaGroups());
    this.authEnabled = configuration.getAuthConfiguration().isEnabled();
  }

  public static boolean groupsIntersect(Set<String> a, Set<String> b) {
    return !Sets.intersection(a, b).isEmpty();
  }

  public boolean hasAdminAuthorization(Optional<SingularityUser> user) {
    // disabled auth == no rules!
    if (!authEnabled) {
      return true;
    }

    // not authenticated, or no groups, or no admin groups == can't possibly be admin
    if (!user.isPresent() || user.get().getGroups().isEmpty() || adminGroups.isEmpty()) {
      return false;
    }

    return groupsIntersect(user.get().getGroups(), adminGroups);
  }

  public void checkAdminAuthorization(Optional<SingularityUser> user) {
    if (authEnabled) {
      if (user.isPresent() && !adminGroups.isEmpty()) {
        checkForbidden(groupsIntersect(user.get().getGroups(), adminGroups), "User must be part of an admin group");
      }
    }
  }

  public void checkRequiredAuthorization(Optional<SingularityUser> user) {
    if (authEnabled) {
      if (user.isPresent() && !requiredGroups.isEmpty()) {
        checkForbidden(groupsIntersect(user.get().getGroups(), requiredGroups), "User must be part of a required group");
      }
    }
  }

  public void checkForAuthorizationByTaskId(String taskId, Optional<SingularityUser> user) {
    if (authEnabled) {
      try {
        final SingularityTaskId taskIdObj = SingularityTaskId.valueOf(taskId);

        final Optional<SingularityRequestWithState> maybeRequest = requestManager.getRequest(taskIdObj.getRequestId());

        if (maybeRequest.isPresent()) {
          checkForAuthorization(maybeRequest.get().getRequest(), Optional.<SingularityRequest>absent(), user);
        }
      } catch (InvalidSingularityTaskIdException e) {
        badRequest(e.getMessage());
      }
    }
  }

  public void checkForAuthorizationByRequestId(String requestId, Optional<SingularityUser> user) {
    if (authEnabled) {
      final Optional<SingularityRequestWithState> maybeRequest = requestManager.getRequest(requestId);

      if (maybeRequest.isPresent()) {
        checkForAuthorization(maybeRequest.get().getRequest(), Optional.<SingularityRequest>absent(), user);
      }
    }
  }

  public boolean isAuthorizedForRequest(SingularityRequest request, Optional<SingularityUser> user) {
    if (authEnabled) {
      // not authenticated == no authorization
      if (!user.isPresent()) {
        return false;
      }

      final Set<String> userGroups = user.get().getGroups();

      // check required groups
      if (!requiredGroups.isEmpty() && Sets.intersection(user.get().getGroups(), requiredGroups).isEmpty()) {
        return false;
      }

      // check admin groups
      if (groupsIntersect(userGroups, adminGroups)) {
        return true;
      }

      // check JITA groups
      if (groupsIntersect(userGroups, jitaGroups)) {
        return true;
      }

      // check request groups
      if (request.getGroup().isPresent()) {
        return user.get().getGroups().contains(request.getGroup().get());
      }
    }

    return true;
  }

  public void checkForAuthorization(SingularityRequest request, Optional<SingularityRequest> existingRequest, Optional<SingularityUser> user) {
    if (authEnabled) {
      checkUnauthorized(user.isPresent(), "user must be present");

      final Set<String> userGroups = user.get().getGroups();

      // check for required group membership...
      if (!requiredGroups.isEmpty()) {
        checkForbidden(groupsIntersect(userGroups, requiredGroups), "User %s must be part of one or more required groups: %s", user.get().getId(), JavaUtils.COMMA_JOINER.join(requiredGroups));
      }

      // if user isn't part of an admin group...
      if (!groupsIntersect(userGroups, adminGroups)) {
        // if changing groups, check for group membership of old group
        if (existingRequest.isPresent() && existingRequest.get().getGroup().isPresent() && !request.getGroup().equals(existingRequest.get().getGroup())) {
          checkForbidden(userGroups.contains(existingRequest.get().getGroup().get()), "User %s must be part of old group %s", user.get().getId(), existingRequest.get().getGroup().get());
        }

        // check for group membership of current / new group
        if (request.getGroup().isPresent()) {
          checkForbidden(userGroups.contains(request.getGroup().get()), "User %s must be part of group %s", user.get().getId(), request.getGroup().get());
        }
      }
    }
  }

  public <T> Iterable<T> filterByAuthorizedRequests(final Optional<SingularityUser> user, List<T> objects, final Function<T, String> requestIdFunction) {
    if (hasAdminAuthorization(user)) {
      return objects;
    }

    final Set<String> requestIds = copyOf(Iterables.transform(objects, new Function<T, String>() {
      @Override
      public String apply(@Nonnull T input) {
        return requestIdFunction.apply(input);
      }
    }));

    final Map<String, SingularityRequestWithState> requestMap = Maps.uniqueIndex(requestManager.getRequests(requestIds), new Function<SingularityRequestWithState, String>() {
      @Override
      public String apply(@Nonnull SingularityRequestWithState input) {
        return input.getRequest().getId();
      }
    });

    return Iterables.filter(objects, new Predicate<T>() {
      @Override
      public boolean apply(@Nonnull T input) {
        final String requestId = requestIdFunction.apply(input);
        return requestMap.containsKey(requestId) && isAuthorizedForRequest(requestMap.get(requestId).getRequest(), user);
      }
    });
  }

  public Iterable<String> filterAuthorizedRequestIds(final Optional<SingularityUser> user, List<String> requestIds) {
    if (hasAdminAuthorization(user)) {
      return requestIds;
    }

    final Map<String, SingularityRequestWithState> requestMap = Maps.uniqueIndex(requestManager.getRequests(requestIds), new Function<SingularityRequestWithState, String>() {
      @Override
      public String apply(@Nonnull SingularityRequestWithState input) {
        return input.getRequest().getId();
      }
    });

    return Iterables.filter(requestIds, new Predicate<String>() {
      @Override
      public boolean apply(@Nonnull String input) {
        return requestMap.containsKey(input) && isAuthorizedForRequest(requestMap.get(input).getRequest(), user);
      }
    });
  }
}
