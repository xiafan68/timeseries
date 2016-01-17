package dase.timeseries.structure;

/**
 * 对时间序列的标准化操作
 * 
 * @author xiafan
 *
 */
public class Standarization {
	public static ITimeSeries mean(ITimeSeries origin) {
		DenseTimeSeries ret = new DenseTimeSeries(origin.getGranu(), origin.getStartTime(), origin.getEndTime());
		double avg = origin.avg();
		for (int i = 0; i < origin.length(); i++) {
			ret.addValueAtIdx(i, (origin.getValueAt(i) - avg));
		}
		return ret;
	}

	public static ITimeSeries zscore(ITimeSeries origin) {
		DenseTimeSeries ret = new DenseTimeSeries(origin.getGranu(), origin.getStartTime(), origin.getEndTime());
		double avg = origin.avg();
		double var = Math.sqrt(origin.var());
		for (int i = 0; i < origin.length(); i++) {
			ret.addValueAtIdx(i, (origin.getValueAt(i) - avg) / var);
		}
		return ret;
	}
}
