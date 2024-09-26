package work.fking.masteringmixology.enums;

public enum MixologyState {
	MIXING,
	MIX_READY,
	READY_TO_REFINE,
	REFINING,
	READY_TO_DEPOSIT,
	WAITING_TO_START;

	@Override
	public String toString() {
		switch (this) {
			case MIXING:
				return "Mixing";
			case MIX_READY:
				return "Mix ready";
			case READY_TO_REFINE:
				return "Ready to refine";
			case REFINING:
				return "Refining";
			case READY_TO_DEPOSIT:
				return "Ready to deposit";
			case WAITING_TO_START:
				return "Waiting to start";
			default:
				return name();
		}
	}
}
