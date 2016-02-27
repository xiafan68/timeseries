package dase.timeseries.structure;

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
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public abstract double getValueAt(long time);

	public int getIndx(long time) {
		return (int) ((time - startTime) / granu);
	}

	public abstract double getValueAtIdx(int idx);

	public abstract void addValueAtTime(long time, double val);

	public abstract void addValueAtIdx(int idx, double val);

	public long getStartTime() {
		return startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public abstract double[] toArray();

	public abstract double maxValue();

	public int length() {
		return (int)( (endTime - startTime) / granu + 1);
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

	public abstract void merge(ITimeSeries series);

	public static ITimeSeries merge(List<ITimeSeries> series) {
		ITimeSeries first = series.get(0);
		ITimeSeries ret = new DenseTimeSeries(first.getGranu(), first.getStartTime(), first.getEndTime());
		for (ITimeSeries ser : series) {
			ret.merge(ser);
		}
		return ret;
	}

	public int getGranu() {
		return granu;
	}
}
