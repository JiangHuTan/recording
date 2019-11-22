package com.recording;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * 首先向李道格老师致敬
 *
 * 读AQS
 * 读RenentractLock
 * 写一个简单的不能再简单的显示锁
 * 2019.11.22
 */
public class ReentrantLock{

    private volatile ExclusiveLock exclusiveLock;   //volatile防止指令重排序,防止获得多个独占锁实例

    /*定义一个内部类,继承了同步器类*/
    abstract static class Sync extends AbstractQueuedSynchronizer {
        abstract void lock();
        abstract void unlock();

    }

    /*内部类,独占锁,非公平的*/
    public static class ExclusiveLock extends Sync{
        /**
         * 调用此方法,线程尝试获取锁,独占机制
         * 实现可重入,所以AQS中的state变量为递增的
         */
        void lock() {
            /**
             * 尝试获取锁,将state标志位置为1,默认为0
             * 因同步器中的acquire方法会调用tryAcquire方法,而原方法抛异常,需要我们手动实现
             * 在我们的类中覆写tryAcquire(int arg)
             */
            acquire(1);
        }

        /**
         * 快速尝试获取锁
         * @param arg
         * @return
         */
        protected boolean tryAcquire(int arg) {
            final Thread current = Thread.currentThread();  //拿到当前线程,为了实现可重入的功能
            int c = getState();                     //获取当前锁的状态
            if (c == 0) {
                if (compareAndSetState(0, arg)) {   //利用原子性的CAS修改锁状态的标志位
                    setExclusiveOwnerThread(current);       //获取锁成功,将获取了锁的线程设置为当前线程
                    return true;                            //返回true,在acquire方法中将放行
                }
            }
            else if (current == getExclusiveOwnerThread()) {    //验证是否要重入锁
                int nextc = c + arg;                    //当前线程是已经获得了锁的线程,则将标志位值递增
                if (nextc < 0) // overflow              //因arg为用户输入,进行验证
                    throw new Error("数量异常");
                setState(nextc);                        //修改锁标志位为最新值
                return true;                        //返回true,在acquire方法中将放行
            }
            return false;                       //返回false,说明锁资源被占用,并且当前线程不是拥有锁的线程,无法进行重入操作
            //查看AQS源码,此种状态会将当前线程放入同步队列,等待资源
            //详情可追AQS中的acquire方法
        }

        /**
         * 释放锁资源
         */
        void unlock() {
            release(1);
        }

        /**
         * 因同步器中的release方法会调用tryRelease()方法,而原方法抛异常,需要我们手动实现
         * 覆写tryRelease()方法
         */
        protected boolean tryRelease(int arg) {
            int state = getState()- arg;
            boolean free = false;
            if (state == 0){        //因为同一时刻只能有一个线程释放锁,固此段不需用cas
                setState(state);
                setExclusiveOwnerThread(null);
                free = true;
            }
            return free;
        }
    }

    /**
     * 获取独占锁实例
     * 双重检查实现单例
     */
    public ExclusiveLock getExclusiveLock(){
        if (exclusiveLock == null){
            synchronized (this){
                if (exclusiveLock == null){
                    return new ExclusiveLock();
                }
            }
        }
        return exclusiveLock;
    }
}


class TestMyLock{
    private ReentrantLock reentrantLock = new ReentrantLock();
    final ReentrantLock.ExclusiveLock exclusiveLock = reentrantLock.getExclusiveLock();

    public void show(){
        exclusiveLock.lock();
        System.out.println(Thread.currentThread().getName() + "拿到锁了");
        try {
            TimeUnit.SECONDS.sleep(2); } catch (InterruptedException e) { e.printStackTrace(); }
        run();
        System.out.println(Thread.currentThread().getName() + "释放锁了");
        exclusiveLock.unlock();
    }

    public void run(){
        exclusiveLock.lock();
        System.out.println("重入了");
        exclusiveLock.unlock();
    }

    public static void main(String[] args) {
        TestMyLock testMyLock = new TestMyLock();

        new Thread(() -> {
            testMyLock.show();
        },"线程A").start();

        new Thread(() -> {
            testMyLock.show();
        },"线程B").start();
    }
}
