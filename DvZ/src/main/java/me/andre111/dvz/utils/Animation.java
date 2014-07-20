package me.andre111.dvz.utils;

import me.andre111.dvz.DvZ;

public abstract class Animation implements Runnable {
	private int taskId;
	private int delay;
	private int interval;
	private int tick;
	
	private boolean stopped;

	public Animation(int delay, int interval, boolean autoStart) {
		this.delay = delay;
		this.interval = interval;
		this.tick = -1;
		this.stopped = false;
		if (autoStart) {
			play();
		}
	}

	public void play() {
		taskId = DvZ.scheduleRepeatingTask(this, delay, interval);
	}

	protected void stop() {
		this.stopped = true;
		DvZ.cancelTask(taskId);
	}

	protected abstract void onTick(int tick);

	public boolean isStopped() {
		return stopped;
	}
	
	@Override
	public final void run() {
		onTick(++tick);
	}
}
        