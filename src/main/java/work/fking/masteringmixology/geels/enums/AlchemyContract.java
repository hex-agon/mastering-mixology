package work.fking.masteringmixology.geels.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class AlchemyContract {
    @Getter
    private AlchemyPotion potion;

    @Getter
    private AlchemyBuilding type;
}
