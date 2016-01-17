package dase.timeseries.structure;

import java.util.Map.Entry;

import org.apache.commons.lang3.math.NumberUtils;

public class DenseTimeSeries extends ITimeSeries {
	double[] data;

	public DenseTimeSeries(int granu, long startTime, long endTime) {
		super(granu, startTime, endTime);
		data = new double[length()];
	}

	@Override
	public double getValueAt(long time) {
		return data[getIndx(time)];
	}

	@Override
	public double getValueAtIdx(int idx) {
		return data[idx];
	}

	@Override
	public void addValueAtTime(long time, double val) {
		data[getIndx(time)] += val;

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
	public void merge(ITimeSeries series) {
		if (series instanceof DenseTimeSeries) {
			DenseTimeSeries o = (DenseTimeSeries) series;
			for (int i = 0; i < length(); i++) {
				data[i] += o.data[i];
			}
		} else {
			SparseTimeSeries o = (SparseTimeSeries) series;
			for (Entry<Integer, Double> entry : o.baseTs.entrySet()) {
				data[entry.getKey()] += entry.getValue();
			}
		}
	}

}
