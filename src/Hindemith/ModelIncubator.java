/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Hindemith;

import static Hindemith.CanonizerModalTransposeModel.same_consonant_count;
import Hindemith.ModeModules.ModeModule;
import static java.lang.Math.abs;
import java.util.ArrayList;
import java.util.Random;
import org.jfugue.Instrument;
import org.jfugue.Note;
import org.jfugue.Pattern;
import org.jfugue.Tempo;
import org.jfugue.Voice;

/**
 *
 * @author Owner
 */
public class ModelIncubator {
    
    static int peak = 0;
    static int peak_count = 0;
    static int trough = 200;
    static int trough_count = 0;
    static ArrayList<PitchCount> melody_pitch_counts = new ArrayList();
    static ArrayList<MotionCount> melody_motion_counts = new ArrayList();
    static MelodicVoice melody_line = new MelodicVoice();
    static int same_inv_consonant_count = 0;
    static int same_consonant_threshold = 6;
    static int same_consonant_count = 0;
    
    public ArrayList<MelodicVoice> PerformTranspositions(Integer number_of_canon_voices, MelodicVoice nextFragment, ModeModule my_mode_module, int [] transpose_interval_array) {
        //System.out.println("about to perform " + (number_of_voices -1) + " transpositions");
        ArrayList<MelodicVoice> transposed_voices = new ArrayList();
        for (int v = 0; v < number_of_canon_voices; v++) { //v means voice index
            Integer fragment_transpose_interval;
            MelodicVoice newTransposition = new MelodicVoice(); //declare melodic voice we'll place into transposition_array
            ArrayList<MelodicNote> t_return_list = new ArrayList(); //declare note_array we'll place into newTransposition
            ArrayList<MelodicNote> frag_note_list = nextFragment.getNoteArray(); //get fragment note array - this is what we'll transpose
            if (v == 0) fragment_transpose_interval = 0; //first melodic voice in transposition array will be the original untransposed fragment
            else fragment_transpose_interval = transpose_interval_array[v -1]; // if voice index v is 1 transpose_interval index is zero etc
            System.out.println("transpose_interval " + fragment_transpose_interval);
            for (MelodicNote mynote : frag_note_list) {//for each note in the fragment
                MelodicNote newnote = new MelodicNote(); //create a newnote just like the old one other than pitch
                newnote.setAccent(mynote.getAccent());
                newnote.setDuration(mynote.getDuration());
                newnote.setRest(mynote.getRest());
                newnote.setStartTime(mynote.getStartTime());
                newnote.setTotalVoiceDuration(mynote.getPreviousDuration());
                Integer mynote_pitchclass = mynote.getPitch()%12; //now find the pitch class of fragment note
                Integer mynote_transposed_pitchclass = my_mode_module.ModeTranspose(mynote_pitchclass, fragment_transpose_interval);
                Integer pitchclassdiff = (mynote_transposed_pitchclass - mynote_pitchclass);
                if (pitchclassdiff < 0) pitchclassdiff +=12;
                if (fragment_transpose_interval > 12) pitchclassdiff += 12;
                newnote.setPitch(mynote.getPitch() + pitchclassdiff);
                System.out.println("old note " + mynote.getPitch() + " is pclass " + mynote_pitchclass + " transposed up modally by " + fragment_transpose_interval  + " to get new pitchclass "  + mynote_transposed_pitchclass + " and then the difference " + pitchclassdiff + " is added to " +  mynote.getPitch() + " to get new note" + newnote.getPitch());
                t_return_list.add(newnote);
            }
            newTransposition.setNoteArray(t_return_list);
            transposed_voices.add(newTransposition);
        }
        return transposed_voices;
    }
    public Pattern CombineFragments(Integer tempo_bpm, Integer number_of_voices, Integer fragment_length, ArrayList<ArrayList> transposed_voices) {
        //Combine fragments and their transpositions into canon voices for the final pattern
        
            //declare the final output Pattern and set its tempo
            Pattern music_output = new Pattern();
            music_output.addElement(new Tempo(tempo_bpm));
            
            //Build canon voices
            for (byte canon_voice_index = 0; canon_voice_index <  number_of_voices ; canon_voice_index++){
                Voice jf_voice = new Voice(canon_voice_index);
                Pattern voice_pattern = new Pattern();
                Pattern loopPattern = new Pattern();
                
                //append voice element and instrument to beginning of voice
                voice_pattern.addElement(jf_voice);
                voice_pattern.addElement(new Instrument(InputParameters.instbyte[canon_voice_index]));
                
                //add padding rests to canon voice
                //first, construct a rest the length of the fragment. 
                String padRest = "R";
                for (int h = 0; h< fragment_length; h++) padRest = padRest + "w"; 
                Pattern padPattern = new Pattern(padRest);
                
                //the for loops below calculate the multiple of of fragment-length rests to append to the start of the voice
                //the number of rests has two components
                //first component is the number of fragments to offset the entrance of the voice
                //ie first fragment of the second voice will be offset by 1 fragment so its fragment 1
                //coincides with first voice fragment 2 etc... 
                //second component is the number of iterations of canon melody to offset the entrance
                //ie so that the second canon voice enters after all of the first voice fragments have been heard
                //third canon voice enters after all the first voice fragments AND second voice fragments have been heard
                //the for loop immediately below acts as an outer multiplier for canon melody length rests
                //it also specifies the number of fragment offset rests
                //its the same as the canon_voice_index ie for voice 0 outer multiplier is 0 for voice 1, outer multiplier is 1 etc
                for (int k = 0; k < canon_voice_index; k++) {
                    voice_pattern.add(padPattern);
                    //this for loop acts as an inner multiplier
                    //its the same as number of voices because the number of voices will always equal 
                    //the number of fragments in the canon melody
                    //together the inner and outer multiplier will add 0 padding rests to the first voice, 1 times number of fragments
                    //to the second voice, 2 times number of fragments to third voice etc. 
                    for (int l = 0; l < number_of_voices; l++) //1 
                        voice_pattern.add(padPattern);
                }
                //DEBUG
                System.out.println(transposed_voices.size());
                System.out.println("canon_voice_index = " + canon_voice_index);
                for (int fragment_index = 0; fragment_index < number_of_voices; fragment_index++) {
                    ArrayList<MelodicVoice> fragment = transposed_voices.get(fragment_index);
                    System.out.println("getting block " + canon_voice_index + " with " + fragment.size() + " melodic voices from transpose_arrayS" );
                    MelodicVoice tfrag = fragment.get(canon_voice_index);
                    tfrag.showNoteArray();
                    ArrayList<MelodicNote> final_note_array = tfrag.getNoteArray(); // get Melodic note array from the Melodic voice
                    for (MelodicNote final_note : final_note_array) {
                        //create jfugue note from MelodicNote
                        int jf_int = 0;
                        Note jf_note = new Note();
                        jf_note.setDecimalDuration(final_note.getDuration());
                        if (final_note.getPitch() != null && final_note.getRest() == false ) {
                            jf_int = final_note.getPitch();
                            byte jf_note_byte = (byte)jf_int;
                            jf_note.setValue(jf_note_byte);
                            //if (!final_note.getAccent()) jf_note.setAttackVelocity((byte)40); UNCOMMENT THIS IN FINAL
                        }
                        else {
                            //DEBUG
                            //System.out.println("setting jf note to rest");
                            jf_note.setRest(true);
                            jf_note.setAttackVelocity((byte)0);
                            jf_note.setDecayVelocity((byte)0);
                        }
                        System.out.println(jf_note.getMusicString());
                        //add the jfugue note to jfugue voice
                        loopPattern.addElement(jf_note);
                    }    
                }
                //System.out.println(loopPattern.getMusicString());                
                for (int x = 0; x < InputParameters.loops; x++) {
                    voice_pattern.add(loopPattern);
                }
                music_output.add(voice_pattern);   
            }// end create a jfugue musicstring from the built voice loop
            return music_output;
    }
    public static ArrayList<PitchCandidate> PitchCandidateVetting (ArrayList<PitchCandidate> pitch_candidates, boolean prog_built, boolean is_accent, int pitch_center, int key_transpose, MelodicVoice melody_line) {
        Integer [] consonances = InputParameters.consonances;
        Integer [] perfect_consonances = InputParameters.perfect_consonances;
        Integer [] root_consonances = InputParameters.root_consonances;
        Random roll = new Random();
        for (PitchCandidate myPC : pitch_candidates){
            int cand_pitch = myPC.getPitch();
            //DEBUG
            //("evaluating pitch candidate " + cand_pitch);
            
            //Check if Dissonant with Root - Does not apply for harmonic prog
            if (prog_built) {
                boolean root_interval_consonant = false;
                int root_interval = abs(cand_pitch%12 - key_transpose);
                for (Integer consonance : root_consonances) {
                    if (root_interval == consonance) root_interval_consonant = true;
                }
                if(root_interval_consonant) {
                    //DEBUG
                    System.out.println(cand_pitch + " consonant with root " + key_transpose );
                }
                else {
                    if (is_accent ) {
                        myPC.decrementRank(Decrements.dissonant_with_root);
                        //DEBUG
                        System.out.println(cand_pitch + " dissonant accent with root " + key_transpose);
                    }
                    else {
                        //DEBUG
                        System.out.println(cand_pitch + " dissonant with root but note not accented" );
                    }
                }                 
            }

            //randomly decrement non-tonics
            if (cand_pitch%12 != key_transpose){
                //DEBUG
                System.out.println(cand_pitch + " is not tonic");
                if (roll.nextInt(2) == 1) myPC.decrementRank(Decrements.is_not_tonic);
            }
            
            //decrement illegal notes
            if(cand_pitch <0 || cand_pitch > 127) {
                myPC.decrementRank(Decrements.illegal_note);
                //DEBUG
                System.out.println(cand_pitch + " is illegal note");                
            }

            //decrement motion outside of voice range                
            if (cand_pitch < melody_line.getRangeMin() || cand_pitch > melody_line.getRangeMax()) {
                myPC.decrementRank(Decrements.outside_range);
                //DEBUG
                System.out.println(cand_pitch + " outside range " + melody_line.getRangeMin() + "-" + melody_line.getRangeMax());                
            }

            //decrement too far from pitch center
            if (abs(cand_pitch - pitch_center) > 16) {
                 myPC.decrementRank(Decrements.remote_from_pitchcenter);
                 //DEBUG
                 System.out.println(cand_pitch + " too far from pitch center" + pitch_center);               
            } 
        }        
        return pitch_candidates; 
    }

