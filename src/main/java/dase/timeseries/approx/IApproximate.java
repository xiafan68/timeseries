package dase.timeseries.approx;

import dase.timeseries.structure.ITimeSeries;

/**
 * 时间序列近似算法
 * 
 * @author xiafan
 *
 */
public interface IApproximate {
	ITimeSeries approx(ITimeSeries origin);
}
