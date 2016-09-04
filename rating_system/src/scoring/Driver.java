package scoring;


public class Driver {

	public static void main(String[] args) throws ClassNotFoundException {

    if(args.length != 2) {
			System.err.println("Error: Enter client <Industry> and client <Country> as command line args");
			System.exit(0);
		}

		// start a new computation by specifying an industry and client location
		Calculation c = new Calculation(args[0], args[1]);
		Scoring scores = null;
		// invoke the scoring algorithms
		c.updateScores(scores);

	}

}
