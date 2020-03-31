package assignment.components;

import net.gameslabs.api.Component;
import net.gameslabs.events.GiveXpEvent;
import net.gameslabs.model.Skill;

public class MyXPBoosterComponent extends Component {

    @Override
    public void onLoad() {
        registerEvent(GiveXpEvent.class, this::onGiveXP);
    }

    // enable DXP in the construction skill
    private void onGiveXP(GiveXpEvent event) {
        if (event.getSkill() == Skill.CONSTRUCTION) event.setXp(2 * event.getXp());

        // if not construction, leave as default XP
        // which incidental is dXP also...
        // LOW: maybe work on a less confusing naming scheme for 1x
    }

    @Override
    public void onUnload() {
        // Do nothing
    }
}