    public static ArrayList<PitchCandidate>  melodicCheck(ArrayList<PitchCandidate> pitch_candidates, ModeModule my_mode_module,
        int melody_pitch_count, Integer previous_melody_pitch, Integer previous_melodic_interval) {
        
        for (PitchCandidate myPC : pitch_candidates){
            int cand_pitch = myPC.getPitch();
            int melody_motion_to_cand = 0;
            
            //DEBUG
            //System.out.println("melodicCheck evaluating pitch candidate " + cand_pitch);
            System.out.println("********************************************************");
            
            if (melody_pitch_count > 0) {
                melody_motion_to_cand = cand_pitch - previous_melody_pitch;
                System.out.println();
                //Check if the melodic motion to the pitch candidate is improbable or impossible within the mode. 
                double thresh = 0;
		Double threshornull = my_mode_module.getMelodicMotionProbability(cand_pitch, previous_melody_pitch, 0, 0);
		if (threshornull == null){
                    //DEBUG
                    System.out.println("motion is not possible in this melodic mode");
                    myPC.decrementRank(Decrements.impossible_melodic_motion);
                }
                else {
                    if (threshornull < .05) {
                        myPC.decrementRank(Decrements.improbable_melodic_motion);
                        thresh = threshornull;
                        //DEBUG
                        System.out.println("motion is unlikely in this mode" );
                    }
                }
                
                //Check if The candidate has already followed the preceding pitch too often.                
                //Look for previous_melody_pitch in PitchCount.
                //If it's there get how many times it's appeared in the melody.
                // If the count is greater than samplesize threshold,
                //check if there are previous_melody_pitch to pitch_candidate motions in Motion Counts
                //if so get the motion count - then divide motion count by pitch count
                // get the percentage of motions from mode module
                //if actual count is greater than mode module percentage decrement
                
                for (PitchCount my_pitch_count: melody_pitch_counts) {
                    System.out.println("found pitch_count of " + my_pitch_count.getCount() + " for pitch " + my_pitch_count.getPitch());
                    if(my_pitch_count.getPitch()%12 == previous_melody_pitch%12) {
			//DEBUG
                        System.out.println("pitch class " + previous_melody_pitch%12 + " occurs " + my_pitch_count.getCount() + " times in the melody so far");
                        if(my_pitch_count.getCount() > InputParameters.sample_size)     
                        for (MotionCount my_motion_count: melody_motion_counts){
                            //DEBUG
                            //System.out.println("pitch_count for " + previous_melody_pitch %12 + " = " + my_pitch_count.getCount());
                            System.out.println("motion from " + my_motion_count.getPreviousPitch()%12 + " to " + my_motion_count.getSucceedingPitch()%12 + " has already occurred "+ my_motion_count.getCount() + " times");
                            if (my_motion_count.getPreviousPitch()%12 == previous_melody_pitch %12 && my_motion_count.getSucceedingPitch()%12 == cand_pitch %12) {
                                double actual = my_motion_count.getCount()/(double)my_pitch_count.getCount();
                                //DEBUG
                                //System.out.println("found " + my_motion_count.getCount() + " instances of motion from " + previous_melody_pitch %12 + " to " +cand_pitch %12 );
                                System.out.println("frequency of motion from " + previous_melody_pitch %12 + " to " + cand_pitch%12 + " = " + actual);       
                                if (actual >= thresh) {
                                    myPC.decrementRank(Decrements.melodic_motion_quota_exceed);
                                    //DEBUG
                                    System.out.println(cand_pitch %12 + " is approached too often from " + previous_melody_pitch %12);
                                }
                            }
                        }
                    }
                }
            }
            
            if (melody_pitch_count > 1){
                // Peak/Trough check
                // a melodic phrase should have no more than two peaks and two troughs
                // a peak is defined as a change in melodic direction 
                // so when a candidate pitch wants to go in the opposite direction of 
                // the previous melodic interval we want to increment the peak or trough count accordingly
                // and determine whether we have more than two peaks or more than two troughs
                // note that the melody can always go higher or lower than the previous peak or trough

                if (previous_melodic_interval < 0 && melody_motion_to_cand > 0 ) {// will there be a change in direction from - to +  ie trough?
                        if (previous_melody_pitch == trough && trough_count > 1) {
                            myPC.decrementRank(Decrements.peak_trough_quota_exceed * (trough_count -1));
                            //DEBUG
                            System.out.println(previous_melody_pitch + " duplicates previous trough too often Decremented by " + (Decrements.peak_trough_quota_exceed * (trough_count -1)));
                        } 
                }        
                if (previous_melodic_interval > 0 && melody_motion_to_cand <0){ // will there be a trough?
                        if (previous_melody_pitch == peak && peak_count > 1) {
                            myPC.decrementRank(Decrements.peak_trough_quota_exceed * (peak_count -1));
                            //DEBUG
                            System.out.println(previous_melody_pitch + " duplicates previous peak too often. Decremented by " + (Decrements.peak_trough_quota_exceed * (peak_count -1)));
                        } 
                }
				
                //Motion after Leaps checks
                //First check if the melody does not go in opposite direction of leap
                // then check if there are two successive leaps in the same direction
                if (previous_melodic_interval > 4 && melody_motion_to_cand > 0){
                    myPC.decrementRank(Decrements.bad_motion_after_leap);
                    //DEBUG
                    System.out.println("motion of " + melody_motion_to_cand + " from " + previous_melody_pitch + " to "+ cand_pitch + " would be  bad motion after leap");
                    if (melody_motion_to_cand > 4) {
                        myPC.decrementRank(Decrements.successive_leaps);
                        //DEBUG
                        System.out.println("leap of " + melody_motion_to_cand + " to " + cand_pitch + " after leap of " + previous_melodic_interval +" would be a successive leap");
                    }
                        
                }    
                if (previous_melodic_interval < -4 && melody_motion_to_cand < 0){
                    myPC.decrementRank(Decrements.bad_motion_after_leap);
                    //DEBUG
                    System.out.println("motion of " + melody_motion_to_cand + " from " + previous_melody_pitch +" to "+ cand_pitch + " would be bad motion after leap");
                    if (melody_motion_to_cand < -4) {
                        myPC.decrementRank(Decrements.successive_leaps);  
                        //DEBUG
                        System.out.println("leap of " + melody_motion_to_cand + " to " + cand_pitch + " after leap of " + previous_melodic_interval +" would be a successive leap");
                    }
                }   
            }           
            // end melody checks
        } //next pitch candidate
        return pitch_candidates; 
    }  
    
