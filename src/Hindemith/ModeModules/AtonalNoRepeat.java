/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Hindemith.ModeModules;

import Hindemith.ModeModules.ModeModule;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author alyssa
 */
public class AtonalNoRepeat implements ModeModule {
    Random roll = new Random();
    private final int tonic = 0;
    


    @Override
    public ArrayList<Integer> getFirstNotePitchCandidates(int input_range_min, int input_range_max, int key_transpose ) {
    //DEBUG    
    //System.out.println("starting get first Note Pitch Candidates");
    ArrayList<Integer> pitch_classes = new ArrayList();
    for (int i = 0; i <12; i++) {
        int j = i;
        while (j < input_range_min) j+=12; 
        pitch_classes.add(j);  
    }
        
    return pitch_classes; 
    }
  
    @Override
    public Integer getPitchCenter (int input_range_min, int input_range_max ){
      Integer pitch_candidate = roll.nextInt(12);
      while (pitch_candidate < input_range_min + 8 || pitch_candidate > input_range_max) {
         pitch_candidate +=  12;
      }
      return pitch_candidate;
    }
    
    @Override
    public ArrayList<Integer> getPitchCandidates(int input_previous_pitch, int key_transpose){
        
        ArrayList<Integer> pitch_step_candidates = new ArrayList();
        ArrayList<Integer> pitch_candidates = new ArrayList();
        Integer previous_pitch_class = input_previous_pitch % 12;
        for (int i = -12; i< 13; i++) pitch_step_candidates.add(i);
        for (Integer pitch_step_candidate : pitch_step_candidates) {
            if (pitch_step_candidate != 0 )pitch_candidates.add(pitch_step_candidate + input_previous_pitch);
        }
        
        return pitch_candidates;
    }

    public Double getMelodicMotionProbability (Integer input_current_pitch_cand, Integer input_previous_pitch, Integer key_transpose, Integer mode_transpose ) {
        Double motion_probability = 0.04;
        return motion_probability;
    }
    @Override
    public Integer getTonic() {
        return tonic;
    }

    @Override
    public String mytoString() {
        return "Atonal No Repeat"; //To change body of generated methods, choose Tools | Templates.
    }
    @Override    
    public Integer ModeTranspose(Integer pclass, Integer mode_transpose_interval) {
        if (mode_transpose_interval > 12) mode_transpose_interval = mode_transpose_interval %12;
        switch ( mode_transpose_interval ){
            case (0):
                break;
            case (1):
            case (2):
//                if (pclass == 10 || pclass == 11) pclass = 0;
//                else pclass += 2;
                pclass += 2;
                //DEBUG
                //System.out.println(step_probability_2.get(difference));
                break;
            case (3):
            case (4):
//                if (pclass == 4 || pclass == 6) pclass += 3;
//                else if (pclass == 10 || pclass == 11) pclass = 2;
//                else if (pclass == 8 || pclass ==9) pclass =0;
//                else pclass +=4;
                pclass +=4;
                //DEBUG
                //System.out.println(step_probability_4.get(difference));
                break;
            case (5):
                pclass = (pclass + 5) % 12;
                //DEBUG
                //System.out.println(step_probability_5.get(difference));
                break;
            case (6):
            case (7):
//                if (pclass == 6) pclass = 0;
//                else pclass = (pclass + 7) % 12;
                pclass = (pclass + 7) % 12;
                //DEBUG
               //System.out.println(step_probability_7.get(difference));
                break;
            case (8):
            case (9):
//                if (pclass == 4) pclass =0;
//                else if (pclass == 6) pclass = 2;
//                else if (pclass == 11) pclass =7;
//                else pclass = (pclass + 9) % 12;
                pclass = (pclass + 9) % 12;
                break;
            case (10):
            case (11):
//                if (pclass == 2) pclass =0;
//                else if (pclass == 4) pclass = 2;
//                else if (pclass == 6) pclass = 4;
//                else if (pclass == 9) pclass = 7;
//                else if (pclass == 11) pclass =9;
                pclass = (pclass + 11) % 12;
                //DEBUG
               //System.out.println(step_probability_11.get(difference));
                break;
            case (12):
                pclass+=12;
                break;
        }        
        return pclass;
    }
    public ArrayList<Integer> getPitchCandidatesGeneric(Integer input_previous_pitch) {
    
        ArrayList<Integer> pitch_step_candidates = new ArrayList();
        ArrayList<Integer> pitch_candidates = new ArrayList();

        pitch_step_candidates.add(0);
        pitch_step_candidates.add(1);
        pitch_step_candidates.add(-1);
        pitch_step_candidates.add(2);
        pitch_step_candidates.add(-2);
        pitch_step_candidates.add(3);
        pitch_step_candidates.add(-3);
        pitch_step_candidates.add(4);
        pitch_step_candidates.add(-4);
        pitch_step_candidates.add(5);
        pitch_step_candidates.add(-5);
        pitch_step_candidates.add(6);
        pitch_step_candidates.add(-6);
        pitch_step_candidates.add(7);
        pitch_step_candidates.add(-7);
        pitch_step_candidates.add(8);
        pitch_step_candidates.add(-8);
        pitch_step_candidates.add(9);
        pitch_step_candidates.add(-9);
        pitch_step_candidates.add(12);
        pitch_step_candidates.add(-12);

        for (Integer pitch_step_candidate : pitch_step_candidates) {
            pitch_candidates.add(pitch_step_candidate + input_previous_pitch);
        }

        return pitch_candidates;
    }
    

}
