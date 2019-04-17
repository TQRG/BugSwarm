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
package com.github.rinde.rinsim.ui.renderers;

import static org.junit.Assert.assertEquals;

import javax.measure.Measure;
import javax.measure.unit.SI;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.github.rinde.rinsim.core.Simulator;
import com.github.rinde.rinsim.core.TickListener;
import com.github.rinde.rinsim.core.TimeLapse;
import com.github.rinde.rinsim.core.model.road.CollisionGraphRoadModel;
import com.github.rinde.rinsim.core.model.road.MovingRoadUser;
import com.github.rinde.rinsim.core.model.road.RoadModel;
import com.github.rinde.rinsim.geom.Graphs;
import com.github.rinde.rinsim.geom.LengthData;
import com.github.rinde.rinsim.geom.ListenableGraph;
import com.github.rinde.rinsim.geom.Point;
import com.github.rinde.rinsim.geom.TableGraph;
import com.github.rinde.rinsim.testutil.GuiTests;
import com.github.rinde.rinsim.ui.View;
import com.google.common.base.Optional;

/**
 * @author Rinde van Lon
 *
 */
@Category(GuiTests.class)
public class WarehouseRendererTest {

  @Test
  public void test2() {

    final Point a = new Point(1, 0);
    final Point b = new Point(3, 0);

    final Point c = new Point(0, 1);
    final Point d = new Point(0, 3);

    final Point z = new Point(0, 0);
    final Point e = new Point(10, 10);
    final Point f = new Point(0, 10);

    assertEquals(new Point(0, 0),
        PointUtil.intersectionPoint(a, b, c, d).get());

    assertEquals(z,
        PointUtil.intersectionPoint(z, e, f, z).get());

  }

  @Test
  public void test() {

    final Simulator sim = new Simulator(new MersenneTwister(123L),
        Measure.valueOf(1000L, SI.MILLI(SI.SECOND)));

    final ListenableGraph<LengthData> graph = new ListenableGraph<>(
        new TableGraph<LengthData>());

    // graph.addConnection(new Point(0, 0), new Point(0, 10));
    // graph.addConnection(new Point(0, 10), new Point(10, 10));
    graph.addConnection(new Point(0, 0), new Point(10, 10));
    // graph.addConnection(new Point(0, 0), new Point(10, 0));
    Graphs.addPath(graph, new Point(0, 0), new Point(0, 10),
        new Point(10, 10), new Point(10, 0), new Point(0, 0));
    Graphs.addBiPath(graph, new Point(10, 0), new Point(20, 0),
        new Point(20, 10), new Point(10, 10));

    Graphs.addBiPath(graph, new Point(10, 10), new Point(20, 15), new Point(30,
        10), new Point(30, 5), new Point(20, 10));

    Graphs.addPath(graph, new Point(30, 5), new Point(30, 3), new Point(28, 3),
        new Point(30, 1), new Point(20, 0));

    sim.register(CollisionGraphRoadModel.builder(graph).build());
    sim.configure();
    sim.register(new Agent(sim.getRandomGenerator()));
    sim.register(new Agent(sim.getRandomGenerator()));
    sim.register(new Agent(sim.getRandomGenerator()));

    View.create(sim)
        .with(WarehouseRenderer.builder()
            .setMargin(0)
            .setDrawOneWayStreetArrows(true))
        .with(AGVRenderer.builder())
        .stopSimulatorAtTime(150 * 1000L)
        .enableAutoClose()
        .enableAutoPlay()
        .setSpeedUp(2)
        .show();

  }

  static class Agent implements TickListener, MovingRoadUser {
    Optional<RoadModel> model;
    Point destination;
    RandomGenerator rng;
    boolean haveABreak;

    long timeOut = 0;

    Agent(RandomGenerator r) {
      rng = r;
      model = Optional.absent();
    }

    @Override
    public void initRoadUser(RoadModel m) {
      model = Optional.of(m);
      model.get().addObjectAt(this, model.get().getRandomPosition(rng));
      destination = model.get().getRandomPosition(rng);
    }

    @Override
    public double getSpeed() {
      return 1;
    }

    @Override
    public void tick(TimeLapse timeLapse) {
      if (rng.nextInt(100) < 1 || timeOut > 0) {
        if (timeOut == 0L) {
          timeOut = 100L;
        } else {
          timeOut--;
        }

      } else {

        if (model.get().getPosition(this).equals(destination)) {
          destination = model.get().getRandomPosition(rng);
        }
        model.get().moveTo(this, destination, timeLapse);
      }
    }

    @Override
    public void afterTick(TimeLapse timeLapse) {}

  }
}
