package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class StoneShovel extends Tool {

    public StoneShovel() {
        this(0, 1);
    }

    public StoneShovel(Integer meta) {
        this(meta, 1);
    }

    public StoneShovel(Integer meta, int count) {
        super(STONE_SHOVEL, meta, count, "Stone Shovel");
    }

    @Override
    public int getMaxDurability() {
        return Tool.DURABILITY_STONE;
    }

    @Override
    public boolean isShovel() {
        return true;
    }

    @Override
    public int getTier() {
        return Tool.TIER_STONE;
    }
}
