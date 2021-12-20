/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Hindemith;

import Hindemith.ModeModules.ModeModule;
import Hindemith.RhythmModule.RhythmModule;
import static java.lang.Math.abs;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import org.jfugue.Instrument;
import org.jfugue.Note;
import org.jfugue.Pattern;
import org.jfugue.Tempo;
import org.jfugue.Voice;
import org.jfugue.Player;
import java.util.Objects;
import org.jfugue.extras.IntervalPatternTransformer;


/**
 *
 * @author Owner
 */
public class ModelIncubator {
    
    static int peak = 0;
    static int peak_count = 0;
    static int trough = 200;
    static int trough_count = 0;
    static ArrayList<MotionCount> melody_motion_counts = new ArrayList();
    static MelodicVoice melody_line = new MelodicVoice();
    static int melody_pitch_count = 0;
    static int same_inv_consonant_count = 0;
    static int same_consonant_threshold = 6;
    static int same_consonant_count = 0;
    static Random roll = new Random();
    static ArrayList<MotionCount> motion_counts = new ArrayList();
    static ArrayList<PitchCount>pitch_counts = new ArrayList();
    static Boolean harmonic_prog_built = false;
    static int voice_pitch_count = 0;
    static ArrayList<ArrayList> transpose_arrayS = new ArrayList();
    static MelodicVoice harmonic_prog = new MelodicVoice();
    static MelodicNote this_key = new MelodicNote();
    static int key_transpose = 0;
    static int transpose_interval = InputParameters.transpose_interval; 
    static ArrayList<MelodicVoice> unbuilt_fragments = new ArrayList();
    
    public ModelIncubator() {
        resetParams();
        int piece_length = InputParameters.piece_length;
        ModeModule my_mode_module = InputParameters.my_mode_module;
        String [] voice_array = InputParameters.voice_array; 
        RhythmModule james = InputParameters.james;       
        int number_of_voices = voice_array.length;
        //System.out.println("number of voices " + number_of_voices);
        melody_line.setRange("tenor");


        //generate a set of rhythm patterns, one for each fragment
        Pattern [] rhythm_patterns = james.generate(piece_length, number_of_voices);


        //build blank Melodic Voice object for each rhythm pattern
        for (int i = 0; i < number_of_voices; i++) {
            MelodicVoice this_fragment = new MelodicVoice();
            this_fragment.setRange(voice_array[i]);
            AccentListener my_accent_listener = new AccentListener();
            this_fragment.setNoteArray(my_accent_listener.listen(rhythm_patterns[i]));
            //DEBUG
            System.out.println("adding voice " + i + " to unbuiltvoices");
            unbuilt_fragments.add(this_fragment);
        }

        //add pitches to blank melodic voices, the first one is a harmonic rhythm
        for (int i = 0; i < number_of_voices; i++){

            //run buildFragmentPitches method on next unbuilt fragment
            int voice_index = i;
            //DEBUG
            //System.out.println(" about to build voice pitches for "+ voice_index);
            MelodicVoice nextFragment = buildFragmentPitches(unbuilt_fragments.get( voice_index ), number_of_voices, my_mode_module);


            //Printing voice strings to Standard Out
//            ArrayList<MelodicNote> verify_array = nextFragment.getNoteArray();
//            //DEBUGS
//            //System.out.println("Fragment " + voice_index + ":");
//            for(MelodicNote verify: verify_array) { 
//              if (verify.getRest()) System.out.println("rest " + verify.getDuration() + "  " );
//               else System.out.println(verify.getPitch() + " " + verify.getDuration() + "   ");
//              }
                transpose_arrayS.add(PerformTranspositions(number_of_voices, nextFragment, my_mode_module, InputParameters.transpose_interval_array)); 
         
        }
                
        unbuilt_fragments.clear();
        Pattern music_output = CombineFragments(InputParameters.tempo_bpm, number_of_voices, piece_length, transpose_arrayS);         
        
        String [] music_output_tokens = music_output.getTokens();
        String last_token = music_output_tokens[music_output_tokens.length -1];
        System.out.println(last_token);
        
        //DEBUG
        System.out.println(music_output.getMusicString());
        //PatternStorerSaver1.add_pattern(music_output);
        IntervalPatternTransformer my_transposer = new IntervalPatternTransformer(transpose_interval);
        music_output = my_transposer.transform(music_output);
        Player player1 = new Player();
        System.out.println("playing music output");
        player1.play(music_output);
    }
      
    
    
