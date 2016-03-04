package ca.uwaterloo.Lab3_206_12;

public class Step {
    private int stepcount;
    private int stage;
    private double x;
    private double y;
    private double[] displacment =new double[2];
    private double angle;
    private final int OUTOFBOUND = -1;
    private final int STABLE = 1;
    private final int RISING = 5;
    private final int PEAK = 3;
    private final int FALLING = 4;
    private final int CHANGING =2;
    //Assign each state a fixed value so that we can manipulate the state easily by just using its name.
    public Step()
    {
        stepcount = 0;
        stage = 1;
    }//The constructor of the step type so that it holds a stage counting and the current stage.
    public int count(float sensorin)
    {
        if(isInRange(sensorin) == OUTOFBOUND)
            {
                this.stage = STABLE;
            }
        else if (isInRange(sensorin) == STABLE)
            {
                if(this.stage == FALLING)
                {
                    this.stage = STABLE;
                    stepcount++;
                    x += 0.8*Math.sin(angle);
                    y += 0.8*Math.cos(angle);
                    displacment[0] = x;
                    displacment[1] = y;
                }
                else if(this.stage == RISING)
                {
                    this.stage = STABLE;
                }
            }
            else if (isInRange(sensorin) == CHANGING)
            {
                if(this.stage == STABLE)
                {
                    this.stage = RISING;
                }
                else if(this.stage == PEAK)
                {
                    this.stage = FALLING;
                }
            }
            else if(isInRange(sensorin) == PEAK)
            {
                if(this.stage == RISING)
                {
                    this.stage = PEAK;
                }
            }
        return stepcount;
    }//This method changes the current stage according the range and the previous stage. And it returns the final count.
    public double[] Displacment(){
    	return displacment;
    }
    public void SetAngle(double input){
    	this.angle = input;
    }
    public void Reset()
    {
    	this.stepcount = 0;
    }
    private int isInRange(float sensorin)
    {
        int result = this.stage;
        if (-0.5f <sensorin && sensorin <0.1f ){
            result = STABLE;
        } else if (0.1f<=sensorin && sensorin <0.4f){
            result = CHANGING;
        } else if (0.4f<=sensorin && sensorin <0.8f){
            result = PEAK;
        } else if (0.8f<sensorin || -0.5f>sensorin){
            result = OUTOFBOUND;
        }
        return result;
    }//This method takes in the sensor value to determine which state contains the sensor value and return the range to the stage-changing method.
    
}
