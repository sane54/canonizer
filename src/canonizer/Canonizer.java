/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package canonizer;
import Hindemith.CanonizerModalTransposeModel;
import Hindemith.CanonizerModel;
import Hindemith.MidiOut;
import Hindemith.ModelIncubator;
import java.util.Random;

/**
 *
 * @author Owner
 */
public class Canonizer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Random roll = new Random();
        
        switch ( roll.nextInt(4) ){
            case (0):
                Hindemith.InputParameters.setModeModule("Lydian");
                break;
            case (1):
                Hindemith.InputParameters.setModeModule("Chromatic Tonic");
                break;
            case (2):
                Hindemith.InputParameters.setModeModule("Atonal (w/o Repeat Notes)");
                break;
            case (3):
                Hindemith.InputParameters.setModeModule("Blues");
                break;
        }
        switch ( roll.nextInt(3) ){
            case (0):
                Hindemith.InputParameters.setJames("Drum and Bass Patterns 1");
                break;
            case (1):
                Hindemith.InputParameters.setJames("FunkRiffPatternGenerator");
                break;
            case (2):
                Hindemith.InputParameters.setJames("Variable Time Signature Unfunky");
                break;
        }
        Hindemith.InputParameters.setTempo(60 + roll.nextInt(200));
        Hindemith.InputParameters.setPieceLength(2 + roll.nextInt(4));
        Integer number_voices = 2 + roll.nextInt(4);
        String []  voice_array = new String[number_voices];
        byte [] inst_array = new byte[number_voices];
        Integer [] t_intervals = {3, 4, 7, 12, 9};
        int [] transpose_intervals = new int[number_voices];
        for (int i = 0; i < number_voices; i++){
            voice_array[i] = "bass";
        }
         for (int i = 0; i < number_voices; i++){
            inst_array[i] = (byte)roll.nextInt(100);
        }  
        for (int i = 0; i < number_voices; i++){
            transpose_intervals[i] = t_intervals[roll.nextInt(5)];
        }
        Hindemith.InputParameters.setVoiceArray(voice_array);
        Hindemith.InputParameters.setInstByte(inst_array);
        Hindemith.InputParameters.setTransposeIntervals(transpose_intervals);
        MidiOut.setDevice();
        ModelIncubator mymodel = new ModelIncubator();
        //CanonizerModalTransposeModel mymodel = new CanonizerModalTransposeModel();
        //CanonizerModel mymodel = new CanonizerModel();
        //Model_1 mymodel = new Model_1();
    }
    
}
