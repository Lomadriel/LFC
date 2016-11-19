package org.lomadriel.lfc.statemachine;

public class DefaultStateMachine implements StateMachine {
	private State currentState;

	public DefaultStateMachine(State initialState) {
		this.currentState = initialState;
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
		this.currentState = state;
		this.currentState.onEnter();
	}
}
