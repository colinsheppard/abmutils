package abmutils.random;

import org.uncommons.maths.random.MersenneTwisterRNG;

public class TriangleGenerator {
	private static MersenneTwisterRNG rng;
	private Double a,b,c;
	private Double h,ALeft,ARight,fLeft,fRight;

	public TriangleGenerator(Double min, Double peak, Double max,MersenneTwisterRNG rng) {
		this.a = min;
		this.b = peak;
		this.c = max;
		TriangleGenerator.rng = rng;
		if(this.a > this.b || this.a > this.c || this.b > this.c)throw new RuntimeException("Inconsistent parameters to triangle disbribution, min="+this.a+" peak="+this.b+" max="+this.c);
		initConstants();
	}
	private void initConstants() {
		h = 2.0/(c - a);
		ALeft = (b-a)*h/2.0;
		ARight = (c-b)*h/2.0;
		fLeft = Math.pow(a*h,2.0)/(b-a) - h*h*a*a/(b-a); 
		fRight = c*c*h*h/(c-b) + 2.0*h*(ALeft + ARight - c*c*h/2/(c-b));
	}
	public Double nextValue(){
		Double result;
		Double draw = TriangleGenerator.rng.nextDouble();
		if(draw <= ALeft){
			Double d = Math.sqrt( (fLeft + 2*h*draw)/(b-a) );
			result = (a*h/(b-a) + d)*(b-a)/h;
		}else{
			Double d = Math.sqrt( (fRight - 2*h*draw)/(c-b) );
			result = (-c*h/(c-b) + d)*(b-c)/h;
		}
		return result;
	}
}
