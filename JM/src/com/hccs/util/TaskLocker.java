package com.hccs.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class TaskLocker {

    private final List<LockThread> threads;
    private CountDownLatch latch;

    public TaskLocker() {
        threads = new ArrayList<>();
    }

    private void run() {
        latch = new CountDownLatch(threads.size());
        for (LockThread xx : threads) {
            xx.start();
        }
    }

    public int getThreadCount() {
        return threads.size();
    }

    public void cancel() {
        latch.countDown();
        while (latch.getCount() > 0) {
            latch.countDown();
        }
    }

    public void runAndWaitLocker() {
        run();
        waitLocker();
    }

    private void waitLocker() {
        try {
            latch.await();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void addThread(TaskLock taskLock) {
        threads.add(new LockThread(taskLock));
    }

    private class LockThread extends Thread {

        public LockThread(Runnable run) {
            super(run);
        }

        @Override
        public void run() {
            try {
                super.run();
            } catch (Exception e) {
                throw e;
            } finally {
                latch.countDown();
            }
        }
    }
}
