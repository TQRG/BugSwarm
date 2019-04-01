package cn.nukkit.level.generator.object.tree;

import cn.nukkit.level.ChunkManager;

import cn.nukkit.utils.Random;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class TallBirchTree extends BirchTree {

    @Override
    public void placeObject(ChunkManager level, int x, int y, int z, Random random) {
        this.treeHeight = random.nextBoundedInt(3) + 10;
        super.placeObject(level, x, y, z, random);
    }
}
