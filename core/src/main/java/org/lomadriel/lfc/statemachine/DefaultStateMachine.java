package org.lomadriel.lfc.statemachine;

import java.lang.ref.WeakReference;

public class DefaultStateMachine implements StateMachine {
	private State currentState;

	public DefaultStateMachine(State initialState) {
		this.currentState = initialState;
		this.currentState.machine = new WeakReference<>(this);
		this.currentState.onEnter();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void update() {
		this.currentState.update();

		State next = this.currentState.next();
		if (next != this.currentState) { // It could be the same class but another instance of this class.
			setCurrentState(next);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public State getCurrentState() {
		return this.currentState;
	}

	private void setCurrentState(State state) {
		this.currentState.onExit();
		this.currentState.machine = null;
		this.currentState = state;
		this.currentState.machine = new WeakReference<>(this);
		this.currentState.onEnter();
	}
}
