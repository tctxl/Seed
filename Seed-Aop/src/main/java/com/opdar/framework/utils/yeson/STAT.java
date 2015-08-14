package com.opdar.framework.utils.yeson;

public class STAT {
	long time;
	long ms;
	public void start(){
		time = System.currentTimeMillis();
	}
	public void add(){
		ms += System.currentTimeMillis()-time;
		time = System.currentTimeMillis();
	}
	public void end(){
		System.out.println(ms);
	}
}
