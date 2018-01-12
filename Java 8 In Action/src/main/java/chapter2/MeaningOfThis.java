package chapter2;

public class MeaningOfThis
{
	public final int value = 4;
	public void doIt()
	{
		int value = 6;
		Runnable r = new Runnable(){
			public final int value = 5;
			public void run(){
				int value = 10;
				System.out.println(this.value); // 这里的this指的是Runnable
			}
		};
		r.run(); 
	}
	public static void main(String[] args)
	{       
		MeaningOfThis m = new MeaningOfThis();
		m.doIt(); // 5
	}
}
