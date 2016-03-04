package ca.uwaterloo.Lab3_206_12;

public abstract class method {
	public static float[] lowpass(float[] in, float [] ave) {
		float a = 0.98f;
		float[] out = new float[in.length];
		for(int i = 1; i < in.length; i++) {
			out[i] =a* ave[i] + (1-a) * in[i];
		}
		return out;
	}
}
/* the whole method is the algorithm to filter the noise in the signal.
we input the signal and use the algorithm. the algorithm will find the average value 
so it changes the value to the average level to eliminate the outliars.*/
