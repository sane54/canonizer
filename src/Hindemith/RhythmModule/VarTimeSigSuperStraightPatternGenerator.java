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

public class VarTimeSigSuperStraightPatternGenerator  implements RhythmModule{
    @Override
    public  Pattern[] generate(int fragmentLength, int numberOfVoices) {
		Random roll = new Random();
		int patternIndex;
                int tempo = 80;
                int measure;
                int barpopulate;

		
		
		
		Pattern VoiceArray [] = new Pattern[numberOfVoices];		
                
                
                
		//System.out.println("starting with # of bars = to " + pieceLength);
		for (int voice = 0; voice < numberOfVoices; voice++) {     //for each voice
                    //System.out.println("voice " + voice);
                    Pattern fragPattern = new Pattern();
                    fragPattern.addElement(new Tempo(tempo));
                        
                    
                        for (int barNum = 0; barNum < fragmentLength; barNum++) { // for each bar
                            //System.out.println("bar " + barNum);
                            int beat = 1;
                            measure = 4;
                            fragPattern.add("|");
                            //should also add time signature token but there is no jFugue string for it except in v5
                            //System.out.println("measure length " + measure);
                            
                            
                            
                            
                            
                            barpopulate = roll.nextInt(5);
                            if (barpopulate == 0) fragPattern.add("A4w");
                            else if (barpopulate == 1) fragPattern.add("A4h A4h");
                            else {
                                while (beat <= measure) 	{                      //for each beat
                                    patternIndex = roll.nextInt(2);
                                    if (patternIndex == 0) fragPattern.add("A4s Rs Rs Rs");
                                    if (patternIndex == 1) fragPattern.add("Rs Rs Rs Rs"); 
          

                                    
                                    beat++;
                                    //System.out.println("patternIndex " + patternIndex);
                                    //System.out.println(jPattern.getMusicString());
                            }
			}
                        }
                    VoiceArray[voice] = fragPattern;
                    //System.out.println("finished voice " + voice);
                   // Player my_player = new Player();
                   // my_player.play(fragPattern);
    }
	return VoiceArray;
    }
}
