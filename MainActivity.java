package ca.uwaterloo.Lab3_206_12;

import java.util.Arrays;

import ca.uwaterloo.Lab3_206_12.R;
import ca.uwaterloo.sensortoy.LineGraphView;
import android.app.Activity;
import android.app.Fragment; 
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
	static LineGraphView graph;
	static Step step;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		graph = new LineGraphView(getApplicationContext(), 100, Arrays.asList("x", "y", "z"));
		graph.setVisibility(View.VISIBLE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		public double getMax(double input, double max)
		{
			if(input > max)
			{
				max = input;
			}
			return max;
		}
		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			
			//Button
			Button Reset = new Button(rootView.getContext());
			Reset.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					step.Reset();
				}
			});
			Reset.setText("Reset steps");
			
			// Add text views to display sensor output
			TextView lightView = new TextView(rootView.getContext());
			TextView linearView = new TextView(rootView.getContext());
			TextView magnetView = new TextView(rootView.getContext());
			TextView rotationView = new TextView(rootView.getContext());
			TextView accView = new TextView(rootView.getContext());
			// Specify the linear layout to be used
			LinearLayout rmain = (LinearLayout) rootView.findViewById(R.id.label2); 
			
			// Add graphs, 
			rmain.addView(graph);
			//rmain.addView(lightView);
			rmain.addView(linearView);
			rmain.addView(magnetView);
			//rmain.addView(rotationView);
			rmain.addView(accView);
			rmain.addView(Reset);
			rmain.setOrientation(LinearLayout.VERTICAL);
	
			
			SensorManager sensorManager = (SensorManager) rootView.getContext().getSystemService(SENSOR_SERVICE);
			//Sensor lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
			//SensorEventListener light = new LightSensorEventListener(lightView);
			Sensor magnetsensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
			SensorEventListener magnet = new MgSensorEventListener(magnetView);
			Sensor linearSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
			SensorEventListener linear = new LinearSensorEventListener(linearView);
			Sensor rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
			//SensorEventListener r = new RoSensorEventListener(rotationView);
			Sensor accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			SensorEventListener acc = new AccSensorEventListener(accView);
			//sensorManager.registerListener(light, lightSensor, SensorManager.SENSOR_DELAY_FASTEST);
			sensorManager.registerListener(linear, linearSensor, SensorManager.SENSOR_DELAY_FASTEST);
			//sensorManager.registerListener(r, rotationSensor, SensorManager.SENSOR_DELAY_FASTEST);
			sensorManager.registerListener(acc,accSensor, SensorManager.SENSOR_DELAY_FASTEST);
			sensorManager.registerListener(magnet, magnetsensor, SensorManager.SENSOR_DELAY_FASTEST);
			return rootView;
		}
		/*class LightSensorEventListener implements SensorEventListener {
			TextView output;
			double maxlight = 0;
			public LightSensorEventListener(TextView outputView){
					output = outputView;
			}
			
			public void onAccuracyChanged(Sensor s, int i){}
			
			public void onSensorChanged(SensorEvent se){
				if(se.sensor.getType() == Sensor.TYPE_LIGHT){
					double lightValue = se.values[0];
					maxlight = getMax(se.values[0], maxlight);
					output.setText(String.format("Light Sensor: (%.2f)\nMaximum Value:(%.2f)\n ", lightValue,maxlight));
				}
			}
		}*/
		float[] accmeter = new float[3];
		float[] magmeter = new float[3];
		float[] rotationmatrix  = new float[9];
		float[] angle = new float[3];
		double x;
		double y;
		double stepdistance = 0.8;
		double[] displacment = new double[2];
		String direction;
		
			
		class LinearSensorEventListener implements SensorEventListener {
			TextView output;
			double maxx = 0;
			double maxy = 0;
			double maxz = 0;
			float[] accAve = new float[3];
			
			public LinearSensorEventListener(TextView outputView){
					output = outputView;
					step = new Step();//A step class that holds step properties and counting method and do the step counting.
			}
			
			public void onAccuracyChanged(Sensor s, int i){}
			
			public void onSensorChanged(SensorEvent se){
				if(se.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION){
					if (accAve [0]==0 &&accAve [1]==0 &&accAve [2]==0){
						accAve[0] = se.values[0];
						accAve[1] = se.values[1];
						accAve[2] = se.values[2];
					}
					double x = se.values[0];
					double y = se.values[1];
					double z = se.values[2];
					maxx = getMax(Math.abs(x), maxx);
					maxy = getMax(Math.abs(y), maxy);
					maxz = getMax(Math.abs(z), maxz);
					accAve = method.lowpass(se.values,accAve); // call the lowpass method to filter the signal
					graph.addPoint(accAve);
					//String accelValues= String.format("Acceleration:\n\n X: %.2f [m/s^2]\n Y: %.2f [m/s^2]\n Z: %.2f [m/s^2]\n Maximum Value:(%.2f,%.2f,%.2f)\n\n", x,y,z,maxx,maxy,maxz); 
					String accelValues = String.format("--Steps Taken--\n");//add the number of steps taken.
					accelValues += step.count(accAve[2]);
					output.setText(accelValues);
					
				}
			}
		}

		class AccSensorEventListener implements SensorEventListener {
			TextView output;
			double maxx = 0;
			double maxy = 0;
			double maxz = 0;
			public AccSensorEventListener(TextView outputView){
					output = outputView;
			}
			
			public void onAccuracyChanged(Sensor s, int i){}
			
			public void onSensorChanged(SensorEvent se){
				if(se.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
					System.arraycopy(se.values, 0, accmeter, 0, 3);
					if(magmeter != null)
					{
						getAngle();
					}
					
					String angleout = String.format("Angle in radians: %f\nAngle in degree: %f", angle[0],Math.toDegrees(angle[0]));
					step.SetAngle(angle[0]);
					displacment = step.Displacment();
					angleout += String.format("\nDisplacment: (%f,%f)",displacment[0],displacment[1]);
					if(Math.toDegrees(angle[0]) < 0 && Math.toDegrees(angle[0]) >-90) direction = "North east";
					else if(Math.toDegrees(angle[0]) < 90 && Math.toDegrees(angle[0])>0) direction = "North west";
					else if(Math.toDegrees(angle[0]) < 180 && Math.toDegrees(angle[0])>90) direction = "South west";
					else if(Math.toDegrees(angle[0]) < -90 && Math.toDegrees(angle[0])>-180) direction = "South east";
					angleout +=String.format("\nHeading direction:"+" "+ direction);
					output.setText(angleout);
				}
			}
		}
		
		class MgSensorEventListener implements SensorEventListener {
			TextView output;
			double maxx = 0;
			double maxy = 0;
			double maxz = 0;
			public MgSensorEventListener(TextView outputView){
					output = outputView;
			}
			
			public void onAccuracyChanged(Sensor s, int i){}
			
			public void onSensorChanged(SensorEvent se){
				if(se.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
					double x = se.values[0];
					double y = se.values[1];
					double z = se.values[2];
					maxx = getMax(Math.abs(x), maxx);
					maxy = getMax(Math.abs(y), maxy);
					maxz = getMax(Math.abs(z), maxz);
					System.arraycopy(se.values, 0, magmeter, 0, 3);
					//String mgValues= String.format("Magnetic Field:\n\n X: %.2f [m/s^2]\n Y: %.2f [m/s^2]\n Z: %.2f [m/s^2]\n Maximum Value: (%.2f,%.2f,%.2f)\n\n", x,y,z,maxx,maxy,maxz); 
					//output.setText(mgValues);
				}
			}
		}
		/*class RoSensorEventListener implements SensorEventListener {
			TextView output;
			double maxx = 0;
			double maxy = 0;
			double maxz = 0;
			public RoSensorEventListener(TextView outputView){
					output = outputView;
			}
			
			public void onAccuracyChanged(Sensor s, int i){}
			
			public void onSensorChanged(SensorEvent se){
				if(se.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR){
					double x = se.values[0];
					double y = se.values[1];
					double z = se.values[2];
					maxx = getMax(Math.abs(x), maxx);
					maxy = getMax(Math.abs(y), maxy);
					maxz = getMax(Math.abs(z), maxz);
					//String roValues= String.format("Rotation:\n\n X: %.2f [m/s^2]\n Y: %.2f [m/s^2]\n Z: %.2f [m/s^2]\nMaximum Value:(%.2f,%.2f,%.2f)", x,y,z,maxx,maxy,maxz); 
					//output.setText(roValues);
				}
			}
		}*/
		public void getAngle (){
			if(SensorManager.getRotationMatrix(rotationmatrix, null, accmeter, magmeter))
			{SensorManager.getOrientation(rotationmatrix,angle);}
		}
	}
}



