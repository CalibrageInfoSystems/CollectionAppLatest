package com.oilpalm3f.mainapp.cloudhelper;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import java.util.concurrent.Future;


/**
 * Class for performing works in the background
 * 
 */
public final class ApplicationThread  {

    private static final String LOG_TAG = ApplicationThread.class.getName();

    private final static int THREAD_DELAY = 0;
    
    // values from AsyncTask.java
    private final static int BGND_POOL_SIZE = 5;
    private final static int BGND_POOL_MAX = 128;
    
    //    private static java.util.concurrent.ExecutorService bgnd;
    private static PriorityExecutor bgnd;
    private static Handler nui;
    private static Handler ui;
    private static Handler db;

    private static boolean started = false;
    private static boolean testMode = false;

            public static void start() {
                if (started) return;
                started = true;

                final HandlerThread nuiThread = new HandlerThread("non-ui");
                nuiThread.start();

                final HandlerThread dbThread = new HandlerThread("db", android.os.Process.THREAD_PRIORITY_BACKGROUND);
                dbThread.start();

                bgnd = new PriorityExecutor(BGND_POOL_SIZE, BGND_POOL_MAX, 1, java.util.concurrent.TimeUnit.MILLISECONDS);

                nui = new Handler(nuiThread.getLooper());
                ui = new Handler(Looper.getMainLooper());
                db = new Handler(dbThread.getLooper());
            }

            public static void stop() {
                if (null != nui) nui.getLooper().quit();
                if (null != db) db.getLooper().quit();
                if (null != bgnd) bgnd.shutdown();

                bgnd = null;
                nui = null;
                ui = null;
                db = null;

                started = false;
            }

            public static boolean uiThreadCheck() { return Looper.getMainLooper().getThread() == Thread.currentThread(); }

            public static boolean nuiThreadCheck() { return null != Looper.myLooper() && Looper.myLooper().equals(nui.getLooper()); }

            public static boolean dbThreadCheck() { return null != Looper.myLooper() && Looper.myLooper().equals(db.getLooper()); }

            public static void uiRemove(final Runnable runner) { ui.removeCallbacks(runner); }

            public static void nuiRemove(final Runnable runner) { nui.removeCallbacks(runner); }

            public static void bgndRemove(final Future<?> runner, final boolean force) {
                if (null != runner) runner.cancel(force);
            }

            /**
             * Note: do not use bgndPost for thread-unsafe operations
             */
            public static Future<?> bgndPost(final String clazz, final String msg, final int priority, final Runnable run) {
                final Runnable runner = new PriorityExecutor.Important() {
                    public int getPriority() {
                        return priority;
                    }

                    public void run() {
                        final long startTime = System.nanoTime();
                        try {
                            run.run();
                        } catch (Throwable ex) {
                            Log.e(LOG_TAG, LOG_TAG, ex);
                        } finally {

                            Log.d(LOG_TAG, "BGND THREAD " + msg + " (" + (System.nanoTime() - startTime) / 1000000 + "ms)");
                        }
                    }
                };
                if (testMode) {
                    runner.run();
                    return null;
                } else {
                    return bgnd.submit(runner);
                }

            }

            public static Future<?> bgndPost(final String clazz, final String msg, final Runnable run) { return bgndPost(clazz, msg, 10, run); }

            public static Runnable nuiPost(final String clazz, final String msg, final Runnable run, final long delay) {
                final Runnable runner = new Runnable() {
                    public void run() {
                        final long startTime = System.nanoTime();
                        try {
                            run.run();
                        } catch (Throwable ex) {
                            Log.e(LOG_TAG, LOG_TAG, ex);
                        } finally {
                            final String msg1 = "NON-UI THREAD " + (null == msg ? "" : msg) + " (" + (System.nanoTime() - startTime) / 1000000 + "ms)";
                            Log.d(LOG_TAG, msg1);
                        }
                    }
                };
                if (testMode) {
                    runner.run();
                } else {
                    nui.postDelayed(runner, delay);
                }
                return runner;
            }

            public static LoggingRunnable nuiPost(LoggingRunnable runner, final long delay) {
                nui.postDelayed(runner, delay);
                return runner;
            }

            public static Runnable dbPost(final String clazz, final String msg, final Runnable run, final long delay) {
                final Runnable runner = new Runnable() {
                    public void run() {
                        final long startTime = System.nanoTime();
                        try {
                            run.run();
                        } catch (Throwable ex) {
                            Log.e(LOG_TAG, LOG_TAG, ex);
                        } finally {
                            final String msg1 = "DB THREAD " + (null == msg ? "" : msg) + " (" + (System.nanoTime() - startTime) / 1000000 + "ms)";
                            Log.d(LOG_TAG, msg1);
                        }
                    }
                };

                if (testMode) {
                    runner.run();
                } else {
                    db.postDelayed(runner, delay);
                }

                return runner;
            }

