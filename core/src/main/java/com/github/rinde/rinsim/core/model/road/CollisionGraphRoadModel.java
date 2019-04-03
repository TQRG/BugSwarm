/*
 * Copyright (C) 2011-2014 Rinde van Lon, iMinds DistriNet, KU Leuven
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.rinde.rinsim.core.model.road;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Verify.verify;

import java.util.Collection;
import java.util.Collections;
import java.util.Queue;
import java.util.Set;

import javax.measure.quantity.Length;
import javax.measure.quantity.Velocity;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;

import com.github.rinde.rinsim.core.TimeLapse;
import com.github.rinde.rinsim.event.Event;
import com.github.rinde.rinsim.event.Listener;
import com.github.rinde.rinsim.geom.Connection;
import com.github.rinde.rinsim.geom.ListenableGraph;
import com.github.rinde.rinsim.geom.ListenableGraph.GraphEvent;
import com.github.rinde.rinsim.geom.Point;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.primitives.Doubles;

/**
 * Graph road model that avoids collisions between {@link RoadUser}s. When a
 * dead lock situation arises a {@link IllegalStateException} is thrown.
 * Instances can be obtained via a dedicated builder, see
 * {@link #builder(ListenableGraph)}.
 * <p>
 * The graph can be modified at runtime, for information about modifying the
 * graph see {@link DynamicGraphRoadModel}.
 * @author Rinde van Lon
 */
public class CollisionGraphRoadModel extends DynamicGraphRoadModel {
  private final double minConnLength;
  private final double vehicleLength;
  private final double minDistance;
  private final BiMap<Point, RoadUser> occupiedNodes;

  CollisionGraphRoadModel(Builder builder, double pMinConnLength) {
    super(builder.graph, builder.distanceUnit, builder.speedUnit);
    vehicleLength = unitConversion.toInDist(builder.vehicleLength);
    minDistance = unitConversion.toInDist(builder.minDistance);
    minConnLength = unitConversion.toInDist(pMinConnLength);
    occupiedNodes = HashBiMap.create();
    builder.graph.getEventAPI().addListener(
        new ModificationChecker(minConnLength),
        ListenableGraph.EventTypes.ADD_CONNECTION,
        ListenableGraph.EventTypes.CHANGE_CONNECTION_DATA);
  }

  @Override
  protected MoveProgress doFollowPath(MovingRoadUser object, Queue<Point> path,
      TimeLapse time) {

    if (occupiedNodes.containsValue(object)) {
      occupiedNodes.inverse().remove(object);
    }
    final MoveProgress mp = super.doFollowPath(object, path, time);

    // detects if the new location of the object occupies a node
    final Loc loc = objLocs.get(object);
    if (loc.isOnConnection()) {
      if (loc.relativePos < vehicleLength) {
        occupiedNodes.put(loc.conn.get().from(), object);
      } else if (loc.relativePos > loc.connLength - vehicleLength) {
        occupiedNodes.put(loc.conn.get().to(), object);
      }
    } else {
      occupiedNodes.put(loc, object);
    }

    return mp;
  }

  @Override
  protected double computeTravelableDistance(Loc from, Point to, double speed,
      TimeLapse time) {
    double closestDist = Double.POSITIVE_INFINITY;
    if (!from.equals(to)) {
      final Connection<?> conn = getConnection(from, to);

      // check if the node is occupied
      if (occupiedNodes.containsKey(conn.to())) {
        closestDist = (from.isOnConnection() ? from.connLength
            - from.relativePos : conn.getLength()) - vehicleLength
            - minDistance;
      }

      // check if there is an obstacle on the connection
      if (connMap.containsKey(conn)) {
        // if yes, how far is it from 'from'
        final Collection<RoadUser> potentialObstacles = connMap.get(conn);

        for (final RoadUser ru : potentialObstacles) {
          final Loc loc = objLocs.get(ru);
          if (loc.isOnConnection() && loc.relativePos > from.relativePos) {
            final double dist = loc.relativePos - from.relativePos
                - vehicleLength - minDistance;
            if (dist < closestDist) {
              closestDist = dist;
            }
          }
        }
      }
    }
    return Math.min(closestDist,
        super.computeTravelableDistance(from, to, speed, time));
  }

