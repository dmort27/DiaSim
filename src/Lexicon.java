import java.util.List;
import java.util.HashMap;
import java.util.ArrayList; 

/**
 * class for representing the set of words (LexPhon instances) being simulated
 * in the language as it develops over time (diachronically) 
 * @author Clayton Marr
 *
 */


public class Lexicon {
	private Etymon[] theWordList; 
	// should contain not only the present words but also the absent ones
		// and the unattested ones if this is not the lexicon undergoing forward reconstruction
	// the etymon indices (functioning as the IDs) should be consistent across Lexicon instances
		// throughout a Simulation ! 
	
	public Lexicon(List<Etymon> theWords)
	{
		theWordList = new Etymon[theWords.size()]; 
		theWords.toArray(theWordList);
	}
	
	public Lexicon(Etymon[] theWords)
	{
		theWordList = new Etymon[theWords.length];
		for (int wi = 0; wi < theWords.length; wi++)
		{
			if (!UTILS.etymonIsPresent(theWords[wi]))
				theWordList[wi] = new PseudoEtymon(theWords[wi].print()) ;
			else
				theWordList[wi] = new Etymon(theWords[wi].getPhonologicalRepresentation(), 
						theWords[wi].isReconstructed());
		}
	}
	
	//retrieve a particular lexical phonology by its "ID" -- i.e. its index in theWordList
	// DerivationSimulation should construct instances of this class for the word set being simulated 
	// such that words with the same index represent different stages of the same word
	public Etymon getByID(int ind)
	{	return theWordList[ind]; 	}
	
	// maps each unique phone feat vect onto the number of times a phone with that feat vect 
		//occurs at least once in a word in the  lexicon
	// this returns frequency, not frequency RATE!
	public HashMap<String, Integer> getPhoneFrequenciesByWord()
	{
		HashMap<String, Integer> output = new HashMap<String, Integer>(); 
		for (Etymon lex : theWordList)
		{
			if (!UTILS.etymonIsPresent(lex))	continue; 
			
			List<SequentialPhonic> phonRep = lex.getPhonologicalRepresentation();
			List<SequentialPhonic> phonesAlreadySeen = new ArrayList<SequentialPhonic>(); 
			for (SequentialPhonic ph : phonRep)
			{
				if(ph.getType().equals("phone"))
				{
					if(!phonesAlreadySeen.contains(ph))
					{	String theFeatVect = ph.getFeatString(); 
						if (output.containsKey(theFeatVect))
							output.put(theFeatVect, output.get(theFeatVect)+1); 	
						else	output.put(theFeatVect, 1); 
						phonesAlreadySeen.add(ph);
					}
				}
			}
		}
		return output; 
	}
	
	public Etymon[] getWordList()
	{	return theWordList;	}
	
	// "Get changed" -- i.e. we get them by having their indexes be true.
	// used for writing the trajectory files as the lexicon moves forward through time.
	public boolean[] applyRuleAndGetChangedWords(SChange rule)
	{
		int wlLen = theWordList.length ;
		boolean[] wordsChanged = new boolean[wlLen]; 
		
		for( int wli = 0; wli < wlLen; wli++)
		{
			if(theWordList[wli].print().equals(UTILS.ABSENT_REPR))	
				wordsChanged[wli] = false;
			else if (theWordList[wli].print().equals(UTILS.UNATTD_GOLD_REPR))
				throw new RuntimeException("Alert: tried to implement a rule on a PseudoEtymon instance (index = "+wli+"). Check this.");
			
			if(theWordList[wli].applyRule(rule))	wordsChanged[wli] = true; 
			else	wordsChanged[wli] = false; 
		}
		return wordsChanged; 
	}
	
