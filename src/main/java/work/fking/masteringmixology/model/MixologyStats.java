package work.fking.masteringmixology.model;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.events.ChatMessage;
import work.fking.masteringmixology.constants.MixologyVarbits;

@Singleton
@Slf4j
public class MixologyStats {
	@Inject
	private Client client;

	@Inject
	private MixologyStateMachine state;

	@Getter
	private int hopperMoxCount = 0;

	@Getter
	private int hopperAgaCount = 0;

	@Getter
	private int hopperLyeCount = 0;

	@Getter
	private int playerMoxCount = -1;

	@Getter
	private int playerAgaCount = -1;

	@Getter
	private int playerLyeCount = -1;

	public void updateVarbits() {
		this.hopperMoxCount = client.getVarbitValue(MixologyVarbits.HOPPER_MOX_COUNT);
		this.hopperAgaCount = client.getVarbitValue(MixologyVarbits.HOPPER_AGA_COUNT);
		this.hopperLyeCount = client.getVarbitValue(MixologyVarbits.HOPPER_LYE_COUNT);
	}

	public void processChatMessage(ChatMessage event) {
		var message = event.getMessage();
		if (state.isStarted() &&
			event.getType() == ChatMessageType.GAMEMESSAGE &&
			message.contains("You are rewarded") &&
			message.contains("You now have")) {
			try {
				var playerRewardCounts = parseMatchingMessage(message);

				if (playerRewardCounts.size() == 3) {
					playerMoxCount = playerRewardCounts.get(0);
					playerAgaCount = playerRewardCounts.get(1);
					playerLyeCount = playerRewardCounts.get(2);
				}
			} catch (Exception e) {
				// Failed to parse the message, leave it for now
			}
		}
	}

	private static List<Integer> parseMatchingMessage(String message) throws Exception {
		String input = message.split("You now have")[1];
		String regex = "<col=[^>]+>([\\d,]+)</col>";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(input);
		ArrayList<Integer> numbers = new ArrayList<>();

		while (matcher.find()) {
			String numberStr = matcher.group(1);
			numberStr = numberStr.replace(",", "");
			int number = Integer.parseInt(numberStr);
			numbers.add(number);
		}

		return numbers;
	}
}