            public static Runnable uiPost(final String clazz, final String msg, final Runnable run, final long delay) {
                final Runnable runner = new Runnable() {
                    public void run() {
                        final long startTime = System.nanoTime();
                        try {
                            run.run();
                        } catch (Throwable ex) {
                            Log.e(LOG_TAG, LOG_TAG, ex);
                        } finally {
                            final String msg1 = "UI THREAD " + (null == msg ? "" : msg) + " (" + (System.nanoTime() - startTime) / 1000000 + "ms)";
                            Log.d(LOG_TAG, msg1);
                        }
                    }
                };
                ui.postDelayed(runner, delay);
                return runner;
            }

            public static Runnable nuiPost(final String clazz, final String msg, final Runnable run) { return nuiPost(clazz, msg, run, THREAD_DELAY); }
    
            public static Runnable dbPost(final String clazz, final String msg, final Runnable run) { return dbPost(clazz, msg, run, THREAD_DELAY); }
    
            public static Runnable uiPost(final String clazz, final String msg, final Runnable run) {
                return uiPost(clazz, msg, run, THREAD_DELAY);
            }

    
    public static final class TestAccess {
        public static void start() {
            ApplicationThread.testMode = true;
        }
    }
    
    public static abstract class OnComplete<T> implements Runnable {
    
        public static final int NUI = 0;
        public static final int UI = 1;
        public static final int BGND = 2;
        private static final String LOG_TAG = OnComplete.class.getName();
        public int mode;
    
        public boolean success;
        public T result;
        public String msg;
    
        public OnComplete() { this.mode = OnComplete.NUI; }
    
        public OnComplete(final int mode) { this.mode = mode; }
    
        public void run() {
            Log.e(LOG_TAG, "run not implemented!");
        }
    
        private void execute() {
            final OnComplete thread = this;
            final Runnable onComplete = new Runnable() {
                public void run() {
                    try {
                        thread.run();
                    } catch (Throwable ex) {
                        Log.e(LOG_TAG, LOG_TAG, ex);
                    }
                }
            };
    
            switch (mode) {
                default:
    
                    Log.e(LOG_TAG, LOG_TAG, new RuntimeException("UNRECOGNIZED OnComplete mode " + mode + " posting to NUI"));
    
                case OnComplete.NUI:
                    ApplicationThread.nuiPost(LOG_TAG, "OnComplete", onComplete);
                    break;
    
                case OnComplete.UI:
                    ApplicationThread.uiPost(LOG_TAG, "OnComplete", onComplete);
                    break;
    
                case OnComplete.BGND:
                    ApplicationThread.bgndPost(LOG_TAG, "OnComplete", onComplete);
                    break;
            }
        }
    
        public void execute(boolean success, T result, String msg) {
            this.success = success;
            this.result = result;
            this.msg = msg;
            execute();
        }
    }
    
    public static abstract class Observable {
        private static final String LOG_TAG = Observable.class.getName();
        private final java.util.List<Object> observers = new java.util.ArrayList<Object>();

        public final void clear() {
            synchronized (observers) {
                observers.clear();
            }
        }

        public final void add(final Object observer) {
            synchronized (observers) {
                if (null == observer) throw new IllegalArgumentException("observer is null");
                if (observers.contains(observer)) {

                    Log.e(LOG_TAG, "ERROR already contains class:" + LOG_TAG + Log.getStackTraceString(new Throwable()));
                    return;
                }
                observers.add(0, observer);
            }
        }

        public final void remove(final Object observer) {
            synchronized (observers) {
                if (!observers.contains(observer)) {

                    Log.e(LOG_TAG, "ERROR does not contain class:" + LOG_TAG + Log.getStackTraceString(new Throwable()));
                    return;
                }
                observers.remove(observer);
            }
        }
    
        public final Object[] get() {
            Object[] observerArray;
            synchronized (observers) {
                observerArray = observers.toArray(new Object[observers.size()]);
            }
            return observerArray;
        }
    
        public final void notify(final int id, final Object obj, final Object... param) {
            ApplicationThread.nuiPost(LOG_TAG, "notify observers", new Runnable() {
                public void run() {
                    final Object[] observers = get();
                    for (int i = observers.length - 1; i >= 0; i--) {
                        ((Message) observers[i]).event(id, obj, param);
                    }
                }
            });
        }
    
        public static abstract class Message {
            private static final String LOG_TAG = Message.class.getName();
    
            public void event(final int id, final Object obj, final Object... param) {
                Log.v(LOG_TAG, "event(int,object,object) not implemented");
            }
        }
    }
    
    // needed, xxxRemove will not work on the embedded runnable
    public static class LoggingRunnable implements Runnable {
        private Runnable run;
        private String msg;
    
        public LoggingRunnable(final String msg, final Runnable run) {
            this.run = run;
            this.msg = msg;
        }
    
        public void run() {
            final long startTime = System.nanoTime();
            try {
                run.run();
            } catch (Throwable ex) {
                Log.e(LOG_TAG, LOG_TAG, ex);
            } finally {
                final String msg1 = "THREAD " + (null == msg ? "" : msg) + " (" + (System.nanoTime() - startTime) / 1000000 + "ms)";
                Log.d(LOG_TAG, msg1);
            }
        }
    }
}

