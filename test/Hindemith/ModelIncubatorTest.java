/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Hindemith;

import Hindemith.ModeModules.Clydian_keyChange;
import Hindemith.ModeModules.ModeModule;
import java.util.ArrayList;
import org.jfugue.Pattern;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;



/**
 *
 * @author Owner
 */
public class ModelIncubatorTest {
    
   
    public ModelIncubatorTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of PerformTranspositions method, of class ModelIncubator.
     */
    @Test
    public void testPerformTranspositions() {
        System.out.println("PerformTranspositions");
        Integer number_of_voices = 4;
        MelodicVoice nextFragment = new MelodicVoice();
        MelodicNote Cfour = new MelodicNote();
        Cfour.setPitch(48);
        Cfour.setDuration(0.25);
        MelodicNote Dfour = new MelodicNote();
        Dfour.setPitch(50);
        Dfour.setDuration(0.25);
        MelodicNote Efour = new MelodicNote();
        Efour.setPitch(52);
        MelodicNote Fsfour = new MelodicNote();
        Fsfour.setPitch(54);
        Fsfour.setDuration(0.25);
        MelodicNote Gfour = new MelodicNote();
        Gfour.setPitch(55);
        Gfour.setDuration(0.25);
        MelodicNote Afour = new MelodicNote();
        Afour.setPitch(57);
        Afour.setDuration(0.25);
        MelodicNote Bfour = new MelodicNote();
        Bfour.setPitch(59);
        Bfour.setDuration(0.25);
        nextFragment.addMelodicNote(Cfour);
        nextFragment.addMelodicNote(Dfour);
        nextFragment.addMelodicNote(Efour);
        nextFragment.addMelodicNote(Fsfour);
        nextFragment.addMelodicNote(Gfour);
        nextFragment.addMelodicNote(Afour);
        nextFragment.addMelodicNote(Bfour);
        ModeModule my_mode_module = new Clydian_keyChange();
        int [] my_transpose_array = {7, 12, 14};
        ModelIncubator instance = new ModelIncubator();
        ArrayList<MelodicVoice> transposeArray =instance.PerformTranspositions(number_of_voices, nextFragment, my_mode_module, my_transpose_array);
        Integer CfourPitchT1 = Cfour.getPitch() + 7;
        Integer DfourPitchT1 = Dfour.getPitch() + 7;
        Integer EfourPitchT1 = Efour.getPitch() + 7;
        Integer FsfourPitchT1 = Fsfour.getPitch() + 6;
        Integer GfourPitchT1 = Gfour.getPitch() + 7;
        Integer AfourPitchT1 = Afour.getPitch() + 7;
        Integer BfourPitchT1 = Bfour.getPitch() + 7;

        Integer CfourPitchT2 = Cfour.getPitch() + 12;
        Integer DfourPitchT2 = Dfour.getPitch() + 12;
        Integer EfourPitchT2 = Efour.getPitch() + 12;
        Integer FsfourPitchT2 = Fsfour.getPitch() + 12;
        Integer GfourPitchT2 = Gfour.getPitch() + 12;
        Integer AfourPitchT2 = Afour.getPitch() + 12;
        Integer BfourPitchT2 = Bfour.getPitch() + 12;
        
        Integer CfourPitchT3 = Cfour.getPitch() + 14;
        Integer DfourPitchT3 = Dfour.getPitch() + 14;
        Integer EfourPitchT3 = Efour.getPitch() + 14;
        Integer FsfourPitchT3 = Fsfour.getPitch() + 14;
        Integer GfourPitchT3 = Gfour.getPitch() + 14;
        Integer AfourPitchT3 = Afour.getPitch() + 14;
        Integer BfourPitchT3 = Bfour.getPitch() + 13;
        

        
//        Integer CfourPitchT1 = Cfour.getPitch() + my_mode_module.ModeTranspose(Cfour.getPitch()%12, my_transpose_array[0]);
//        Integer EfourPitchT1 = Efour.getPitch() + my_mode_module.ModeTranspose(Efour.getPitch()%12, my_transpose_array[0]);
//        Integer GfourPitchT1 = Gfour.getPitch() + my_mode_module.ModeTranspose(Gfour.getPitch()%12, my_transpose_array[0]);
//        Integer CfourPitchT2 = Cfour.getPitch() + my_mode_module.ModeTranspose(Cfour.getPitch()%12, my_transpose_array[1]);
//        Integer EfourPitchT2 = Efour.getPitch() + my_mode_module.ModeTranspose(Efour.getPitch()%12, my_transpose_array[1]);
//        Integer GfourPitchT2 = Gfour.getPitch() + my_mode_module.ModeTranspose(Gfour.getPitch()%12, my_transpose_array[1]);
        
        assertEquals(transposeArray.get(0).getMelodicNote(0).getPitch(), Cfour.getPitch());
        assertEquals(transposeArray.get(0).getMelodicNote(1).getPitch(), Dfour.getPitch());
        assertEquals(transposeArray.get(0).getMelodicNote(2).getPitch(), Efour.getPitch());
        assertEquals(transposeArray.get(0).getMelodicNote(3).getPitch(), Fsfour.getPitch());
        assertEquals(transposeArray.get(0).getMelodicNote(4).getPitch(), Gfour.getPitch());
        assertEquals(transposeArray.get(0).getMelodicNote(5).getPitch(), Afour.getPitch());
        assertEquals(transposeArray.get(0).getMelodicNote(6).getPitch(), Bfour.getPitch());
        
        assertEquals(transposeArray.get(1).getMelodicNote(0).getPitch(), CfourPitchT1);
        assertEquals(transposeArray.get(1).getMelodicNote(1).getPitch(), DfourPitchT1);
        assertEquals(transposeArray.get(1).getMelodicNote(2).getPitch(), EfourPitchT1);
        assertEquals(transposeArray.get(1).getMelodicNote(3).getPitch(), FsfourPitchT1);
        assertEquals(transposeArray.get(1).getMelodicNote(4).getPitch(), GfourPitchT1);
        assertEquals(transposeArray.get(1).getMelodicNote(5).getPitch(), AfourPitchT1);
        assertEquals(transposeArray.get(1).getMelodicNote(6).getPitch(), BfourPitchT1);
        
        assertEquals(transposeArray.get(2).getMelodicNote(0).getPitch(), CfourPitchT2);
        assertEquals(transposeArray.get(2).getMelodicNote(1).getPitch(), DfourPitchT2);
        assertEquals(transposeArray.get(2).getMelodicNote(2).getPitch(), EfourPitchT2);
        assertEquals(transposeArray.get(2).getMelodicNote(3).getPitch(), FsfourPitchT2);
        assertEquals(transposeArray.get(2).getMelodicNote(4).getPitch(), GfourPitchT2);
        assertEquals(transposeArray.get(2).getMelodicNote(5).getPitch(), AfourPitchT2);
        assertEquals(transposeArray.get(2).getMelodicNote(6).getPitch(), BfourPitchT2);
        
        assertEquals(transposeArray.get(3).getMelodicNote(0).getPitch(), CfourPitchT3);
        assertEquals(transposeArray.get(3).getMelodicNote(1).getPitch(), DfourPitchT3);
        assertEquals(transposeArray.get(3).getMelodicNote(2).getPitch(), EfourPitchT3);
        assertEquals(transposeArray.get(3).getMelodicNote(3).getPitch(), FsfourPitchT3);
        assertEquals(transposeArray.get(3).getMelodicNote(4).getPitch(), GfourPitchT3);
        assertEquals(transposeArray.get(3).getMelodicNote(5).getPitch(), AfourPitchT3);
        assertEquals(transposeArray.get(3).getMelodicNote(6).getPitch(), BfourPitchT3);

    }

