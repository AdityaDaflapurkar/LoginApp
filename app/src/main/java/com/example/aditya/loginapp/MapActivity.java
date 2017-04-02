package com.example.aditya.loginapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class MapActivity extends Activity implements SensorEventListener {

    class Pt {
        float x, y;
        Pt(float _x, float _y) {
            x = _x;
            y = _y;
        }
    }

    ArrayList<Pt> pointerlist = new ArrayList<Pt>();

    private float mLastX, mLastY, mLastZ;
    private boolean mInitialized;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private int nsteps=0;
    private boolean pulse=false;
    private final float NOISEX = (float) 0.5 ;
    private final float NOISEY = (float) 0.3 ;
    private final float NOISEZ = (float) 0.3 ;
    private  long i=0;
    TextView testtext;
    TextView steptest;
    ImageView imageview;
    Button button1,button2;
    Bitmap bitmap1,bitmap2;
    Canvas canvas;
    Paint paint;
    Resources resources;
    Matrix matrix;
    int bitmap1Width, bitmap1Height, CanvasWidth,CanvasHeight;
    float clx,cly;
    float theta, theta2;
    int degreesAngle = 0 ;
    private boolean firstrotate = true;

    private Sensor mMagnetometer;
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;
    private float[] mR = new float[9];
    private float[] mOrientation = new float[3];
    // private float mCurrentDegree = 0f;

    //FOR THE GRAPH
    private String mapJSON;
    private Graph currentGraph;
    private LinkedList<Vertex> givenPath;
    private DijkstraAlgorithm dijkstraAlgorithm;

  

    private static List<Vertex> nodes;
    private static List<Edge> edges;

    


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_map);
        imageview = (ImageView)findViewById(R.id.imageView1);
        testtext = (TextView) findViewById(R.id.txtbox);
        steptest = (TextView) findViewById(R.id.stpbx);


        nodes = new ArrayList<Vertex>();
        edges = new ArrayList<Edge>();

        Vertex location = new Vertex("Gate_L1", "Node_00", 192f, 388f);
        nodes.add(location);
        location = new Vertex("Gate_L2", "Node_01", 395f, 388f);
        nodes.add(location);
        location = new Vertex("Gate_U1", "Node_02", 95f, 273f);
        nodes.add(location);
        location = new Vertex("Gate_U2", "Node_03", 495f, 273f);
        nodes.add(location);

        location = new Vertex("Gate_L3", "Node_04", 268f, 360f);
        nodes.add(location);
        location = new Vertex("Gate_L4", "Node_05", 335f, 360f);
        nodes.add(location);

        location = new Vertex("Gate_U3", "Node_06", 212f, 235f);
        nodes.add(location);
        location = new Vertex("Gate_U4", "Node_07", 376f, 238f);
        nodes.add(location);

        addLane("Edge_0", 0, 1, 17);
        addLane("Edge_1", 0, 2, 15);
        addLane("Edge_2", 1, 3, 15);
        addLane("Edge_3", 2, 3, 30);
        addLane("Edge_4", 2, 6, 12);
        addLane("Edge_5", 6, 7, 15);
        addLane("Edge_6", 7, 3, 12);
        addLane("Edge_7", 0, 4, 7);
        addLane("Edge_8", 4, 5, 10);
        addLane("Edge_9", 5, 1, 6);
        addLane("Edge_10", 4, 6, 15);
        addLane("Edge_11", 5, 7, 15);
        currentGraph = new Graph(nodes, edges);
        ///////
