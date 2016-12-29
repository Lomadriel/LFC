package org.lomadriel.lfc.statemachine;

import java.lang.ref.WeakReference;

/**
 * Defines a state of a state machine.
 *
 * @author Jérome BOULMIER
 * @see StateMachine
 * @since 0.2
 */
public abstract class State {
	protected WeakReference<StateMachine> machine;

	/**
	 * This method is called when the state machine enters in this state.
	 */
	protected abstract void onEnter();

	/**
	 * This method is called when the state machine is updated.
	 */
	protected abstract void update();

	/**
	 * This method is called when the state machine leaves this state.
	 */
	protected abstract void onExit();

	/**
	 * Returns the next state of the state machine.
	 * <p>
	 * If this is returned, then the state machine doesn't change the current state.
	 *
	 * @return the next state of the state machine.
	 */
	protected abstract State next();
}