    public ArrayList<MelodicVoice> PerformTranspositions(Integer number_of_canon_voices, MelodicVoice nextFragment, ModeModule my_mode_module, int [] transpose_interval_array) {
        //System.out.println("about to perform " + (number_of_voices -1) + " transpositions");
        ArrayList<MelodicVoice> transposed_voices = new ArrayList();
        for (int v = 0; v < number_of_canon_voices; v++) { //v means voice index
            Integer fragment_transpose_interval;
            MelodicVoice newTransposition = new MelodicVoice(); //declare melodic voice we'll place into transposition_array
            ArrayList<MelodicNote> t_return_list = new ArrayList(); //declare note_array we'll place into newTransposition
            ArrayList<MelodicNote> frag_note_list = nextFragment.getNoteArray(); //get fragment note array - this is what we'll transpose
            if (v == 0) fragment_transpose_interval = 0; //first melodic voice in transposition array will be the original untransposed fragment
            else fragment_transpose_interval = transpose_interval_array[v]; // if voice index v is 1 transpose_interval index is zero etc
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
//                if (pitchclassdiff < 7) {
//                    newnote.setPitch(mynote.getPitch() + pitchclassdiff + 12*v);
//                }
//                else {
//                    if (v > 0) {
//                        newnote.setPitch(mynote.getPitch() + pitchclassdiff + 12*(v-1));
//                    }
//                }
                newnote.setPitch(mynote.getPitch() + pitchclassdiff + 12*v);
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
            
            Byte voice_order_array [] = new Byte[number_of_voices];
            List<Byte> voice_order_list = Arrays.asList(voice_order_array);
            for (Byte i = 0; i <  number_of_voices ; i++){
                voice_order_array[i] = i;
            }
            Collections.shuffle(voice_order_list);
            voice_order_list.toArray(voice_order_array);
            System.out.println("VOICE ORDER ARRAY");
            for (Byte my_voice: voice_order_array){
                System.out.println(my_voice);
            }
            
            //Build canon voices
            for (int i = 0; i <  number_of_voices ; i++){
                Byte canon_voice_index = voice_order_array[i];
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
                for (int k = 0; k < i; k++) {
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
                        //System.out.println(jf_note.getMusicString());
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
            if (Hindemith.InputParameters.getTempo() > 100) music_output.add("c4ww Rww");
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
//            if (prog_built) {
//                boolean root_interval_consonant = false;
//                int root_interval = abs(cand_pitch%12 - key_transpose);
//                for (Integer consonance : root_consonances) {
//                    if (root_interval == consonance) root_interval_consonant = true;
//                }
//                if(root_interval_consonant) {
//                    //DEBUG
//                    System.out.println(cand_pitch + " consonant with root " + key_transpose );
//                }
//                else {
//                    if (is_accent ) {
//                        myPC.decrementRank(Decrements.dissonant_with_root);
//                        //DEBUG
//                        System.out.println(cand_pitch + " dissonant accent with root " + key_transpose);
//                    }
//                    else {
//                        //DEBUG
//                        System.out.println(cand_pitch + " dissonant with root but note not accented" );
//                    }
//                }                 
//            }

            //randomly decrement non-tonics
            if (cand_pitch%12 != key_transpose){
                //DEBUG
                //System.out.println(cand_pitch + " is not tonic");
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
                //System.out.println(cand_pitch + " outside range " + melody_line.getRangeMin() + "-" + melody_line.getRangeMax());                
            }

            //decrement too far from pitch center
            if (abs(cand_pitch - pitch_center) > 16) {
                 myPC.decrementRank(Decrements.remote_from_pitchcenter);
                 //DEBUG
                 //System.out.println(cand_pitch + " too far from pitch center" + pitch_center);               
            } 
        }        
        return pitch_candidates; 
    }

    public static PitchCandidate melodicCheck(PitchCandidate myPC, ModeModule my_mode_module,
        int melody_pitch_count, Integer previous_melody_pitch, Integer previous_melodic_interval) {

            int cand_pitch = myPC.getPitch();
            int melody_motion_to_cand = 0;
            
            //DEBUG
            //System.out.println("melodicCheck evaluating pitch candidate " + cand_pitch);
            //System.out.println("********************************************************");
            
            if (melody_pitch_count > 0) {
                melody_motion_to_cand = cand_pitch - previous_melody_pitch;
                //System.out.println();
                //Check if the melodic motion to the pitch candidate is improbable or impossible within the mode. 
                double thresh = 0;
		Double threshornull = my_mode_module.getMelodicMotionProbability(cand_pitch, previous_melody_pitch, 0, 0);
		if (threshornull == null){
                    //DEBUG
                    //System.out.println("motion is not possible in this melodic mode");
                    myPC.decrementRank(Decrements.impossible_melodic_motion);
                }
                else {
                    if (threshornull < .05) {
                        myPC.decrementRank(Decrements.improbable_melodic_motion);
                        thresh = threshornull;
                        //DEBUG
                        //System.out.println("motion is unlikely in this mode" );
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
                
                for (PitchCount my_pitch_count: pitch_counts) {
                    //System.out.println("found pitch_count of " + my_pitch_count.getCount() + " for pitch " + my_pitch_count.getPitch());
                    if(my_pitch_count.getPitch()%12 == previous_melody_pitch%12) {
			//DEBUG
                        //System.out.println("pitch class " + previous_melody_pitch%12 + " occurs " + my_pitch_count.getCount() + " times in the melody so far");
                        if(my_pitch_count.getCount() > InputParameters.sample_size)     
                        for (MotionCount my_motion_count: melody_motion_counts){
                            //DEBUG
                            //System.out.println("pitch_count for " + previous_melody_pitch %12 + " = " + my_pitch_count.getCount());
                            //System.out.println("motion from " + my_motion_count.getPreviousPitch()%12 + " to " + my_motion_count.getSucceedingPitch()%12 + " has already occurred "+ my_motion_count.getCount() + " times");
                            if (my_motion_count.getPreviousPitch()%12 == previous_melody_pitch %12 && my_motion_count.getSucceedingPitch()%12 == cand_pitch %12) {
                                double actual = my_motion_count.getCount()/(double)my_pitch_count.getCount();
                                //DEBUG
                                //System.out.println("found " + my_motion_count.getCount() + " instances of motion from " + previous_melody_pitch %12 + " to " +cand_pitch %12 );
                                //System.out.println("frequency of motion from " + previous_melody_pitch %12 + " to " + cand_pitch%12 + " = " + actual);       
                                if (actual >= thresh) {
                                    myPC.decrementRank(Decrements.melodic_motion_quota_exceed);
                                    //DEBUG
                                    //System.out.println(cand_pitch %12 + " is approached too often from " + previous_melody_pitch %12);
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
                            //System.out.println(previous_melody_pitch + " duplicates previous trough too often Decremented by " + (Decrements.peak_trough_quota_exceed * (trough_count -1)));
                        } 
                }        
                if (previous_melodic_interval > 0 && melody_motion_to_cand <0){ // will there be a trough?
                        if (previous_melody_pitch == peak && peak_count > 1) {
                            myPC.decrementRank(Decrements.peak_trough_quota_exceed * (peak_count -1));
                            //DEBUG
                            //System.out.println(previous_melody_pitch + " duplicates previous peak too often. Decremented by " + (Decrements.peak_trough_quota_exceed * (peak_count -1)));
                        } 
                }
				
                //Motion after Leaps checks
                //First check if the melody does not go in opposite direction of leap
                // then check if there are two successive leaps in the same direction
                if (previous_melodic_interval > 4 && melody_motion_to_cand > 0){
                    myPC.decrementRank(Decrements.bad_motion_after_leap);
                    //DEBUG
                    //System.out.println("motion of " + melody_motion_to_cand + " from " + previous_melody_pitch + " to "+ cand_pitch + " would be  bad motion after leap");
                    if (melody_motion_to_cand > 4) {
                        myPC.decrementRank(Decrements.successive_leaps);
                        //DEBUG
                        //System.out.println("leap of " + melody_motion_to_cand + " to " + cand_pitch + " after leap of " + previous_melodic_interval +" would be a successive leap");
                    }
                        
                }    
                if (previous_melodic_interval < -4 && melody_motion_to_cand < 0){
                    myPC.decrementRank(Decrements.bad_motion_after_leap);
                    //DEBUG
                    //System.out.println("motion of " + melody_motion_to_cand + " from " + previous_melody_pitch +" to "+ cand_pitch + " would be bad motion after leap");
                    if (melody_motion_to_cand < -4) {
                        myPC.decrementRank(Decrements.successive_leaps);  
                        //DEBUG
                        //System.out.println("leap of " + melody_motion_to_cand + " to " + cand_pitch + " after leap of " + previous_melodic_interval +" would be a successive leap");
                    }
                }   
            }           
        return myPC; 
    }  
    
    public static void setPeakTroughPitchCount(int test_peak, int test_peak_count, int test_trough, int test_trough_count, 
        ArrayList<PitchCount> test_pitch_counts, ArrayList<MotionCount> test_motion_counts) {
        peak = test_peak;
        trough = test_trough;
        peak_count = test_peak_count;
        trough_count = test_trough_count;
        pitch_counts = test_pitch_counts;
        melody_motion_counts = test_motion_counts;
    }
    
    //THIS HAS NOT BEEN TESTED
    public static PitchCandidate harmonicChecks(PitchCandidate myPC, MelodicNote CF_note, Integer previous_pitch_cf,
        Integer previous_melody_pitch, MelodicNote fragment_note, int canon_transpose_interval, int cp_transpose_interval_index, int cf_transpose_interval_index ){
        
        Boolean large_dissonance_bad = InputParameters.large_dissonance_bad;
        Integer [] consonances = InputParameters.consonances;
        Integer [] perfect_consonances = InputParameters.perfect_consonances;
        Integer [] root_consonances = InputParameters.root_consonances;
        Integer cf_pitch = CF_note.getPitch();
        if (CF_note.getRest()) cf_pitch = previous_pitch_cf;

        Integer cand_pitch = myPC.getPitch() + canon_transpose_interval;            
        Integer cand_prev_pitch = previous_melody_pitch + canon_transpose_interval;
        Integer melody_motion_to_cand = cand_pitch  - cand_prev_pitch;
        
        Integer this_interval = (cand_pitch - cf_pitch);
  
        
        
        if (cp_transpose_interval_index < cf_transpose_interval_index) {
            this_interval = (cf_pitch - cand_pitch);
        }
        
        if (this_interval < 0) {
            this_interval = abs(this_interval);
            System.out.println("OVERLAPPING VOICES!!");
            myPC.decrementRank(Decrements.overlapping_voices);
        }

        Integer this_interval_class = this_interval%12;
        

        //if cf start time is before CP start time then previous interval is between CF pitch and cand_prev_pitch
        //line below is wrong - fix it!!!

        Double cp_start_time = fragment_note.getStartTime();
        Double cf_start_time = CF_note.getStartTime();

        Boolean directm = false;
        boolean this_interval_consonant = false;
        boolean this_interval_perf_consonant = false;
        boolean cand_prev_cf_diss = true;
        boolean previous_interval_consonant = false;
        boolean previous_interval_perf_consonant = false;
        boolean use_CF_accent = false;
        boolean major_dissonance = false;
        
        
        System.out.println("evaluating pitch candidate " + cand_pitch + " against " + cf_pitch);

        //is this interval consonant
        for (Integer consonance : consonances) {
            if (this_interval_class == consonance){
                this_interval_consonant = true; 
                break;
            }
        }

        //is this interval perfect consonance
        for (Integer consonance : perfect_consonances) {
            if (this_interval_class == consonance){
                this_interval_perf_consonant = true; 
                break;
            }
        }

        if(this_interval_consonant) {
            //System.out.println(cand_pitch + " against " + cf_pitch + "is consonant");
            if (this_interval_class ==0) {//decrement if an octave
                myPC.decrementRank(Decrements.octave);
            }
        }

        else {
            //System.out.println(cand_pitch + " against " + cf_pitch + "is dissonant");
            myPC.decrementRank(Decrements.dissonance);
            //decrement if a severe dissonance
            if ((this_interval_class == 1 || this_interval_class == 6 || this_interval_class == 11)&& (this_interval <36 || large_dissonance_bad)){
                myPC.decrementRank(Decrements.severe_dissonance);
                myPC.set_minor_9th();
                major_dissonance = true;
            }
            //decrement accented dissonance
            if (CF_note.getStartTime() > cp_start_time)
                use_CF_accent = true;

            if (!use_CF_accent) {
                if (fragment_note.getAccent() && (this_interval <36 || large_dissonance_bad)) {
                    //System.out.println("caught accented dissoance");
                    myPC.decrementRank(Decrements.accented_dissonance);
                    myPC.set_accent_diss();
                }                   
            }
            
            else {
                if (CF_note.getAccent() && (this_interval <36 || large_dissonance_bad)) {
                    //System.out.println("caught accented dissoance");
                    myPC.decrementRank(Decrements.accented_dissonance);
                    myPC.set_accent_diss();
                }
            }
            
            if (major_dissonance && cp_start_time == CF_note.getStartTime()){
                myPC.decrementRank(Decrements.accented_severe_dissonance);
            }

        }
        
        if (previous_pitch_cf == 1111) return myPC; //ie stop if this is the first note of the counterpoint in the piece
        if (cand_prev_pitch > 1000) return myPC;
        
        Integer  previous_interval = (cand_prev_pitch  - previous_pitch_cf);


        if (cp_transpose_interval_index < cf_transpose_interval_index) {
            previous_interval= (previous_pitch_cf - cand_prev_pitch);
        }
                  
        if (previous_interval < 0) {
            previous_interval = abs(previous_interval);
            System.out.println("OVERLAPPING VOICES!! in previous interval");
        }
    
        Integer previous_interval_class = previous_interval%12;  
        Integer melodic_motion_to_ = cf_pitch - previous_pitch_cf;
        
        //check for pitch candidate dissonance against previous cantus firmus
        for (Integer consonance : consonances) {
            if (abs(cand_pitch - previous_pitch_cf)%12 == consonance) {
                cand_prev_cf_diss = false;    
            }
        }         
        if (cand_prev_cf_diss) {
            myPC.decrementRank(Decrements.diss_cp_previous_cf);
        }

        //compute whether previous_interval consonant
        for (Integer consonance : consonances) {
            if (previous_interval_class == consonance){
                previous_interval_consonant = true;
                for (Integer perfect_consonance : perfect_consonances) {
                    if (previous_interval_class == perfect_consonance) {
                        previous_interval_perf_consonant = true;
                        break;
                    }
                }
                break;
            } 
        }
        

        //check for same type of consonance
        if (previous_interval_consonant && (previous_interval_class == this_interval_class) ){
            myPC.decrementRank(Decrements.seq_same_type_cons);
        }
        //check for sequence of dissonances
        if(!previous_interval_consonant && !this_interval_consonant) {
            myPC.decrementRank(Decrements.seq_of_diss);
            if(this_interval_class == previous_interval_class ){
                myPC.decrementRank(Decrements.seq_same_type_diss);
            }
        }

        //check for too long a sequence of same interval
        if (previous_interval_class == this_interval_class) {
            same_consonant_count++;
            if (same_consonant_count > same_consonant_threshold) {
                myPC.decrementRank(Decrements.seq_of_same_cons);
            }                  
        }
        else {
            same_consonant_count =0;
        }


        //if CF starts before CP 
        if (cp_start_time > cf_start_time){
        //the following  checks rely on knowing motion to pitch candidate from previous pitch
        //check for a bad approach to a dissonance from a consonance
        //ie CP pitch approached by greater than a step
            if (previous_interval_consonant){
                if ((!this_interval_consonant) && abs(melody_motion_to_cand) >2) {
                    myPC.decrementRank(Decrements.bad_diss_approach_from_cons);
                }    
            }
            //check for a bad approach to consonance from dissonance
            else if (this_interval_consonant){
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
                if (this_interval_consonant) {
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

            if (this_interval_perf_consonant) {
                if (previous_interval_perf_consonant && directm)	{
                    if (this_interval == previous_interval ){
                        myPC.decrementRank(Decrements.parallel_perf_consonance);          
                    }
                    else {
                        //check for direct motion into a perfect consonance
                        myPC.decrementRank(Decrements.direct_motion_perf_cons);
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
	return myPC;
    }
    public static MelodicVoice buildFragmentPitches (MelodicVoice alter_me, int number_of_voices, ModeModule my_mode_module){
        
        MelodicVoice return_me = new MelodicVoice();
        same_consonant_count = 0;
        same_inv_consonant_count = 0;
        ArrayList<Integer> pitch_candidate_values = new ArrayList();
        ArrayList<PitchCandidate> pitch_candidates = new ArrayList();
        MelodicNote this_cf = null;
        ArrayList<MelodicNote> holdover_cf = new ArrayList();
        ArrayList<MelodicNote> previous_pitch_cf = new ArrayList();
        ArrayList<LinkedList> built_voice_queues = new ArrayList();
        //COMPUTE PITCH CENTER
        Integer pitch_center = my_mode_module.getPitchCenter(alter_me.getRangeMin(), alter_me.getRangeMax());
        

	//CREATE A CHORD PROGRESSION FROM THE HARMONIC PROGRESSION
                
        if (!transpose_arrayS.isEmpty()) {
            //CREATE CF STACKS FROM ARRAY OF TRANSPOSED FRAGMENTS
            built_voice_queues = createbuilt_voice_queues();
            //CREATE HOLDOVER CF ARRAY
            holdover_cf = createHoldoverCFArray(built_voice_queues.size());
            //CREATE PREVIOUS CF PITCH ARRAY
            previous_pitch_cf = createPreviousCFPitchArray(built_voice_queues.size());
        }
        else {
            //DEBUG
            System.out.println("********");
            System.out.println("built voices Empty - start first melody");
        }
        
	//FOR EACH UNPITCHED NOTE IN THE CURRENT FRAGMENT
        for (int fragindex = 0; fragindex < alter_me.getVoiceLength(); fragindex++){ //for each melodic note in the fragment
            
            MelodicNote fragment_note = alter_me.getMelodicNote(fragindex);
            System.out.println("pulling unpitched fragment note ");
            System.out.println(" this note has start time: " + fragment_note.getStartTime());
            System.out.println(" this note has duration: " + fragment_note.getDuration());

            
            //IF THE UNPITCHED NOTE IS A REST CONTINUE TO NEXT UNPITCHED NOTE
            if (fragment_note.getRest()) {
                return_me.addMelodicNote(fragment_note);
                melody_line.addMelodicNote(fragment_note);
                System.out.println("this note is a rest so let's continue to the next unpitched note");
                continue;
            }
            
            //OTHERWISE....
            //GET EFFECTIVE DURATION OF THE NOTE
            Double effective_duration = getEffectiveDuration(alter_me, fragment_note, fragindex);
            System.out.println(" this note has effective duration (duration plus subsequent rests: " + effective_duration);
            
            //GET WHETHER NOTE IS ACCENTED
            Boolean got_accent = fragment_note.getAccent();
            if (got_accent) System.out.println(" note is accented ");
            //key_transpose = getKeyTranspose(chord_prog_stack, fragment_note);
            key_transpose = 0; //NOTE FOR THE CANON WE ARE IGNORING HARMONIC PROG AND KEY TRANSPOSE

            // GET PITCH CANDIDATE VALUES FOR THE FRAGMENT NOTE BASED ON PREVIOUS PITCH
            // THESE ARE ACTUALLY MELODIC MOVES FROM THE PREVIOUS PITCH
            if(melody_line.getLatestPitch() == null) { // If there is no previous pitch ie this is the first note 
                System.out.println("this is the first note of the canon melody - you should only see this message once");
                pitch_candidate_values = my_mode_module.getFirstNotePitchCandidates(alter_me.getRangeMin(), alter_me.getRangeMax(), key_transpose);
                //DEBUG
                //System.out.println("using first note pitch candidates");
            }
            else {
                if (melody_line.getLatestMelodicInterval() != null && melody_line.getLatestPitch() != null)
                pitch_candidate_values = my_mode_module.getPitchCandidatesGeneric(melody_line.getLatestPitch());
                //DEBUG
                //if (pitch_candidate_values.isEmpty()) System.out.println("EMPTY ARRAY!!!!");
            }
            System.out.println(" the pitch candidate pitch integers returned by the mode module are ");
            for (Integer each : pitch_candidate_values ) {
                System.out.print(each + " ");
            }
            System.out.println();
            
            //THIS STEP CREATES PITCH CANDIDATE OBJECTS FROM THE  PITCH CANDIDATE INTEGER VALUES RETURNED BY MODE MODULE
            for (Integer pitch_candidate_value : pitch_candidate_values) {
                PitchCandidate myPC = new PitchCandidate();
                myPC.setPitch(pitch_candidate_value);
                pitch_candidates.add(myPC);
            }
            
            System.out.println(" the pitch candidates are: ");
            for (PitchCandidate each : pitch_candidates ) {
                System.out.println(each.getPitch() + " with rank " + each.getRank() +" and accent dissonance " + 
                        each.get_accent_diss() + " and min 9th " + each.get_accented_minor_9th());   
            }           
            
            //EACH PITCH CANDIDATE IS RANKED VIA A VETTING METHOD 
            pitch_candidates = PitchCandidateVetting(pitch_candidates, true, got_accent, pitch_center, key_transpose,  melody_line);            
            
            System.out.println(" after initial vetting the pitch candidates rankings are: ");
            for (PitchCandidate each : pitch_candidates ) {
                System.out.println(each.getPitch() + " with rank " + each.getRank() +" and accent dissonance " + 
                        each.get_accent_diss() + " and min 9th " + each.get_accented_minor_9th());   
            }             
            //Integer previous_melodic_interval = 1111; //Dummy value to pass to melodic check - not used.
            //if (melody_line.getLatestMelodicInterval() != null) previous_melodic_interval = melody_line.getLatestMelodicInterval();
            
            //EACH PITCH CANDIDATE IS CHECKED FOR QUALITY OF MELODIC MOTION 
            if (melody_line.getLatestPitch() != null) {
                for (PitchCandidate pitch_candidate: pitch_candidates) {
                    pitch_candidate = melodicCheck(pitch_candidate, my_mode_module, 
                        voice_pitch_count, melody_line.getLatestPitch(),  melody_line.getLatestMelodicInterval());
                }
                System.out.println(" after melodic checking the pitch candidates are: ");
                for (PitchCandidate each : pitch_candidates ) {
                    System.out.println(each.getPitch() + " with rank " + each.getRank() +" and accent dissonance " + 
                            each.get_accent_diss() + " and min 9th " + each.get_accented_minor_9th());   
                } 
            }
            else {
                System.out.println("we are at the beginning of canon melody so skip melodic check");
            }
                
            //NOW WE EVALUATE EACH PITCH CANDIDATE HARMONICALLY AGAINST SIMULTANEOUS NOTES IN EACH PREVIOUSLY
            //COMPOSED FRAGMENT AND ITS TRANSPOSITIONS
            if (!built_voice_queues.isEmpty()){
                int vector_shift  = built_voice_queues.size()/number_of_voices;//gets number of fragments that have already been composed
                for (int b = 0; b < built_voice_queues.size(); b++){
                    //System.out.println("there are " + built_voice_queues.size() + " fragment transpositions in the queue");
                    //System.out.println("there is/are " + vector_shift + " melody fragments(s) that have been composed already");
                    
                    System.out.println("this note is in fragment " + (vector_shift + 1) + " of " + number_of_voices);
                    System.out.println(" another way to say it is the fragment has index " + vector_shift);
                    System.out.println(" we are evaluating pitch candidates against corresponding notes in fragment " + b/number_of_voices + " transposition " + b%number_of_voices);
                    int canon_transpose_interval_index = calculate_canon_transpose_interval(vector_shift, b, number_of_voices, InputParameters.transpose_interval_array);
                    
                    Integer canon_transpose_interval = InputParameters.transpose_interval_array[canon_transpose_interval_index ];
//                    if (canon_transpose_interval < 7) {
//                        canon_transpose_interval = canon_transpose_interval + canon_transpose_interval_index*12;
//                    }
//                    else if (canon_transpose_interval_index > 0) {
//                        canon_transpose_interval = canon_transpose_interval + (canon_transpose_interval_index-1)*12;
//                    }
                    canon_transpose_interval = canon_transpose_interval + canon_transpose_interval_index*12;
                    //DEBUGS                    
                    System.out.println(" Runing harmonic checks with pitch candidates transposed up by " + canon_transpose_interval);
                    System.out.println(" the pitch candidates have transpose index " + canon_transpose_interval_index);
                    System.out.println(" the voice of the cfs against which we evaluate the candidates has transposition index " + b%number_of_voices);
                    if (canon_transpose_interval_index > b%number_of_voices) {
                        System.out.println("we expect candidates to be above all or most cf notes");            
                    }
                    else if (canon_transpose_interval_index < b%number_of_voices) {
                        System.out.println("we expect candidates to be below all or most cf notes");
                    }
                    else {
                         System.out.println("something is wrong");
                    }
                    do {
                        boolean skip_me = false; //Assume we won't skip harmonic checks
                        
                        //A crude bit of exception handling
                        if (holdover_cf.isEmpty() && built_voice_queues.get(b).isEmpty()){
                            System.out.println("both holdover vector and built_voices_queues are empty. this isn't normal");
                            break; 
                        }
                        
                        if (!holdover_cf.isEmpty()){
                            
                            //DEBUG START ***********************
                            //The below is really just a big debug to print contents of holdover array
//                            for (int h = 0; h < holdover_cf.size(); h++) {
//                                MelodicNote entre = holdover_cf.get(h);
//                                int printpitch;
//                                if (entre == null) printpitch = -1;
//                                else printpitch = entre.getPitch();
//                                System.out.print(printpitch + ",");
//                            }
//                            System.out.println();
                            //DEBUG END **************************
                            
                            //If a pitch from a previous voice is held over into this one
                            //we evaluate our pitch candidates against it before popping a note from the CF linked list
                            if (holdover_cf.get(b) != null) {
                                this_cf = holdover_cf.get(b);
                                System.out.println("heldover pitch " + this_cf.getPitch() + " with start time " + this_cf.getStartTime() + " and end time " + this_cf.getPreviousDuration() +" is held over into this fragment note");
                            }
                        }
                        //pop the note from CF linked list if no note is heldover or if built voice queues 
                        //has no notes in its queue for this CF voice
                        if (!built_voice_queues.get(b).isEmpty() && holdover_cf.get(b) == null) {
                                this_cf = (MelodicNote)built_voice_queues.get(b).pop();
                                System.out.println("new cf pitch " + this_cf.getPitch() + " with start time " + this_cf.getStartTime() + " and end time " + this_cf.getPreviousDuration() +" is popped from voice " + b );
                        }

                        //If the CF note is a rest  the previous cf pitch is the one we are concerned with
                        //We normally send both this cf and the previous to the harmonic check
                        //the logic in that method asks if this cf is a rest (again)
                        //and then if so evaluates pitch candidates against the previous cf pitch
                        //the exceptions being 1. if the first note(s) of the cf voice have rest flag 
                        //or 2. if the accumuluated silence in the cf voice between when the previous cf note sounded
                        //and when this fragment's note sounded is greater than a half note. 
                        if (this_cf.getRest()) {
                            if (fragment_note.getStartTime() - previous_pitch_cf.get(b).getPreviousDuration() > .5 || //if the cf rest is longer than a half note the power of the preceding note doesn't carry
                                previous_pitch_cf.get(b).getPitch() == 1111 ) skip_me = true; //if there is no previous cf pitch ie we are at beginning of cf voice
                        }
                        
                        Integer got_latest_pitch = 1111;
                        if (melody_line.getLatestPitch() != null) got_latest_pitch = melody_line.getLatestPitch();
                        
                        
                        //or 3. if the accumulated silence between when the fragment note ended and when the CF starts is greater than .5
                        if (effective_duration - fragment_note.getDuration() > .5 &&
                                this_cf.getStartTime() > fragment_note.getPreviousDuration() + .5) skip_me = true;
                        if (skip_me) {
                            System.out.println("Harmonic Checks were skipped ");
                        }
                        
                        if (!skip_me){
                            System.out.println("running harmonic checks for pitch candidates against " + this_cf.getPitch());
                            System.out.println("w previous pitch " + (got_latest_pitch + canon_transpose_interval));
                            System.out.println("and previous cf pitch in cf  " + b + " is "+ previous_pitch_cf.get(b).getPitch());
                            for (PitchCandidate pitch_candidate: pitch_candidates) {
                                pitch_candidate = harmonicChecks(pitch_candidate, this_cf, previous_pitch_cf.get(b).getPitch(),
                                                                got_latest_pitch, fragment_note,  canon_transpose_interval, canon_transpose_interval_index, (b%number_of_voices) );
                            }
//                            System.out.println(" after harmonic checking against transposition " + b + " the pitch candidates are: ");
//                            for (PitchCandidate each : pitch_candidates ) {
//                                System.out.println(each.getPitch() + " with rank " + each.getRank() +" and accent dissonance " + 
//                                        each.get_accent_diss() + " and min 9th " + each.get_accented_minor_9th());   
//                            }                             
                            
                        } 
                        //if cf note extends into next melody note
                        //reassign holdover note in cf voice, you'll break out of while loop after this
                        if (this_cf.getPreviousDuration() > fragment_note.getStartTime() + effective_duration) {  
                            holdover_cf.set(b, this_cf); 
                            //System.out.println("adding " + this_cf.getPitch() + " to holdover_cf index " + b);
                        }
                        else holdover_cf.set(b, null);//note it doesn't matter if holdover is rest or pitched.
                        //if the cf isn't a rest its the new previous aka "current" cf pitch
                        if (!this_cf.getRest()) {
                            System.out.println(" adding " + this_cf.getPitch() + " to index " + b + " of previous cf array");
                            previous_pitch_cf.set(b, this_cf);
                            System.out.println("contents of previous cf array now is: ");
                            int prev_note_count = 0;
                            for (MelodicNote prev_note: previous_pitch_cf) {
                                System.out.println(prev_note_count + " " + prev_note.getPitch());
                                prev_note_count++;
                            }
                        }
                        
                    } while (this_cf.getPreviousDuration() < fragment_note.getStartTime() + effective_duration); //loop while CP note extends into next CF in voice
                }
            }
            
            PitchCandidate pitch_winner = pickWinner(pitch_candidates);
            
            
            //DEBUG
            System.out.println("CP winner " + pitch_winner.getPitch() + " to note " + fragindex + " of fragment");
            if (pitch_winner.get_accented_minor_9th()) System.out.println("CP Winner " + pitch_winner.getPitch() + " is accented minor 9th");
            if (pitch_winner.get_accent_diss()) System.out.println("CP Winner " + pitch_winner.getPitch() + " is accented dissonance");
            
            pitch_candidates.clear();
            fragment_note.setPitch(pitch_winner.getPitch());
            return_me.addMelodicNote(fragment_note); 
            melody_line.addMelodicNote(fragment_note);
            calcPeaksTroughs(pitch_winner);
            //DEBUG
            System.out.println("canon melody:");
            melody_line.showNoteArray();
            if (melody_line.getLatestMelodicInterval() != null) System.out.println("last melodic interval in this line is " 
                    + melody_line.getLatestMelodicInterval());
            voice_pitch_count++;    
        }        
        return return_me;
    }//loop through next CP note     
    
    public static PitchCandidate pickWinner(ArrayList<PitchCandidate> pitch_candidates){
        //pick highest ranking pitch candidate -> return_me[i]
        //System.out.println("start Pick Winner");
        ArrayList<PitchCandidate> pitch_winners = new ArrayList();
        for (PitchCandidate myPC : pitch_candidates){
            //DEBUG
            //System.out.println( "pitch candidate pitch: "+ myPC.getPitch() + " and rank: " + myPC.getRank() + " and accent_diss = " + myPC.get_accent_diss() + " and severe dissonance = " + myPC.get_accented_minor_9th());
            if (pitch_winners.isEmpty()) {
                pitch_winners.add(myPC);
                //DEBUG
                //System.out.println("pitch_winners is empty. adding " + myPC.getPitch() + " with rank" + myPC.getRank());
            }
            else if (myPC.getRank() > pitch_winners.get(0).getRank()) {
                     pitch_winners.clear();
                     pitch_winners.add(myPC);
                     //DEBUG
                     //System.out.println("after emptying pitch_winners adding " + myPC.getPitch() +" with rank "+ myPC.getRank());
                }
            else if (Objects.equals(myPC.getRank(), pitch_winners.get(0).getRank())) {
                pitch_winners.add(myPC);
                //DEBUG
                //System.out.println("adding " + myPC.getPitch() + " to pitch_winners with rank " + myPC.getRank());
            }
            
        }
        PitchCandidate pitch_winner = pitch_winners.get(0);
         
        if (pitch_winners.size() >1) pitch_winner = pitch_winners.get(roll.nextInt(pitch_winners.size()));
        pitch_winners.clear();
        return pitch_winner;
    }    
    
    public static ArrayList<LinkedList> createbuilt_voice_queues () {
        ArrayList<LinkedList> built_voice_queues = new ArrayList();
        for (ArrayList<MelodicVoice> transpose_array : transpose_arrayS) { //For each block of fragment transpositions
            for (Iterator<MelodicVoice> it = transpose_array.iterator(); it.hasNext();) { //For each fragment transposition within that block
                MelodicVoice transposition = it.next();
                LinkedList <MelodicNote> cf_stack = new LinkedList<>(); //Create a linked list for melodic notes
                ArrayList <MelodicNote> temp = transposition.getNoteArray(); //extract the note array from the fragment transposition
                for (MelodicNote b_voice_note : temp){ //each note in this note array goes into the linked list
                    cf_stack.add(b_voice_note);
                }
            built_voice_queues.add(cf_stack); //this stack is added to built_voice_queues
            //DEBUG
            //System.out.println("created stack of melodic notes for each previously built voice ");
            }
        }
        return built_voice_queues;
    }
    
    public static void calcPeaksTroughs (PitchCandidate pitch_winner) {
        //Calculate Peaks and Trough note counts
        if (melody_line.getLatestMelodicInterval() == null) {
            System.out.println("melody line latest Melodic Interval is null");
            return;
        }

        
        if (melody_line.getLatestPitch() == null) {
            System.out.println("melody line latest pitch is null");
            return;
        }
        
        if (pitch_winner.getPitch() == null) {
            System.out.println("pitch winner is null");
            return;
        }

        
        
        if (melody_line.getLatestMelodicInterval()  < 0 && pitch_winner.getPitch() - melody_line.getLatestPitch()  > 0 ) {// will there be a change in direction from - to +  ie trough?
            if (melody_line.getLatestPitch() == trough) {
                trough_count++;

            }
            else if (melody_line.getLatestPitch() < trough)  { //used to be != trough ... why??

                trough = melody_line.getLatestPitch();
                trough_count = 1;
                //DEBUG
                //System.out.println("setting new trough = " + previous_melody_pitch);

            }
        }

        if (melody_line.getLatestMelodicInterval() > 0 && pitch_winner.getPitch() - melody_line.getLatestPitch()  < 0 ) {// will there be a change in direction from - to +  ie trough?
            if (melody_line.getLatestPitch() == peak) peak_count++;
            else{
                if (melody_line.getLatestPitch() > peak) {
                peak = melody_line.getLatestPitch();
                peak_count = 1;
                //DEBUG
                //System.out.println("setting new peak = " + previous_melody_pitch);    
                }

            }
        }    

        // Calculate pitch counts
        boolean add_pitch = true;
        for(int pc = 0; pc < pitch_counts.size(); pc++) {
            if (pitch_counts.get(pc).getPitch() == pitch_winner.getPitch()%12) {
                pitch_counts.get(pc).incrementCount();
                add_pitch = false;
                break;
            }        
        }
        if (add_pitch == true){
        PitchCount my_pitch_count = new PitchCount(pitch_winner.getPitch()%12);
        my_pitch_count.incrementCount();
        pitch_counts.add(my_pitch_count);
        }

        //Caldulate motion counts
        boolean add_motn = true;
        for(int mc = 0; mc< motion_counts.size(); mc++){
            //DEBUG
            //System.out.println("in 'motion counts ' found motion count " + motion_counts.get(mc).getPreviousPitch() + " / " + motion_counts.get(mc).getSucceedingPitch());
            if (motion_counts.get(mc).getPreviousPitch() == melody_line.getLatestPitch() %12 && motion_counts.get(mc).getSucceedingPitch() == pitch_winner.getPitch() %12) {
                //DEBUG
                //System.out.println("Motion count from " + previous_melody_pitch %12 + " to " + previous_melody_pitch %12 + " FOUND");
                motion_counts.get(mc).incrementCount();
                add_motn = false;
            }   
        }
        if(add_motn == true) {
            //DEBUG
            //System.out.println("Motion count from " + previous_melody_pitch %12 + " to " + previous_melody_pitch %12 + " not found. Adding");
            MotionCount my_motionCount = new MotionCount(melody_line.getLatestPitch() %12, pitch_winner.getPitch() %12);
            motion_counts.add(my_motionCount);
        }
    }
    
    public static Integer calculate_canon_transpose_interval(Integer vector_shift, Integer b, Integer number_of_voices, int [] transpose_interval_array) {
        //CALCULATE CANON_TRANSPOSE_VALUE THAT THE FRAGMENT NOTE WILL BE TRANSPOSED TO WHEN 
        //IT APPEARS AGAINST A PARTICULAR TRANSPOSITION OF A FRAGMENT. 'b'
        if (number_of_voices == 0 ) {
            System.out.println("number of voices is zero - why?");
            return 0;
        }
        Integer transposition_multiplier_vector_seed[] = new Integer[number_of_voices];        
        //Fill in transposition_multiplier_vector_seed
        for (Integer i = 0; i < (number_of_voices); i++) {
           transposition_multiplier_vector_seed[i] = i;
        }
        Integer transposition_multiplier_vector[] = new Integer[number_of_voices ];
        if (b >0 && b%(number_of_voices) == 0 ) vector_shift = vector_shift - 1;
        //System.out.println("vector shift " + vector_shift);
        for (int seed : transposition_multiplier_vector_seed) {
            int new_index = (seed + vector_shift) %(number_of_voices);
            //System.out.println("placing seed " + seed + " into new index " + new_index );
            transposition_multiplier_vector[new_index] = seed;
        }  
        Integer transpose_interval_array_index = transposition_multiplier_vector[b%(number_of_voices)];

        return transpose_interval_array_index ;        
    }
    
    public static Double getEffectiveDuration(MelodicVoice alter_me, MelodicNote fragment_note, int fragindex) {
        //THIS ADDS THE DURATION OF ANY TRAILING RESTS TO THE NOTE'S OVERALL DURATION
        //SO THAT THE NOTE'S DURATION EXTENDS UP TO THE NEXT NON-REST NOTE
        //THIS ALLOWS US LATER TO EVALUATE PITCHES OF CF NOTES THAT OCCUR AFTER THE NOTE
        //AGAINST THE PITCH CANDIDATES OF THIS NOTE 
        //WE WANT TO DO THIS AS THE NOTE WILL STILL BE 'HEARD' EVEN THOUGH IT HAS STOPPED SOUNDING
        Double effective_duration = fragment_note.getDuration();
        Boolean next_note_has_pitch = false;
        int edi = 1;
        while (!next_note_has_pitch) {
            if ((fragindex + edi) == alter_me.getVoiceLength()) break;
            if (alter_me.getMelodicNote(fragindex+edi).getRest()) {
                effective_duration += alter_me.getMelodicNote(fragindex+edi).getDuration();
                edi++;
            }
            else next_note_has_pitch = true;
        }
        return effective_duration;
    }
    
    public static ArrayList<MelodicNote> createHoldoverCFArray(int size) {
        MelodicNote nullnote = null;
        ArrayList<MelodicNote> holdover_cf = new ArrayList();
        for(int h = 0; h < size; h++){ //create "blank" arrays the size of the built voice queue
            holdover_cf.add(nullnote);//effectively populates with nulls since this_cf has been initialized to null
        }       
        return holdover_cf;
    }
    
    public static ArrayList<MelodicNote> createPreviousCFPitchArray(int size) {
        MelodicNote dummynote = new MelodicNote();
        dummynote.setPitch(1111);
        ArrayList<MelodicNote> previous_pitch_cf = new ArrayList();
        for(int h = 0; h < size; h++){ //create "blank" arrays the size of the built voice queue
            previous_pitch_cf.add(dummynote); //the 1111 is a flag for "first pitch in the cf voice"
        }       
        return previous_pitch_cf;
    }
    
    public static Integer getKeyTranspose(LinkedList<MelodicNote> chord_prog_stack, MelodicNote fragment_note) {
        //COMPUTE KEY TRANSPOSE FOR THIS UNPITCHED NOTE
        if (harmonic_prog_built) {
              Double prog_start_time;
              do {
                MelodicNote prog_stack_note = chord_prog_stack.pop();
                prog_start_time = prog_stack_note.getStartTime();
                if (!prog_stack_note.getRest()) {
                    this_key.setPitch(prog_stack_note.getPitch());
                }
                //System.out.println("key transpose: " + this_key.getPitch()%12);
              } while (prog_start_time< fragment_note.getStartTime());
              key_transpose = this_key.getPitch()%12;
        }
        return key_transpose;
    }
    
    public void resetParams() {
        unbuilt_fragments.clear();
        motion_counts.clear();
        pitch_counts.clear();
        melody_motion_counts.clear();
        trough = 0;
        trough_count = 0;
        peak = 0;
        peak_count = 0;
        same_consonant_count = 0;
        key_transpose = 0;
        harmonic_prog_built = false;
        melody_line = new MelodicVoice();
        melody_pitch_count = 0;
        same_inv_consonant_count = 0;
        same_consonant_count = 0;
        voice_pitch_count = 0;
        transpose_arrayS.clear();
        harmonic_prog = new MelodicVoice();
        this_key = new MelodicNote();
        
    }
}
