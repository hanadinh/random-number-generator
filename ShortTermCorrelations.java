package final_project;
import org.opensourcephysics.controls.AbstractSimulation;
import org.opensourcephysics.controls.SimulationControl;
import org.opensourcephysics.frames.PlotFrame;
import java.util.Random;

public class ShortTermCorrelations extends AbstractSimulation {
	Random rand = new Random();
    int sequenceLength;
    PlotFrame LCGAutoCorrelationPlot = new PlotFrame("k", "C(k)", "Autocorrelation Function (LCG)");
    PlotFrame GFSRAutoCorrelationPlot = new PlotFrame("k", "C(k)", "Autocorrelation Function (GFSR)");
    double[] lcgSequence;
    double[] gfsrSequence;
    LCG lcg;
	GFSR gfsr;

    public ShortTermCorrelations() {
    }

    public void initialize() {
    	sequenceLength = control.getInt("Sequence length");
        // Clear previous data
        LCGAutoCorrelationPlot.clearData();
        GFSRAutoCorrelationPlot.clearData();

        // GFSR
        int p = 250;
        int q = 103;
        int[] sequence = new int[p];
        for (int i = 0; i < p; i++) {
            sequence[i] = rand.nextInt(Integer.MAX_VALUE);
        }
        
        // Initialize random generators
        lcg = new LCG(12, 16807, 2147483647, 0);
        gfsr = new GFSR(sequence, p, q);
        
        // Initialize arrays for storing random values
        lcgSequence = new double[sequenceLength];
        gfsrSequence = new double[sequenceLength];
        for (int i = 0; i < sequenceLength; i++) {
        	lcgSequence[i] = lcg.nextRandom();
        	gfsrSequence[i] = gfsr.nextRandom();
        }
    }

    public static void main(String[] args) {
        SimulationControl.createApp(new ShortTermCorrelations());
    }

    protected void doStep() {
    	double[] LCGautocorrelation = computeAutocorrelation(lcgSequence);
    	double[] GFSRautocorrelation = computeAutocorrelation(gfsrSequence);
    	
    	for (int k = 0; k < sequenceLength; k++) {
    		LCGAutoCorrelationPlot.append(0, k, LCGautocorrelation[k]);
    		GFSRAutoCorrelationPlot.append(0, k, GFSRautocorrelation[k]);
        }
        
        this.stopSimulation();
    }

    public void reset() {
        control.setValue("Sequence length", 1000);

        this.initialize();
    }
    
    public static double[] computeAutocorrelation(double[] sequence) {
        int n = sequence.length;
        double[] autocorrelation = new double[n];

        // Calculate mean of the sequence
        double mean = calculateMean(sequence);

        // Calculate ⟨x_i * x_i⟩
        double xiSquaredMean = calculateMeanOfSquared(sequence);

        // Calculate autocovariance and autocorrelation
        for (int k = 0; k < n; k++) {
            double sumCovariance = 0;
            for (int i = 0; i < n - k; i++) {
                sumCovariance += (sequence[i + k] * sequence[i]);
            }
            autocorrelation[k] = (sumCovariance / (n - k) - Math.pow(mean, 2)) / (xiSquaredMean - Math.pow(mean, 2));
        }

        return autocorrelation;
    }

    // Calculate mean of the sequence
    public static double calculateMean(double[] sequence) {
        double sum = 0;
        for (double num : sequence) {
            sum += num;
        }
        return sum / sequence.length;
    }

    // Calculate mean of squared values of the sequence
    public static double calculateMeanOfSquared(double[] sequence) {
        double sum = 0;
        for (double num : sequence) {
            sum += Math.pow(num, 2);
        }
        return sum / sequence.length;
    }

}
