package project_parallel_scoring;

import java.util.*;
import java.util.Map.Entry;

/**
 * This class represents a client belonging to a particular industry and location
 * 
 * @author Ashwin Menon
 * @version 1.0
 * @since 2015-10-31
 *  
 */

class Client {
	
	private int ID; // unique client ID
	private String name; // company name
	private String industry;  // clients industry - aerospace, defense, IT, consulting etc.
	private String location;  // country - USA, India etc.
	private Double total_abs_score; // sum of the scores for the client over all domains
	private Double global_rel_score; // client's relative score wrt to the total abs score
	private HashMap<String, Double> rel_scores; // domain -> relative score mapping (latest)
	private HashMap<String, Double> abs_scores; // domain -> absolute score mapping (latest)
	
	/**
	 * @param indus client industry
	 * @param loc client location
	 * @param nme client name
	 * @param id client unique ID no.
	 * 
	 */
	
	public Client(String indus, String loc, String nme, int id) {	
		industry = indus;
		location = loc;	
		name = nme;
		total_abs_score = 0.0;
		global_rel_score = 0.0;
		rel_scores = new HashMap<String, Double>();
		abs_scores = new HashMap<String, Double>();
		ID = id;
	}
	
	public String getName() {
		return name;
	}
	
	/**
	 * This function sets the domain score for the domain specified if
	 * the score has not already been computed
	 * 
	 * @param domain domain 
	 * @param x absolute (domain score) to be assigned 
	 */
	
	public void setAbsScore(String domain, Double x) {
		if(abs_scores.containsKey(domain))
			return;
		else
			abs_scores.put(domain, x);
		return;
	}
	
	public Double getGlobalRelScore() { 
		return global_rel_score;
	}
	
	public void setGlobalRelScore(Double x) { 
		global_rel_score = x;
	}
	
	public Double getTotalScore() { 
		return total_abs_score;
	}
	
	public int getID() {
		return ID;
	}
	
	public Double getAbsScore(String domain) {
		return abs_scores.get(domain);
	}
	
	public Double getRelScore(String domain) {
		return rel_scores.get(domain);
	}
	public String getIndustry() {
		return industry;
	}
	
	public String getLocation() {
		return location;
	}
	
	/**
	 * This function computes the domain score for the specified domain by using
	 * score = w1*s1 + w2*s2 .... wn*sn where w1,w2....wn are the question rank/weights
	 * and s1,s2....sn are the corresponding responses/answers (range 1-5)
	 *  
	 * @param scores question weight -> response (score) mapping
	 * @param domain domain whose score has to be computed 
	 */
	
	public void computeAbsScore(HashMap<Double, Integer> scores, String domain) {
		
		double score = 0.0;
		for(Entry<Double, Integer> entry:scores.entrySet())
			score += entry.getValue()*entry.getKey();
		abs_scores.put(domain, score);
		return;
		
	}
	
	public void setRelativeScore(Double score, String domain) {
		if(rel_scores.containsKey(domain) == false)
		     rel_scores.put(domain, score);
		else
			return;
	}
	
	public void dispAbsoluteScores() {
		for(Entry<String, Double> entry:abs_scores.entrySet())
			System.out.println("Domain: " + entry.getKey() + 
					" Abs. Score: " + entry.getValue());
	}
	
	/**
	 * This function computes the total score for all over all the domains that 
	 * were assessed for the client
	 * 
	 * @param domains list of domains currently common to all the clients in the industry
	 */
	
	public void computeTotalAbsScore(List<String> domains) {
	   Double total = 0.0;
       for(String d: domains) {
    	   if(getAbsScore(d) >= 0.0)
    		   total += getAbsScore(d);
       }
	   total_abs_score = total;
	}

}
