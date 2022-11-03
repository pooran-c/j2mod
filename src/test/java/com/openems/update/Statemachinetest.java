package openems;

import openems.App.StateMachine;

public class Statemachinetest {

	public static StateMachine stateMachine = StateMachine.FC40;

	public enum StateMachine {
		FC40, //
		FC41, //
		FC42, //
		FC43, //
		FC44, //
		FINISHED
	}

	public static void main(String[] args) {

		boolean isFinished = false;

		do {

			switch (stateMachine) {
			case FC40:
				System.out.println("In StateMachine.FC40");
				isFinished = changeState(StateMachine.FC41);
				break;
			case FC41:
				System.out.println("In StateMachine.FC41");
				isFinished = changeState(StateMachine.FC42);
				break;
			case FC42:
				System.out.println("In StateMachine.FC42");
				isFinished = changeState(StateMachine.FC43);
				break;
			case FC43:
				System.out.println("In StateMachine.FC43");
				isFinished = changeState(StateMachine.FC44);
				break;
			case FC44:
				System.out.println("In StateMachine.44");
				isFinished = changeState(StateMachine.FINISHED);
				break;
			case FINISHED:
				System.out.println("In StateMachine.finished");
				isFinished = false;
				break;
			}
		} while (isFinished);

	}

	private static boolean changeState(StateMachine nextState) {
		if (stateMachine != nextState) {
			stateMachine = nextState;
			return true;
		}
		return false;
	}

}
