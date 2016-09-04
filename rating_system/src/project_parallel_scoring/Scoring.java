package project_parallel_scoring;

import java.util.*;
import java.util.Map.Entry;

/**
 * This class scores clients across multiple domains by computing the 
 * relative and absolute scores for each domain. The industry averages and 
 * domain averages are also computed by the member functions of this class
 * 
 * @author Ashwin Menon
 * @version 1.0
 * @since 2015-10-31
 *
 */


public class Scoring {
	
	static final Double d = 0.85; // damping factor
	static final Integer num_iter = 8;
	
    private HashMap<Integer, Client> clients; // client ID -> client mapping
    
    /**
     * @param cl set/list of all clients
     * 
     */
    
    public Scoring(HashSet<Client> cl) {
    	clients = new HashMap<Integer, Client>();
    	for(Client c: cl)
    		clients.put(c.getID(), c);
    }
	
    /**
     * This function computes absolute score for the client whose ID is specified
     * and if the client is not listed, the client is added to the list
     * 
     * @param scores question rank -> score mapping
     * @param name client name
     * @param ID client identification no.
     * @param domain domain for which score must be computed
     * @param ind industry 
     * @param loc location 
     */
    
    public void updateAbsScores(HashMap<Double, Integer> scores, String name, int ID, 
    		String domain, String ind, String loc) {
    	
        if(clients.containsKey(ID))
        	clients.get(ID).computeAbsScore(scores, domain);
    	else 
           addClient(ind, loc, name, ID);
  
    }
    
    public void addClient(String name, String loc, String industry, int ID) {
  
    	Client c = new Client(industry, loc, name, ID);
    	clients.put(ID, c);
    	
    }
    
    /**
     * This function calculates the relative scores for all the clients
     * in the specified industry and location for a particular domain
     * 
     * @param ind industry
     * @param loc location
     * @param dom domain 
     * 
     */
    
    public void computeRelativeRanks(String ind, String loc, String dom) {
    	
    	// list of all clients in the specified industry and location
        ArrayList<Client> l = new ArrayList<Client>();
        for(Entry<Integer, Client> entry:clients.entrySet()) {
        	if(entry.getValue().getIndustry().equals(ind) && 
        	 entry.getValue().getLocation().equals(loc) 
        	 && entry.getValue().getAbsScore(dom) >= 0.0) 
        		l.add(entry.getValue()); // add the client to the list of companies that have this domain
        }
        if(l.size() == 1) {  // id only one client is found 
        	l.get(0).setRelativeScore(100.0, dom);
        	return;
        }
        else if(l.size() == 0) {
        	return;
        }
        // client ID -> number of companies that are scored higher mapping
        HashMap<Integer, Integer> rel = new HashMap<Integer, Integer>();
        // highest domain score
        Double max_score = Double.MIN_VALUE;
        // ID of client with the highest score
        int max_id = 0;
        // for each client compute the number of clients who have been scored higher
        for(Client c: l) {
			if(c.getAbsScore(dom) > max_score) {
				 max_score = c.getAbsScore(dom);
				 max_id = c.getID();
			}
        	for(Client d: l) {
        		if(c.getID() != d.getID()) {
        			if(c.getAbsScore(dom) < d.getAbsScore(dom)) {
        				if(rel.containsKey(c.getID()) == false)
        					rel.put(c.getID(), 1);
        				else
        					rel.put(c.getID(), rel.get(c.getID()) + 1);
        			}		
        		}
        	}
        }
        // client with the highest score has zero clients who have been score higher
        rel.put(max_id, 0);
        // rank -> client ID mapping
        int IDs[] = new int[l.size()];
        // rank -> rank mapping
        int Ls[] = new int[l.size()];

        for(Entry<Integer, Integer> entry:rel.entrySet()) {
        	IDs[entry.getValue()] = entry.getKey();
        	Ls[entry.getValue()] = rel.get(entry.getKey());
        }
        // compute the relative rankings iteratively
        getRelativeRanks(IDs, Ls, l.size(), dom);
    }
    
    /**
     * This function calculates the global/total relative scores for all the clients
     * in the specified industry and location 
     * 
     * @param ind industry
     * @param loc location
     * 
     */
    
    public void computeGlobalRelativeRanks(String ind, String loc) {
    	
    	// list of all clients in the specified industry and location
        ArrayList<Client> l = new ArrayList<Client>();
        for(Entry<Integer, Client> entry:clients.entrySet()) {
        	if(entry.getValue().getIndustry().equals(ind) && 
        	 entry.getValue().getLocation().equals(loc)) 
        		l.add(entry.getValue());
        }

        // client ID -> number of companies that are scored higher mapping
        HashMap<Integer, Integer> rel = new HashMap<Integer, Integer>();
        // highest total score
        Double max_score = Double.MIN_VALUE;
        // client with the highest score
        int max_id = 0;
        // for each client compute the number of clients who have been scored higher
        for(Client c: l) {
			if(c.getTotalScore() > max_score) {
				 max_score = c.getTotalScore();
				 max_id = c.getID();
			}
        	for(Client d: l) {
        		if(c.getID() != d.getID()) {
        			if(c.getTotalScore() < d.getTotalScore()) {
        				if(rel.containsKey(c.getID()) == false)
        					rel.put(c.getID(), 1);
        				else
        					rel.put(c.getID(), rel.get(c.getID()) + 1);
        			}		
        		}
        	}
        }
        // client with the highest score has zero clients who have been score higher
        rel.put(max_id, 0);
        // rank -> client ID mapping
        int IDs[] = new int[l.size()];
        // rank -> rank mapping
        int Ls[] = new int[l.size()];
        for(Entry<Integer, Integer> entry:rel.entrySet()) {
        	IDs[entry.getValue()] = entry.getKey();
        	Ls[entry.getValue()] = rel.get(entry.getKey());
        }
        // compute the relative rankings iteratively
        getRelativeRanks(IDs, Ls, l.size(), "");
    }
    
