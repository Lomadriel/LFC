package org.lomadriel.lfc.statemachine;

/**
 * Defines a state of a state machine.
 *
 * @author Jérome BOULMIER
 * @see StateMachine
 * @since 0.2
 */
public interface State {
	/**
	 * This method is called when the state machine enters in this state.
	 */
	void onEnter();

	/**
	 * This method is called when the state machine is updated.
	 */
	void update();

	/**
	 * This method is called when the state machine leaves this state.
	 */
	void onExit();

	/**
	 * Returns the next state of the state machine.
	 * <p>
	 * If this is returned, then the state machine doesn't change the current state.
	 *
	 * @return the next state of the state machine.
	 */
	State next();
}