/*          //// COMMENTED TO IMPLEMENT NEW GRAPH
        Gson gson = new Gson();
        Type type = new TypeToken<Graph>() {}.getType();
        mapJSON = "{\"vertexes\":[{\"id\":\"Gate_L1\",\"name\":\"Node_00\",\"xCoord\":150.0,\"yCoord\":400.0},{\"id\":\"Gate_L2\",\"name\":\"Node_01\",\"xCoord\":450.0,\"yCoord\":400.0},{\"id\":\"Gate_U1\",\"name\":\"Node_02\",\"xCoord\":100.0,\"yCoord\":200.0},{\"id\":\"Gate_U2\",\"name\":\"Node_03\",\"xCoord\":500.0,\"yCoord\":200.0}],\"edges\":[{\"id\":\"Edge_0\",\"source\":{\"id\":\"Gate_L1\",\"name\":\"Node_00\",\"xCoord\":150.0,\"yCoord\":400.0},\"destination\":{\"id\":\"Gate_L2\",\"name\":\"Node_01\",\"xCoord\":450.0,\"yCoord\":400.0},\"weight\":20},{\"id\":\"Edge_0\",\"source\":{\"id\":\"Gate_L2\",\"name\":\"Node_01\",\"xCoord\":450.0,\"yCoord\":400.0},\"destination\":{\"id\":\"Gate_L1\",\"name\":\"Node_00\",\"xCoord\":150.0,\"yCoord\":400.0},\"weight\":20},{\"id\":\"Edge_1\",\"source\":{\"id\":\"Gate_L1\",\"name\":\"Node_00\",\"xCoord\":150.0,\"yCoord\":400.0},\"destination\":{\"id\":\"Gate_U1\",\"name\":\"Node_02\",\"xCoord\":100.0,\"yCoord\":200.0},\"weight\":15},{\"id\":\"Edge_1\",\"source\":{\"id\":\"Gate_U1\",\"name\":\"Node_02\",\"xCoord\":100.0,\"yCoord\":200.0},\"destination\":{\"id\":\"Gate_L1\",\"name\":\"Node_00\",\"xCoord\":150.0,\"yCoord\":400.0},\"weight\":15},{\"id\":\"Edge_2\",\"source\":{\"id\":\"Gate_L2\",\"name\":\"Node_01\",\"xCoord\":450.0,\"yCoord\":400.0},\"destination\":{\"id\":\"Gate_U2\",\"name\":\"Node_03\",\"xCoord\":500.0,\"yCoord\":200.0},\"weight\":15},{\"id\":\"Edge_2\",\"source\":{\"id\":\"Gate_U2\",\"name\":\"Node_03\",\"xCoord\":500.0,\"yCoord\":200.0},\"destination\":{\"id\":\"Gate_L2\",\"name\":\"Node_01\",\"xCoord\":450.0,\"yCoord\":400.0},\"weight\":15},{\"id\":\"Edge_3\",\"source\":{\"id\":\"Gate_U1\",\"name\":\"Node_02\",\"xCoord\":100.0,\"yCoord\":200.0},\"destination\":{\"id\":\"Gate_U2\",\"name\":\"Node_03\",\"xCoord\":500.0,\"yCoord\":200.0},\"weight\":30},{\"id\":\"Edge_3\",\"source\":{\"id\":\"Gate_U2\",\"name\":\"Node_03\",\"xCoord\":500.0,\"yCoord\":200.0},\"destination\":{\"id\":\"Gate_U1\",\"name\":\"Node_02\",\"xCoord\":100.0,\"yCoord\":200.0},\"weight\":30}]}";
        currentGraph = gson.fromJson(mapJSON, type);

        */
        dijkstraAlgorithm = new DijkstraAlgorithm(currentGraph);
        Bundle bundle = getIntent().getExtras();

        int startingVertex = bundle.getInt("startingVertex");
        int destinationVertex = bundle.getInt("destinationVertex");
        dijkstraAlgorithm.execute(currentGraph.getVertexes().get(startingVertex));
        givenPath = dijkstraAlgorithm.getPath(currentGraph.getVertexes().get(destinationVertex));


        mInitialized = false;
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);



        button1 = (Button)findViewById(R.id.button1);
        resources = getResources();
        matrix =  new Matrix();
        CreateBitmap();
        bitmap2 = Bitmap.createBitmap(600, 800, Bitmap.Config.RGB_565);
        CreateCanvas();

        GetWidthHeight();

        clx = givenPath.getFirst().getxCoord();
        cly = givenPath.getFirst().getyCoord();

        matrix.preTranslate(clx, cly);
        pointerlist.add(new Pt(clx, cly));
        DrawCanvas();
        imageview.setImageBitmap(bitmap2);
        button1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private static void addLane(String laneId, int sourceLocNo, int destLocNo, int duration) {
        Edge lane = new Edge(laneId,nodes.get(sourceLocNo), nodes.get(destLocNo), duration);
        edges.add(lane);
        lane = new Edge(laneId,nodes.get(destLocNo), nodes.get(sourceLocNo), duration);
        edges.add(lane);
    }

    public void CreateBitmap(){
        bitmap1 = BitmapFactory.decodeResource(resources, R.drawable.redcircle);
    }
    public void CreateCanvas(){
        canvas = new Canvas(bitmap2);
     
        Drawable d = getResources().getDrawable(R.drawable.floorplan);
        d.setBounds(0, 0, 600, 800);
        d.draw(canvas);

        Paint p=new Paint();
        p.setColor(Color.BLUE);
        p.setStrokeWidth(10);
        Vertex from;
        Vertex to;
        for(int i = 0; i< givenPath.size() - 1; i++){
            from = givenPath.get(i);
            to = givenPath.get(i+1);
            canvas.drawLine(from.getxCoord(), from.getyCoord(), to.getxCoord(), to.getyCoord(), p);
        }
        Paint paint=new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);

        Paint graypaint=new Paint();
        graypaint.setColor(Color.GRAY);
        graypaint.setStyle(Paint.Style.FILL);

        for(Vertex vertex : currentGraph.getVertexes()) {
            if(givenPath.contains(vertex)) {
                canvas.drawCircle(vertex.getxCoord(), vertex.getyCoord(), 10, paint);
            }
            else{
                canvas.drawCircle(vertex.getxCoord(), vertex.getyCoord(), 10, graypaint);

            }
        }

    }
    public void GetWidthHeight(){
        bitmap1Width = bitmap1.getWidth() / 2 ;
        bitmap1Height = bitmap1.getHeight() / 2 ;
        CanvasWidth = canvas.getWidth() / 2 ;
        CanvasHeight = canvas.getHeight() / 2 ;
    }
    public void DrawCanvas(){

        //FOR PATH

        Paint paintPath = new Paint();
        paintPath.setColor(Color.RED);
        paintPath.setStrokeWidth(5);
        paintPath.setStyle(Paint.Style.STROKE);
        Path path = new Path();
        path.moveTo(pointerlist.get(0).x, pointerlist.get(0).y);
        Pt circle = new Pt(0f,0f);
        for (Pt p : pointerlist) {
            path.lineTo(p.x, p.y);
            circle.x = p.x;
            circle.y = p.y;
        }
        canvas.drawPath(path, paintPath);
        paintPath.setStyle(Paint.Style.FILL);
        canvas.drawCircle(circle.x, circle.y, 10, paintPath);

    }


   
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_GAME);
    }
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this, mAccelerometer);
        mSensorManager.unregisterListener(this, mMagnetometer);
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    @Override
    public void onSensorChanged(SensorEvent event)
    {
        if (event.sensor == mAccelerometer) {
            System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.length);
            mLastAccelerometerSet = true;

            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            if (!mInitialized) {
                mLastX = x;
                mLastY = y;
                mLastZ = z;
                mInitialized = true;
            } else {
                float deltaX = Math.abs(mLastX - x);
                float deltaY = Math.abs(mLastY - y);
                float deltaZ = Math.abs(mLastZ - z);
                if (deltaY>NOISEY && deltaZ>NOISEZ)
                {
                    nsteps++;
                    steptest.setText(Integer.toString(nsteps));

                    this.onPause();
                    bitmap2 = Bitmap.createBitmap(600, 800, Bitmap.Config.RGB_565);
                    CreateCanvas();
                    GetWidthHeight();
                    Paint p1=new Paint();
                    p1.setStrokeWidth(5);
                    p1.setColor(Color.WHITE);
                    float clx_t = (float) (10 * Math.sin(Math.toRadians(theta2)));
                    float cly_t = (float) (10 * Math.cos(Math.toRadians(theta2)));
                    canvas.drawLine(clx,cly,clx + clx_t, cly - cly_t,p1);
                    clx = clx + clx_t;
                    cly = cly - cly_t;
                    pointerlist.add(new Pt(clx, cly));
                    matrix.postTranslate(clx_t, cly_t);


                    DrawCanvas();
                    imageview.setImageBitmap(bitmap2);

                    try {
                        TimeUnit.MILLISECONDS.sleep(330);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    this.onResume();
                }
                mLastX = x;
                mLastY = y;
                mLastZ = z;
            }
        }
        else if (event.sensor == mMagnetometer) {
            System.arraycopy(event.values, 0, mLastMagnetometer, 0, event.values.length);
            mLastMagnetometerSet = true;
        }
        if (mLastAccelerometerSet && mLastMagnetometerSet) {
            SensorManager.getRotationMatrix(mR, null, mLastAccelerometer, mLastMagnetometer);
            SensorManager.getOrientation(mR, mOrientation);
            float azimuthInRadians = mOrientation[0];
            float azimuthInDegress = (float)(Math.toDegrees(azimuthInRadians)+360 - 24 )%360;
            testtext.setText(Float.toString(azimuthInDegress));
          
            theta2 = azimuthInDegress;


            mLastMagnetometerSet = false;
            mLastAccelerometerSet = false;
            
        }


    }
}
