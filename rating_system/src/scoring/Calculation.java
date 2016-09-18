package scoring;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 *
 * This class contains the function(s) that are used to accept a client industry and client
 * industry as inputs, identify the latest assessments performed for each client in the industry,
 * update the relative and absolute scores for the clients for each domain which was assessed, and
 * finally update the database with the latest domain-wise scores and industry averages
 *
 * @author Ashwin Menon
 * @version 1.0
 * @since 2015-10-31
 *
 */

public class Calculation {

    //  Database credentials

	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	//static final String DB_URL = "jdbc:mysql://192.168.99.100:8081/client_ratings?useSSL=false";
	static final String DB_URL = "jdbc:mysql://192.168.99.100:8081/client_ratings?useSSL=false";
	static final String password = "abcd";

	// client specifications
	private String location;
	private String industry;
	HashSet<Client> clients;
	// list of all the domains that will be encountered
	HashSet<String> domains;
	// client ID -> latest assessment mapping
	HashMap<Integer, Integer> assess_ids;
	// domain -> domain ID mapping
	HashMap<String, String> dom_IDs;
	// client -> domains assessed mapping
	HashMap<Integer, List<String> > client_domains;


    /**
     * @param ind client industry
     * @param loc client location
     */

	public Calculation(String ind, String loc) {
		industry = ind;
		location = loc;
		clients = new HashSet<Client>();
		domains = new HashSet<String>();
		assess_ids = new HashMap<Integer,Integer>();
		dom_IDs = new HashMap<String, String>();
		client_domains = new HashMap<Integer , List<String> >();
	}

	/**
	 * This function computes the absolute and relative scores for each
	 * client and updates the database with the results
	 *
	 * @param s object of class Scoring which is used to carry out computations
	 * @throws ClassNotFoundException
	 *
	 */

