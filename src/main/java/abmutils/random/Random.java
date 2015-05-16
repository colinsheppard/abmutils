package abmutils.random;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import org.apache.logging.log4j.*;
import org.uncommons.maths.random.*;

//TODO Make this faster by adding the generators to hashtables keyed by the parameters so we don't have to recreate the generator over and over again

public class Random {
	static final Logger log = LogManager.getLogger(Random.class.getName());
	
	private static Random instance = null;
	private static MersenneTwisterRNG rng = new MersenneTwisterRNG();
	private static DiscreteUniformGenerator fiftyFifty = new DiscreteUniformGenerator(0, 1, rng);

	public static Random getInstance(){ 
		if(instance == null){
			instance = new Random();
		}
		return (instance);
	}
	public void setSeed(Long seed){
		byte[] byteSeed = ByteBuffer.allocate(16).putLong(seed).array();
		rng = new MersenneTwisterRNG(byteSeed);
		// We should also seed the beta generator which uses it's own Mersenne Twister
		// we draw a psuedo-random number from rng to avoid both generators starting in the exact same location
		new BetaGenerator(1.0, 1.0, rng.nextInt());
		// I'm naive and paranoid and suspicious that we need a new generator here
		fiftyFifty = new DiscreteUniformGenerator(0, 1, rng);
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
	public TriangleGenerator triangle(Double min, Double peak, Double max){
		return new TriangleGenerator(min,peak,max,rng);
	}
	public Boolean drawFiftyFifty(){
		return Random.fiftyFifty.nextValue() == 1;
	}
	public Integer drawRandomIndexFromArray(@SuppressWarnings("rawtypes") ArrayList array){
		return this.randomInt(0, array.size()-1).nextValue();
	}
}