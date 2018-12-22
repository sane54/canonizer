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
public class PitchCandidate {
        
        Integer rank = 0;
        Integer pitch = 0;
        boolean minor_9th = false;
        boolean accent_diss = false;
        
        public Integer getRank (){
        return rank;
        }
        
        public void decrementRank(int decrement){
            rank -= decrement;
        }
        
        public void setPitch (Integer input_pitch){
            pitch = input_pitch;
        }
        
        public Integer getPitch (){
            return pitch;
        }
        
        public boolean get_accented_minor_9th() {
            if (minor_9th && accent_diss)
            return true;
            else return false;
        }
        
        public void set_minor_9th() {
            minor_9th = true;
        }
        
        public void set_accent_diss() {
            accent_diss = true;
        }
        public boolean get_accent_diss() {
            return accent_diss;
        }
}