    /**
     * This function iteratively computes the relative ranks for all the 
     * clients listed in the ids[] array
     * 
     * @param ids client IDs
     * @param counts client ranks (based on domain score)
     * @param N number of clients 
     * @param d 
     * @param num_iter number of iterations 
     * @param dom domain 
     */
    
    private void getRelativeRanks(int[] ids, int[] counts, int N, String dom) {

        // initialize the scores to 1/N 
        double t1[] = new double[N];
        for(int i = 0; i < N; i++) {
        	t1[i] = (double)1/N;
        }
        // updated scores for the (i+1)th/next iteration
        double t2[] = new double[N];
        // initialize iterations
        int iter = 1;
        // begin iterations
        while(iter < num_iter) {
        	
            for(int i = 0; i < N; i++) {
            	
              Double temp = (double)(1-d)/N + (double)(d/N)*t1[0];
              for(int j = i+1; j < N; j++) 
            	   temp += d*(double)t1[j]/counts[j];
              t2[i] = temp;	
            }
            Double norm = diff_norm(t1, t2, N);
            // display iteration number and current norm (residual)
            System.out.println("iter: " + iter + " norm: "+ norm);
            System.out.println("");
            // update scores (t_i+1 = t_i)
            for(int k = 0; k < N; k++) {
            	t1[k] = t2[k];
            }
            iter++;
        	
        }
        // if the domain is specified update the relative scores for that domain 
        if(dom.equals("") == false) {
            for(int i = 0; i < N; i++)
        	    clients.get(ids[i]).setRelativeScore(t2[i]*100, dom);
        }
        // if a domain is not specified compute the global relative score
        else {
        	for(int i = 0; i < N; i++)
        		clients.get(ids[i]).setGlobalRelScore(t1[i]*100);
        }
    }
    
    /**
     * Returns the norm of two vectors x and y where
     * norm(x,y) = |x-y|
     * 
     * @param x first vector
     * @param y second vector 
     * @param N vector size
     * @return norm(x,y)
     * 
     */
    
    private Double diff_norm(double[] x, double[] y, int N) {
    	
    	Double norm = 0.0;
    	for(int i = 0; i < N; i++) 
    		norm += (x[i] - y[i])*(x[i] - y[i]);
    	norm = Math.sqrt(norm);
    	return norm;
    	
    }
    
    /**
     * This function will return the domain average for all the clients
     * in the specified industry and location
     * 
     * @param ind industry for which the domain average must be computed
     * @param dom domain for which the average is being computed
     * @param loc location of clients whose scores are being computed
     * @return domain average 
     */
    
    public Double getDomainAvg(String ind, String dom, String loc) {
    	
    	// initialize average
    	Double sum = 0.0; 
    	// number of clients initialized to zero
    	int n = 0;
    	for(Entry<Integer, Client> entry:clients.entrySet()) {
    		
    		if(entry.getValue().getIndustry().equals(ind) && 
    	     entry.getValue().getLocation().equals(loc) 
    	     && entry.getValue().getAbsScore(dom) >= 0.0) {
    			// take running sum of the domain scores
    			sum += entry.getValue().getAbsScore(dom);
    			n++;
    		}
    		
    	}
    	if(n == 0)
    		return 0.0;
    	return sum/n;   	
    }
    
    /**
     * This function will return the industry average for all the domains
     * under consideration for the given industry and location
     * 
     * @param ind
     * @param loc
     * @param domains list of domains
     * @return industry average 
     * 
     */
    
    public Double getIndustryAvg(String ind, String loc) {
    	
    	Double sum = 0.0;
    	int n = 0;
    	// for each client in the specified industry and location
    	for(Entry<Integer, Client> entry:clients.entrySet()) {
    		if(entry.getValue().getIndustry().equals(ind) && 
    		 entry.getValue().getLocation().equals(loc)) {
    			n++;
    			sum += entry.getValue().getTotalScore();
    		}
    	}
    	if(n == 0) 
    		return 0.0;
    	else 	
    	    return sum/n;	
    } 
    
    /**
     * This function returns true if the domain 'd' is common for all clients in 
     * industry and location specified
     * 
     * @param d domain
     * @param ind industry
     * @param loc location
     * @return true if the domain is common for all clients 
     * 
     */
    
    public Boolean commDomain(String d, String ind, String loc) {
    	// select all the clients in industry 'ind' and location 'loc'
    	for(Entry<Integer, Client> entry:clients.entrySet()) {
    		if(entry.getValue().getIndustry().equals(ind)
    		 && entry.getValue().getLocation().equals(loc)) {
    			if(entry.getValue().getAbsScore(d) <= 0.0)
    				return false;
    		}
    	}	
    	return true;
    }
}      
         
    	
    
    
  

