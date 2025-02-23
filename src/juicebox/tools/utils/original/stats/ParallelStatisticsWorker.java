/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2011-2021 Broad Institute, Aiden Lab, Rice University, Baylor College of Medicine
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */

package juicebox.tools.utils.original.stats;

import juicebox.data.ChromosomeHandler;
import juicebox.tools.utils.original.AlignmentPairLong;
import juicebox.tools.utils.original.AsciiPairIterator;
import juicebox.tools.utils.original.Chunk;
import juicebox.tools.utils.original.FragmentCalculation;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParallelStatisticsWorker extends StatisticsWorker {
    public ParallelStatisticsWorker(String siteFile, List<String> statsFiles, List<Integer> mapqThresholds,
                                    String ligationJunction, String inFile, ChromosomeHandler localHandler,
                                    FragmentCalculation chromosomes) {
        super(siteFile, statsFiles, mapqThresholds, ligationJunction, inFile, localHandler, chromosomes);
    }

    public void infileStatistics(Chunk chunk) {
        //read in infile and calculate statistics
        try {
            //create index for AsciiIterator
            Map<String, Integer> chromosomeIndexes = new HashMap<>();
            for (int i = 0; i < localHandler.size(); i++) {
                chromosomeIndexes.put(localHandler.getChromosomeFromIndex(i).getName(), i);
            }
            //iterate through input file
            AsciiPairIterator files = new AsciiPairIterator(inFile, chromosomeIndexes, chunk, localHandler);
            if (files.hasNext()) {
                AlignmentPairLong firstPair = (AlignmentPairLong) files.next();
                String previousBlock = firstPair.getChr1() + "_" + firstPair.getChr2();
                processSingleEntry(firstPair, previousBlock, true);
                while (files.hasNext()) {
                    AlignmentPairLong pair = (AlignmentPairLong) files.next();
                    if (processSingleEntry(pair, previousBlock, true)) {
                        break;
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
