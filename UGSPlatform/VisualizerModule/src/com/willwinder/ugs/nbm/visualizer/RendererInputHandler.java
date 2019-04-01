/**
 * Process all the listeners and call methods in the renderer.
 */
/*
    Copywrite 2016 Will Winder

    This file is part of Universal Gcode Sender (UGS).

    UGS is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    UGS is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with UGS.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.willwinder.ugs.nbm.visualizer;

import com.google.common.eventbus.Subscribe;
import com.jogamp.opengl.util.FPSAnimator;
import com.willwinder.ugs.nbp.eventbus.HighlightEvent;
import com.willwinder.universalgcodesender.listeners.ControllerListener;
import com.willwinder.universalgcodesender.model.Position;
import com.willwinder.universalgcodesender.types.GcodeCommand;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowListener;
import javax.swing.SwingUtilities;
import org.openide.util.Lookup;

/**
 *
 * @author wwinder
 */
public class RendererInputHandler implements
        WindowListener, MouseWheelListener, MouseMotionListener,
        MouseListener, KeyListener, ControllerListener {
    final private GcodeRenderer gcodeRenderer;
    final private FPSAnimator animator;

    public RendererInputHandler(GcodeRenderer gr, FPSAnimator a) {
        gcodeRenderer = gr;
        animator = a;
    }

    @Subscribe
    public void highlightEventListener(HighlightEvent he) {
        gcodeRenderer.setHighlightedLines(he.getLines());
        gcodeRenderer.forceRedraw();
    }
    
    /**
     * Mouse Motion Listener
     */

    @Override
    public void mouseDragged(java.awt.event.MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            int x = e.getX();
            int y = e.getY();
            
            int panMouseButton = InputEvent.BUTTON2_MASK; // TODO: Make configurable

            if (e.isShiftDown() || e.getModifiers() == panMouseButton) {
                gcodeRenderer.mousePan(new Point(x,y));
            } else {
                gcodeRenderer.mouseRotate(new Point(x,y));
            }
        }
    }

    @Override
    public void mouseMoved(java.awt.event.MouseEvent e) {
        int x = e.getX();
        int y = e.getY();

        gcodeRenderer.mouseMoved(new Point(x, y));
    }

    /**
     * Mouse Wheel Listener
     */
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        gcodeRenderer.zoom(e.getWheelRotation());
        gcodeRenderer.forceRedraw();
    }

    
    /**
     * Window Listener
     */

    @Override
    public void windowDeactivated(java.awt.event.WindowEvent e) {
        // Run this on another thread than the AWT event queue to
        // make sure the call to Animator.stop() completes before
        // exiting
        new Thread(new Runnable() {
            @Override
            public void run() {
                animator.stop();
            }
        }).start();
    }

    @Override
    public void windowOpened(java.awt.event.WindowEvent e) {
    }

    @Override
    public void windowClosing(java.awt.event.WindowEvent e) {
    }

    @Override
    public void windowClosed(java.awt.event.WindowEvent e) {
    }

    @Override
    public void windowIconified(java.awt.event.WindowEvent e) {
    }

    @Override
    public void windowDeiconified(java.awt.event.WindowEvent e) {
    }

    @Override
    public void windowActivated(java.awt.event.WindowEvent e) {
    }

    /**
     * Mouse Listener
     */

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        animator.start();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        animator.stop();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    /**
     * Key Listener
     */

     /**
     * KeyListener method.
     */
    @Override
    public void keyTyped(KeyEvent ke) {
    }

    /**
     * KeyListener method.
     */
    @Override
    public void keyPressed(KeyEvent ke) {
        animator.start();
        int DELTA_SIZE = 1;
            
        switch(ke.getKeyCode()) {
            case KeyEvent.VK_UP:
                gcodeRenderer.pan(0, DELTA_SIZE);
                //this.eye.y+=DELTA_SIZE;
                break;
            case KeyEvent.VK_DOWN:
                gcodeRenderer.pan(0, -DELTA_SIZE);
                break;
            case KeyEvent.VK_LEFT:
                gcodeRenderer.pan(-DELTA_SIZE, 0);
                break;
            case KeyEvent.VK_RIGHT:
                gcodeRenderer.pan(DELTA_SIZE, 0);
                break;
            case KeyEvent.VK_MINUS:
                if (ke.isControlDown())
                    gcodeRenderer.zoom(-1);
                break;
            case KeyEvent.VK_0:
            case KeyEvent.VK_ESCAPE:
                gcodeRenderer.resetView();
                break;
        }
        
        switch(ke.getKeyChar()) {
            case '+':
                if (ke.isControlDown())
                    gcodeRenderer.zoom(1);
                break;
        }
    }
    
    /**
     * KeyListener method.
     */
    @Override
    public void keyReleased(KeyEvent ke) {
        animator.stop();
    }

    /**
     * Controller listener methods
     */
    @Override
    public void statusStringListener(String state, Position machineCoord, Position workCoord) {
        gcodeRenderer.setMachineCoordinate(machineCoord);
        gcodeRenderer.setWorkCoordinate(workCoord);
    }

    @Override
    public void fileStreamComplete(String filename, boolean success) {
    }

    @Override
    public void commandSkipped(GcodeCommand command) {
    }

    @Override
    public void commandSent(GcodeCommand command) {
    }

    @Override
    public void commandComplete(GcodeCommand command) {
    }

    @Override
    public void commandComment(String comment) {
    }

    @Override
    public void messageForConsole(String msg, Boolean verbose) {
    }

    @Override
    public void postProcessData(int numRows) {
    }
}
