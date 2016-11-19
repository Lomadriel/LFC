package org.lomadriel.lfc.statemachine;

/**
 * Interface of a state machine.
 *
 * @author Jérome BOULMIER
 * @see State
 * @since 0.2
 */
public interface StateMachine {
	/**
	 * Updates the state machine and so the current state.
	 * <p>
	 * Then gets the next state and sets it if the next state isn't the same instance as the current one.
	 */
	void update();

	/**
	 * Returns the current state of this machine
	 *
	 * @return the current state of this machine.
	 */
	State getCurrentState();
}
