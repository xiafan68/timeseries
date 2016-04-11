package dase.timeseries.structure;

import java.util.Iterator;
import java.util.List;

/**
 * 时间序列类的抽象
 * 
 * @author xiafan
 *
 */
public abstract class ITimeSeries {
	protected long startTime = Long.MAX_VALUE;
	protected long endTime = Long.MIN_VALUE;
	protected int granu;

	public ITimeSeries(int granu, long startTime, long endTime) {
		this.granu = granu;
		this.startTime = startTime / granu * granu;
		this.endTime = endTime / granu * granu;
	}

	public abstract double getValueAt(long time);

	public int getIndex(long time) {
		return (int) ((time - startTime) / granu);
	}

	public abstract double getValueAtIdx(int idx);

	public abstract void addValueAtTime(long time, double val);

	public abstract void addValueAtIdx(int idx, double val);

	public abstract Iterator<Long> timeIterator();

	public long getStartTime() {
		return startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public abstract double[] toArray();

	public abstract double maxValue();

	public int length() {
		return (int) ((endTime - startTime) / granu + 1);
	}

	double total = -1;

	public double total() {
		if (total == -1) {
			total = 0.0f;
			Iterator<Long> timeIter = timeIterator();
			while (timeIter.hasNext()) {
				total += getValueAt(timeIter.next());
			}
		}
		return total;
	}

	public abstract double avg();

	public double var() {
		Double avg = this.avg();
		Double var = 0.0;
		for (int i = 0; i < length(); i++) {
			var += Math.pow((getValueAtIdx(i) - avg), 2);
		}
		return var / length();
	}

	public void merge(ITimeSeries series) {
		Iterator<Long> timeIter = series.timeIterator();
		while (timeIter.hasNext()) {
			long nextTime = timeIter.next();
			addValueAtTime(nextTime, series.getValueAt(nextTime));
		}
	}

	public static ITimeSeries merge(List<ITimeSeries> series) {
		ITimeSeries first = series.get(0);
		long startTime = Long.MAX_VALUE;
		long endTime = Long.MIN_VALUE;
		for (ITimeSeries ts : series) {
			startTime = Math.min(startTime, ts.getStartTime());
			endTime = Math.max(endTime, ts.getEndTime());
		}

		ITimeSeries ret = new DenseTimeSeries(first.getGranu(), startTime, endTime);
		for (ITimeSeries ser : series) {
			ret.merge(ser);
		}
		return ret;
	}

	public int getGranu() {
		return granu;
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		Iterator<Long> iter = this.timeIterator();
		while (iter.hasNext()) {
			long time = iter.next();
			buf.append(String.format("%d:%d;", time, getValueAt(time)));
		}
		return buf.toString();
	}
}
