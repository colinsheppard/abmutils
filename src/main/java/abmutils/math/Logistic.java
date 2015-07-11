package abmutils.math;

import java.util.Hashtable;

/**
 * @class Logistic
 * 
 * Helper classed used to store parameters relevant to the calculation of logistic 
 * curves and to perform those calculations in an efficient manner.
 */
public class Logistic {
	private Hashtable<String,Double> 	logistA = new Hashtable<String,Double>();
	private Hashtable<String,Double> 	logistB = new Hashtable<String,Double>();
	private double 		logistD = Math.log(0.9/0.1);
	private double 		logistC = Math.log(0.1/0.9);
	private double 		logisticLimiter = 20.0;
	
	/**
	 * @brief Logistic constructor
	 * 
	 * Empty constructor, nothing happens.
	 */
	public Logistic(){
	}
	/**
	 * @brief Set parameters wrapper
	 * 
	 * Wrapper for setParameters with 2 arguments, default value for the missing parameters are:
	 * 
	 * 	- logistKey = "default"
	 * 	- logistLimiter = 20.0 
	 * 
	 * @param habVarAtS01	The value of the habitat variable that results in a probability of 0.1
	 * @param habVarAtS09	The value of the habitat variable that results in a probability of 0.9
	 * @throws IllegalArgumentException
	 */
	public void setParams(double habVarAtS01, double habVarAtS09) throws IllegalArgumentException{
		setParams(habVarAtS01, habVarAtS09, "default", 20.0);
	}
	/**
	 * @brief Set parameters wrapper
	 * 
	 * Wrapper for setParameters with 3 arguments, default value for the missing parameter is:
	 * 
	 * 	- logistLimiter = 20.0 
	 * 
	 * @param habVarAtS01	The value of the habitat variable that results in a probability of 0.1
	 * @param habVarAtS09	The value of the habitat variable that results in a probability of 0.9
	 * @param logistKey		A key to uniquely identify the curve to be produced from the passed parameters.
	 * @throws IllegalArgumentException
	 */
	public void setParams(double habVarAtS01, double habVarAtS09,String logistKey) throws IllegalArgumentException{
		setParams(habVarAtS01, habVarAtS09, logistKey, 20.0);
	}
	/**
	 * @brief Set parameters
	 * 
	 * Store the parameters habVarAtS01, habVarAtS09, logistLimiter and associated them with the key logistKey.
	 * 
	 * @param habVarAtS01	The value of the habitat variable that results in a probability of 0.1
	 * @param habVarAtS09	The value of the habitat variable that results in a probability of 0.9
	 * @param logistKey		A key to uniquely identify the curve to be produced from the passed parameters.
	 * @param logistLimiter	A Z-value above which the Logistic class returns a value of 1 and below which it 
	 * 						returns a value of 0 without performing any calculations.  This is an efficiency measure.
	 * @throws IllegalArgumentException
	 */
	public void setParams(double habVarAtS01, double habVarAtS09,String logistKey, double logistLimiter) throws IllegalArgumentException{
		if(habVarAtS01 == habVarAtS09){
			throw new IllegalArgumentException("Habitat variable values specifying the logistic function for "+logistKey+" are equal in value");
		}
		if(logistLimiter<0){
			logisticLimiter = -logistLimiter;
		}else{
			logisticLimiter = logistLimiter;
		}
		double logistBval = (logistC-logistD)/(habVarAtS01-habVarAtS09);
		logistB.put(logistKey, logistBval);
		logistA.put(logistKey, logistC-(logistBval*habVarAtS01));
	}
	/**
	 * @brief Get probability wrapper
	 * 
	 * Wrapper for getProbability with 1 argument.  The default value for missing parameter is:
	 * 
	 * 	- logistKey = "default"
	 * 
	 * @param habVar	The value of the habitat variable for which a corresponding probability is sought.
	 * @return double
	 */
	public double getProbability(double habVar){
		return getProbability(habVar,"default");
	}
	/**
	 * @brief Get probability
	 * 
	 * Using a logistic curved defined by the parameters associated with logistKey, returns the probability associated with the
	 * value habVar.
	 * 
	 * @param habVar	The value of the habitat variable for which a corresponding probability is sought.
	 * @param logistKey	A key uniquely identifying the curve to be produced for the parameter habVar.
	 * @return double
	 */
	public double getProbability(double habVar, String logistKey){
		double Z = (Double)logistA.get(logistKey) + (Double)logistB.get(logistKey)*habVar;
		if(Z > logisticLimiter){
			return(1.0);
		}else if(Z < -logisticLimiter){
			return(0.0);
		}else{
			return Math.exp(Z)/(1 + Math.exp(Z));
		}
	}
}
