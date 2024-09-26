package work.fking.masteringmixology.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum RefinementType {
	AGITATOR("Agitator", 1),
	RETORT("Retort", 2),
	ALEMBIC("Alembic", 3),
	NONE("", 0);

	public final String displayName;
	public final int varbitValue;

	public static RefinementType fromVarbitValue(int varbitValue) {
		switch (varbitValue) {
			case 1:
				return AGITATOR;
			case 2:
				return RETORT;
			case 3:
				return ALEMBIC;
			default:
				return NONE;
		}
	}

	@Override
	public String toString() {
		return displayName;
	}
}