  @Override
  protected void checkMoveValidity(Loc objLoc, Point nextHop) {
    super.checkMoveValidity(objLoc, nextHop);
    // check if there is a vehicle driving in the opposite direction
    if (!objLoc.equals(nextHop)) {
      final Connection<?> conn = getConnection(objLoc, nextHop);
      checkArgument(
          !(graph.hasConnection(conn.to(), conn.from())
          && connMap.containsKey(graph.getConnection(conn.to(), conn.from()))),
          "Deadlock detected: there is a vehicle driving in the opposite direction on the same connection.");
    }
  }

  @Override
  public void addObjectAt(RoadUser newObj, Point pos) {
    checkArgument(!occupiedNodes.containsKey(pos),
        "An object can not be added on an already occupied position %s.", pos);
    super.addObjectAt(newObj, pos);
    occupiedNodes.put(pos, newObj);
  }

  @Override
  @Deprecated
  public void addObjectAtSamePosition(RoadUser newObj, RoadUser existingObj) {
    throw new UnsupportedOperationException(
        "Vehicles can not be added at the same position.");
  }

  /**
   * Checks whether the specified node is occupied.
   * @param node The node to check for occupancy.
   * @return <code>true</code> if the specified node is occupied,
   *         <code>false</code> otherwise.
   */
  public boolean isOccupied(Point node) {
    return occupiedNodes.containsKey(node);
  }

  /**
   * @return A read-only <b>indeterministic</b> ordered live view of all
   *         currently occupied nodes in the graph.
   */
  public Set<Point> getOccupiedNodes() {
    return Collections.unmodifiableSet(occupiedNodes.keySet());
  }

  /**
   * @return The length of all vehicles. The length is expressed in the unit as
   *         specified by {@link #getDistanceUnit()}.
   */
  public double getVehicleLength() {
    return vehicleLength;
  }

  /**
   * @return The minimum distance vehicles need to be apart from each other. The
   *         length is expressed in the unit as specified by
   *         {@link #getDistanceUnit()}.
   */
  public double getMinDistance() {
    return minDistance;
  }

  /**
   * @return The minimum length all connections need to have in the graph. The
   *         length is expressed in the unit as specified by
   *         {@link #getDistanceUnit()}.
   */
  public double getMinConnLength() {
    return minConnLength;
  }

  /**
   * Create a {@link Builder} for constructing {@link CollisionGraphRoadModel}
   * instances. Note that all connections in the specified graph must have
   * length <code>2 * vehicleLength</code>, where vehicle length can be
   * specified in {@link Builder#setVehicleLength(double)}.
   * @param graph A {@link ListenableGraph}.
   * @return A new {@link Builder} instance.
   */
  public static Builder builder(ListenableGraph<?> graph) {
    return new Builder(graph);
  }

  static void checkConnectionLength(double minConnLength, Connection<?> conn) {
    checkArgument(
        Point.distance(conn.from(), conn.to()) >= minConnLength,
        "Invalid graph: the minimum connection length is %s, connection %s->%s is too short.",
        minConnLength, conn.from(), conn.to());
    checkArgument(
        conn.getLength() >= minConnLength,
        "Invalid graph: the minimum connection length is %s, connection %s->%s defines length data that is too short: %s.",
        minConnLength, conn.from(), conn.to(), conn.getLength());
  }

  /**
   * A builder for constructing {@link CollisionGraphRoadModel} instances. Use
   * {@link CollisionGraphRoadModel#builder(ListenableGraph)} for obtaining
   * builder instances.
   * @author Rinde van Lon
   */
  public static final class Builder {
    /**
     * The default distance unit: {@link SI#METER}.
     */
    public static final Unit<Length> DEFAULT_DISTANCE_UNIT = SI.METER;

