package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class GoldHoe extends Tool {

    public GoldHoe() {
        this(0, 1);
    }

    public GoldHoe(Integer meta) {
        this(meta, 1);
    }

    public GoldHoe(Integer meta, int count) {
        super(GOLD_HOE, meta, count, "Gold Hoe");
    }

    @Override
    public int getMaxDurability() {
        return Tool.DURABILITY_GOLD;
    }

    @Override
    public boolean isHoe() {
        return true;
    }

    @Override
    public int getTier() {
        return Tool.TIER_GOLD;
    }
}
