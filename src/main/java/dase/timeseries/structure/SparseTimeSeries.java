package dase.timeseries.structure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * 用于表示sparse的time series
 * 
 * @author xiafan
 *
 */
public class SparseTimeSeries extends ITimeSeries {
	TreeMap<Integer, Double> baseTs = new TreeMap<Integer, Double>();

	public SparseTimeSeries(int granu, long startTime, long endTime) {
		super(granu, startTime, endTime);
	}

	public SparseTimeSeries(List<long[]> ts, int granu, long startTime, long endTime) {
		super(granu, startTime, endTime);
		for (long[] point : ts) {
			baseTs.put(getIndex(point[0]), (double) point[1]);
		}
	}

	public SparseTimeSeries(Map<Long, Long> ts, int granu, long startTime, long endTime) {
		super(granu, startTime, endTime);
		for (Entry<Long, Long> point : ts.entrySet()) {
			baseTs.put(getIndex(point.getKey()), (double) point.getValue());
		}
	}

	public double getValueAt(long time) {
		if (baseTs.containsKey(getIndex(time))) {
			return baseTs.get(getIndex(time));
		} else {
			return 0f;
		}
	}

	public int getIndex(long time) {
		return (int) ((time - startTime) / granu);
	}

	public double getValueAtIdx(int idx) {
		if (baseTs.containsKey(idx)) {
			return baseTs.get(idx);
		} else {
			return 0f;
		}
	}

	public void addValueAtTime(long time, double val) {
		int idx = getIndex(time);
		addValueAtIdx(idx, val);
	}

	public void addValueAtIdx(int idx, double val) {
		if (baseTs.containsKey(idx)) {
			baseTs.put(idx, baseTs.get(idx) + val);
		} else {
			baseTs.put(idx, val);
		}
	}

	public long getStartTime() {
		return startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public double[] toArray() {
		int len = (int) ((endTime - startTime) / granu + 1);
		double[] ret = new double[len];

		for (int i = 0; i < len; i++) {
			ret[i++] = getValueAtIdx(i);
		}
		return ret;
	}

	public double maxValue() {
		if (baseTs.size() > 0) {
			return Collections.max(baseTs.values());
		}
		return 0.0;
	}

	public int length() {
		return (int) ((endTime - startTime) / granu);
	}

	public double avg() {
		Double ret = 0.0;
		for (Double value : baseTs.values()) {
			ret += value;
		}
		return ret / length();
	}

	public double var() {
		Double avg = this.avg();
		Double var = 0.0;
		for (int i = 0; i < length(); i++) {
			var += Math.pow((getValueAtIdx(i) - avg), 2);
		}
		return var / length();
	}

	public static Long[] outlineRange(ITimeSeries ser) {
		double max = ser.maxValue();

		long curTime = ser.getStartTime();
		while (curTime <= ser.getEndTime()) {
			if (ser.getValueAt(curTime) >= max * 0.05) {
				break;
			}
			curTime += ser.getGranu();
		}
		long startTime = curTime;
		curTime = ser.getEndTime();
		while (curTime >= ser.getStartTime()) {
			if (ser.getValueAt(curTime) >= max * 0.05) {
				break;
			}
			curTime -= ser.getGranu();
		}
		return new Long[] { startTime, curTime };
	}

	public int getGranu() {
		return granu;
	}

	@Override
	public Iterator<Long> timeIterator() {
		return new Iterator<Long>() {
			Iterator<Integer> iter = baseTs.keySet().iterator();

			@Override
			public boolean hasNext() {
				return iter.hasNext();
			}

			@Override
			public Long next() {
				return iter.next() * (long) granu + startTime;
			}

			@Override
			public void remove() {
			}
		};
	}
}
