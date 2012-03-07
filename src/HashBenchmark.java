import java.util.*;
import dk.brics.automaton.*;

public class HashBenchmark {
    public static void main(String[] args) {
	Set<RunAutomaton> auts = new HashSet<RunAutomaton>();
	RegExp re = new RegExp("TEST");
	Automaton a = re.toAutomaton();
	RunAutomaton ra = new RunAutomaton(a);
	HashDispatcher.dispatch(ra, 8);
	Runnable r = new Runnable() {
		public void run() {
		    try {
			Thread.sleep(10000);
		    } catch (InterruptedException ie) {
		    }
		    long tps = HashDispatcher.getKtps();
		    HashDispatcher.killAll();
		    System.out.println(tps + " kT/s over ~10 seconds.");
		    String check = Hash.getTripCode("password");
		    boolean werks = check.equals("ozOtJW9BFA");
		    System.out.println("Sanity check: " + werks);
		}
	    };
	Thread t = new Thread(r);
	t.start();
    }
}