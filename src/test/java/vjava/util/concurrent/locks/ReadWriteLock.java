package vjava.util.concurrent.locks;

import java.util.Random;

//关键部分
public class ReadWriteLock {
	private int readingReaders = 0;// 实际正在读取的线程数目
	private int waitingWriters = 0;// 正在等待写入的线程数目
	private int writingWriters = 0;// 实际正在写入的线程数目
	private boolean preferWriter = true;// 写入优先的话，值为true

	// 读取的时候获取锁
	public synchronized void readLock() throws InterruptedException {
		// 当有写入的时候，或者写入为优先级并且有等待的写入线程
		while (writingWriters > 0 || (preferWriter && waitingWriters > 0)) {
			wait();
		}
		readingReaders++;
	}

	// 读完毕后释放锁
	public synchronized void readUnlock() throws InterruptedException {
		readingReaders--;
		preferWriter = true;
		notifyAll();
	}

	// 写入的时候获取锁
	public synchronized void writeLock() throws InterruptedException {
		waitingWriters++;// 正在等待的写入的线程数目
		try {
			// 有写入或者读入的时候
			while (readingReaders > 0 || writingWriters > 0) {
				wait();
			}
		} finally {
			waitingWriters--;// 被唤醒了，则就是进而真正写入
		}
		writingWriters++;
	}

	// 写入毕后释放锁
	public synchronized void writeUnlock() throws InterruptedException {
		writingWriters--;
		preferWriter = false;// 写入后马上更换优先级，让读者继续
		notifyAll();
	}
	
	public static void main(String[] args) {
        // TODO Auto-generated method stub
        Data data=new Data(10);
        //读取线程
        new ReaderThread(data).start();
        new ReaderThread(data).start();
        new ReaderThread(data).start();
        new ReaderThread(data).start();
        new ReaderThread(data).start();
        new ReaderThread(data).start();
           
        //写入线程
        new WriterThread(data,"ABCDEFGHIJKLMNOPQRSTUVWXYZ").start();
        new WriterThread(data,"abcdefghijklmnopqrstuvwxyz").start();
    }
}

class Data{
	private final char[] buffer;
    private final ReadWriteLock lock=new ReadWriteLock();
                                                                                                                                                                                                                                                                                                    
    public Data(int size)
    {
        this.buffer=new char[size];
        for(int i=0;i<buffer.length;i++)
            buffer[i]='*';
    }
                                                                                                                                                                                                                                                                                                    
    public char[] read()throws InterruptedException
    {
        lock.readLock();
        try{
            return doRead();
        }finally{
            lock.readUnlock();
        }
    }
                                                                                                                                                                                                                                                                                                    
    private char[] doRead()
    {
        char[] newbuf=new char[buffer.length];
        for(int i=0;i<buffer.length;i++)
            newbuf[i]=buffer[i];
        slowly();
        return newbuf;
    }
                                                                                                                                                                                                                                                                                                    
    public void write(char c)throws InterruptedException
    {
        lock.writeLock();
        try{
             doWrite(c);
        }finally{
            lock.writeUnlock();
        }
    }
                                                                                                                                                                                                                                                                                                    
    private void doWrite(char c)
    {
        for(int i=0;i<buffer.length;i++)
        {
            buffer[i]=c;
            slowly();
            //这里的sleep并不会切换到别的线程
            //这里就是体现了使用while的好处
            //当该线程sleep时候，其余等待读取的还在wait中，而要写入的线程会判断它的状态，还没有释放锁
        }
    }
                                                                                                                                                                                                                                                                                                    
    private void slowly()
    {
        try{
            Thread.sleep(50);
        }catch(InterruptedException e)
        {
                                                                                                                                                                                                                                                                                                            
        }
    }
}

class ReaderThread extends Thread{
    private final Data data;
    public ReaderThread(Data data)
    {
        this.data=data;
    }
                                                                                                                                                                                                                                                                                           
    public void run()
    {
        try{
            while(true)
            {
                char[] readbuf=data.read();
                System.out.println(Thread.currentThread().getName()
                        +" reads "+String.valueOf(readbuf));
            }
        }catch(InterruptedException e)
        {
        }
    }
}

class WriterThread extends Thread{
    
    private static final Random random=new Random();
    private final Data data;
    private final String filler;
    private int index=0;
                                                                                                                                                                                                                                                                                   
    public WriterThread(Data data,String filler)
    {
        this.data=data;
        this.filler=filler;
    }
                                                                                                                                                                                                                                                                                   
    public void run()
    {
        try{
            while(true)
            {
                char c=nextChar();
                data.write(c);
                System.out.println(Thread.currentThread().getName()
                        +" write "+String.valueOf(c));
                Thread.sleep(random.nextInt(1000));
            }
        }catch(InterruptedException e)
        {
                                                                                                                                                                                                                                                                                           
        }
    }
                                                                                                                                                                                                                                                                                   
    private char nextChar()
    {
        char c=filler.charAt(index);
        index++;
        if(index>=filler.length())
            index=0;
        return c;
    }
}
