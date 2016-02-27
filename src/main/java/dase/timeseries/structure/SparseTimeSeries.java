package dase.timeseries.structure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
			baseTs.put(getIndx(point[0]), (double) point[1]);
		}
	}

	public SparseTimeSeries(Map<Long, Long> ts, int granu, long startTime, long endTime) {
		super(granu, startTime, endTime);
		for (Entry<Long, Long> point : ts.entrySet()) {
			baseTs.put(getIndx(point.getKey()), (double) point.getValue());
		}
	}

	public double getValueAt(long time) {
		if (baseTs.containsKey(getIndx(time))) {
			return baseTs.get(getIndx(time));
		} else {
			return 0f;
		}
	}

	public int getIndx(long time) {
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
		int idx = getIndx(time);
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

	public void merge(ITimeSeries series) {
		startTime = Math.min(startTime, series.startTime);
		endTime = Math.max(endTime, series.endTime);
		granu = series.granu;
		if (series instanceof SparseTimeSeries) {
			for (Entry<Integer, Double> entry : ((SparseTimeSeries) series).baseTs.entrySet()) {
				addValueAtIdx(entry.getKey(), entry.getValue());
			}
		} else {
			for (int i = 0; i < series.length(); i++) {
				addValueAtIdx(i, series.getValueAtIdx(i));
			}
		}

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

		long start = ser.getStartTime();
		List<Long[]> intervals = new ArrayList<Long[]>();
		while (start <= ser.getEndTime()) {
			long curStart = start;
			while (start <= ser.getEndTime()) {
				if (ser.getValueAt(start) < max * 0.2) {
					break;
				}
				start += ser.granu;
			}
			if (start > curStart)
				intervals.add(new Long[] { curStart, start - ser.granu });
			if (start <= ser.getEndTime() && ser.getValueAt(start) < max * 0.2) {
				start += ser.granu;
			}
		}

		Collections.sort(intervals, new Comparator<Long[]>() {

			@Override
			public int compare(Long[] o1, Long[] o2) {
				int len1 = (int) (o1[1] - o1[0]);
				int len2 = (int) (o2[1] - o2[0]);
				int ret = Integer.compare(len2, len1);
				if (ret == 0) {
					ret = Long.compare(o1[0], o2[0]);
				}
				return ret;
			}

		});
		if (intervals.size() > 0)
			return intervals.get(0);
		else
			return new Long[] { ser.getStartTime(), ser.getEndTime() };
	}

	public int getGranu() {
		return granu;
	}
}