    /**
     * Test of CombineFragments method, of class ModelIncubator.
     */
    @Test
    public void testCombineFragments() {
        System.out.println("CombineFragments");
        
        MelodicNote f1n1t0 = new MelodicNote();
        f1n1t0.setPitch(36);
        f1n1t0.setDuration(.5);
        MelodicNote f1n2t0 = new MelodicNote();
        f1n2t0.setPitch(48);
        f1n2t0.setDuration(0.375);
        MelodicNote f1n3t0 = new MelodicNote();
        f1n3t0.setPitch(40);
        f1n3t0.setDuration(0.125);        
        MelodicVoice f1t0 = new MelodicVoice();
        f1t0.addMelodicNote(f1n1t0);
        f1t0.addMelodicNote(f1n2t0);
        f1t0.addMelodicNote(f1n3t0);
        
        MelodicNote f1n1t1 = new MelodicNote();
        f1n1t1.setPitch(55);
        f1n1t1.setDuration(.5);
        MelodicNote f1n2t1 = new MelodicNote();
        f1n2t1.setPitch(67);
        f1n2t1.setDuration(0.375);
        MelodicNote f1n3t1 = new MelodicNote();
        f1n3t1.setPitch(59);
        f1n3t1.setDuration(0.125);        
        MelodicVoice f1t1 = new MelodicVoice();
        f1t1.addMelodicNote(f1n1t1);
        f1t1.addMelodicNote(f1n2t1);
        f1t1.addMelodicNote(f1n3t1);
        
        MelodicNote f1n1t2 = new MelodicNote();
        f1n1t2.setPitch(60);
        f1n1t2.setDuration(.5);
        MelodicNote f1n2t2 = new MelodicNote();
        f1n2t2.setPitch(72);
        f1n2t2.setDuration(0.375);
        MelodicNote f1n3t2 = new MelodicNote();
        f1n3t2.setPitch(64);
        f1n3t2.setDuration(0.125);        
        MelodicVoice f1t2 = new MelodicVoice();
        f1t2.addMelodicNote(f1n1t2);
        f1t2.addMelodicNote(f1n2t2);
        f1t2.addMelodicNote(f1n3t2);

        MelodicNote f2n1t0 = new MelodicNote();
        f2n1t0.setPitch(36);
        f2n1t0.setDuration(.5);
        MelodicNote f2n2t0 = new MelodicNote();
        f2n2t0.setPitch(36);
        f2n2t0.setDuration(0.375);
        MelodicNote f2n3t0 = new MelodicNote();
        f2n3t0.setPitch(38);
        f2n3t0.setDuration(0.125);        
        MelodicVoice f2t0 = new MelodicVoice();
        f2t0.addMelodicNote(f2n1t0);
        f2t0.addMelodicNote(f2n2t0);
        f2t0.addMelodicNote(f2n3t0);
        
        MelodicNote f2n1t1 = new MelodicNote();
        f2n1t1.setPitch(55);
        f2n1t1.setDuration(.5);
        MelodicNote f2n2t1 = new MelodicNote();
        f2n2t1.setPitch(55);
        f2n2t1.setDuration(0.375);
        MelodicNote f2n3t1 = new MelodicNote();
        f2n3t1.setPitch(57);
        f2n3t1.setDuration(0.125);        
        MelodicVoice f2t1 = new MelodicVoice();
        f2t1.addMelodicNote(f2n1t1);
        f2t1.addMelodicNote(f2n2t1);
        f2t1.addMelodicNote(f2n3t1);
        
        MelodicNote f2n1t2 = new MelodicNote();
        f2n1t2.setPitch(60);
        f2n1t2.setDuration(.5);
        MelodicNote f2n2t2 = new MelodicNote();
        f2n2t2.setPitch(60);
        f2n2t2.setDuration(0.375);
        MelodicNote f2n3t2 = new MelodicNote();
        f2n3t2.setPitch(62);
        f2n3t2.setDuration(0.125);        
        MelodicVoice f2t2 = new MelodicVoice();
        f2t2.addMelodicNote(f2n1t2);
        f2t2.addMelodicNote(f2n2t2);
        f2t2.addMelodicNote(f2n3t2);

        MelodicNote f3n1t0 = new MelodicNote();
        f3n1t0.setPitch(36);
        f3n1t0.setDuration(.5);
        MelodicNote f3n2t0 = new MelodicNote();
        f3n2t0.setPitch(48);
        f3n2t0.setDuration(0.375);
        MelodicNote f3n3t0 = new MelodicNote();
        f3n3t0.setPitch(40);
        f3n3t0.setDuration(0.125);        
        MelodicVoice f3t0 = new MelodicVoice();
        f3t0.addMelodicNote(f3n1t0);
        f3t0.addMelodicNote(f3n2t0);
        f3t0.addMelodicNote(f3n3t0);
        
        MelodicNote f3n1t1 = new MelodicNote();
        f3n1t1.setPitch(55);
        f3n1t1.setDuration(.5);
        MelodicNote f3n2t1 = new MelodicNote();
        f3n2t1.setPitch(67);
        f3n2t1.setDuration(0.375);
        MelodicNote f3n3t1 = new MelodicNote();
        f3n3t1.setPitch(59);
        f3n3t1.setDuration(0.125);        
        MelodicVoice f3t1 = new MelodicVoice();
        f3t1.addMelodicNote(f3n1t1);
        f3t1.addMelodicNote(f3n2t1);
        f3t1.addMelodicNote(f3n3t1);
        
        MelodicNote f3n1t2 = new MelodicNote();
        f3n1t2.setPitch(60);
        f3n1t2.setDuration(.5);
        MelodicNote f3n2t2 = new MelodicNote();
        f3n2t2.setPitch(72);
        f3n2t2.setDuration(0.375);
        MelodicNote f3n3t2 = new MelodicNote();
        f3n3t2.setPitch(64);
        f3n3t2.setDuration(0.125);        
        MelodicVoice f3t2 = new MelodicVoice();
        f3t2.addMelodicNote(f3n1t2);
        f3t2.addMelodicNote(f3n2t2);
        f3t2.addMelodicNote(f3n3t2);
        
        ArrayList<MelodicVoice> transposed_fragment1 = new ArrayList();
        ArrayList<MelodicVoice> transposed_fragment2 = new ArrayList();
        ArrayList<MelodicVoice> transposed_fragment3 = new ArrayList();
        transposed_fragment1.add(f1t0);
        transposed_fragment1.add(f1t1);
        transposed_fragment1.add(f1t2);
        transposed_fragment2.add(f2t0);
        transposed_fragment2.add(f2t1);
        transposed_fragment2.add(f2t2);
        transposed_fragment3.add(f3t0);
        transposed_fragment3.add(f3t1);
        transposed_fragment3.add(f3t2);
        
        ArrayList<ArrayList> transposed_fragments = new ArrayList();
        
        transposed_fragments.add(transposed_fragment1);
        transposed_fragments.add(transposed_fragment2);
        transposed_fragments.add(transposed_fragment3);
        
        Integer tempo_bpm = 60;
        Integer number_of_voices = 3;
        Integer piece_length = 1;
        ModelIncubator instance = new ModelIncubator();
        Pattern canon_voice1 = new Pattern("V1 Rw Rw Rw Rw G4h G5q. B4i G4h G4q. A4i G4h G5q. B4i G4h G5q. B4i G4h G4q. A4i G4h G5q. B4i G4h G5q. B4i G4h G4q. A4i G4h G5q. B4i");
        Pattern canon_voice2 = new Pattern("V2 Rw Rw Rw Rw Rw Rw C5h C6q. E5e C5h C5q. D5i C5h C6q. E5i C5h C6q. E5i C5h C5q. D5i C5h C6q. E5i C5h C6q. E5i C5h C5q. D5i C5h C6q. E5i");
        Pattern expResult = new Pattern("T60 V0 I[Bassoon] C3h C4q. E3i C3h C3q. D3i C3h C4q. E3i C3h C4q. E3i C3h C3q. D3i C3h C4q. E3i C3h C4q. E3i C3h C3q. D3i C3h C4q. E3i");
        expResult.add(canon_voice1);
        expResult.add(canon_voice2);
        Pattern result = instance.CombineFragments(tempo_bpm, number_of_voices, piece_length, transposed_fragments);
        System.out.println(result.getMusicString());
        System.out.println(expResult.getMusicString());
        assertEquals(expResult, result);
    }   

