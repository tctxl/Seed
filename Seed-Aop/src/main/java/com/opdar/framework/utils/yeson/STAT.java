package com.opdar.framework.utils.yeson;

public class STAT {
	Long time;
	Long ms;
	public void start(){
		time = System.currentTimeMillis();
	}
	public void add(){
		ms += System.currentTimeMillis()-time;
		time = System.currentTimeMillis();
	}

	@Override
	public String toString() {
		return "STAT{" +
				"time=" + time +
				", ms=" + ms +
				'}';
	}

	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}

	public Long getMs() {
		return ms;
	}

	public void setMs(Long ms) {
		this.ms = ms;
	}

	public void end(){
		System.out.println(ms);
	}
}
