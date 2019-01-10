 /*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package Hindemith;

import java.util.concurrent.atomic.AtomicBoolean;
import Hindemith.RhythmModule.*;
import Hindemith.ModeModules.*;
import static java.lang.Math.abs;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;
import java.util.logging.Logger; //the Java Logger
import java.util.Random;
import org.jfugue.*;




/**
 * This is the class that creates counterpoint from rhythm patterns and melodic
 * rules. It is always called in a thread so that it may be canceled by the 
 * user. 
 * @author Trick's Music Boxes
 */
public class CanonizerModel {
    public AtomicBoolean shouldThrow = new AtomicBoolean(false);
    static ArrayList<MelodicVoice> unbuilt_fragments = new ArrayList();
    static ArrayList<MelodicVoice> built_fragments = new ArrayList();
    static ArrayList<ArrayList> transpose_arrayS = new ArrayList();
    static ArrayList<MotionCount> motion_counts = new ArrayList();
    static ArrayList<PitchCount>pitch_counts = new ArrayList();
    static int same_consonant_threshold = 6;
    static Random roll = new Random();
    static int sample_size = 5;
    private static final Logger logger = Logger.getLogger("org.jfugue");
    static int trough = 200;
    static int trough_count = 0;
    static int peak = 0;
    static int peak_count = 0;
    static int same_consonant_count = 0;
    static int same_inv_consonant_count = 0;
    static int root_key = 0;
    static int key_transpose = 0;
    static MelodicVoice harmonic_prog = new MelodicVoice();
    static Boolean harmonic_prog_built = false;
    static MelodicNote this_key = new MelodicNote();
    byte instbyte = 70;
    static int transpose_interval = InputParameters.transpose_interval;   
    static int loops = InputParameters.loops;
    static int last_pitch;
    static boolean beginning_of_voice = true;
    static int voice_pitch_count = 0;
    static MelodicVoice melody_line = new MelodicVoice();
    static int previous_melodic_interval = 0;
    