    /**
     * The default speed unit: {@link NonSI#KILOMETERS_PER_HOUR}.
     */
    public static final Unit<Velocity> DEFAULT_SPEED_UNIT = NonSI.KILOMETERS_PER_HOUR;

    /**
     * The default vehicle length: <code>1</code>.
     */
    public static final double DEFAULT_VEHICLE_LENGTH = 1;

    /**
     * The default minimum distance: <code>.25</code>.
     */
    public static final double DEFAULT_MIN_DISTANCE = .25;

    final ListenableGraph<?> graph;
    Unit<Length> distanceUnit;
    Unit<Velocity> speedUnit;
    double vehicleLength;
    double minDistance;

    Builder(ListenableGraph<?> g) {
      graph = g;
      distanceUnit = DEFAULT_DISTANCE_UNIT;
      speedUnit = DEFAULT_SPEED_UNIT;
      vehicleLength = DEFAULT_VEHICLE_LENGTH;
      minDistance = DEFAULT_MIN_DISTANCE;
    }

    /**
     * Sets the distance unit used to interpret all coordinates and distances,
     * including those of the supplied {@link ListenableGraph}. The default
     * value is {@link #DEFAULT_DISTANCE_UNIT}.
     * @param unit The unit to set.
     * @return This, as per the builder pattern.
     */
    public Builder setDistanceUnit(Unit<Length> unit) {
      distanceUnit = unit;
      return this;
    }

    /**
     * Sets the speed unit used to interpret the speeds of all vehicles. The
     * default value is {@link #DEFAULT_SPEED_UNIT}.
     * @param unit The unit to set.
     * @return This, as per the builder pattern.
     */
    public Builder setSpeedUnit(Unit<Velocity> unit) {
      speedUnit = unit;
      return this;
    }

    /**
     * Sets the length of each vehicle added to the
     * {@link CollisionGraphRoadModel} that will be constructed by this builder.
     * The vehicle length must be a strictly positive number. The default value
     * is {@link #DEFAULT_VEHICLE_LENGTH}.
     * @param length A length expressed in the unit set by
     *          {@link #setDistanceUnit(Unit)}.
     * @return This, as per the builder pattern.
     */
    public Builder setVehicleLength(double length) {
      checkArgument(length > 0d,
          "Only positive vehicle lengths are allowed, found %s.", length);
      checkArgument(Doubles.isFinite(length),
          "%s is not a valid vehicle length.", length);
      vehicleLength = length;
      return this;
    }

    /**
     * Sets the minimum required distance between two vehicles. The minimum
     * distance must be a positive number &le; to 2 * vehicle length. The
     * default value is {@link #DEFAULT_MIN_DISTANCE}.
     * @param dist A distance expressed in the unit set by
     *          {@link #setDistanceUnit(Unit)}.
     * @return This, as per the builder pattern.
     */
    public Builder setMinDistance(double dist) {
      checkArgument(dist >= 0d);
      minDistance = dist;
      return this;
    }

    /**
     * @return A new {@link CollisionGraphRoadModel} instance.
     */
    public CollisionGraphRoadModel build() {
      final double minConnectionLength = 2 * vehicleLength;
      checkArgument(
          minDistance <= minConnectionLength,
          "Min distance must be smaller than 2 * vehicle length (%s), but is %s.",
          vehicleLength, minDistance);
      for (final Connection<?> conn : graph.getConnections()) {
        checkConnectionLength(minConnectionLength, conn);
      }
      return new CollisionGraphRoadModel(this, minConnectionLength);
    }
  }

  static class ModificationChecker implements Listener {
    private final double minConnLength;

    ModificationChecker(double minLength) {
      minConnLength = minLength;
    }

    @Override
    public void handleEvent(Event e) {
      verify(e instanceof GraphEvent);
      final GraphEvent event = (GraphEvent) e;
      checkConnectionLength(minConnLength, event.getConnection());
    }
  }
}
