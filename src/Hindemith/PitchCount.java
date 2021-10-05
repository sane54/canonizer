/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Hindemith;

/**
 *
 * @author Owner
 */
    public class PitchCount {
        private final int the_pitch;
        private int count;
       
        public PitchCount(int input_the_pitch){
            this.the_pitch = input_the_pitch;
        }
        
        public void incrementCount(){
           count++;
        }
        
        public int getPitch() {
            return the_pitch;
        }
        
        public int getCount() {
            return count;
        }
        
        public void setCount(int testcount){ //for testing purposes
           count = testcount;
       }
    }  
