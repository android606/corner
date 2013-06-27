package com.diabolicalschema.corner;

import android.os.CountDownTimer;

//
// CDownTimerWithPause()
// CountdownTimer with pause/restart functionality added
/*
* Had to override that sucka to add pause/restart functionality
* Use it like this:
*
//Make a timer set to 30 seconds, with onTick event every second:
   timer = new CDownTimerWithPause(30000,1000);
       public void _onFinish(){
           //DO STUFF HERE WHEN THE TIMER TIMES OUT//
       }
       public void _onTick(long millisUntilFinished){
           //DO STUFF HERE WHEN onTick() HAPPENS//
       }
    };
    
* timer.start()				//Start the timer
* timer.cancel()			//Stop the timer
* timer.pause()				//Pause the timer so it can be resumed later
* timer.resume()			//Resume the timer after it has been paused
* timer.togglePause()		//Pause a running timer, resume a paused timer
* boolean F = isRunning()	//Check if the timer is running or not
* setTimeRemaining()		//Change the amount of time that's left on the timer.  Works if the timer is running or stopped.
*/
public class CDownTimerWithPause {
	private boolean _isRunning = false;
	private boolean _isFinished = false;
	long millisUntilFinished;
	long _millisRemainingWhenPaused = 0;
	long _tickInterval;
	CountDownTimer _itimer;

	public CDownTimerWithPause(long startTime, long tickInterval){
		_tickInterval = tickInterval;
		_itimer = newTimer(startTime, tickInterval);
	}

	private CountDownTimer newTimer(long startTime, long tickInterval){
		// Create a new timer, with a countdown duration equal to the time we saved when we paused
		return new CountDownTimer(startTime, tickInterval){
			public void onFinish(){
				_onFinish();
				_millisRemainingWhenPaused = 0;
				_isRunning = false;
				_isFinished = true;
			}
			public void onTick(long millisUntilFinished){
				_onTick(millisUntilFinished);
				_millisRemainingWhenPaused = millisUntilFinished;
			}
		};
	}
	
	public void _onFinish(){
		//Called when the timer times out.  Override this.
	}
	
	public void _onTick(long millisUntilFinished){
		//Called with every timer tick event.  Override this.
	}

	// cancel()
	// stops and resets the timer, unsets the flag
	public void cancel(){
		_itimer.cancel();
		_isRunning = false;
	}

	// start()
	// Starts the timer, sets the flag
	public void start(){
		_itimer.start();
		_isRunning = true;
		_isFinished = false;
	}

	// pause()
	// Pauses the timer (actually just does cancel(), but keeps the API consistent and satisfies my OCD)
	public void pause(){
		if(_isRunning){
			//cancel the timer
			_itimer.cancel();
			_isRunning = false;
		}
	}

	// resume()
	// Restarts a timer where it left off after a pause
	public void resume(){
		if(!isRunning()){
			// If the timer has been paused, and there was more than 0 milliseconds left on the timer when it was paused,
			// Create a new timer, with a countdown duration equal to the time we saved when we paused
			if (_millisRemainingWhenPaused > 0 ){
				_itimer.cancel();
				_itimer = newTimer(_millisRemainingWhenPaused - (_tickInterval/2), _tickInterval);
			}
			if(!_isFinished){ start(); }
		}
	}

	// Pause/Restart functionality
	// Actually kills the timer on pause and instantiates a new one on resume
	public void togglePause(){

		if(_isRunning){
			pause();
		} else {
			resume();
		}
	}


	// setTimeRemaining()
	// Set timer to a different "remaining" time, abstract away the fact that I'm killing/creating the timer
	public void setTimeRemaining(long newMillisRemaining){

		boolean restart = isRunning();
		_itimer.cancel();
		_itimer = newTimer(newMillisRemaining, _tickInterval);
		_millisRemainingWhenPaused = newMillisRemaining;
		if (restart){ start(); }
	}

	// isRunning()
	// returns true if the timer is running
	public boolean isRunning() {
		return _isRunning;
	}
}

