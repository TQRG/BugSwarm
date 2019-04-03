/*
 * Copyright (c) 2013-2014 Parallel Universe Software Co.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package co.paralleluniverse.galaxy.example.simplegenevent;

import co.paralleluniverse.actors.Actor;
import co.paralleluniverse.actors.LocalActor;
import co.paralleluniverse.actors.behaviors.EventHandler;
import co.paralleluniverse.actors.behaviors.EventSource;
import co.paralleluniverse.actors.behaviors.EventSourceActor;
import co.paralleluniverse.actors.behaviors.Initializer;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.strands.dataflow.Val;
import java.util.concurrent.ExecutionException;

/**
 *
 * @author eitan
 */
public class Server {
    private static final int nodeId = 2;

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.setProperty("galaxy.nodeId", Integer.toString(nodeId));
        System.setProperty("galaxy.port", Integer.toString(7050 + nodeId));
        System.setProperty("galaxy.slave_port", Integer.toString(8050 + nodeId));

        EventSource<String> ge = new EventSourceActor<String>(new Initializer() {
            @Override
            public void init() throws SuspendExecution {
                Actor.currentActor().register("myEventServer");
                final EventSource<String> ge = LocalActor.self();
                try {
                    ge.addHandler(new EventHandler<String>() {
                        @Override
                        public void handleEvent(String event) {
                            System.out.println("Handling event: " + event);
                            ge.shutdown();
                        }
                    });
                } catch (InterruptedException ex) {
                    System.out.println("error " + ex);
                }
            }

            @Override
            public void terminate(Throwable cause) throws SuspendExecution {
                System.out.println("server terminated");
            }
        }).spawn();

        LocalActor.join(ge);
        System.exit(0);
    }
}