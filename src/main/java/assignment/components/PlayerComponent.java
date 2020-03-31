package assignment.components;

import net.gameslabs.api.Component;
import net.gameslabs.api.Player;
import net.gameslabs.events.GetPlayerLevel;
import net.gameslabs.model.Skill;

/** A drop-in replacement for {@link Component} with helper functions */
abstract class PlayerComponent extends Component {
    final protected int getPlayerLevel(Player player, Skill skill) {
        GetPlayerLevel getPlayerLevel = new GetPlayerLevel(player, skill);
        send(getPlayerLevel);
        return getPlayerLevel.getLevel();
    }
}
