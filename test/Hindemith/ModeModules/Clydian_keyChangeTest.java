/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Hindemith.ModeModules;

import java.util.ArrayList;
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
public class Clydian_keyChangeTest {
    
    public Clydian_keyChangeTest() {
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
     * Test of getFirstNotePitchCandidates method, of class Clydian_keyChange.
     */
    @Test
    public void testGetFirstNotePitchCandidates() {
        System.out.println("getFirstNotePitchCandidates");
        int input_range_min = 36;
        int input_range_max = 60;
        int key_transpose = 0;
        Clydian_keyChange instance = new Clydian_keyChange();
        ArrayList<Integer> expResult_key0 = new ArrayList<Integer>(){{
		   add(48);
		   add(50);
		   add(52);
                   add(55);
                   add(45);
		   }};
        ArrayList<Integer> result = instance.getFirstNotePitchCandidates(input_range_min, input_range_max, key_transpose);
        assertEquals(expResult_key0, result);
    }

    /**
     * Test of getPitchCenter method, of class Clydian_keyChange.
     */
    @Test
    public void testGetPitchCenter() {
        int input_range_min = 36;
        int input_range_max = 60;
        Clydian_keyChange instance = new Clydian_keyChange();
        Integer expResult = null;
        for (int i = 0; i <20; i++) {
            Integer result = instance.getPitchCenter(input_range_min, input_range_max);
            System.out.println("getPitchCenter " + result);
            assertTrue(result > input_range_min + 8 && result < input_range_max);
        }
    }

    /**
     * Test of getPitchCandidates method, of class Clydian_keyChange.
     */
    @Test
    public void testGetPitchCandidates() {
        System.out.println("getPitchCandidates");
        int input_previous_pitch = 0;
        int key_transpose = 0;
        Clydian_keyChange instance = new Clydian_keyChange();
        ArrayList<Integer> expResult = null;
        ArrayList<Integer> result = instance.getPitchCandidates(input_previous_pitch, key_transpose);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getMelodicMotionProbability method, of class Clydian_keyChange.
     */
    @Test
    public void testGetMelodicMotionProbability() {
        System.out.println("getMelodicMotionProbability");
        Integer input_current_pitch_cand = null;
        Integer input_previous_pitch = null;
        Integer key_transpose = null;
        Integer mode_transpose = null;
        Clydian_keyChange instance = new Clydian_keyChange();
        Double expResult = null;
        Double result = instance.getMelodicMotionProbability(input_current_pitch_cand, input_previous_pitch, key_transpose, mode_transpose);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getTonic method, of class Clydian_keyChange.
     */
    @Test
    public void testGetTonic() {
        System.out.println("getTonic");
        Clydian_keyChange instance = new Clydian_keyChange();
        Integer expResult = null;
        Integer result = instance.getTonic();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of mytoString method, of class Clydian_keyChange.
     */
    @Test
    public void testMytoString() {
        System.out.println("mytoString");
        Clydian_keyChange instance = new Clydian_keyChange();
        String expResult = "";
        String result = instance.mytoString();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getPitchCandidatesGeneric method, of class Clydian_keyChange.
     */
    @Test
    public void testGetPitchCandidatesGeneric() {
        System.out.println("getPitchCandidatesGeneric");
        Integer input_previous_pitch = null;
        Clydian_keyChange instance = new Clydian_keyChange();
        ArrayList<Integer> expResult = null;
        ArrayList<Integer> result = instance.getPitchCandidatesGeneric(input_previous_pitch);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of ModeTranspose method, of class Clydian_keyChange.
     */
    @Test
    public void testModeTranspose() {
        System.out.println("ModeTranspose");
        Integer pclass = null;
        Integer mode_transpose_interval = null;
        Clydian_keyChange instance = new Clydian_keyChange();
        Integer expResult = null;
        Integer result = instance.ModeTranspose(pclass, mode_transpose_interval);
        assertEquals(expResult, result);
    }
    
}
