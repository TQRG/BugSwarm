package cn.nukkit.level.generator.task;

import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManagerPool;
import cn.nukkit.level.Level;
import cn.nukkit.level.SimpleChunkManager;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.level.generator.biome.Biome;
import cn.nukkit.scheduler.AsyncTask;

import java.util.Map;
import java.util.Random;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class GeneratorRegisterTask extends AsyncTask {

    public Class<? extends Generator> generator;
    public Map<String, Object> settings;
    public long seed;
    public int levelId;

    public GeneratorRegisterTask(Level level, Generator generator) {
        this.generator = generator.getClass();
        this.settings = generator.getSettings();
        this.seed = level.getSeed();
        this.levelId = level.getId();
    }

    @Override
    public void onRun() {
        Block.init();
        Biome.init();
        SimpleChunkManager manager = new SimpleChunkManager(this.seed);
        ChunkManagerPool.put(this.levelId, manager);
        try {
            Generator generator = this.generator.getConstructor(Map.class).newInstance(this.settings);
            generator.init(manager, new cn.nukkit.utils.Random(manager.getSeed()));
            GeneratorPool.put(this.levelId, generator);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
