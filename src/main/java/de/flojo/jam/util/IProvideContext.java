package de.flojo.jam.util;

import de.flojo.jam.game.board.terrain.Architect;
import de.flojo.jam.game.board.traps.TrapCollection;
import de.flojo.jam.game.board.traps.TrapSpawner;
import de.flojo.jam.game.creature.CreatureCollection;
import de.flojo.jam.game.creature.CreatureFactory;
import de.flojo.jam.game.creature.skills.IProvideReadContext;
import de.flojo.jam.game.creature.skills.SkillsPresenter;

public interface IProvideContext extends IProvideReadContext {
    CreatureFactory getFactory();

    TrapSpawner getSpawner();

    Architect getArchitect();

    SkillsPresenter getPresenter();

    int getMoneyLeft();

    default CreatureCollection getCreatures() {
        return getFactory().getCreatures();
    }

    default TrapCollection getTraps() {
        return getSpawner().getTraps();
    }
}
