package ANN1;

import java.util.*;

public class BN {
	Double u, xc[], xs[], xhat[], y[], var, std, istd, gamma = 1.0, beta = 0.0, epsilon = 0.0001;
	int batchNum, batchSize;
	LinkedList<Double> vars, us;
	Double vars_u, us_u;

	Double running_u = -100.0, running_var = -100.0, momentum = 0.99;

	BN(int batchNum, int batchSize) {
		this.batchNum = batchNum;
		this.batchSize = batchSize;
		vars = new LinkedList<Double>();
		us = new LinkedList<Double>();

		momentum = (double) 1 - (1 / batchNum);

	}

	void forward(Double[] x) {// 
		int size = x.length;

		// u
		u = 0.0;
		for (int i = 0; i < size; i++) {
			u += x[i];
			// System.out.print(x[i]+"\t");
		}
		// System.out.println();
		u /= size;

		if (running_u == -100.0) {
			running_u = u;
		} else {
			running_u *= momentum;
			running_u += (1 - momentum) * u;
		}

		us.add(u);
		if (us.size() > batchNum) {
			us.removeFirst();
		}
		// u end

		// xc = x-u
		xc = new Double[size];
		for (int i = 0; i < size; i++) {
			xc[i] = x[i] - u;

			// System.out.print(xc[i]+"\t");
		}
		// xc end
		// System.out.println();

		// xs = xc^2
		xs = new Double[size];
		for (int i = 0; i < size; i++) {
			xs[i] = Math.pow(xc[i], 2);

			// System.out.print(xs[i]+"\t");
		}
		// xs end

		// var
		var = 0.0;
		for (int i = 0; i < size; i++) {
			var += xs[i];
		}
		var /= size;

		if (running_var == -100.0) {
			running_var = var;
		} else {
			running_var *= momentum;
			running_var += (1 - momentum) * var;
		}
		vars.add(var);
		if (vars.size() > batchNum) {
			vars.removeFirst();
		}
		// var end

		// std = (var + epsilon)^(1/2)
		std = Math.pow(var + epsilon, 0.5);
		// System.out.println(std);

		istd = 1 / std;
		// System.out.println(istd);

		xhat = new Double[size];
		y = new Double[size];
		for (int i = 0; i < size; i++) {
			xhat[i] = xc[i] * istd;

			y[i] = xhat[i] * gamma + beta;

			x[i] = y[i];
		}
		// System.out.println("---------------------");

	}

	Double forward(Double x) {// 資料,維度
		Double y;

		double running_std = Math.pow(running_var + epsilon, 0.5);

		double running_istd = 1 / running_std;

		// System.out.print(x);
		Double xx = (x - running_u) * running_istd;
		y = xx * gamma + beta;

		return y;
	}

	void back(Double[] dl_dy, double lr) {
		int size = dl_dy.length;

		double dbeta = 0;
		double dgamma = 0;
		double dxhat[] = new double[size];
		double distd = 0;

		for (int i = 0; i < size; i++) {
			dbeta += dl_dy[i];
			dgamma += xhat[i] * dl_dy[i];

			dxhat[i] = dl_dy[i] * gamma;

			distd += dxhat[i] * xc[i];
		}
		// System.out.println();
		double dstd = distd * -1 / Math.pow(std, 2);
		double dvar = dstd / (2 * std);
		double dxs[] = new double[size];
		double dxc[] = new double[size];
		double du = 0;
		for (int i = 0; i < size; i++) {
			dxs[i] = dvar / size;
			dxc[i] = dxhat[i] * istd + dxs[i] * 2 * xc[i];
			du -= dxc[i];
		}

		Double dx[] = new Double[size];
		for (int i = 0; i < size; i++) {
			dx[i] = dxc[i] + du / size;
			dl_dy[i] = dx[i];

		}
		// System.out.println("------------");

		beta -= dbeta * lr;
		gamma -= dgamma * lr;

	}
/*
	void testSet() {
		vars_u = 0.0;
		us_u = 0.0;
		Iterator<Double> itr = vars.iterator();
		while (itr.hasNext()) {

			Double x = itr.next();
			// System.out.print(x+"\t");

			vars_u += x;
		}
		// System.out.print("---\t"+vars_u+"\t"+(double)batchNum/(batchNum -
		// 1)/vars.size()+"\t");

		vars_u *= (double) batchNum / (batchNum - 1) / vars.size();

		// System.out.println(vars_u);

		itr = us.iterator();
		while (itr.hasNext()) {
			us_u += itr.next();
		}
		us_u /= us.size();
	}
*/
}
