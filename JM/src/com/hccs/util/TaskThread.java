/*
 * TaskThread.java
 *
 * Created on August 3, 2006, 1:37 PM
 *
 */
package com.hccs.util;

import javax.swing.SwingUtilities;

public class TaskThread extends Thread {

    private boolean recurring;
    private long interval; // in milliseconds
    private boolean terminated;
    private boolean paused;
    private Task task;

    public TaskThread() {
    }

    public TaskThread(Task task, long interval) {
        this.task = task;
        this.recurring = true;
        this.interval = interval;
    }

    public TaskThread(Task task) {
        this.task = task;
    }

    public TaskThread(Task task, ThreadGroup group, String threadName) {
        super(group, threadName);

        this.task = task;
    }

    @Override
    public void run() {
        if (task == null) {
            return;
        }

        if (recurring) {
            task.initialize();
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    task.start();
                }
            });

            task.finished = false;
            while (true) {
                try {
                    if (terminated) {
                        break;
                    }

                    if (!paused) {
                        task.doInBackground();

                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                task.progress();
                            }
                        });
                    }
                    sleep(interval);
                } catch (InterruptedException ie) {
                    break;
                }
            }
            task.finished = true;

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    task.finished();
                }
            });
            task.destroy();
        } else {
            if (paused || terminated) {
                return;
            }

            task.initialize();
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    task.start();
                }
            });

            task.finished = false;
            try {
                task.doInBackground();
            } catch (InterruptedException iex) {
            }
            task.finished = true;

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    task.finished();
                }
            });
            task.destroy();
        }
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public void taskPause() {
        paused = true;
    }

    public void taskResume() {
        paused = false;
    }

    public void taskTerminate() {
        this.terminated = true;
    }

    public boolean isPaused() {
        return paused;
    }

    public boolean isTermitated() {
        return terminated;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public boolean isFinished() {
        return task.finished;
    }

    @Override
    public void interrupt() {
        super.interrupt();

        taskPause();
    }
}
