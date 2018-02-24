import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseInputListener;

public class Core implements NativeMouseInputListener{
	private Window w;
  private static ScheduledExecutorService scheduler;
  private ScheduledFuture<?> scheduled_task;
  private Runnable task;
  private AudioManager audio_manager;
	
	public Core() {
		scheduler = Executors.newScheduledThreadPool(1);
		w = new Window();
		w.showWindow();
		
		try{
			audio_manager = new AudioManager(!(System.getenv("eclipse_dev_notinjar")!=null), true);
		}catch(CacheException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Let's cache the task in memory
		task = new Runnable() {
      @Override  public void run(){
      	// At moment no task
      	try{
					audio_manager.playback("deactivate.wav");
				}catch(CacheException e){
					e.printStackTrace();
				}
      }
    };
    scheduled_task=null;
    
		setMouseHook();
	}

	private void setMouseHook() {
		try{
			// Disable JNativeHook logger
			Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
			logger.setLevel(Level.OFF);
			logger.setUseParentHandlers(false);
			
			// Register JNativeHook
			GlobalScreen.registerNativeHook();
			GlobalScreen.addNativeMouseListener(this);
			GlobalScreen.addNativeMouseMotionListener(this);
			
			System.out.println("JNativeHook: hooked correctly.\n");
		}catch(NativeHookException ex){
			System.out.println("There was a problem registering the native hook.");
			System.out.println(ex.getMessage());
		}
	}
	
	private void startTaskAfterSeconds(int seconds) {
		try{
			if(!w.checkbox_disable.isSelected()) audio_manager.playback("activate.wav");
			if(scheduled_task!=null) scheduled_task.cancel(true);
			scheduled_task=scheduler.schedule(task,seconds,TimeUnit.SECONDS);
		}catch(CacheException e){
			e.printStackTrace();
		}
	}

	@Override
	public void nativeMousePressed(NativeMouseEvent nativeEvent){
		if(nativeEvent.getButton()==NativeMouseEvent.BUTTON3) {
			// Start task playback sound
			int seconds;
			try {
				seconds=Integer.parseInt(w.textfield_timer.getText());
			}catch(NumberFormatException e) {
				seconds=2;
			}
			startTaskAfterSeconds(seconds);
		}
		
	}

	// Unimplemented methods.
	@Override public void nativeMouseClicked(NativeMouseEvent nativeEvent){}
	@Override public void nativeMouseReleased(NativeMouseEvent nativeEvent){}
	@Override public void nativeMouseMoved(NativeMouseEvent nativeEvent){}
	@Override public void nativeMouseDragged(NativeMouseEvent nativeEvent){}
}
