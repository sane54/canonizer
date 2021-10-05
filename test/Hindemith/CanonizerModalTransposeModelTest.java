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
public class CanonizerModalTransposeModelTest {
    
    public CanonizerModalTransposeModelTest() {
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
     * Test of buildFragmentPitches method, of class CanonizerModalTransposeModel.
     */
    @Test
    public void testBuildFragmentPitches() {
        System.out.println("buildFragmentPitches");
        MelodicVoice alter_me = null;
        int number_of_voices = 0;
        ModeModule my_mode_module = null;
        MelodicVoice expResult = null;
        MelodicVoice result = CanonizerModalTransposeModel.buildFragmentPitches(alter_me, number_of_voices, my_mode_module);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of melodicCheck method, of class CanonizerModalTransposeModel.
     */
    @Test
    public void testMelodicCheck() {
        System.out.println("melodicCheck");
        ArrayList<CanonizerModalTransposeModel.PitchCandidate> pitch_candidates = null;
        ModeModule my_mode_module = null;
        MelodicVoice alter_me = null;
        Integer pitch_center = null;
        int voice_pitch_count = 0;
        Integer previous_melody_pitch = null;
        Integer previous_melodic_interval = null;
        Boolean is_accent = null;
        Boolean prog_built = null;
        ArrayList<CanonizerModalTransposeModel.PitchCandidate> expResult = null;
        ArrayList<CanonizerModalTransposeModel.PitchCandidate> result = CanonizerModalTransposeModel.melodicCheck(pitch_candidates, my_mode_module, alter_me, pitch_center, voice_pitch_count, previous_melody_pitch, previous_melodic_interval, is_accent, prog_built);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of harmonicChecks method, of class CanonizerModalTransposeModel.
     */
    @Test
    public void testHarmonicChecks() {
        System.out.println("harmonicChecks");
        ArrayList<CanonizerModalTransposeModel.PitchCandidate> pitch_candidates = null;
        MelodicNote CF_note = null;
        Integer previous_cf_pitch = null;
        Integer previous_melody_pitch = null;
        MelodicNote fragment_note = null;
        int canon_transpose_interval = 0;
        ArrayList<CanonizerModalTransposeModel.PitchCandidate> expResult = null;
        ArrayList<CanonizerModalTransposeModel.PitchCandidate> result = CanonizerModalTransposeModel.harmonicChecks(pitch_candidates, CF_note, previous_cf_pitch, previous_melody_pitch, fragment_note, canon_transpose_interval);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of pickWinner method, of class CanonizerModalTransposeModel.
     */
    @Test
    public void testPickWinner() {
        System.out.println("pickWinner");
        ArrayList<CanonizerModalTransposeModel.PitchCandidate> pitch_candidates = null;
        CanonizerModalTransposeModel.PitchCandidate expResult = null;
        CanonizerModalTransposeModel.PitchCandidate result = CanonizerModalTransposeModel.pickWinner(pitch_candidates);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of resetParams method, of class CanonizerModalTransposeModel.
     */
    @Test
    public void testResetParams() {
        System.out.println("resetParams");
        CanonizerModalTransposeModel instance = new CanonizerModalTransposeModel();
        instance.resetParams();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of PitchCandidateVetting method, of class CanonizerModalTransposeModel.
     */
    @Test
    public void testPitchCandidateVetting() {
        System.out.println("PitchCandidateVetting");
        ArrayList<CanonizerModalTransposeModel.PitchCandidate> pitch_candidates = null;
        ModeModule my_mode_module = null;
        boolean prog_built = false;
        boolean is_accent = false;
        int pitch_center = 0;
        ArrayList<CanonizerModalTransposeModel.PitchCandidate> expResult = null;
        ArrayList<CanonizerModalTransposeModel.PitchCandidate> result = CanonizerModalTransposeModel.PitchCandidateVetting(pitch_candidates, my_mode_module, prog_built, is_accent, pitch_center);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of CombineFragments method, of class CanonizerModalTransposeModel.
     */
    @Test
    public void testCombineFragments() {
        System.out.println("CombineFragments");
        Integer tempo_bpm = null;
        Integer number_of_voices = null;
        Integer piece_length = null;
        //CanonizerModalTransposeModel instance = new CanonizerModalTransposeModel();
        Pattern expResult = null;
        //Pattern result = instance.CombineFragments(tempo_bpm, number_of_voices, piece_length);
        //assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of PerformTranspositions method, of class CanonizerModalTransposeModel.
     */
    @Test
    public void testPerformTranspositions() {
        System.out.println("PerformTranspositions");
        Integer number_of_voices = 3;
        MelodicVoice nextFragment = new MelodicVoice();
        MelodicNote Cfour = new MelodicNote();
        Cfour.setPitch(48);
        Cfour.setDuration(0.25);
        MelodicNote Efour = new MelodicNote();
        Efour.setPitch(52);
        Efour.setDuration(0.25);
        MelodicNote Gfour = new MelodicNote();
        Gfour.setPitch(55);
        Gfour.setDuration(0.25);
        nextFragment.addMelodicNote(Cfour);
        nextFragment.addMelodicNote(Efour);
        nextFragment.addMelodicNote(Gfour);
        ModeModule my_mode_module = new Clydian_keyChange();
        int [] my_transpose_array = {5, 12};
        CanonizerModalTransposeModel instance = new CanonizerModalTransposeModel();
        ArrayList<MelodicVoice> transposeArray =instance.PerformTranspositions(number_of_voices, nextFragment, my_mode_module, my_transpose_array);
        assertEquals(transposeArray.get(0).getMelodicNote(0).getPitch(), "48");
        assertEquals(transposeArray.get(0).getMelodicNote(1).getPitch(), "52");
        assertEquals(transposeArray.get(0).getMelodicNote(2).getPitch(), "55");
    }
    
}
