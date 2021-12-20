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
import java.util.ArrayList;
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
        while (true) {
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
        Hindemith.InputParameters.setPieceLength(1 + roll.nextInt(2));
        Integer number_voices = 2 + roll.nextInt(4);
        String []  voice_array = new String[number_voices];
        byte [] inst_array = new byte[number_voices];
        ArrayList<Byte> inst_collection = new ArrayList();
        Integer [] t_intervals = {3, 5, 7, 12};
        int [] transpose_intervals = new int[number_voices];
        for (int i = 0; i < number_voices; i++){
            voice_array[i] = "bass";
        }

        //choose an instrument collection
        switch ( roll.nextInt(11) ){
            case (0):
                for (int i = 0; i < number_voices; i++){
                    inst_array[i] = (byte)19;
                }
                break;
            case (1):
                inst_collection.add((byte)16);
                inst_collection.add((byte)17);
                for (int i = 0; i < number_voices; i++){
                    inst_array[i] = inst_collection.get(roll.nextInt(inst_collection.size()));
                }
                break;
            case (2):
                for (int i = 0; i < number_voices; i++){
                    inst_array[i] = (byte)51;
                }
                break;
            case (3):
                inst_collection.add((byte)49);
                inst_collection.add((byte)51);
                inst_array[0] = (byte)51;
                for (int i = 1; i < number_voices; i++){
                    inst_array[i] = inst_collection.get(roll.nextInt(inst_collection.size()));
                }
                break;
            case (4):
                inst_collection.add((byte)49);
                inst_collection.add((byte)51);
                inst_collection.add((byte)45);
                inst_collection.add((byte)93);
                inst_collection.add((byte)95);
                inst_collection.add((byte)53);
                inst_array[0] = (byte)51;
                for (int i = 1; i < number_voices; i++){
                    inst_array[i] = inst_collection.get(roll.nextInt(inst_collection.size()));
                }
                break;
            case (5):
                inst_collection.add((byte)46);
                inst_collection.add((byte)8);
                inst_array[0] = (byte)46;
                for (int i = 1; i < number_voices; i++){
                    inst_array[i] = inst_collection.get(roll.nextInt(inst_collection.size()));
                }                
                break;
            case (6):
                inst_collection.add((byte)46);
                inst_collection.add((byte)11);
                inst_array[0] = (byte)46;
                for (int i = 1; i < number_voices; i++){
                    inst_array[i] = inst_collection.get(roll.nextInt(inst_collection.size()));
                }                
                break;
            case (7):
                inst_collection.add((byte)80);
                inst_collection.add((byte)81);
                inst_collection.add((byte)85);
                inst_collection.add((byte)92);
                inst_collection.add((byte)99);
                inst_array[0] = (byte)87;
                for (int i = 1; i < number_voices; i++){
                    inst_array[i] = inst_collection.get(roll.nextInt(inst_collection.size()));
                }                
                break;
            case (8):
                inst_collection.add((byte)102);
                inst_collection.add((byte)103);
                inst_collection.add((byte)98);
                inst_collection.add((byte)94);
                inst_collection.add((byte)91);
                inst_collection.add((byte)96);
                inst_array[0] = (byte)96;
                for (int i = 1; i < number_voices; i++){
                    inst_array[i] = inst_collection.get(roll.nextInt(inst_collection.size()));
                }                
                break;
            case (9):
                inst_collection.add((byte)8);
                inst_collection.add((byte)46);
                inst_collection.add((byte)54);
                inst_collection.add((byte)11);
                inst_collection.add((byte)10);
                inst_array[0] = (byte)46;
                for (int i = 1; i < number_voices; i++){
                    inst_array[i] = inst_collection.get(roll.nextInt(inst_collection.size()));
                }                
                break;                
            default:
                for (int i = 0; i < number_voices; i++){
                    inst_array[i] = (byte)46;
                }
        }
        
        transpose_intervals[0] = 0;
        for (int i = 1; i < number_voices; i++){
            transpose_intervals[i] = t_intervals[roll.nextInt(3)];
        }
        
        Hindemith.InputParameters.setVoiceArray(voice_array);
        Hindemith.InputParameters.setInstByte(inst_array);
        Hindemith.InputParameters.setTransposeIntervals(transpose_intervals);
        Hindemith.InputParameters.setOverallTranspose(roll.nextInt(5));
        MidiOut.setDevice();
        try {
            ModelIncubator mymodel = new ModelIncubator();
        }
        catch ( Exception e ){
           System.out.println("There was an error generating the canon"); 
        }
    }
        //CanonizerModalTransposeModel mymodel = new CanonizerModalTransposeModel();
        //CanonizerModel mymodel = new CanonizerModel();
        //Model_1 mymodel = new Model_1();
    }
    
}
