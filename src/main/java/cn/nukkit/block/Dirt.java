package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Dirt extends Solid {


    public Dirt() {
        this(0);
    }

    public Dirt(int meta) {
        super(0);
    }

    @Override
    public int getId() {
        return DIRT;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public double getHardness() {
        return 0.5;
    }

    @Override
    public int getToolType() {
        return Tool.TYPE_SHOVEL;
    }

    @Override
    public String getName() {
        return "Dirt";
    }

    @Override
    public boolean onActivate(Item item) {
        return this.onActivate(item, null);
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (item.isHoe()) {
            item.useOn(this);
            this.getLevel().setBlock(this, Block.get(Item.FARMLAND, 0), true);

            return true;
        }

        return false;
    }
}
