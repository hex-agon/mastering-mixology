package work.fking.masteringmixology.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Potion {
	MAMMOTH("Mammoth-might mix", PotionComponent.MOX, PotionComponent.MOX, PotionComponent.MOX, 1, 60, 135),
	MYSTIC("Mystic mana amalgam", PotionComponent.MOX, PotionComponent.MOX, PotionComponent.AGA, 2, 60, 175),
	MARLEY("Marley's moonlight", PotionComponent.MOX, PotionComponent.MOX, PotionComponent.LYE, 3, 60, 215),
	MIXALOT("Mixalot", PotionComponent.MOX, PotionComponent.AGA, PotionComponent.LYE, 10, 64, 255),

	ALCO("Alco-augmentator", PotionComponent.AGA, PotionComponent.AGA, PotionComponent.AGA, 4, 76, 255),
	AZURE("Azure aura mix", PotionComponent.AGA, PotionComponent.AGA, PotionComponent.MOX, 5, 68, 215),
	AQUA("Aqualux amalgam", PotionComponent.AGA, PotionComponent.AGA, PotionComponent.LYE, 6, 72, 295),

	LIPLACK("Liplack liquor", PotionComponent.LYE, PotionComponent.LYE, PotionComponent.LYE, 7, 86, 375),
	MEGA("Megalite liquid", PotionComponent.LYE, PotionComponent.LYE, PotionComponent.MOX, 8, 80, 295),
	ANTI("Anti-leech lotion", PotionComponent.LYE, PotionComponent.LYE, PotionComponent.AGA, 9, 84, 335),

	NONE("", PotionComponent.NONE, PotionComponent.NONE, PotionComponent.NONE, 0, 0, 0),
	;

	public final String potionName;
	public final PotionComponent firstComponent;
	public final PotionComponent secondComponent;
	public final PotionComponent thirdComponent;
	public final int varbitValue;
	public final int herbloreLevel;
	public final int potionShopValue;
	public final int totalXP;
	public final int moxValue;
	public final int agaValue;
	public final int lyeValue;


	Potion(String potionName, PotionComponent firstComponent, PotionComponent secondComponent, PotionComponent thirdComponent,
		   int varbitValue,
		   int herbloreLevel, int totalXP) {
		this.firstComponent = firstComponent;
		this.secondComponent = secondComponent;
		this.thirdComponent = thirdComponent;
		this.varbitValue = varbitValue;
		this.herbloreLevel = herbloreLevel;
		this.totalXP = totalXP;
		this.potionName = potionName;
		potionShopValue = firstComponent.rewardValue + secondComponent.rewardValue + thirdComponent.rewardValue;

		int moxCount = 0;
		int agaCount = 0;
		int lyeCount = 0;

		if (firstComponent == PotionComponent.AGA) {
			agaCount++;
		}
		if (secondComponent == PotionComponent.AGA) {
			agaCount++;
		}
		if (thirdComponent == PotionComponent.AGA) {
			agaCount++;
		}
		if (firstComponent == PotionComponent.LYE) {
			lyeCount++;
		}
		if (secondComponent == PotionComponent.LYE) {
			lyeCount++;
		}
		if (thirdComponent == PotionComponent.LYE) {
			lyeCount++;
		}
		if (firstComponent == PotionComponent.MOX) {
			moxCount++;
		}
		if (secondComponent == PotionComponent.MOX) {
			moxCount++;
		}
		if (thirdComponent == PotionComponent.MOX) {
			moxCount++;
		}

		agaValue = agaCount * 10;
		moxValue = moxCount * 10;
		lyeValue = lyeCount * 10;
	}

	public static Potion fromVarbitValue(int varbitValue) {
		switch (varbitValue) {
			case 1:
				return MAMMOTH;
			case 2:
				return MYSTIC;
			case 3:
				return MARLEY;
			case 10:
				return MIXALOT;
			case 4:
				return ALCO;
			case 5:
				return AZURE;
			case 6:
				return AQUA;
			case 7:
				return LIPLACK;
			case 8:
				return MEGA;
			case 9:
				return ANTI;
			default:
				return NONE;
		}
	}

	@Override
	public String toString() {
		return potionName;
	}
}