    /**
     * Test of PitchCandidateVetting method, of class ModelIncubator.
     */
    @Test
    public void testPitchCandidateVetting() {
        
        System.out.println("PitchCandidateVetting");
        ModeModule my_mode_module = new Clydian_keyChange();
        ArrayList<PitchCandidate> pitch_candidates = new ArrayList();
        boolean prog_built = true;
        boolean is_accent = false;
        int pitch_center = 41;
        int key_transpose = 0; //run tests where it's greater than 0?
        ArrayList<Integer> pitch_candidate_values = my_mode_module.getPitchCandidatesGeneric(48);
        
        for (Integer pitch_candidate_value : pitch_candidate_values) {
            //DEBUG
            System.out.print(pitch_candidate_value + " ");
            PitchCandidate myPC = new PitchCandidate();
            myPC.setPitch(pitch_candidate_value);
            pitch_candidates.add(myPC);
        }
        
        PitchCandidate illegalPC = new PitchCandidate();
        illegalPC.setPitch(1000);
        pitch_candidates.add(illegalPC);
        
        PitchCandidate outarangePC = new PitchCandidate();
        outarangePC.setPitch(28);
        pitch_candidates.add(outarangePC);
        
        for (Integer pitch_candidate_value : pitch_candidate_values) {
            //DEBUG
            System.out.print(pitch_candidate_value + " ");
        }        
        System.out.println();
        
        MelodicNote f1n1t0 = new MelodicNote();
        f1n1t0.setPitch(36);
        f1n1t0.setDuration(.5);
        MelodicNote f1n2t0 = new MelodicNote();
        f1n2t0.setPitch(48);
        f1n2t0.setDuration(0.375);
        MelodicNote f1n3t0 = new MelodicNote();
        f1n3t0.setPitch(40);
        f1n3t0.setDuration(0.125);        
        MelodicVoice melody_line = new MelodicVoice();
        melody_line.addMelodicNote(f1n1t0);
        melody_line.addMelodicNote(f1n2t0);
        melody_line.addMelodicNote(f1n3t0);
        melody_line.setRange("tenor");
        melody_line.setPitchCenter(pitch_center);
        
        System.out.println("latest melodic interval " + melody_line.getLatestMelodicInterval());
        System.out.println("latest pitch " + melody_line.getLatestPitch());
        System.out.println("range max " + melody_line.getRangeMax());
        System.out.println("range min " + melody_line.getRangeMin());
        System.out.println("voice length " + melody_line.getVoiceLength());
        System.out.println("pitch center " + melody_line.getPitchCenter());
        System.out.println("range id " + melody_line.getRangeId());
        //ArrayList<ModelIncubator.PitchCandidate> expResult = null;
        ArrayList<PitchCandidate> result = ModelIncubator.PitchCandidateVetting(pitch_candidates, prog_built, is_accent, pitch_center, key_transpose, melody_line);
        for (PitchCandidate P : result) {
            System.out.println(P.getPitch() + " " + P.getRank());
        }
                //assertEquals(expResult, result);
    }