    public static void setPeakTroughPitchCount(int test_peak, int test_peak_count, int test_trough, int test_trough_count, 
        ArrayList<PitchCount> test_pitch_counts, ArrayList<MotionCount> test_motion_counts) {
        peak = test_peak;
        trough = test_trough;
        peak_count = test_peak_count;
        trough_count = test_trough_count;
        melody_pitch_counts = test_pitch_counts;
        melody_motion_counts = test_motion_counts;
    }
    
    //THIS HAS NOT BEEN TESTED
    public static ArrayList<PitchCandidate> harmonicChecks(ArrayList<PitchCandidate> pitch_candidates, MelodicNote CF_note, Integer previous_cf_pitch,
        Integer previous_melody_pitch, MelodicNote fragment_note, int canon_transpose_interval ){
        
        Boolean large_dissonance_bad = InputParameters.large_dissonance_bad;
        Integer [] consonances = InputParameters.consonances;
        Integer [] perfect_consonances = InputParameters.perfect_consonances;
        Integer [] root_consonances = InputParameters.root_consonances;
        

        for (PitchCandidate myPC : pitch_candidates){
		
            Integer cand_pitch = myPC.getPitch() + canon_transpose_interval;
            Integer cf_pitch = CF_note.getPitch();
            boolean CF_accent = CF_note.getAccent();
            boolean use_CF_accent = false;
            if (CF_note.getRest()) cf_pitch = previous_cf_pitch;
            Integer cand_prev_pitch = previous_melody_pitch + canon_transpose_interval;
            //if(previous_melody_pitch == 9999) cand_prev_pitch = cand_pitch;// 9999 means the CP is held over to multiple cfs
            Integer melody_motion_to_cand = cand_pitch  - cand_prev_pitch;
            int this_interval = abs(cand_pitch - cf_pitch)%12;
            int this_inv_interval = abs(cf_pitch - cand_pitch)%12;
            Integer melodic_motion_to_ = cf_pitch - previous_cf_pitch;
            Integer previous_interval = abs(cand_prev_pitch - previous_cf_pitch)%12;
            Integer previous_inv_interval = abs(previous_cf_pitch - cand_prev_pitch)%12;
            Double cp_start_time = fragment_note.getStartTime();
            Double cf_start_time = CF_note.getStartTime();
            Boolean directm = false;
            boolean this_interval_consonant = false;
            boolean this_inv_interval_consonant = false;
            boolean cand_prev_cf_diss = true;
            boolean inv_cand_prev_cf_diss = true;
            boolean previous_interval_consonant = false;
            boolean previous_inv_interval_consonant = false;
            
            //System.out.println("evaluating pitch candidate " + cand_pitch + " against " + cf_pitch);
            //is this interval consonant
            for (Integer consonance : consonances) {
                if (this_interval == consonance){
                    this_interval_consonant = true; 
                    break;
                }
            }
            for (Integer consonance : consonances) {
                if (this_inv_interval == consonance){
                    this_inv_interval_consonant = true; 
                    break;
                }
            }
	    	
            
            if(this_interval_consonant && this_inv_interval_consonant) {
                //System.out.println(cand_pitch + " against " + cf_pitch + "is consonant");
                if (this_interval ==0) {//decrement if an octave
                    myPC.decrementRank(Decrements.octave);
                }
            }
            
            else {
                //System.out.println(cand_pitch + " against " + cf_pitch + "is dissonant");
                myPC.decrementRank(Decrements.dissonance);
		//decrement if a minor 9th
                if (this_interval == 1 && (abs(cand_pitch - cf_pitch)<14 || large_dissonance_bad)){
                    myPC.decrementRank(Decrements.minor_9th);
                    myPC.set_minor_9th();
                }
                if (this_inv_interval == 1 && (abs(cf_pitch - cand_pitch)<14 || large_dissonance_bad)){
                    myPC.decrementRank(Decrements.minor_9th);
                    myPC.set_minor_9th();
                }
                //decrement accented dissonance
                if (CF_note.getStartTime() > fragment_note.getStartTime())
                    use_CF_accent = true;
                
                if (!use_CF_accent) {
                    if (fragment_note.getAccent() && (abs(cand_pitch - cf_pitch)<36 || large_dissonance_bad)) {
                        //System.out.println("caught accented dissoance");
                        myPC.decrementRank(Decrements.accented_dissonance);
                        myPC.set_accent_diss();
                    }
                    if (fragment_note.getAccent() && (abs(cf_pitch - cand_pitch)<36 || large_dissonance_bad)) {
                        //System.out.println("caught accented dissoance");
                        myPC.decrementRank(Decrements.accented_dissonance);
                        myPC.set_accent_diss();
                    }                    
                }
                else {
                    if (CF_note.getAccent() && (abs(cand_pitch - cf_pitch)<36 || large_dissonance_bad)) {
                        //System.out.println("caught accented dissoance");
                        myPC.decrementRank(Decrements.accented_dissonance);
                        myPC.set_accent_diss();
                    }
                    if (CF_note.getAccent() && (abs(cf_pitch - cand_pitch)<36 || large_dissonance_bad)) {
                        //System.out.println("caught accented dissoance");
                        myPC.decrementRank(Decrements.accented_dissonance);
                        myPC.set_accent_diss();
                    }
                }

            }
            
	    //check for pitch candidate dissonance against previous cantus firmus
            for (Integer consonance : consonances) {
                if (abs(cand_pitch - previous_cf_pitch)%12 == consonance) {
		    cand_prev_cf_diss = false;    
                }
            }
            for (Integer consonance : consonances) {
                if (abs(previous_cf_pitch - cand_pitch)%12 == consonance) {
                    inv_cand_prev_cf_diss = false;    
                }
            }            
            if (cand_prev_cf_diss ||inv_cand_prev_cf_diss) {
		myPC.decrementRank(Decrements.diss_cp_previous_cf);
            }
			
            //compute whether previous_interval consonant
            for (Integer consonance : consonances) {
                if (previous_interval == consonance) previous_interval_consonant = true;
		break;
            }
            for (Integer consonance : consonances) {
                if (previous_inv_interval == consonance) previous_inv_interval_consonant = true;
		break;
            }
			
	    //check for same type of consonance
            if (previous_interval_consonant && (previous_interval == this_interval) ){
		myPC.decrementRank(Decrements.seq_same_type_cons);
            }
            if (previous_inv_interval_consonant && (previous_inv_interval == this_inv_interval) ){
		myPC.decrementRank(Decrements.seq_same_type_cons);
            }            
			
	    //check for sequence of dissonances
            if(!previous_interval_consonant && !this_interval_consonant) {
		myPC.decrementRank(Decrements.seq_of_diss);
		if(this_interval == previous_interval ){
                    myPC.decrementRank(Decrements.seq_same_type_diss);
		}
            }
            if(!previous_inv_interval_consonant && !this_inv_interval_consonant) {
		myPC.decrementRank(Decrements.seq_of_diss);
		if(this_inv_interval == previous_inv_interval ){
                    myPC.decrementRank(Decrements.seq_same_type_diss);
		}
            } 
	
            //check for too long a sequence of same interval
            if (previous_interval == this_interval) {
                same_consonant_count++;
                if (same_consonant_count > same_consonant_threshold) {
                    myPC.decrementRank(Decrements.seq_of_same_cons);
                }                  
            }
	    else {
		same_consonant_count =0;
	    }
            if (previous_inv_interval == this_inv_interval) {
                same_inv_consonant_count++;
                if (same_inv_consonant_count > same_consonant_threshold) {
                    myPC.decrementRank(Decrements.seq_of_same_cons);
                }                  
            }
	    else {
		same_inv_consonant_count =0;
	    }

			
	    //if CF starts before CP 
            if (cp_start_time > cf_start_time){
	    //the following  checks rely on knowing motion to pitch candidate from previous pitch
	    //check for a bad approach to a dissonance from a consonance
	    //ie CP pitch approached by greater than a step
                if (previous_interval_consonant){
                    if ((!this_interval_consonant || !this_inv_interval_consonant) && abs(melody_motion_to_cand) >2) {
                        myPC.decrementRank(Decrements.bad_diss_approach_from_cons);
                    }    
                }
                //check for a bad approach to consonance from dissonance
                else if (this_interval_consonant || this_inv_interval_consonant){
                    if (abs(melody_motion_to_cand) >2){
                        myPC.decrementRank(Decrements.bad_cons_approach_from_diss);
                    }    
                }
                //check for bad approach to dissonance from dissonance
                else { //implies both this interval and previous are dissonant
                    if (abs(melody_motion_to_cand) > 4){
                        myPC.decrementRank(Decrements.bad_diss_approach_from_diss);
                    }    
                }
            }
            // If CP starts before CF
            else if (cp_start_time < cf_start_time) {
		// the following checks rely on knowing motion to CF pitch from previous CF pitch
		//check for bad motion into consonance from dissonance
                if (!previous_interval_consonant) {//ie Previous_Interval is dissonant
                    if (this_interval_consonant || this_inv_interval_consonant) {
                        if (abs(melodic_motion_to_) > 2) {
                              myPC.decrementRank(Decrements.bad_cons_approach_from_diss);
			}
                    }
		    //check for bad motion into dissonance from dissonance
                    else {
                        if (abs(melodic_motion_to_) > 4){
			    myPC.decrementRank(Decrements.bad_diss_approach_from_diss);   
                        }
                    }          
                }
            }
	    // If CP and CF start at the same time
            else  {
                //Check for parallel perfect consonances
		if((melody_motion_to_cand > 0 && melodic_motion_to_ >0) || (melody_motion_to_cand < 0 && melodic_motion_to_ <0) )
		    directm = true;
                if (this_interval_consonant) {
                    if (previous_interval_consonant)	{
                        if (this_interval == previous_interval ){
                            myPC.decrementRank(Decrements.seq_same_type_cons);
                            if (directm) {
                                myPC.decrementRank(Decrements.parallel_perf_consonance);
                            }      
                        }
                        else {
                            //check for direct motion into a perfect consonance
                            if (directm ) {
                                myPC.decrementRank(Decrements.direct_motion_perf_cons);
                            }
                        }
                    }
                }
                if (this_inv_interval_consonant) {
                    if (previous_inv_interval_consonant)	{
                        if (this_inv_interval == previous_inv_interval ){
                            myPC.decrementRank(Decrements.seq_same_type_cons);
                            if (directm) {
                                myPC.decrementRank(Decrements.parallel_perf_consonance);
                            }      
                        }
                        else {
                            //check for direct motion into a perfect consonance
                            if (directm ) {
                                myPC.decrementRank(Decrements.direct_motion_perf_cons);
                            }
                        }
                    }
                }                
		//check for motion into a dissonance
                else  { //this interval is dissonant
                    myPC.decrementRank(Decrements.motion_into_diss_both_voices_change);
		    if (directm ) {
			myPC.decrementRank(Decrements.direct_motion_into_diss);
                    }
                }

            }
        }  
	return pitch_candidates;
    }
}
