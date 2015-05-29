package abmutils.random;

import java.nio.ByteBuffer;
import java.util.Collection;

import org.uncommons.maths.random.*;

//TODO Make this faster by adding the generators to hashtables keyed by the parameters so we don't have to recreate the generator over and over again

public class Random {
	private MersenneTwisterRNG rng = new MersenneTwisterRNG();
	private DiscreteUniformGenerator fiftyFifty = new DiscreteUniformGenerator(0, 1, rng);
	public ContinuousUniformGenerator simpleRand = uniform();
	private Long seed = 0L;

	public Random (){ 
	}
	public void setSeed(Long seed){
		this.seed = seed;
		byte[] byteSeed = ByteBuffer.allocate(16).putLong(seed).array();
		this.rng = new MersenneTwisterRNG(byteSeed);
		// I'm naive and paranoid and suspicious that we need a new generator here
		this.fiftyFifty = new DiscreteUniformGenerator(0, 1, this.rng);
		this.simpleRand = new ContinuousUniformGenerator(0.0, 1.0, this.rng);
	}
	public BetaGenerator beta(double a, double b){
		return new BetaGenerator(a, b, this.seed.intValue());
	}
	public BetaGenerator betaFromMeanAndSD(double meanProb, double sdProb){
		return BetaGenerator.BetaGeneratorFromMeanAndSD(meanProb, sdProb, this.seed.intValue());
	}
	public ExponentialGenerator exponential(Double rate){
		return new ExponentialGenerator(rate,rng);
	}
	public PoissonGenerator poisson(Double mean){
		return new PoissonGenerator(mean,rng);
	}
	public DiscreteUniformGenerator randomInt(Integer low,Integer high){
		return new DiscreteUniformGenerator(low, high, rng);
	}
	public ContinuousUniformGenerator uniform(){
		return uniform(0.0,1.0);
	}
	public ContinuousUniformGenerator uniform(Double low,Double high){
		return new ContinuousUniformGenerator(low, high, rng);
	}
	public GaussianGenerator gaussian(){
		return gaussian(0.0,1.0);
	}
	public GaussianGenerator gaussian(Double mean,Double stDev){
		return new GaussianGenerator(mean,stDev,rng);
	}
	public TriangleGenerator triangle(int min, int peak, int max){
		return new TriangleGenerator((new Integer(min)).doubleValue(),(new Integer(peak)).doubleValue(),(new Integer(max)).doubleValue(),rng);
	}
	public TriangleGenerator triangle(Double min, Double peak, Double max){
		return new TriangleGenerator(min,peak,max,rng);
	}
	public Boolean drawFiftyFifty(){
		return this.fiftyFifty.nextValue() == 1;
	}
	public Integer drawRandomIndexFromCollection(@SuppressWarnings("rawtypes") Collection set){
		return this.randomInt(0, set.size()-1).nextValue();
	}
	public Double drawSimpleRand(){
		return this.simpleRand.nextValue();
	}
}