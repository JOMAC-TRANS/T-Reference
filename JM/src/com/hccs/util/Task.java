/*
 * Task.java
 *
 * Created on August 3, 2006, 2:42 PM
 *
 */
package com.hccs.util;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class Task {

    public static final String PROP_DESTROY = "destroy";
    public static final String PROP_DOINBACKGROUND = "doInBackground";
    public static final String PROP_FINISHED = "finished";
    public static final String PROP_INITIALIZE = "initialize";
    public static final String PROP_PROGRESS = "progress";
    public static final String PROP_START = "start";
    private final PropertyChangeSupport ps = new PropertyChangeSupport(this);
    protected boolean finished;
    protected boolean running;

    /**
     * All Initializations goes here.
     */
    public void initialize() {
        ps.firePropertyChange(PROP_INITIALIZE, null, this);
    }

    /**
     * Starting code goes here. This method is invoked in EDT.
     */
    public void start() {
        ps.firePropertyChange(PROP_START, null, this);
    }

    /**
     * Long running task goes here.
     */
    public void doInBackground() throws InterruptedException {
        ps.firePropertyChange(PROP_DOINBACKGROUND, null, this);
    }

    /**
     * Called after doInBackground() only if task is recursive. This method is
     * invoked in EDT.
     */
    public void progress() {
        ps.firePropertyChange(PROP_PROGRESS, null, this);
    }

    /**
     * Ending code goes here. This method is invoked in EDT.
     */
    public void finished() {
        ps.firePropertyChange(PROP_FINISHED, null, this);
    }

    /**
     * Cleanups goes here.
     */
    public void destroy() {
        ps.firePropertyChange(PROP_DESTROY, null, this);
    }

    public boolean isFinished() {
        return finished;
    }

    public boolean isRunning() {
        return running;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        ps.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        ps.removePropertyChangeListener(listener);
    }
}