    /**
     * Test of melodicCheck method, of class ModelIncubator.
     */
    @Test
    public void testMelodicCheck() {
        int test_peak = 48;
        int test_peak_count = 2;
        int test_trough = 48; 
        int test_trough_count = 2;
        PitchCount pitch_count_c4 = new PitchCount(0);
        pitch_count_c4.setCount(20);
        PitchCount pitch_count_d4 = new PitchCount(2);
        pitch_count_d4.setCount(20);
        MotionCount motion_count_c4 = new MotionCount(0, 7);
        motion_count_c4.setCount(20);
        MotionCount motion_count_d4_1 = new MotionCount(0, 4);
        motion_count_d4_1.setCount(20);
        MotionCount motion_count_d4_2 = new MotionCount(0, 0);
        motion_count_d4_2.setCount(20);
        ArrayList<PitchCount> test_pitch_counts = new ArrayList();
        ArrayList<MotionCount> test_motion_counts = new ArrayList();
        test_pitch_counts.add(pitch_count_d4);
        test_pitch_counts.add(pitch_count_c4);
        test_motion_counts.add(motion_count_c4);
        test_motion_counts.add(motion_count_d4_1);
        test_motion_counts.add(motion_count_d4_2);
        ModelIncubator.setPeakTroughPitchCount(test_peak, test_peak_count, test_trough, test_trough_count, test_pitch_counts, test_motion_counts);
        Integer mode_transpose = InputParameters.transpose_interval_array[0] ;
        int voice_pitch_count = 6;
        Integer previous_melody_pitch = 48;
        Integer previous_melodic_interval = -7;
        int key_transpose = 5;
        
        
        ModeModule my_mode_module = new Clydian_keyChange();
        ArrayList<Integer> pitch_candidate_values = my_mode_module.getPitchCandidatesGeneric(48);
        ArrayList<PitchCandidate> pitch_candidates = new ArrayList();
        
        System.out.println("Melodic Checks on candidates for pitches to follow melody pitch of " + previous_melody_pitch);
        System.out.println("The candidates are: ");
        
        for (Integer pitch_candidate_value : pitch_candidate_values) {
            //DEBUG
            System.out.print(pitch_candidate_value + " ");
            PitchCandidate myPC = new PitchCandidate();
            myPC.setPitch(pitch_candidate_value);
            pitch_candidates.add(myPC);
        }
        System.out.println();

        ArrayList<PitchCandidate> expResult = null;
        ArrayList<PitchCandidate> result = ModelIncubator.melodicCheck(pitch_candidates, my_mode_module, voice_pitch_count, previous_melody_pitch, previous_melodic_interval);
        //assertEquals(expResult, result);
    }
}