	//return list of all phones present in words of the lexicon
	public Phone[] getPhonemicInventory()
	{
		List<String> hitPhonesListStr = new ArrayList<String>(); 
		List<SequentialPhonic> phList = new ArrayList<SequentialPhonic>(); 
		for (Etymon theWord : theWordList)
		{	
			if (UTILS.etymonIsPresent(theWord))
			{
				List<SequentialPhonic> thePhones = theWord.getPhonologicalRepresentation(); 
				for (SequentialPhonic curPh : thePhones)
				{
					if(curPh.getType().equals("phone"))
					{
						if(!hitPhonesListStr.contains(curPh.print()))
						{
							hitPhonesListStr.add(curPh.print()); 
							phList.add(curPh);
		}}}}}
		
		
		int numPhones = phList.size(); 
		Phone[] output = new Phone[numPhones]; 
		for (int phi = 0; phi < numPhones; phi++)	output[phi] = new Phone(phList.get(phi)); 
		return output; 
	}
	
	// does not appear to be in use. 
	public boolean[] getPhonePresenceByEt(Phone ph)
	{
		boolean[] out = new boolean[theWordList.length];
		for (int wi = 0 ; wi < theWordList.length; wi++)
		{
			out[wi] = 
					UTILS.etymonIsPresent(theWordList[wi]) 
						? theWordList[wi].findPhone(ph) != -1 : false;  
		}
		return out;
	}
	
	//counts for each phoneme
	public HashMap<String,Integer> getPhonemeCounts()
	{
		HashMap<String,Integer> theMap = new HashMap<String,Integer>(); 
		for (Etymon lex: theWordList)
		{
			if (!lex.print().equals(UTILS.ABSENT_REPR))
				{SequentialPhonic[] thePhones = lex.getPhOnlySeq();
				for (SequentialPhonic curPh : thePhones)
				{
					if(!theMap.containsKey(curPh.print()))
						theMap.put(curPh.print(), 1);
					else	theMap.put(curPh.print(), theMap.get(curPh.print()) + 1);
				}
			}
		}
		return theMap;
	}
	
	//get number of times a particular sequence of phones occurs
	// for use in error analysis when predicting with a third, predictor, stage. 
	public int getPhoneSeqFrequency(List<Phone> targSeq)
	{
		int currSeqInd = 0, count = 0;
		for (Etymon lex : theWordList)
		{	
			if (!UTILS.etymonIsPresent(lex))	continue;
			
			List<SequentialPhonic> thePhones = lex.getPhonologicalRepresentation(); 
			for (SequentialPhonic curPh : thePhones)
			{
				if (curPh.getType().equals("phone"))
				{
					if(curPh.print().equals(targSeq.get(currSeqInd).print()))
					{
						currSeqInd++;
						if (currSeqInd == targSeq.size())
						{
							currSeqInd = 0;
							count++; 
						}
					}
					else
						currSeqInd = 0;
				}
			}	
		}	
		return count;
	}
	
	//sets all present etyma to be marked (*) as reconstructed. No need to set absent words as such -- nothing to "attest". 
		// reconstructions from forward or (supplied) backward reconstruction are to be compared in analysis
			// -- but etyma "not attested" (i.e., not available) in gold lexica are not.
	public void markEtymaReconstructed()
	{
		for (int eti = 0; eti < theWordList.length; eti++)
			if (!theWordList[eti].print().equals(UTILS.ABSENT_REPR))
				theWordList[eti].setReconstructed(true);
	}
	
