package abmutils.random;

import cern.jet.random.Beta;
import cern.jet.random.engine.RandomEngine;

public class BetaGenerator {
	private RandomEngine rng;
	private Beta betaDist;

	public static BetaGenerator BetaGeneratorFromMeanAndSD(double meanProb, double sdProb, int seed){
		if(meanProb == 0.0)meanProb = 0.000001;
		if(meanProb == 1.0)meanProb = 0.999999;
		Double varProb = Math.pow(sdProb,2.0);
		if(varProb >= meanProb*(1.0-meanProb))varProb = meanProb*(1.0-meanProb) - 0.000001;
		return new BetaGenerator(meanProb*(meanProb*(1-meanProb)/varProb - 1.0), (1-meanProb)*(meanProb*(1-meanProb)/varProb - 1), seed);
	}
	public BetaGenerator(double a, double b,int seed){
		if(seed==0){
			this.rng = new cern.jet.random.engine.MersenneTwister();
		}else{
			this.rng = new cern.jet.random.engine.MersenneTwister(seed);
		}
		this.betaDist = new Beta(a,b,this.rng);
	}
	public Double nextValue(){
		return this.betaDist.nextDouble();
	}
}