package org.logicail.framework.script.job;

/**
 * Created with IntelliJ IDEA.
 * User: Michael
 * Date: 23/06/13
 * Time: 17:17
 */
public abstract interface JobListener
{
	public abstract void jobStarted(Job job);

	public abstract void jobStopped(Job job);
}
