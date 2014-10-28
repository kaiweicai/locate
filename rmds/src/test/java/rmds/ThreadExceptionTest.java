package rmds;

import com.locate.common.exception.LocateException;

public class ThreadExceptionTest {

	private Thread t = new Thread() {
		private int i=0;
		public void run() {
			while (true) {
				try {
					i++;
					Thread.sleep(1000);
					if(i>10){
						throw new LocateException("打断你的狗腿!");
					}
					System.out.println("I am keep running!");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}catch(Exception e){
					System.out.println(e);
					System.out.println("我抓住你了,我们还是朋友!");
				}
			}
		}
	};

	public static void main(String[] args) {
		ThreadExceptionTest threadExceptionTest = new ThreadExceptionTest();
		threadExceptionTest.t.start();
	}
}
