package dase.timeseries.structure;

import java.util.Iterator;

import org.apache.commons.lang3.math.NumberUtils;

public class DenseTimeSeries extends ITimeSeries {
	double[] data;

	public DenseTimeSeries(int granu, long startTime, long endTime) {
		super(granu, startTime, endTime);
		data = new double[length()];
	}

	@Override
	public double getValueAt(long time) {
		return data[getIndex(time)];
	}

	@Override
	public double getValueAtIdx(int idx) {
		return data[idx];
	}

	@Override
	public void addValueAtTime(long time, double val) {
		data[getIndex(time)] += val;

	}

	@Override
	public void addValueAtIdx(int idx, double val) {
		data[idx] += val;
	}

	@Override
	public double[] toArray() {
		return data;
	}

	@Override
	public double maxValue() {
		return NumberUtils.max(data);
	}

	@Override
	public double avg() {
		Double sum = 0.0;
		for (double point : data) {
			sum += point;
		}
		return sum / data.length;
	}

	@Override
	public Iterator<Long> timeIterator() {
		return new Iterator<Long>() {
			long cur = startTime;

			@Override
			public boolean hasNext() {
				return cur < endTime;
			}

			@Override
			public Long next() {
				Long ret = cur;
				cur += granu;
				return ret;
			}

			@Override
			public void remove() {
			}
		};
	}
}
