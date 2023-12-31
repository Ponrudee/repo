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

package org.langkit.tagger.tagger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.langkit.tagger.data.Model;
import org.langkit.tagger.data.TriGram;
import org.langkit.tagger.languagemodel.LanguageModel;
import org.langkit.tagger.wordhandler.WordHandler;

/**
 * Instances of this class can be used to tag sequences using a Hidden
 * Markov Model (HMM).
 */
public class HMMTagger {
	private final Model d_model;
	private final WordHandler d_wordHandler;
	private final LanguageModel d_languageModel;
	private final double d_beamFactor;
	
	private static class TagMatrixEntry {
		public int tag;
		public Map<TagMatrixEntry, Double> probs =
			new HashMap<TagMatrixEntry, Double>();
		public Map<TagMatrixEntry, TagMatrixEntry> bps =
			new HashMap<TagMatrixEntry, TagMatrixEntry>();
		//public double prob;
		//public TagMatrixEntry bp;

		public TagMatrixEntry(int tag) {
			this.tag = tag;
		}
	}
	
	/**
	 * Wrapper class for a sequence, and its associated probability.
	 */
	public static class Sequence
	{		
		public Sequence(List<Integer> sequence, double logProb, Model model) {
			d_sequence = sequence;
			d_logProb = logProb;
			d_numberTags = model.numberTags();
		}

		/**
		 * Return the sequence.
		 * @return
		 */
		public List<String> sequence() {
			List<String> tagSequence = new ArrayList<String>(d_sequence.size());
			
			for (Integer tagNumber: d_sequence) {
				tagSequence.add(d_numberTags.get(tagNumber));
			}
			
			return tagSequence;
		}

		/**
		 * Return the probability of the sequence in log space.
		 * @return
		 */
		public double logProb() {
			return d_logProb;
		}

		private final List<Integer> d_sequence;
		private final double d_logProb;
		private final Map<Integer, String> d_numberTags;
	}
	
	/**
	 * Construct an <i>HMMTagger</i> instance.
	 * @param wordHandler The handler to be used for retrieving the probabilities
	 * 	of a word given a tag.
	 * @param languageModel The language model.
	 * @param beamFactor A beam factor, states with a probability lower than the
	 * 	most probably state divided by this factor will be discarded.
	 */
	public HMMTagger(Model model, WordHandler wordHandler, LanguageModel languageModel,
			double beamFactor) {
		d_wordHandler = wordHandler;
		d_model = model;
		d_languageModel = languageModel;
		d_beamFactor = Math.log(beamFactor);
	}
	
	/**
	 * Extract the most probable sequence from a tag matrix.
	 * @param tagMatrix
	 * @return
	 */
	public static Sequence highestProbabilitySequence(List<List<TagMatrixEntry>> tagMatrix,
			Model model)
	{
		// Find the most probably final state.
		double highestProb = Double.NEGATIVE_INFINITY;
		TagMatrixEntry tail = null;
		TagMatrixEntry beforeTail = null;
		
		List<TagMatrixEntry> lastColumn = tagMatrix.get(tagMatrix.size() - 1);
		
		// Find the most probable state in the last column.
		for (TagMatrixEntry entry: lastColumn) {
			for (Map.Entry<TagMatrixEntry, Double> probEntry: entry.probs.entrySet()) {
				if (probEntry.getValue() > highestProb) {
					highestProb = probEntry.getValue();
					tail = entry;
					beforeTail = probEntry.getKey();
				}
			}
		}
		
		List<Integer> tagSequence = new ArrayList<Integer>(tagMatrix.size());
		
		for (int i = 0; i < tagMatrix.size(); ++i) {
			tagSequence.add(tail.tag);			
			
			if (beforeTail != null) {
				TagMatrixEntry tmp = tail.bps.get(beforeTail);
				tail = beforeTail;
				beforeTail = tmp;
			}
		}

		Collections.reverse(tagSequence);
		
		return new Sequence(tagSequence, highestProb, model);		
	}

	/**
	 * Tag a sentence.
	 * @param sentence The actual sentence with two start markers, and preferably
	 * 	one end marker.
	 * @return
	 */
	public List<List<TagMatrixEntry>> viterbi(List<String> sentence) {
		List<List<TagMatrixEntry>> tagMatrix = new ArrayList<List<TagMatrixEntry>>(sentence.size());

		int startTag = d_model.tagNumbers().get(sentence.get(0));

		// Prepare initial matrix entries;
		TagMatrixEntry firstEntry = new TagMatrixEntry(startTag);
		tagMatrix.add(new ArrayList<TagMatrixEntry>(1));
		tagMatrix.get(0).add(firstEntry);
	
		tagMatrix.add(new ArrayList<TagMatrixEntry>(1));
		tagMatrix.get(1).add(new TagMatrixEntry(startTag));
		tagMatrix.get(1).get(0).probs.put(firstEntry, 0.0);
		tagMatrix.get(1).get(0).bps.put(firstEntry, null);
		
		double beam = 0.0;
		
		// Loop through the tokens.
		for (int i = 2; i < sentence.size(); ++i) {
			double columnHighestProb = Double.NEGATIVE_INFINITY;

			tagMatrix.add(new ArrayList<TagMatrixEntry>());

			for (Entry<Integer, Double> tagEntry:
					d_wordHandler.tagProbs(sentence.get(i)).entrySet()) {
				TagMatrixEntry newEntry = new TagMatrixEntry(tagEntry.getKey());
				
				// Loop over all possible trigrams
				for (TagMatrixEntry t2: tagMatrix.get(i - 1)) {
					double highestProb = Double.NEGATIVE_INFINITY;
					TagMatrixEntry highestProbBp = null;

					for (Map.Entry<TagMatrixEntry, Double> t1Entry: t2.probs.entrySet()) {
						if (t1Entry.getValue() < beam)
							continue;
						
						TriGram curTriGram = new TriGram(t1Entry.getKey().tag, t2.tag,
							tagEntry.getKey());

						double triGramProb = d_languageModel.triGramProb(curTriGram);
						double prob = triGramProb + tagEntry.getValue() + t1Entry.getValue();

						if (prob > highestProb)
						{
							highestProb = prob;
							highestProbBp = t1Entry.getKey();
						}
					}
					
					newEntry.probs.put(t2, highestProb);
					newEntry.bps.put(t2, highestProbBp);

					if (highestProb > columnHighestProb)
						columnHighestProb = highestProb;
				}

				tagMatrix.get(i).add(newEntry);
			}
			
			beam = columnHighestProb - d_beamFactor;
		}
		
		return tagMatrix;
	}
}
