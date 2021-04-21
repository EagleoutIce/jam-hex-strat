package de.flojo.jam.audio;

import de.flojo.jam.util.Back2Future;
import de.flojo.jam.util.HexStratLogger;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.resources.Resources;
import de.gurkenlabs.litiengine.sound.Sound;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RandomSoundPool implements SoundPool<Sound> {
    private final Set<Future<Sound>> futureSounds;
    private final SoundPoolPlayGroup group;

    public RandomSoundPool(SoundPoolPlayGroup group, String soundPrefix, String soundPostfix, int rangeStart, int rangeEnd) {
        this.group = group;
        this.futureSounds = IntStream.range(rangeStart, rangeEnd).mapToObj(i -> soundPrefix + i + soundPostfix).map(RandomSoundPool::loadSound).collect(Collectors.toUnmodifiableSet());
    }

    public RandomSoundPool(SoundPoolPlayGroup group, String... sounds) {
        this.group = group;
        this.futureSounds = Arrays.stream(sounds).map(RandomSoundPool::loadSound).collect(Collectors.toUnmodifiableSet());
    }

    public RandomSoundPool(SoundPoolPlayGroup group, Set<Sound> sounds) {
        this.group = group;
        this.futureSounds = sounds.stream().map(Back2Future::new).collect(Collectors.toUnmodifiableSet());
    }

    private static Future<Sound> loadSound(String s) {
        return Resources.sounds().getAsync(s);
    }

    @Override
    public Optional<Sound> get() {
        try {
            return Optional.of(Game.random().choose(futureSounds).get(150, TimeUnit.MILLISECONDS));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException | TimeoutException e) {
            HexStratLogger.log().log(Level.WARNING, "Waiting on future sound", e);
        }
        return Optional.empty();
    }

    @Override
    public SoundPoolPlayGroup getGroup() {
        return this.group;
    }
}
