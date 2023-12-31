/*
 * Copyright 2008, 2009 Daniël de Kok
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.langkit.tagger.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Model {
	private static class NGrams {
		private final Map<String, Integer> tagNumbers;
		private final Map<Integer, String> numberTags;
		private final Map<UniGram, Integer> uniGramFreqs;
		private final Map<BiGram, Integer> biGramFreqs;
		private final Map<TriGram, Integer> triGramFreqs;

		public NGrams(Map<String, Integer> tagNumbers, Map<Integer, String> numberTags,
				Map<UniGram, Integer> uniGramFreqs, Map<BiGram, Integer> biGramFreqs,
				Map<TriGram, Integer> triGramFreqs) {
			this.tagNumbers = tagNumbers;
			this.numberTags = numberTags;
			this.uniGramFreqs = uniGramFreqs;
			this.biGramFreqs = biGramFreqs;
			this.triGramFreqs = triGramFreqs;
		}
	}

	private Model(Map<String, Map<Integer, Integer>> wordTagFreqs, Map<String, Integer> tagNumbers,
			Map<Integer, String> numberTags, Map<UniGram, Integer> uniGramFreqs,
			Map<BiGram, Integer> biGramFreqs, Map<TriGram, Integer> triGramFreqs) {	
		for (Entry<String, Map<Integer, Integer>> lexiconEntry: wordTagFreqs.entrySet()) {
			wordTagFreqs.put(lexiconEntry.getKey(),
				Collections.unmodifiableMap(lexiconEntry.getValue()));
		}
		
		d_wordTagFreqs = Collections.unmodifiableMap(wordTagFreqs);
		
		d_tagNumbers = Collections.unmodifiableMap(tagNumbers);
		d_numberTags = Collections.unmodifiableMap(numberTags);
		d_uniGramFreqs = Collections.unmodifiableMap(uniGramFreqs);
		d_biGramFreqs = Collections.unmodifiableMap(biGramFreqs);
		d_triGramFreqs = Collections.unmodifiableMap(triGramFreqs);
	}

	/**
	 * Returns the model bigram frequencies.
	 * @return
	 */
	public Map<BiGram, Integer> biGrams() {
		return d_biGramFreqs;
	}

	/**
	 * Returns the model word/tag frequencies.
	 * @return
	 */
	public Map<String, Map<Integer, Integer>> lexicon() {
		return d_wordTagFreqs;
	}
	
	public Map<Integer, String> numberTags() {
		return d_numberTags;
	}
	
	/**
	 * Read a model from files, and construct a <i>Model</i> instance. The
	 * model should be stored in two text files. The first text file should
	 * contain the word/tag frequencies. One line is used per word, each line
	 * starts with the word, followed by tags and their frequencies. For
	 * example:
	 * <p>
	 * <tt><pre>
	 * advised VBN 13 VBD 11
	 * grotesque JJ 5
	 * </pre></tt>
	 * </p>
	 * <p>
	 * The second text file contains the uni/bi/trigram frequencies. Each
	 * line lists an n-gram, and its frequencies. For example:
	 * </p>
	 * <p>
	 * <tt><pre>
	 * DT 6230
	 * BEN PN$ 2
	 * </pre></tt>
	 * </p>
	 * <p>
	 * Grams with a different <i>n</i> can be mixed freely.
	 * @return The constructed <i>Model</i> instance.
	 */	
	public static Model readModel(BufferedReader wordTagFreqReader,
			BufferedReader nGramReader) throws IOException {
		NGrams nGrams = readNGrams(nGramReader);
		Map<String, Map<Integer, Integer>> wordTagFreqs =
			readWordTagFreqs(wordTagFreqReader, nGrams.tagNumbers);
		
		return new Model(wordTagFreqs, nGrams.tagNumbers, nGrams.numberTags,
			nGrams.uniGramFreqs, nGrams.biGramFreqs, nGrams.triGramFreqs);
	}

	private static NGrams readNGrams(BufferedReader reader) throws IOException {
		Map<String, Integer> tagNumbers = new HashMap<String, Integer>();
		Map<Integer, String> numberTags = new HashMap<Integer, String>();
		Map<UniGram, Integer> uniGramFreqs = new HashMap<UniGram, Integer>();
		Map<BiGram, Integer> biGramFreqs = new HashMap<BiGram, Integer>();
		Map<TriGram, Integer> triGramFreqs = new HashMap<TriGram, Integer>();
	
		int tagNumber = 0;
		String line = null;
		while ((line = reader.readLine()) != null) {
			String[] lineParts = line.split("\\s+");
	
			int freq = Integer.parseInt(lineParts[lineParts.length - 1]);
	
			if (lineParts.length == 2) {
				tagNumbers.put(lineParts[0], tagNumber);
				numberTags.put(tagNumber, lineParts[0]);
				uniGramFreqs.put(new UniGram(tagNumber), freq);
				++tagNumber;
			}
			else if (lineParts.length == 3)
				biGramFreqs.put(new BiGram(tagNumbers.get(lineParts[0]),
					tagNumbers.get(lineParts[1])), freq);
			else if (lineParts.length == 4)
				triGramFreqs.put(new TriGram(tagNumbers.get(lineParts[0]),
					tagNumbers.get(lineParts[1]), tagNumbers.get(lineParts[2])), freq);
	
		}
	
		return new NGrams(tagNumbers, numberTags, uniGramFreqs, biGramFreqs, triGramFreqs);
	}

	private static Map<String, Map<Integer, Integer>> readWordTagFreqs(BufferedReader reader,
			Map<String, Integer> tagNumbers)
	throws IOException {
		Map<String, Map<Integer, Integer>> wordTagFreqs = new HashMap<String, Map<Integer, Integer>>();
	
		String line = null;
		while ((line = reader.readLine()) != null) {
			String[] lineParts = line.split("\\s+");
			String word = lineParts[0];
	
			// Make a lexicon entry for this word represented by this line.
			wordTagFreqs.put(word, new HashMap<Integer, Integer>());
	
			for (int i = 1; i < lineParts.length; i += 2) {
				int p = Integer.parseInt(lineParts[i + 1]);
				wordTagFreqs.get(word).put(tagNumbers.get(lineParts[i]), p);
			}			
		}
	
		return wordTagFreqs;
	}
	
	public Map<String, Integer> tagNumbers() {
		return d_tagNumbers;
	}

	/**
	 * Returns the model trigram frequencies.
	 * @return
	 */
	public Map<TriGram, Integer> triGrams() {
		return d_triGramFreqs;
	}
	
	/**
	 * Returns the model unigram frequencies.
	 * @return
	 */
	public Map<UniGram, Integer> uniGrams() {
		return d_uniGramFreqs;
	}
	
	private final Map<String, Map<Integer, Integer>> d_wordTagFreqs;
	private final Map<String, Integer> d_tagNumbers;
	private final Map<Integer, String> d_numberTags;
	private final Map<UniGram, Integer> d_uniGramFreqs;
	private final Map<BiGram, Integer> d_biGramFreqs;
	private final Map<TriGram, Integer> d_triGramFreqs;
}
