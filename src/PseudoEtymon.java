import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Class for representing one of two cases...
 *  	ABSENT -- a word that has either not entered the vocabulary yet, or has fallen out of usage.
 *				indicator in lexicon file: "--" (as of July 2023) 
 *				if a previously present/inherited word is indicated as absent, it will be REMOVED
 *				if it is absent from the beginning, 
 *					it remains such until phonological material is provided in a later column, 
 *						and this will be replaced with a "real" Etymon instance.
 *		UNATTESTED (GOLD) -- for use ONLY in gold stages or stages for insertion/removal of vocab
 *					(this was the case even before "reconstructed *" was added to the parent Etymon class!)
 *			this means the form continues to be inherited, but is not attested at the stage
 *				indicator in lexicon file: ">*" (as of July 2023)
 *			NOTE THAT THIS IS TO BE DISTINGUISHED FROM NOT ATTESTED IN INTERNAL USAGE IN Etymon (as inherited) which just governs display! 
 * 				(all of these are treated as "attested" because we don't prefix them with an extra asterisk!) 
 * 		variables in UTILS relevant: 	public final static String ABSENT_INDIC = "--", ABSENT_REPR = "{ABSENT}"; 
			public final static String UNATTD_GOLD_INDIC = ">*", UNATTD_GOLD_REPR = "{UNATTESTED}"; 
				// the -INDIC items are the strings used in lexicon files provided by the user and processed by the system
				// whereas the -REPR items are the internal representation within the Etymon subclasses.
					// the latter are for unattested GOLD lexicon items -- i.e. those not included in diagnostic analysis
					// i.e. NOT unattested reconstructions!
				public final static List<String> PSEUDO_ETYM_REPRS = Arrays.asList(ABSENT_REPR, UNATTD_GOLD_REPR); 
			also hte method UTILS.etymonIsPresent
 * @author Clayton Marr
 */
public class PseudoEtymon extends Etymon {
	
	private String representation; 
	
	public	PseudoEtymon(String repr)
	{
		super(new ArrayList<SequentialPhonic>(),false);
		this.representation = ""+repr; 
		
		// guard rail: 
		if (!UTILS.PSEUDO_ETYM_REPRS.contains(representation))
			throw new RuntimeException("Alert: illegal typing of PseudoEtymon instance: '"+repr+"'. Investigate this."); 
	}
	
	public List<SequentialPhonic> getPhonologicalRepresentation()	{	return null;	}
	
	//TODO consider overriding phRepLen and/or getNumPhones, perhaps with an error/exception
	
	public int findPhone(Phone ph)	{	return -1;	}
	
	public boolean applyRule(SChange sch)	{	return false;	}
	
	public String toString()	{	return representation;	}
	
	public String print() {		return representation;	}

	public int findSequence(RestrictPhone[] sequence)	{	
		System.out.println("Warning: searching for sequence in a PseudoEtymon...");
		//TODO consider throwing error.
		return -1;  
	}
	public int rFindSequence(RestrictPhone[] sequence)	{	
		System.out.println("Warning: searching backward for sequence in a PseudoEtymon...");
		//TODO consider throwing error.
		return -1;  
	}
	
	
	/**  the following mutators for morpohlogical info are legal for unattested etyma, 
		* but not for absent etyma! 
		* this for the edge case that an etymon is unattested but we know or want to test a scenario where it changed 
		* something morphologically (likely as part of some larger phenomena)*/ 
	public void setLemma (String lemma) 
	{
		checkForMutateAbsentError("the lemma id", lemma);
		this.lemma = ""+lemma; 
	}
	
	public void setLexClass (String lex_class) 
	{
		checkForMutateAbsentError("the morpholexical class", lex_class); 
		this.lexClass = ""+lex_class; 
	}
	public void setMorphSynSpec (String feat, String val) 
	{
		checkForMutateAbsentError("the morphosyntactic feature '"+feat+"'", val); 
		this.morphSynSpecs.put(feat, val); 
	}
	public void removeMorphSynSpec (String feat)
	{
		checkForMutateAbsentError("the morphosyntactic feature '"+feat+"'", "null"); 
		this.morphSynSpecs.remove(feat); 
	}
	public void resetMorphSynSpecs ( HashMap<String, String> newSpecs)
	{
		checkForMutateAbsentError("the morphosyntactic feature mapping", 
				Arrays.asList(newSpecs).toString()); 
		this.morphSynSpecs = new HashMap<String,String>(newSpecs); 
	}
	public void setFrequency(double freq)
	{
		checkForMutateAbsentError("the token frequency", ""+freq); 
		this.frequency = freq; 
	}
	public void addDomain(String domain)
	{
		checkForMutateAbsentError("the domain set", ""+Arrays.asList(this.domains)+"+"+domain); 
		this.domains.add(domain); 
	}
	public void removeDomain(String domain)
	{
		checkForMutateAbsentError("the semantic domain '"+domain+"'", "removed"); 
		this.domains.remove(domain); 
	}
	public void resetDomains (List<String> newDomains)
	{
		checkForMutateAbsentError("the semantic domain set", ""+Arrays.asList(newDomains)); 
		this.domains = new ArrayList<String>(newDomains);
	}
	
	private void checkForMutateAbsentError (String param, String target)
	{
		if (representation.equals(UTILS.ABSENT_REPR))
			throw new RuntimeException( "Alert: tried to set "+param+" (to '"+target+"') "
					+ "an etymon that is currently absent! Check this.");	 
	}
}