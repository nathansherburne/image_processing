package elements;

public class LinearMapping {
	private double slope;
	private double bias;
	
	private LinearMapping(double x0, double y0, double x1, double y1) {
		this.slope = (double) (y1 - y0) / (x1 - x0);
		this.bias = y0 - (this.slope * x0);
	}
	
	public LinearMapping(OrderedPair<Double> inRange, OrderedPair<Double> outRange) {
		this(inRange.getMin(), outRange.getMin(), inRange.getMax(), outRange.getMax());
	}
	
	public double getSlope() {
		return slope;
	}

	public double getBias() {
		return bias;
	}

	
	public double map(double x) {
		return  (x * slope) + bias;
	}
	
}
