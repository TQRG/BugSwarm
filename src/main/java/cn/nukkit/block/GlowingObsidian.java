package cn.nukkit.block;

/**
 * Created on 2015/11/22 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class GlowingObsidian extends Solid {
    public GlowingObsidian() {
        this(0);
    }

    public GlowingObsidian(int meta) {
        super(GLOWING_OBSIDIAN, meta);
    }

    @Override
    public String getName() {
        return "Glowing Obsidian";
    }

    @Override
    public int getLightLevel() {
        return 12;
    }
}