	/**
	 * update which phones are absent (not yet in language or fell out of use) 
	 * based on whether they are absent or not in the latest column in lexicon file. 
	 * this is to be implemented on the lexicon that is undergoing forward reconstruction
	 * NOT upon a gold lexicon! 
	 * @param etymaInColumn -- array ([]) of Etymon objects derived (probably via DiachronicSimulator.parseLexPhon() 
	 * 		from String valued cells in a column of a lexicon file -- i.e. the forms associated for a certain stage
	 * 		which may be a proper Etymon, which should be used for attested (GOLD) forms to compare to
	 * 			or "--" which will make an absent etymon 
	 * 				 -- either not present yet in the language, or removed
	 * 			or ">*" which makes an unattested (but present) etymon  
	 */
	public void updateAbsence(Etymon[] etymaInColumn)
	{
		int theLen = etymaInColumn.length;
		if (theLen != theWordList.length)
			throw new RuntimeException("ERROR: mismatch on vocab size of current column ("
					+theLen+") and lexicon under reconstruction ("+theWordList.length+")! Investigate this!");
		
		for (int wi = 0 ; wi < theLen ; wi++)
		{	
			Etymon et_here = etymaInColumn[wi]; 
			
			// if the etymon is still absent in the lexicon being CFR-d, but present in the stage spec'd forms..
				// ... then insert it! 	
			if(theWordList[wi].print().equals(UTILS.ABSENT_REPR))
			{	
				if(UTILS.etymonIsPresent(et_here))						
				{		/** original condition  : !etymaInColumn[wi].print().equals(UTILS.ABSENT_REPR))
							*  -- however in practice allow "errors" 
							* whereby uses unattested indicator in lexicon files (currently ">*") 
							*  to "continue" the absence of an etymon */ 
					theWordList[wi] = 
						new Etymon(et_here.getPhonologicalRepresentation(),false);
			}}
		
			// remove from lexicon if it is present and we encounter indication it is now absent 
			if(et_here.print().equals(UTILS.ABSENT_REPR))
				if(!theWordList[wi].print().equals(UTILS.ABSENT_REPR))
					theWordList[wi] = new PseudoEtymon(UTILS.ABSENT_REPR); 
			
			// don't need to do anything for unattested ">*" entries -- point is that they keep things. 
		}
	}

	/**
	 * cloning constructor for Etymon objects outside of the Etymon class to avoid class hierarchy-related issues 
	 * 		that could arise if @param origin is in fact a PseudoEtymon
	 * @param origin -- Etymon object to clone
	 * @return
	 */
	public Etymon cloneLexeme (Etymon origin)
	{
		if (!UTILS.etymonIsPresent(origin))	return new PseudoEtymon(origin.print()); 
		
		Etymon dolly = new Etymon (
				new ArrayList<SequentialPhonic> (origin.getPhonologicalRepresentation()), origin.isReconstructed()); 
		
		dolly.setLemma(origin.getLemma());
		dolly.setLexClass(origin.getLexClass());
		dolly.resetMorphSynSpecs(origin.getMorphSynSpecs());
		dolly.setFrequency(origin.getFrequency());
		dolly.resetDomains(origin.getDomains());
		return dolly; 		
	}
	
	public Etymon cloneLexemeAt (int id)
	{	return cloneLexeme(getByID(id));	}
	
	/**
	 * @return number of etyma in this lexicon that are actually present
	 * 	if this is not the lexicon being operated upon, a result of zero 
	 * 		... will make it an effectively black stage lexicon
	 * 		... that is only used for insertion or removal of etyma.
	 */
	public int numObservedEtyma()
	{
		int cnt= theWordList.length; 
		for (Etymon lex: theWordList)
			if (UTILS.PSEUDO_ETYM_REPRS.contains(lex.print()))	cnt--; 
		return cnt; 
	}
	
	public int numAbsentEtyma()
	{
		int cnt = 0;
		for (Etymon lex: theWordList)
			if (lex.print().equals(UTILS.ABSENT_REPR))	cnt += 1;
		return cnt;
	}
	
	public int numUnattestedEtyma()
	{
		int cnt = 0;
		for (Etymon lex: theWordList)
			if (lex.print().equals(UTILS.UNATTD_GOLD_REPR))	cnt += 1;
		return cnt;
	}
	
	public int totalEtyma()	{	return theWordList.length; 	}
	
	// get the ID numbers of all etyma that are actually present. 
	public int[] IDsWithPresentEtyma() 
	{
		int[] ID_array = new int[this.numObservedEtyma()]; 
		int id_i = 0; 
		for (int et_i = 0; et_i < theWordList.length; et_i++)
		{	if (UTILS.etymonIsPresent(theWordList[et_i]))
			{
				ID_array[id_i] = et_i; 
				id_i++; 
		}}
		
		//guard rail. 
		if (ID_array[ID_array.length-1] == 0)
			throw new RuntimeException("Alert: there is some error in the counting of absent or unattested etyma at this stage, as ID_array was not filled. Investigate."); 
		
		return ID_array;
	}
}
