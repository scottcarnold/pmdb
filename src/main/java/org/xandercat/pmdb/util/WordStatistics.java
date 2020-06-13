package org.xandercat.pmdb.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.xandercat.pmdb.util.format.FormatUtil;

/**
 * Class for word statistics.
 * 
 * @author Scott Arnold
 */
public class WordStatistics {

	public static class WordCount implements Comparable<WordCount> {
		private String wordLc;
		private int count;
		public WordCount(String word) {
			this.wordLc = word.trim().toLowerCase();
		}
		protected void incrementCount() {
			this.count++;
		}
		public int getCount() {
			return count;
		}
		public String getWord() {
			return FormatUtil.titleCase(wordLc);
		}
		@Override
		public int compareTo(WordCount o) {
			return o.count - count;
		}
		public String toString() {
			return getWord() + " (" + count + ")";
		}
	}

	private Map<String, WordCount> wordCounts = new HashMap<String, WordCount>();
	
	public WordStatistics() {
	}
	
	/**
	 * Add words contained with provided string to the collected statistics.  Individual words within the 
	 * provided string can be separated by white space or commas.
	 * 
	 * @param words  string containing words to collect statistics on
	 */
	public void addWords(String words) {
		String[] wordsArray = words.split("\\s+");
		for (int i=0; i<wordsArray.length; i++) {
			String[] commaSplitWords = wordsArray[i].split(",");
			for (String word : commaSplitWords) {
				addWord(word);
			}
		}
	}
	
	private void addWord(String word) {
		word = word.trim().toLowerCase();
		WordCount wordCount = wordCounts.get(word);
		if (wordCount == null) {
			wordCount = new WordCount(word);
			wordCounts.put(word, wordCount);
		}
		wordCount.incrementCount();
	}
	
	/**
	 * Returns list of all word counts in sorted order from most frequent to least frequent.
	 * 
	 * @return list of all word counts from most frequent to least frequent
	 */
	public List<WordCount> getWordCounts() {
		return wordCounts.values().stream().sorted().collect(Collectors.toList());
	}

	/**
	 * Returns list of top word counts in sorted order.
	 * 
	 * @param top number of results to include
	 * 
	 * @return list of top word counts
	 */
	public List<WordCount> getTopWordCounts(int top) {
		if (wordCounts.size() == 0) {
			return Collections.emptyList();
		}
		return getWordCounts().subList(0, Math.min(top, wordCounts.size()));
	}
	
	/**
	 * Returns list of bottom word counts in sorted order.
	 * 
	 * @param bottom number of results to include
	 * 
	 * @return list of bottom word counts
	 */
	public List<WordCount> getBottomWordCounts(int bottom) {
		if (wordCounts.size() == 0) {
			return Collections.emptyList();
		}
		return getWordCounts().subList(Math.max(0, wordCounts.size()-bottom), wordCounts.size());
	}
	
	/**
	 * Returns list of word counts within collected statistics for words contained within the provided string of words.
	 * Provided string of words can have words separated by white space or commas.
	 * 
	 * @param words  words to get word counts for from the collected statistics
	 * 
	 * @return list of word counts for words within the provided words string
	 */
	public List<WordCount> getWordCountsForWords(String words) {
		List<WordCount> specificWordCounts = new ArrayList<WordCount>();
		if (FormatUtil.isNotBlank(words)) {
			String[] wordsArray = words.split("\\s+");
			for (int i=0; i<wordsArray.length; i++) {
				String[] commaSplitWords = wordsArray[i].split(",");
				for (String word : commaSplitWords) {
					WordCount wordCount = wordCounts.get(word.toLowerCase());
					if (wordCount != null) {
						specificWordCounts.add(wordCount);
					}
				}
			}
			Collections.sort(specificWordCounts);
		}
		return specificWordCounts;
	}
}
