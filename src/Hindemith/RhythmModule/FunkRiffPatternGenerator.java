/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Hindemith.RhythmModule;



/**
 *
 * @author alyssa
 */

import org.jfugue.*;
import java.util.Random;

public class FunkRiffPatternGenerator  implements RhythmModule{
    @Override
    public  Pattern[] generate(int pieceLength, int numberOfVoices) {
		Random roll = new Random();
		int patternIndex;
                int tempo = 80;
                int measure;

		
		

		Pattern VoiceArray [] = new Pattern[numberOfVoices];
		//DEBUG
		System.out.println("starting with # of bars = to " + pieceLength);
		for (int voice = 0; voice < numberOfVoices; voice++) {     //for each voice
                    //DEBUG
                    //System.out.println("voice " + voice);
                    Pattern jPattern = new Pattern();
                    jPattern.addElement(new Tempo(tempo));
                        for (int barNum = 0; barNum < pieceLength; barNum++) {
                                jPattern.add("|");
                                if(voice == 7) jPattern.add("A4s Rs Rs Rs Rs Rs Rs Rs Rs Rs Rs Rs Rs Rs Rs Rs");
                                else if (voice == 0) {
                                        patternIndex = roll.nextInt(2);
                                        if (patternIndex == 0) jPattern.add("A4s Rs C4s Rs A4s Rs Rs Rs A4s Rs Rs Rs A4s Rs C4s Rs ");
                                        if (patternIndex == 1) jPattern.add("A4s Rs C4s Rs A4s Rs C4s Rs A4s Rs C4s Rs A4s Rs Rs Rs ");
                                        if (patternIndex > 1) jPattern.add("Rs Rs Rs Rs Rs Rs Rs Rs Rs Rs Rs Rs Rs Rs Rs Rs ");
                                }       
                                else if (voice == 1) {
                                        patternIndex = roll.nextInt(2);
                                        if (patternIndex == 0) jPattern.add("A4s A4s Rs C4s A4s A4s Rs C4s A4s A4s Rs A4s Rs Rs Rs Rs ");
                                        if (patternIndex == 1) jPattern.add("Rs Rs C4s A4s Rs Rs C4s Rs A4s Rs Rs Rs A4s Rs C4s Rs ");
                                        if (patternIndex >1) jPattern.add("Rs Rs Rs Rs Rs Rs Rs Rs Rs Rs Rs Rs Rs Rs Rs Rs");
                                }
                                else  {
                                        patternIndex = roll.nextInt(3);
                                        if (patternIndex == 0) jPattern.add("A4s Rs Rs A4s Rs Rs Rs Rs Rs Rs A4s Rs A4s Rs Rs Rs ");
                                        if (patternIndex == 1) jPattern.add("Rs Rs A4s Rs Rs Rs C4s A4s Rs C4s A4s Rs A4s Rs C4s C4s ");
                                        if (patternIndex == 2) jPattern.add("Rs C4s C4s C4s A4s C4s C4s A4s Rs Rs Rs Rs Rs Rs A4s Cs ");
                                        if (patternIndex > 2) jPattern.add("Rs Rs Rs Rs Rs Rs Rs Rs Rs Rs Rs Rs Rs Rs Rs Rs");
                                }
			}
                    //jPattern.add("A4w");
                    VoiceArray[voice] = jPattern;
                    //DEBUG
                    System.out.println("finished voice " + voice);
		}
	return VoiceArray;
    }
}
