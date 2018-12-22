/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Hindemith.RhythmModule;

import Hindemith.RhythmModule.RhythmModule;

/**
 *
 * @author alyssa
 */

import org.jfugue.*;
import java.util.Random;

public class CanonExtObjRefStraightPatternGenerator  implements RhythmModule{
    @Override
    public  Pattern[] generate(int fragmentLength, int numberOfVoices) {
		Random roll = new Random();
		int patternIndex;
                int tempo = 80;
                int measure;
		Pattern VoiceArray [] = new Pattern[numberOfVoices];

		
		//System.out.println("starting with # of bars = to " + fragmentLength);
		for (int voice = 0; voice < numberOfVoices; voice++) {     //for each voice
                    //System.out.println("voice " + voice);
                    Pattern fragPattern = new Pattern();
                    Pattern hrPattern = new Pattern();
                    fragPattern.addElement(new Tempo(tempo));
                    int canon_length = fragmentLength * numberOfVoices;
                    //Build Harmonic Rhythm for Canon
                    for (int barNum = 0; barNum < canon_length; barNum++){
                        int beat = 1;
                        measure = 4;
                        while (beat <= measure) {
                                                    
                            if (beat == 1 && barNum == 0 ) {
                                hrPattern.add("A4s Rs Rs Rs");
                            }
                            else {
                                patternIndex = roll.nextInt(2);
                                if (patternIndex == 0) hrPattern.add("A4s Rs Rs Rs");
                                if (patternIndex == 1) hrPattern.add("Rs Rs Rs Rs"); 
                            }
                        }
                    }
                    //Build Rhythm patterns for voices
                    for (int barNum = 0; barNum < fragmentLength; barNum++) { // for each bar
                        //System.out.println("bar " + barNum);
                        int beat = 1;
                        measure = 4;
                        fragPattern.add("|");
                        //should also add time signature token but there is no jFugue string for it except in v5
                        //System.out.println("measure length " + measure);
                        while (beat <= measure) 	{                      //for each beat
                            //System.out.println("beat " + beat);
                            patternIndex = (roll.nextInt(4));
                            if (patternIndex == 0) fragPattern.add("A4q");   
                            if (patternIndex == 1) fragPattern.add("A4i C4i");
                            if (patternIndex == 2) fragPattern.add("A4s C4s C4i");
                            if (patternIndex == 3) fragPattern.add("A4s C4s C4s C4s");
                        }
                        beat++;
                                //System.out.println("patternIndex " + patternIndex);
                                //System.out.println(fragPattern.getMusicString());
		    }
                    VoiceArray[voice] = fragPattern;
                    //System.out.println("finished voice " + voice);
                   // Player my_player = new Player();
                   // my_player.play(fragPattern);
		}
	return VoiceArray;
    }
}
