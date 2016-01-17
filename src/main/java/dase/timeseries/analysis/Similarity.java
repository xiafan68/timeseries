package dase.timeseries.analysis;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

import dase.timeseries.structure.ITimeSeries;

public abstract class Similarity {
	private final static Map<String, Similarity> sims = new HashMap<String, Similarity>();

	static {
		sims.put(CosineSim.class.getName(), new CosineSim());
		sims.put(PearsonSim.class.getName(), new PearsonSim());
		sims.put(GrangerSim.class.getName(), new GrangerSim());
	}

	public static Similarity getSim(String type) {
		return sims.get(type);
	}

	public abstract double sim(ITimeSeries a, ITimeSeries b);

	public static class CosineSim extends Similarity {
		public double sim(ITimeSeries a, ITimeSeries b) {
			double numerator = 0f;
			double aLen = 0;
			double bLen = 0;
			for (int i = 0; i < a.length(); i++) {
				aLen += Math.pow(a.getValueAtIdx(i), 2);
				bLen += Math.pow(b.getValueAtIdx(i), 2);
				numerator += a.getValueAtIdx(i) * b.getValueAtIdx(i);
			}

			numerator = Math.sqrt(numerator);
			return numerator / (Math.sqrt(aLen) * Math.sqrt(bLen));
		}
	}

	/**
	 * 基于pearson系数计算两个时间序列的相似性
	 * 
	 * @author xiafan
	 *
	 */
	public static class PearsonSim extends Similarity {
		public double sim(ITimeSeries a, ITimeSeries b) {
			float ret = 0;
			PearsonsCorrelation cor = new PearsonsCorrelation();
			ret = (float) cor.correlation(a.toArray(), b.toArray());
			return ret;

		}
	}

	/**
	 * 基于granger causality test的p-value来判断两个时序的相似度,比较耗时间，而且不满足欧式空间
	 * 
	 * @author xiafan
	 *
	 */
	public static class GrangerSim extends Similarity {
		public double sim(ITimeSeries a, ITimeSeries b) {
			double[] dataA = a.toArray();
			double[] dataB = b.toArray();
			double ret = 0;
			for (int i = 0; i < 5; i++) {
				double pvalue = GrangerTest.granger(dataA, dataB, i);
				ret = Math.max(ret, pvalue);
				pvalue = GrangerTest.granger(dataB, dataA, i);
				ret = Math.max(ret, pvalue);
			}

			return ret;

		}
	}
}
