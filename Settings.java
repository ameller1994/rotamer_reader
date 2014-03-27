import java.util.*;
import java.io.*;

/**
 * Holds variables that control the behavior of the program.
 */
public class Settings
{
    /** the directory containing the Dunbrack backbone-dependent rotamer data */
    public static final String ROTAMER_LIBRARY_DIRECTORY = "rotamer_library/";

    /** the probability below which rotamers will be ignored */
    public static final double ROTAMER_LIBRARY_THRESHOLD = 0.01;

    /** the file containing the omega data */
    public static final String OMEGA_DATA_FILENAME = "omega/omegaCDL_OmegaBetweenAsPhi1Psi0_KernRegr_v1.3.1_Aug12-2011.txt";
}
