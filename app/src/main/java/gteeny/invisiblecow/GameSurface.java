package gteeny.invisiblecow;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Handler;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import com.firebase.client.Firebase;

public class GameSurface extends SurfaceView implements SurfaceHolder.Callback {

    private Context _context;
    private GameThread _thread;
    private GameControls _controls;
    private Firebase fb;
    private long player;

	private GameJoystick _joystick;
	private Mooer cow;

	private Bitmap _pointer;
	private Bitmap cowbell;

    public GameSurface(Context context, Mooer cow, Firebase fb) {
        super(context);
        this.fb = fb;
        // TODO Auto-generated constructor stub
        _context = context;
        this.cow = cow;
        init();
    }

    private void init() {
        //initialize our screen holder
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        //initialize our game engine

        //initialize our Thread class. A call will be made to start it later
        _thread = new GameThread(holder, _context, new Handler(), this);
        setFocusable(true);


		_joystick = new GameJoystick(getContext().getResources());
		_pointer = (Bitmap)BitmapFactory.decodeResource(getResources(), R.drawable.icon);
		cowbell = (Bitmap)BitmapFactory.decodeResource(getResources(), R.drawable.cowbell);
		//controls
		_controls = new GameControls(cow, fb);
		setOnTouchListener(_controls);
	}

    public void doDraw(Canvas canvas) {

		//update the pointer
		_controls.update(null);
		
		//draw the pointer
		canvas.drawBitmap(_pointer, _controls._pointerPosition.x, _controls._pointerPosition.y, null);
		canvas.drawBitmap(cowbell, 100, 1100, null);

        //draw the joystick background
        canvas.drawBitmap(_joystick.get_joystickBg(), 500, 1050, null);

        //draw the dragable joystick
        canvas.drawBitmap(_joystick.get_joystick(), _controls._touchingPoint.x - 26, _controls._touchingPoint.y - 26,
                null);

    }


    //these methods are overridden from the SurfaceView super class. They are automatically called
    //when a SurfaceView is created, resumed or suspended.
    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        _controls.setHeight(getHeight());
        _controls.setWidth(getWidth());
    }

    private boolean retry;

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        retry = true;
        //code to end gameloop
        _thread.state = GameThread.STOPPED;
        while (retry) {
            try {
                //code to kill Thread
                _thread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }

    }

    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
        if (_thread.state == GameThread.PAUSED) {
            //When game is opened again in the Android OS
            _thread = new GameThread(getHolder(), _context, new Handler(), this);
            _thread.start();
        } else {
            //creating the game Thread for the first time
            _thread.start();
        }
    }

    public void Update() {
        // TODO Auto-generated method stub

    }

}