	public void updateScores(Scoring s) throws ClassNotFoundException {

		Connection conn = null;
		Statement stmt = null;

		try {
		      // Register JDBC driver
		      Class.forName("com.mysql.jdbc.Driver");
		      // Open a connection
		      System.out.println("Connecting to database...");
		      conn = DriverManager.getConnection(DB_URL,"root",password);

		      Class.forName("com.mysql.jdbc.Driver");
		      DriverManager.getConnection(DB_URL,"root",password);

		      System.out.println("Creating statement...");
		      stmt = conn.createStatement();
		      // select all the clients who are part of the industry and in the location specified
		      String sql = "SELECT * FROM `client` WHERE `client_industry` = '" + industry
		    		     + "' AND `client_location` = '" + location + "' AND `num_assessments` > 0";
		      ResultSet sel = stmt.executeQuery(sql);
		      // Extract data from result set
		      while(sel.next()) {
		    	   // Retrieve by column name (client details)
		    	   String ind = sel.getString("client_industry");
		    	   String client_loc = sel.getString("client_location");
		    	   int id = sel.getInt("client_id");
		    	   String name = sel.getString("client_name");
                   // create a new client object
		    	   Client c = new Client(ind, client_loc, name, id);
		    	   clients.add(c);
		      }
					if(clients.size() == 0) {
						 System.out.println("no assessments found");
						 return;
					}
		      // start a new client scoring
		      s = new Scoring(clients);
		      // for each client extract the latest assessment information
		      for(Client c: clients) {
		    	  // select from the 'assessments' table by client ID
		    	  String ID = Integer.toString(c.getID());
		    	  int reqd_ID = 0;
		    	  sql = "SELECT `assess_id` FROM `assessments` WHERE "
		    	  		+ "`assess_date` = (SELECT max(`assess_date`) FROM "
		    	  		+ "`assessments` WHERE `client_id` = " + ID + ")";
		    	  stmt = conn.createStatement();
		    	  sel = stmt.executeQuery(sql);
		    	  // get the latest assessment ID
		    	  while(sel.next()) {
		    		  reqd_ID = sel.getInt("assess_id");
		    		  assess_ids.put(c.getID(), reqd_ID);
		    		  break;
		    	  }
            // for this client select all the domains that were recently assessed
		    	  sql = "SELECT * FROM `domain` WHERE `assess_id` = " + Integer.toString(reqd_ID);
		    	  stmt = conn.createStatement();
		    	  sel = stmt.executeQuery(sql);
                  // for each of these domains extract the responses (answers -> score between 1-5)
		    	  while(sel.next()) {
		    		  // question weight (0 < w < 1) -> answer mapping
		    		  HashMap<Double, Integer> ans = new HashMap<Double, Integer>();
		    		  // domain ID
		    		  String dom_id = Integer.toString(sel.getInt("domain_id"));
		    		  // domain name
		    		  String dom_name = sel.getString("domain_name");
		    		  // domain name -> ID mapping
		    		  dom_IDs.put(dom_name, dom_id);
		    		  // client -> domains assesses mapping update
			    	  if(client_domains.containsKey(c.getID()) == false) {
			    		  ArrayList<String> doms = new ArrayList<String>();
			    		  doms.add(dom_name);
			    		  client_domains.put(c.getID(), doms);
			    	  }
			    	  else
			    		  client_domains.get(c.getID()).add(dom_name);
			          // add domain to the set of all the domains encountered
		    		  domains.add(dom_name);
		    		  // select all the questions for this domain
		    		  sql = "SELECT * FROM `questions` WHERE `domain_id` = " + dom_id;
		    		  stmt = conn.createStatement();
		    		  ResultSet pq = stmt.executeQuery(sql);
		    		  // for each question get the corresponding response entered
		    		  while(pq.next()) {
		    			  // question ID
		    			  String q_id = Integer.toString(pq.getInt("question_id"));
		    			  stmt = conn.createStatement();
		    			  sql = "SELECT * FROM `responses` WHERE `question_id` = " + q_id +
		    					 " AND `assess_id` = " + Integer.toString(reqd_ID);
		    			  ResultSet rp = stmt.executeQuery(sql);
		    			  // extract and store responses
		    			  while(rp.next()) {
		    				  ans.put(pq.getDouble("question_rank"), rp.getInt("answer_numeric"));
		    			  }
		    			  rp.close();
		    		  }
		    		  pq.close();
		    		  // compute the absolute score for the client for this domain
		    		  s.updateAbsScores(ans, c.getName(), c.getID(), dom_name,
		    				          c.getIndustry(), c.getLocation());
		    	  }

		      }

		      /* compute total scores for all the clients (set a default value of
		        -1.0 for the domains that are missing for a client for their latest assessment
		       */
		      List<String> cdoms = new ArrayList<String>(); // domains common to all the clients
		      for(Client c: clients) {
		    	  for(String d: domains) {
		    		  if(client_domains.get(c.getID()).contains(d) == false)
		    			  c.setAbsScore(d, -1.0);
		    	  }
		      }
		      // form a list of all the domains common to all the clients
		      for(String d: domains) {
		    	  if(s.commDomain(d, industry, location))
		    		  cdoms.add(d);
		      }
		      // compute the total abs. score over all the common domains for each client
		      for(Client c: clients)
		    	  c.computeTotalAbsScore(cdoms);
		      // compute the industry average
		      Double ind_avg = s.getIndustryAvg(industry, location);
		      // compute the global relative rank for each client
		      s.computeGlobalRelativeRanks(industry, location);
    		  // update the assessments table with the total score (for each client)
		      for(Client c: clients) {
    		      sql = "UPDATE `assessments` SET `total_score` = "
    		  		     + "" + Double.toString(c.getTotalScore()) +
    		  		     ", `global_rel_score` = " +
    		  		     Double.toString(c.getGlobalRelScore()) +
    		  		     " WHERE `client_id` = " + Integer.toString(c.getID());
    		      stmt = conn.createStatement();
    		      stmt.executeUpdate(sql);
		      }
		      // for each domain compute the relative ranks and update the database for each client assessment
		      for(String d: domains) {
		    	  // compute the relative scores
		    	  s.computeRelativeRanks(industry, location, d);
		    	  // compute the domain average
		    	  Double avg = s.getDomainAvg(industry, d, location);
		    	  // for each client update
		    	  for(Client c: clients) {
		    	    if(c.getAbsScore(d) >= 0.0) { // if the domain d is a valid domain for client c
		    		  // update the assessments table for the client
		    		  sql = "INSERT INTO `client_ratings`.`assessment_score` (`domain_id`, `assess_id`, "
		    		  		+ "`dom_score`, `rel_score`) "
		    		  		+ "VALUES (" + dom_IDs.get(d) + "," + Integer.toString(assess_ids.get(c.getID())) +
		    		  		"," + Double.toString(c.getAbsScore(d)) + "," +
		    		  		Double.toString(c.getRelScore(d)) + ")";
		    		  stmt = conn.createStatement();
		    		  stmt.executeUpdate(sql);
		    		  // update the average score table for the client
		    		  sql = "SELECT * FROM `assessment_score` WHERE `assess_id` = " +
		    		  Integer.toString(assess_ids.get(c.getID()))
		    		  + " AND domain_id = " + dom_IDs.get(d);
		    		  stmt = conn.createStatement();
		    		  ResultSet gh = stmt.executeQuery(sql);

		    		  while(gh.next()) {
		    			  // update the average score table
								sql = "INSERT IGNORE INTO `client_ratings`.`avg_score` (`assessment_score_id`, `industry_domain_avg`, "
										+ "`industry_cum_avg`) VALUES "
										+ "(" + Integer.toString(gh.getInt("assessment_score_id")) +
										"," + Double.toString(avg) + "," + Double.toString(ind_avg) + ")";
								/*
		    			  sql = "INSERT INTO `client_ratings`.`avg_score` (`assessment_score_id`, `industry_domain_avg`, "
		    			  		+ "`industry_cum_avg`) VALUES "
		    			  		+ "(" + Integer.toString(gh.getInt("assessment_score_id")) +
		    			  		"," + Double.toString(avg) + "," + Double.toString(ind_avg) + ")";*/
								/*
								sql = "REPLACE INTO `client_ratings`.`avg_score` SET `assessment_score_id` = " + Integer.toString(gh.getInt("assessment_score_id"))
								      + ", `industry_domain_avg` = " + Double.toString(avg) + ", `industry_cum_avg` = " +
											 Double.toString(ind_avg);*/
		    			  stmt = conn.createStatement();
		    			  stmt.executeUpdate(sql);
		    		  }
		    		  gh.close();
		    	  }
		    	}
		      }
		      // close connections
		      sel.close();
		      stmt.close();
		      conn.close();
		   }

		   catch(SQLException se2) {
			   se2.printStackTrace();
		}
	 }


	/**
	 * get the list of all the domains that were assessed
	 *
	 * @return
	 */

    public HashSet<String> getDomainsAssessed() {
     	return domains;
    }


    /**
     * get the client -> latest assessment ID mappings
     *
     * @return
     */

    public HashMap<Integer, Integer> getClientAssessmentIDs() {
    	return assess_ids;
    }

}
