package edu.smith.cs.csc212.p8;

import java.io.File;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class CheckSpelling {
	/**
	 * Read all lines from the UNIX dictionary.
	 * @return a list of words!
	 */
	public static List<String> loadDictionary() {
		long start = System.nanoTime();
		List<String> words;
		try {
			words = Files.readAllLines(new File("src/main/resources/words").toPath());
		} catch (IOException e) {
			throw new RuntimeException("Couldn't find dictionary.", e);
		}
		long end = System.nanoTime();
		double time = (end - start) / 1e9;
		System.out.println("Loaded " + words.size() + " entries in " + time +" seconds.");
		return words;
	}
	
	/**
	 * This method looks for all the words in a dictionary.
	 * @param words - the "queries"
	 * @param dictionary - the data structure.
	 */
	public static void timeLookup(List<String> words, Collection<String> dictionary) {
		long startLookup = System.nanoTime();
		
		int found = 0;
		for (String w : words) {
			if (dictionary.contains(w)) {
				found++;
			}
		}
		
		long endLookup = System.nanoTime();
		double fractionFound = found / (double) words.size();
		double timeSpentPerItem = (endLookup - startLookup) / ((double) words.size());
		int nsPerItem = (int) timeSpentPerItem;
		System.out.println(dictionary.getClass().getSimpleName()+": Lookup of items found="+fractionFound+" time="+nsPerItem+" ns/item. \n");
	}
	
	/**
	 * This method creates a mixed data set.
	 * @param yesWords - list of words from the dictionary.
	 * @param numSamples - length of output list.
	 * @param fractionYes - fraction of words from the original dictionary.
	 * @return "numSamples" words in an output list, with fractionYes*numSamples of original words and (1-fractionYes)*numSamples fake words. 
	 */
	public static List<String> createMixedDataset(List<String> yesWords, int numSamples, double fractionYes){
		List<String> newList= new ArrayList<>();
		for (int i =0; i<fractionYes*numSamples; i++) {
			newList.add(yesWords.get(i));
		}
		for (int j= (int)fractionYes*numSamples; j<numSamples; j++) {
			String fakeWord= yesWords.get(j)+"zzz";
			newList.add(fakeWord);
		}
		return newList;
	}
	
	/**
	 * This method takes a text file and splits its contents into a list of of words.
	 * @return a list of words.
	 */
	public static List<String> wordsInBook(){
		List<String> bookWords;
		List<String> listWords= new ArrayList<>();
		try {
			bookWords = Files.readAllLines(new File("src/main/resources/Frankenstein.txt").toPath());
		} catch (IOException e) {
			throw new RuntimeException("Couldn't find book.", e);
		}
		for (String line: bookWords) {
			listWords.addAll(WordSplitter.splitTextToWords(line));
		}
		return listWords;
		
	}
	
	/**
	 * This method checks whether a word in a book is also in the dictionary or not.
	 * @param  dictionary - the data structure.
	 * @param listWords - a list of all the words in the book to be compared.
	 * @return ratio of words in the book but not in the dictionary.  
	 */
	public static double spellCheck(Collection<String> dictionary, List<String> listWords) {
		
		List<String> inDict= new ArrayList<>();
		List<String> notInDict= new ArrayList<>();
		
		for (String w :listWords ) {
			if (dictionary.contains(w)) {
				inDict.add(w);
			} else {
				notInDict.add(w);
			}
		}
		
		double nIDS = notInDict.size();
		double totalSize= listWords.size();
		double ratio = nIDS/totalSize;
		
		System.out.println("Some 'mis-spelled' words: \n" );
		for (int i=50; i<61; i++) {
			System.out.println(notInDict.get(i));
		}
		
		return ratio;
		
		
	}
	
	
	public static void main(String[] args) {
		// --- Load the dictionary.
		List<String> listOfWords = loadDictionary();
		
		
		// --- Create a bunch of data structures for testing:
		long startT = System.nanoTime();
		TreeSet<String> treeOfWords = new TreeSet<>(listOfWords);
//		TreeSet<String> treeOfWords = new TreeSet<>();
//		for (String w : listOfWords) {
//			treeOfWords.add(w);
//		}
		long endT = System.nanoTime();
		double timeT = (endT - startT)/ 1e9;
		double timeTI = (endT - startT)/treeOfWords.size();
		System.out.println( "\n Time taken to fill TreeSet: "+timeT  +" seconds.");
		System.out.println( " Per-item insertion time for TreeSet: "+timeTI  +" ns/items. \n");
		
		long startH = System.nanoTime();
		HashSet<String> hashOfWords = new HashSet<>(listOfWords);
//		HashSet<String> hashOfWords = new HashSet<>();
//		for (String w : listOfWords) {
//			hashOfWords.add(w);
//		}
		long endH = System.nanoTime();
		double timeH = (endH - startH)/ 1e9 ;
		double timeHI = (endH - startH)/hashOfWords.size();
		System.out.println( " Time taken to fill HashSet: "+timeH +" seconds.");
		System.out.println( " Per-item insertion time for HashSet: "+timeHI +" ns/items. \n");
		
		long startS = System.nanoTime();
		SortedStringListSet bsl = new SortedStringListSet(listOfWords);
		long endS = System.nanoTime();
		double timeS = (endS - startS)/ 1e9 ;
		double timeSI = (endS - startS)/ bsl.size();
		System.out.println( " Time taken to fill SortedStringList: "+timeS +" seconds.");
		System.out.println( " Per-item insertion time for SortedStringList: "+timeSI +" ns/items. \n");
		
		long startC = System.nanoTime();
		CharTrie trie = new CharTrie();
		for (String w : listOfWords) {
			trie.insert(w);
		}
		long endC = System.nanoTime();
		double timeC = (endC - startC)/ 1e9 ;
		double timeCI = (endC - startC)/ trie.size() ;
		System.out.println( " Time taken to fill CharTrie: "+timeC +" seconds.");
		System.out.println( " Per-item insertion time for CharTrie: "+timeCI +" ns/items. \n");
		
		long startL = System.nanoTime();
		LLHash hm100k = new LLHash(100000);
		for (String w : listOfWords) {
			hm100k.add(w);
		}
		long endL = System.nanoTime();
		double timeL = (endL - startL)/ 1e9 ;
		double timeLI = (endL - startL)/ hm100k.size() ;
		System.out.println( " Time taken to fill LLHash: "+timeL +" seconds.");
		System.out.println( " Per-item insertion time for LLHash: "+timeLI +" ns/items. \n");
		

		
		// --- Make sure that every word in the dictionary is in the dictionary:
		timeLookup(listOfWords, treeOfWords);
		timeLookup(listOfWords, hashOfWords);
		timeLookup(listOfWords, bsl);
		timeLookup(listOfWords, trie);
		timeLookup(listOfWords, hm100k);
		
		for (int j=0; j<2; j++) {
			System.out.println("Warm-up, j="+j+" \n");
			for (int i=0; i<=10; i++) {
				double fraction = i / 10.0;
				// --- Create a dataset of mixed hits and misses:
				List<String> hitsAndMisses = createMixedDataset(listOfWords, 10000, fraction);
				System.out.println("For i="+i+ " \n");
					
				System.out.println("TreeSet");
				timeLookup(hitsAndMisses, treeOfWords);
				
				System.out.println("HashSet");
				timeLookup(hitsAndMisses, hashOfWords);
				
				System.out.println("SortedStringListSet");
				timeLookup(hitsAndMisses, bsl);
				
				System.out.println("CharTrie");
				timeLookup(hitsAndMisses, trie);
				
				System.out.println("LLHash");
				timeLookup(hitsAndMisses, hm100k);
			}
		}
		
		// --- linear list timing:
		// Looking up in a list is so slow, we need to sample:
		System.out.println("Start of list: ");
		timeLookup(listOfWords.subList(0, 1000), listOfWords);
		System.out.println("End of list: ");
		timeLookup(listOfWords.subList(listOfWords.size()-100, listOfWords.size()), listOfWords);
		
	
		// --- print statistics about the data structures:
		System.out.println("Count-Nodes: "+trie.countNodes()+" \n");
		System.out.println("Count-Items: "+hm100k.size());

		System.out.println("Count-Collisions[100k]: "+hm100k.countCollisions());
		System.out.println("Count-Used-Buckets[100k]: "+hm100k.countUsedBuckets());
		System.out.println("Load-Factor[100k]: "+hm100k.countUsedBuckets() / 100000.0+" \n");

		
		System.out.println("log_2 of listOfWords.size(): "+listOfWords.size()+" \n");
		
		
		
		//List of words in the book
		List<String> bookWords = wordsInBook();
		
		//ratio of the 'mis-spelled' words 
		double misSpelled = spellCheck(bsl,bookWords);
		System.out.println("\nThe ratio of 'mis-spelled' words:" +misSpelled+ " \n");
	    
	    System.out.println("TreeSet");
	    timeLookup(bookWords, treeOfWords);
	    
	    System.out.println("HashSet");
	    timeLookup(bookWords, hashOfWords);
	    
	    System.out.println("SortedStringListSet");
	    timeLookup(bookWords, bsl);
	    
	    System.out.println("CharTrie");
	    timeLookup(bookWords, trie);
	    
	    System.out.println("LLHash");
	    timeLookup(bookWords, hm100k);
		
	    System.out.println("Done!");
		
		
	}
}
