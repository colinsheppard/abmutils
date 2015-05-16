package abmutils.random;

import cern.jet.random.Beta;
import cern.jet.random.engine.RandomEngine;

public class BetaGenerator {
	private static RandomEngine rng;
	private Beta betaDist;

	public static BetaGenerator BetaGeneratorFromMeanAndSD(Double meanProb, Double sdProb){
		if(meanProb == 0.0)meanProb = 0.000001;
		if(meanProb == 1.0)meanProb = 0.999999;
		Double varProb = Math.pow(sdProb,2.0);
		if(varProb >= meanProb*(1.0-meanProb))varProb = meanProb*(1.0-meanProb) - 0.000001;
		return new BetaGenerator(meanProb*(meanProb*(1-meanProb)/varProb - 1.0), (1-meanProb)*(meanProb*(1-meanProb)/varProb - 1));
	}
	public BetaGenerator(Double a, Double b){
		this(a, b, null);
	}
	public BetaGenerator(Double a, Double b,Integer seed){
		if(BetaGenerator.rng == null){
			if(seed==null){
				BetaGenerator.rng = new cern.jet.random.engine.MersenneTwister();
			}else{
				BetaGenerator.rng = new cern.jet.random.engine.MersenneTwister(seed);
			}
		}
		this.betaDist = new Beta(a,b,rng);
	}
	public Double nextValue(){
		return this.betaDist.nextDouble();
	}
}