    public  CanonizerModel() {
            resetParams();
            int piece_length = InputParameters.piece_length;
            int tempo_bpm = InputParameters.getTempo();
            //DEBUG
            //System.out.println("tempo =  " + tempo_bpm);
            ModeModule my_mode_module = InputParameters.my_mode_module;
            String [] voice_array = InputParameters.voice_array; 
            RhythmModule james = InputParameters.james;       
            int number_of_voices = voice_array.length;
            //System.out.println("number of voices " + number_of_voices);
            melody_line.setRange("tenor");
            //get rhythm patterns
            Pattern [] rhythm_patterns = james.generate(piece_length, number_of_voices);
            
                    
            //build blank Melodic Voice for each rhythm pattern
            for (int i = 0; i < number_of_voices; i++) {
                MelodicVoice this_fragment = new MelodicVoice();
                this_fragment.setRange(voice_array[i]);
                AccentListener my_accent_listener = new AccentListener();
                this_fragment.setNoteArray(my_accent_listener.listen(rhythm_patterns[i]));
                //DEBUG
                //System.out.println("adding voice " + i + " to unbuiltvoices");
                unbuilt_fragments.add(this_fragment);
            }

       

            for (int i = 0; i < number_of_voices; i++){ 
                //build contrapuntal voice
                
                
                //run buildFragmentPitches method on next unbuilt fragment
                int voice_index = i;
                //DEBUG
                System.out.println(" about to build voice pitches for "+ voice_index);
                MelodicVoice nextFragment = buildFragmentPitches(unbuilt_fragments.get(voice_index), number_of_voices, my_mode_module);

               
                //Printing voice strings to Standard Out
                ArrayList<MelodicNote> verify_array = nextFragment.getNoteArray();
                //DEBUGS
                System.out.println("Fragment " + voice_index + ":");
                for(MelodicNote verify: verify_array) { 
                  if (verify.getRest()) System.out.println("rest " + verify.getDuration() + "  " );
                   else System.out.println(verify.getPitch() + " " + verify.getDuration() + "   ");
                  }
                
                if (i == 0) {
                    harmonic_prog = nextFragment;
                    harmonic_prog_built = true;
                    voice_pitch_count = 0; //reset voice_pitch count as actual melody will start now. 
                }
                else {
                    //System.out.println("got fragment  - now will perform " + (number_of_voices -1) + " transpositions");
                    ArrayList<MelodicVoice> transposed_voices = new ArrayList();
                    for (int v = 0; v < number_of_voices -1; v++) {
                        //System.out.println("transposing fragment " + (i) + " by " + (v*transpose_interval));
                        //System.out.println("here is what transposed_voices looks like before I perform this transposition");
//                        for (MelodicVoice mary : transposed_voices) {
//                            mary.showNoteArray();
//                        }                      
                        MelodicVoice newTransposition = new MelodicVoice();
                        newTransposition.setNoteArray(nextFragment.transposeMe(v*transpose_interval));
                       // System.out.println("Here is what tranposed_voices looks like immediately after the transposition");
//                        for (MelodicVoice mary : transposed_voices) {
//                            mary.showNoteArray();
//                        }
                        //System.out.println("here is what nextFragment looks like after the transposition");
//                        nextFragment.showNoteArray();
//                       // System.out.println("will add this to transposed_voices at index " + v);
//                        newTransposition.showNoteArray();

                        transposed_voices.add(newTransposition);
                        //System.out.println("this is what transposed_voices looks like after I add the voice");
                        //for (MelodicVoice mary : transposed_voices) {
                        //    mary.showNoteArray();
                        //}
                    }
                    //System.out.println(" now adding the above array to transpose_arrayS");
                    transpose_arrayS.add(transposed_voices);
//                    for (ArrayList<MelodicVoice> mike : transpose_arrayS)
                    //for (MelodicVoice m : mike){
//                        m.showNoteArray();
//                    }
                } 
            }// end build contrapuntal voice
            
            unbuilt_fragments.clear();
            Pattern music_output = new Pattern();
            music_output.addElement(new Tempo (tempo_bpm));
            //UNPACKING START HERE
            for (byte i = 0; i <  number_of_voices -1 ; i++){
                Voice jf_voice = new Voice(i);
                Pattern voice_pattern = new Pattern();
                voice_pattern.addElement(jf_voice);
                voice_pattern.addElement(new Instrument(instbyte));
                String padRest = "R";
                for (int h = 0; h< piece_length; h++) padRest = padRest + "w";
                Pattern padPattern = new Pattern(padRest);
                for (int k = 0; k < i; k++)
                    for (int l = 0; l < number_of_voices; l++)
                        voice_pattern.add(padPattern);
//                Note padding_rest = new Note();
//                padding_rest.setRest(true);
//                padding_rest.setDuration(8); //change this
//                voice_pattern.addElement(padding_rest);
                Pattern loopPattern = new Pattern();
                //System.out.println(transpose_arrayS.size());
                //System.out.println("i = " + i);
                for (int jay = 0; jay < number_of_voices -1; jay++) {
                    ArrayList<MelodicVoice> fragment = transpose_arrayS.get(jay);
                    System.out.println("getting block " + i + " with " + fragment.size() + " melodic voices from transpose_arrayS" );
                    MelodicVoice tfrag = fragment.get(i);
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
                            if (!final_note.getAccent()) jf_note.setAttackVelocity((byte)40);
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
                for (int x = 0; x < loops; x++) {
                    voice_pattern.add(loopPattern);
                }
                music_output.add(voice_pattern);


                
            }// end create a jfugue musicstring from the built voice loop
           

            //save and play the pattern as a MIDI file
            //DEBUG
            System.out.println(music_output.getMusicString());
            PatternStorerSaver1.add_pattern(music_output);
            Player player1 = new Player();
            player1.play(music_output);
            //all done
        }
    
      
   

    public static MelodicVoice buildFragmentPitches (MelodicVoice alter_me, int number_of_voices, ModeModule my_mode_module){
        MelodicVoice return_me = new MelodicVoice();
        ArrayList<LinkedList> built_voice_queues = new ArrayList();
        //int previous_melody_pitch = -13;//null value for comparisons  - 
        same_consonant_count = 0;
        same_inv_consonant_count = 0;
        ArrayList<Integer> pitch_candidate_values = new ArrayList();
        ArrayList<PitchCandidate> pitch_candidates = new ArrayList();
        MelodicNote this_cf = null;
        MelodicNote nullnote = null;
        LinkedList <MelodicNote> chord_prog_stack = new LinkedList<>();
        Integer transposition_multiplier_vector_seed[] = new Integer[number_of_voices -1];
        ArrayList<MelodicNote> holdover_cf = new ArrayList();
        ArrayList<Integer> previous_cf_pitch = new ArrayList();
        //Fill in transposition_multiplier_vector_seed
        for (Integer i = 0; i < (number_of_voices - 1); i++) {
           transposition_multiplier_vector_seed[i] = i;
        }
        
        //DEBUG
        //System.out.println("voiceRange min " + alter_me.getRangeMin() + "   voicerange max " + alter_me.getRangeMax());
        Integer pitch_center = my_mode_module.getPitchCenter(alter_me.getRangeMin(), alter_me.getRangeMax());
        //DEBUG
        //System.out.println("pitchcenter = " + pitch_center);
        

	//Build Chord Progression
        if (harmonic_prog_built) {
            ArrayList <MelodicNote> chord_prog_temp = harmonic_prog.getNoteArray();
            for (MelodicNote b_voice_note : chord_prog_temp){
                chord_prog_stack.add(b_voice_note);
            }  
        }
	//Create stack from previously built voices        
        if (!transpose_arrayS.isEmpty()) {
            for (ArrayList<MelodicVoice> transpose_array : transpose_arrayS) {
                for (Iterator<MelodicVoice> it = transpose_array.iterator(); it.hasNext();) {
                    MelodicVoice transposition = it.next();
                    LinkedList <MelodicNote> cf_stack = new LinkedList<>();
                    ArrayList <MelodicNote> temp = transposition.getNoteArray();
                    for (MelodicNote b_voice_note : temp){
                        cf_stack.add(b_voice_note);
                    }
                built_voice_queues.add(cf_stack);
                //DEBUG
                //System.out.println("created stack of melodic notes for each previously built voice ");
                }
            }
            holdover_cf = new ArrayList();
            previous_cf_pitch = new ArrayList();
            for(int h = 0; h<built_voice_queues.size(); h++){
                previous_cf_pitch.add(1111);
                holdover_cf.add(nullnote);//effectively populates with nulls since this_cf has been initialized to null
            }
        }
        else {
            //DEBUG
            if (harmonic_prog_built) System.out.println("built voices Empty - start first melody");
            else System.out.println("start harmonic prog");
        }

	//For each note in fragment
        for (int fragindex = 0; fragindex < alter_me.getVoiceLength(); fragindex++){ //for each melodic note in the fragment
            MelodicNote fragment_note = alter_me.getMelodicNote(fragindex);
            //If a rest
            if (fragment_note.getRest()) {
                return_me.addMelodicNote(fragment_note);
                melody_line.addMelodicNote(fragment_note);
                continue;
            }
            
            Boolean got_accent = fragment_note.getAccent();
            
            //COMPUTE KEY TRANSPOSE
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

            // GET PITCH CANDIDATES for the CP note given the voice range and key transpose
            //Debug
            System.out.println("getting pitch candidates from my modemodule based on key " + key_transpose + "voicerange:  " + alter_me.getRangeMin() + " to " + alter_me.getRangeMax());
            if(voice_pitch_count == 0) { // If there is no previous pitch ie this is the first note 
                pitch_candidate_values = my_mode_module.getFirstNotePitchCandidates(alter_me.getRangeMin(), alter_me.getRangeMax(), key_transpose);
                //DEBUG
                System.out.println("using first note pitch candidates");
            }
            else {
                pitch_candidate_values = my_mode_module.getPitchCandidatesGeneric(melody_line.getLatestPitch());
                //DEBUG
                if (pitch_candidate_values.isEmpty()) System.out.println("EMPTY ARRAY!!!!");
            }
            //DEBUG
            System.out.println("Melody pitch " + voice_pitch_count + " from " + fragment_note.getStartTime() + " to " + fragment_note.getPreviousDuration());
            System.out.print("pitch candidates: ");
            
            
            for (Integer pitch_candidate_value : pitch_candidate_values) {
                //DEBUG
                System.out.print(pitch_candidate_value + " ");
                PitchCandidate myPC = new PitchCandidate();
                myPC.setPitch(pitch_candidate_value);
                pitch_candidates.add(myPC);
            }
            for (PitchCandidate this_PC : pitch_candidates) {
                System.out.println("pitchcandidate: " + this_PC.getPitch() + " with rank: " + this_PC.getRank());
            }
            
            System.out.println();
            pitch_candidates = PitchCandidateVetting(pitch_candidates, my_mode_module, harmonic_prog_built, got_accent, pitch_center);
            
            if (melody_line.getLatestMelodicInterval() != null && melody_line.getLatestPitch() != null)
                pitch_candidates = melodicCheck(pitch_candidates, my_mode_module, alter_me, 
                    pitch_center, voice_pitch_count, melody_line.getLatestPitch(),  melody_line.getLatestMelodicInterval(), got_accent, harmonic_prog_built);

            if (!built_voice_queues.isEmpty()){ 
                int vector_shift  = built_voice_queues.size()/(number_of_voices -1);
                for (int b = 0; b < built_voice_queues.size(); b++){
                //CALCULATE CANON_TRANSPOSE_VALUE HERE - YOU'LL PASS THIS AS AN ADDITIONAL PARAMETER TO HARMONIC CHECKS
                    Integer transposition_multiplier_vector[] = new Integer[number_of_voices -1];
                    if (b >0 && b%(number_of_voices -1) ==0 )vector_shift = vector_shift - 1;
                    //System.out.println("vector shift " + vector_shift);
                    for (int seed : transposition_multiplier_vector_seed) {
                        int new_index = (seed + vector_shift) %(number_of_voices - 1);
                        //System.out.println("placing seed " + seed + " into new index " + new_index );
                        transposition_multiplier_vector[new_index] = seed;
                    }  

                    int canon_transpose_interval = transpose_interval * transposition_multiplier_vector[b%(number_of_voices-1)];
                    //DEBUGS                    
                    System.out.println(" will start harmonic checks using transpose_interval " + canon_transpose_interval);
                    //System.out.println("Starting harmonic analysis of pitch candidates against contemoraneous pitches in voice "+ b);  
                    
                    System.out.println("start looping thru CF");
                    
                    do {
                        boolean skip_me = false;
                        System.out.println("Getting simulataneous pitches in voice " + b);
                        if (holdover_cf.isEmpty() && built_voice_queues.get(b).isEmpty()){
                            System.out.println("both holdover vector and built_voices_queues are empty. this isn't normal");
                            break;
                        }
                        
                        if (!holdover_cf.isEmpty()){
                            
                            for (int h = 0; h < holdover_cf.size(); h++) {
                                MelodicNote entre = holdover_cf.get(h);
                                int printpitch;
                                if (entre == null) printpitch = -1;
                                else printpitch = entre.getPitch();
                                System.out.print(printpitch + ",");
                            }
                            System.out.println();
                            
                            if (holdover_cf.get(b) != null) {
                                this_cf = holdover_cf.get(b);
                                System.out.println("heldover pitch " + this_cf.getPitch() + " with start time " + this_cf.getStartTime() + " and end time " + this_cf.getPreviousDuration() +" is held over into this CP note");
                            }
                        }
                        if (!built_voice_queues.get(b).isEmpty() && holdover_cf.get(b) == null) {
                                this_cf = (MelodicNote)built_voice_queues.get(b).pop();
                                System.out.println("new cf pitch " + this_cf.getPitch() + " with start time " + this_cf.getStartTime() + " and end time " + this_cf.getPreviousDuration() +" is popped from voice " + b );
                        }
                            //DEBUGS
                            //System.out.println("CF pitch = " + this_cf.getPitch());
                            //System.out.println(" rest = " + this_cf.getRest());
                            //System.out.println(" duration up to  = " + this_cf.getPreviousDuration());
//                        
//                        else {
//                            //DEBUG
//                            System.out.println("built_voice_queue is empty and holdover_cf is null");
//                            break;
//                        }
                        
                        if (this_cf.getRest()) {
                            //System.out.println("This cf is a rest of " + this_cf.getDuration());
                            if (this_cf.getDuration() > .5 || //if the cf rest is longer than a half note the power of the preceding note doesn't carry
                                previous_cf_pitch.get(b) == 1111 || //if there is no previous cf pitch ie we are at beginning of cf voice
                                    fragment_note.getRest()) skip_me = true; // if the CP note and CF note both are rests
                        }
                        if (!skip_me){
                            //HARMONICALLY EVALUATE cp candidates against this_cf
                            //DEBUG
                            //System.out.println("previous cf pitch = "+previous_cf_pitch[b]);
                            pitch_candidates = harmonicChecks(pitch_candidates, this_cf, previous_cf_pitch.get(b),
                                                                melody_line.getLatestPitch(), fragment_note,  canon_transpose_interval );
                            //previous_melody_pitch =9999; // 9999 is assigned in case CP is held over into next CF note
                        }                            //in which case the while loop repeats. When you break out
                                                    //of while loop previous_melody_pitch will be checked and re-assigned above
                        else {
                            //DEBUG
                            //System.out.println("current cf index" + b + "is null");
                            //System.out.println("skip me was true skip harmonic checks ");
                            //break;
                        }
                        if (this_cf.getPreviousDuration() > fragment_note.getPreviousDuration()) {  //if cf note extends into next melody note
                            holdover_cf.set(b, this_cf); //reassign holdover note in cf voice, you'll break out of while loop after this
                            System.out.println("adding " + this_cf.getPitch() + " to holdover_cf index " + b);
                        }
                        else holdover_cf.set(b, null);//note it doesn't matter if holdover is rest or pitched. 
                        if (!this_cf.getRest()) previous_cf_pitch.add(b, this_cf.getPitch());//if the cf isn't a rest its the new previous aka "current" cf pitch
                    } while (this_cf.getPreviousDuration() < fragment_note.getPreviousDuration()); //loop while CP note extends into next CF in voice
                }
            }
            PitchCandidate pitch_winner = pickWinner(pitch_candidates);
            //re-set variables and put pitch-assigned note in new array
            pitch_candidates.clear();
            //DEBUG
            System.out.println("CP winner " + pitch_winner.getPitch() + " to note " + fragindex + " of fragment");
            //if (pitch_winner.get_accented_minor_9th()) System.out.println("CP Winner " + pitch_winner.getPitch() + " is accented minor 9th");
            if (pitch_winner.get_accent_diss()) System.out.println("CP Winner " + pitch_winner.getPitch() + " is accented dissonance");
            fragment_note.setPitch(pitch_winner.getPitch());
            return_me.addMelodicNote(fragment_note);
                  
            //If there is a previous CP Pitch
            //Calculate peaks and troughs in melody
            if (melody_line.getLatestPitch() != null && melody_line.getLatestMelodicInterval() != null) { // and its not null
                //Calculate Peaks and Trough note counts                        
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
            melody_line.addMelodicNote(fragment_note);
            voice_pitch_count++;    
        }
        return return_me;
    }//loop through next CP note 
		

    //end method buildFragmentPitches
    
    public static class PitchCandidate {
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
    
    public static class MotionCount {
        
       private final int previous_pitch;
       private final int succeeding_pitch;
       private int count;

        
       public int getCount() {
       return count;
       }
       
       public int getPreviousPitch() {
       return previous_pitch;
       }
       
       public int getSucceedingPitch() {
       return succeeding_pitch;
       }
       
       public void incrementCount(){
           count++;
       }
               
       private MotionCount (int input_prev_pitch, int input_succ_pitch) {
           this.previous_pitch = input_prev_pitch;
           this.succeeding_pitch = input_succ_pitch;
           this.count = 1;
       }
    }
    
    public static class PitchCount {
        private final int the_pitch;
        private int count;
       
        private PitchCount(int input_the_pitch){
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
    }
    /**
     * Given a set of possible candidates for a note's pitch along with key,
     * surrounding notes, and state of the melodic curve, determine decrements
     * for each candidate. 
     * @param pitch_candidates
     * @param my_mode_module
     * @param alter_me
     * @param pitch_center
     * @param voice_pitch_count
     * @param previous_melody_pitch
     * @param previous_melodic_interval
     * @param is_accent
     * @return 
     */
    public static ArrayList<PitchCandidate>  melodicCheck(ArrayList<PitchCandidate> pitch_candidates, ModeModule my_mode_module, MelodicVoice alter_me, 
        Integer pitch_center, int voice_pitch_count, Integer previous_melody_pitch, Integer previous_melodic_interval, Boolean is_accent, Boolean prog_built) {
        Boolean large_dissonance_bad = InputParameters.large_dissonance_bad;
        Integer [] consonances = InputParameters.consonances;
        Integer [] perfect_consonances = InputParameters.perfect_consonances;
        Integer [] root_consonances = InputParameters.root_consonances;
        
        for (PitchCandidate myPC : pitch_candidates){
            int cand_pitch = myPC.getPitch();
            int melody_motion_to_cand = 0;
            
            //DEBUG
            //System.out.println("melodicCheck evaluating pitch candidate " + cand_pitch);
            
            if (voice_pitch_count > 0) {
                melody_motion_to_cand = cand_pitch - previous_melody_pitch;
            
            
                //Check if The candidate has already followed the preceding pitch too often.                
                //look for previous_melody_pitch in PitchCount
                //if it's there get how many times it's appeared in the melody
                // if the count is greater than samplesize threshold
                //check if there are previous_melody_pitch to pitch_candidate motions in MOtion Counts
                //if so get the motion count - then divide motion count by pitch count
                // get the percentage of motions from mode module
                //if actual count is greater than mode module percentage decrement
				
                double thresh = 0;
				Double threshornull = my_mode_module.getMelodicMotionProbability(cand_pitch, previous_melody_pitch, key_transpose, 0);
				if (threshornull == null){
                    //DEBUG
                    //System.out.println("From mode module motion probability of " + previous_melody_pitch %12 +" to " + cand_pitch%12 + " is NULL");
                    myPC.decrementRank(Decrements.melodic_motion_quota_exceed);
                    myPC.decrementRank(Decrements.improbable_melodic_motion);
                }
                else {
                    thresh = threshornull;
                    //DEBUG
                    //System.out.println("From mode module, motion probability of " + previous_melody_pitch%12 +" to " + cand_pitch%12 + " = " + thresh  );
                }
                for (PitchCount my_pitch_count: pitch_counts) {
                    if(my_pitch_count.getPitch() == previous_melody_pitch%12)
						//DEBUG
                        //System.out.println("found preceding cp pitch " + previous_melody_pitch%12 +" in pitch counts with count " + my_pitch_count.getCount());
                        if(my_pitch_count.getCount() > sample_size)     
                        for (MotionCount my_motion_count: motion_counts){
                            //DEBUG
                            //System.out.println("pitch_count for " + previous_melody_pitch %12 + " = " + my_pitch_count.getCount());
                            //System.out.println("motion count for " + my_motion_count.getPreviousPitch() + "/" + my_motion_count.getSucceedingPitch() + "="+ my_motion_count.getCount());
                            if (my_motion_count.getPreviousPitch()== previous_melody_pitch %12 && my_motion_count.getSucceedingPitch() == cand_pitch %12) {
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
            
            if (voice_pitch_count > 1){
                // Peak/Trough check
                // a melodic phrase should have no more than two peaks and two troughs
                // a peak is defined as a change in melodic direction 
                // so when a candidate pitch wants to go in the opposite direction of 
                // the previous melodic interval we want to increment the peak or trough count accordingly
                // and determine whether we have more than two peaks or more than two troughs
                // note that the melody can always go higher or lower than the previous peak or trough

                if (previous_melodic_interval < 0 && melody_motion_to_cand > 0 ) {// will there be a change in direction from - to +  ie trough?
                        if (previous_melody_pitch == trough && trough_count >=2) {
                            myPC.decrementRank(Decrements.peak_trough_quota_exceed);
                            //DEBUG
                            //System.out.println(previous_melody_pitch + " duplicates previous peak");
                        } //will this trough = previous trough? then increment
                }        
                if (previous_melodic_interval > 0 && melody_motion_to_cand <0){ // will there be a trough?
                        if (previous_melody_pitch == peak && peak_count >=2) {
                            myPC.decrementRank(Decrements.peak_trough_quota_exceed);
                            //DEBUG
                            //System.out.println(previous_melody_pitch + " duplicates previous trough");
                        } //will this trough = previous trough? then increment
                }
				
                //Motion after Leaps checks
                //First check if the melody does not go in opposite direction of leap
                // then check if there are two successive leaps in the same direction
                if (previous_melodic_interval > 4 && melody_motion_to_cand > 0){
                    myPC.decrementRank(Decrements.bad_motion_after_leap);
                    //DEBUG
                    //System.out.println(melody_motion_to_cand + " to "+ cand_pitch + " is bad motion after leap");
                    if (melody_motion_to_cand > 4) {
                        myPC.decrementRank(Decrements.successive_leaps);
                        //DEBUG
                        //System.out.println(cand_pitch + " is successive leap");
                    }
                        
                }    
                if (previous_melodic_interval < -4 && melody_motion_to_cand < 0){
                    myPC.decrementRank(Decrements.bad_motion_after_leap);
                    //DEBUG
                    //System.out.println(melody_motion_to_cand + " to "+cand_pitch + " is bad motion after leap");
                    if (melody_motion_to_cand < -4) {
                        myPC.decrementRank(Decrements.successive_leaps);  
                        //DEBUG
                        //System.out.println(cand_pitch + " is successive leap");
                    }

                }   
            }           
            // end melody checks
        } //next pitch candidate
        return pitch_candidates; 
    }
    /**
     * Similar to melodicChecks however in these method we are interested in 
     * harmonic properties of the pitch candidates relative to pitches in voices
     * that have already been built. 
     * @param pitch_candidates
     * @param CF_note
     * @param CFnoteRoot
     * @param previous_cf_pitch
     * @param previous_melody_pitch
     * @param fragment_note
     * @param voice_pitch_count
     * @return 
     */
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
                        System.out.println("caught accented dissonance between cand pitch " + cand_pitch + " and cf_pitch " + cf_pitch);
                        myPC.decrementRank(Decrements.accented_dissonance);
                        myPC.set_accent_diss();
                    }
                    if (CF_note.getAccent() && (abs(cf_pitch - cand_pitch)<36 || large_dissonance_bad)) {
                        System.out.println("caught accented dissonance between cand pitch " + cand_pitch + " and cf_pitch " + cf_pitch);
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
   
    /**
     * Picks the pitch candidate that will be the pitch of the note. 
     * @param pitch_candidates
     * @return 
     */
    public static PitchCandidate pickWinner(ArrayList<PitchCandidate> pitch_candidates){
        //pick highest ranking pitch candidate -> return_me[i]
        //System.out.println("start Pick Winner");
        ArrayList<PitchCandidate> pitch_winners = new ArrayList();
        for (PitchCandidate myPC : pitch_candidates){
            //DEBUG
            System.out.println( "pitch candidate pitch: "+ myPC.getPitch() + " and rank: " + myPC.getRank() + " and accent_diss = " + myPC.get_accent_diss());
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
    
    public void resetParams() {
        built_fragments.clear();
        unbuilt_fragments.clear();
        motion_counts.clear();
        pitch_counts.clear();
        trough = 0;
        trough_count = 0;
        peak = 0;
        peak_count = 0;
        same_consonant_count = 0;
        key_transpose = 0;
        root_key = 0;
        harmonic_prog_built = false;
    }
    public static ArrayList<PitchCandidate> PitchCandidateVetting (ArrayList<PitchCandidate> pitch_candidates, ModeModule my_mode_module, boolean prog_built, boolean is_accent, int pitch_center) {
        Boolean large_dissonance_bad = InputParameters.large_dissonance_bad;
        Integer [] consonances = InputParameters.consonances;
        Integer [] perfect_consonances = InputParameters.perfect_consonances;
        Integer [] root_consonances = InputParameters.root_consonances;
        for (PitchCandidate myPC : pitch_candidates){
            int cand_pitch = myPC.getPitch();

            
            //DEBUG
            //("melodicCheck evaluating pitch candidate " + cand_pitch);
            
            //Check if Dissonant with Root - Does not apply for harmonic prog
            if (prog_built) {
                boolean root_interval_consonant = false;
                int root_interval = abs(cand_pitch%12 - key_transpose);
                for (Integer consonance : root_consonances) {
                    if (root_interval == consonance) root_interval_consonant = true;
                }
                if(root_interval_consonant) {
                    //DEBUG
                    //System.out.println(cand_pitch + " consonant with root " + key_transpose );
                }
                else {
                    if (is_accent ) {
                        myPC.decrementRank(Decrements.dissonant_with_root);
                        //DEBUG
                        //System.out.println(cand_pitch + " dissonant accent with root " + key_transpose);
                    }
                    else {
                        //DEBUG
                        //System.out.println("dissonant with root but note not accented" );
                    }
                }                 
            }

            //randomly decrement non-tonics
            if (cand_pitch%12 != my_mode_module.getTonic() && roll.nextInt(2) == 1){
                myPC.decrementRank(Decrements.is_not_tonic);
                //DEBUG
                //System.out.println(cand_pitch + " is not tonic");
            }
            
            //decrement illegal notes
            if(cand_pitch <0 || cand_pitch > 127) {
                myPC.decrementRank(Decrements.illegal_note);
                //DEBUG
                //System.out.println(cand_pitch + " is illegal note");                
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
}
