package ca.uwaterloo.Lab2_206_12;

import java.util.Arrays;

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
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
	static LineGraphView graph;
	
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
			
			// Add text views to display sensor output
			TextView lightView = new TextView(rootView.getContext());
			TextView linearView = new TextView(rootView.getContext());
			TextView magView = new TextView(rootView.getContext());
			TextView rotationView = new TextView(rootView.getContext());
			
			// Specify the linear layout to be used
			LinearLayout rmain = (LinearLayout) rootView.findViewById(R.id.label2);
			
			// Add graphs, 
			rmain.addView(graph);
			rmain.addView(lightView);
			rmain.addView(linearView);
			//rmain.addView(magView);
			//rmain.addView(rotationView);
			rmain.setOrientation(LinearLayout.VERTICAL);
	
			
			SensorManager sensorManager = (SensorManager) rootView.getContext().getSystemService(SENSOR_SERVICE);
			Sensor lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
			SensorEventListener light = new LightSensorEventListener(lightView);
			Sensor linearSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
			SensorEventListener linear = new LinearSensorEventListener(linearView);
			//Sensor rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
			//SensorEventListener r = new RoSensorEventListener(rotationView);
			sensorManager.registerListener(light, lightSensor, SensorManager.SENSOR_DELAY_FASTEST);
			sensorManager.registerListener(linear, linearSensor, SensorManager.SENSOR_DELAY_FASTEST);
			//sensorManager.registerListener(r, rotationSensor, SensorManager.SENSOR_DELAY_FASTEST);
			return rootView;	
			
		}
		class LightSensorEventListener implements SensorEventListener {
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
		}
		class LinearSensorEventListener implements SensorEventListener {
			TextView output;
			double maxx = 0;
			double maxy = 0;
			double maxz = 0;
			public LinearSensorEventListener(TextView outputView){
					output = outputView;
			}
			
			public void onAccuracyChanged(Sensor s, int i){}
			
			public void onSensorChanged(SensorEvent se){
				if(se.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION){
					double x = se.values[0];
					double y = se.values[1];
					double z = se.values[2];
					maxx = getMax(Math.abs(x), maxx);
					maxy = getMax(Math.abs(y), maxy);
					maxz = getMax(Math.abs(z), maxz);
					graph.addPoint(se.values);
					String accelValues= String.format("Acceleration:\n\n X: %.2f [m/s^2]\n Y: %.2f [m/s^2]\n Z: %.2f [m/s^2]\n Maximum Value:(%.2f,%.2f,%.2f)\n\n", x,y,z,maxx,maxy,maxz); 
					output.setText(accelValues);
					
				}
			}
		}

/*		class MgSensorEventListener implements SensorEventListener {
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
					String mgValues= String.format("Magnetic Field:\n\n X: %.2f [m/s^2]\n Y: %.2f [m/s^2]\n Z: %.2f [m/s^2]\n Maximum Value: (%.2f,%.2f,%.2f)\n\n", x,y,z,maxx,maxy,maxz); 
					output.setText(mgValues);
				}
			}
		}

		class RoSensorEventListener implements SensorEventListener {
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
					String roValues= String.format("Rotation:\n\n X: %.2f [m/s^2]\n Y: %.2f [m/s^2]\n Z: %.2f [m/s^2]\nMaximum Value:(%.2f,%.2f,%.2f)", x,y,z,maxx,maxy,maxz); 
					output.setText(roValues);
				}
			}
		}*/
	}
}



