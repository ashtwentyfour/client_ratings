package project_parallel_scoring;


public class Driver {
	
	public static void main(String[] args) throws ClassNotFoundException {
	
		// start a new computation by specifying an industry and client location
		Calculation c = new Calculation("Electricity", "USA");
		Scoring scores = null;
		// invoke the scoring algorithms
		c.updateScores(scores);
	    
	}

}
