package com.hccs.util;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

public class TaskProgress extends JProgressBar {

    private boolean finished;
    private String remainingTime;

    /**
     * @param minimum minimum value for progress bar
     * @param maximum maximum value for progress bar
     * @param taskName name of task for updating and removing progress task
     * @param remainingTime label for the progress task
     * @param status if this is set, progress task automatically converted to
     * indeterminate
     */
    public TaskProgress(int minimum, int maximum, String taskName, String remainingTime, final String status) {
        this.setName(taskName);
        setMinimumSize(new Dimension(200, this.getMinimumSize().height));
        setMinimum(minimum);
        setMaximum(maximum);
        this.remainingTime = remainingTime;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                setStringPainted(true);
                setValue(0);
                setForeground(new Color(0xFF9933));
                if (!status.isEmpty()) {
                    setIndeterminate(true);
                    setString(status);
                }
            }
        });
    }

    public boolean isFinished() {
        return finished;
    }

    public int getCurrentValue() {
        return this.getValue();
    }

    public void setRemainingTime(String remainingTime) {
        this.remainingTime = remainingTime;
    }

    public String getRemainingTime() {
        return remainingTime;
    }
}
