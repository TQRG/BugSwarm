package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;

/**
 * Created on 2015/12/1 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class Emerald extends Solid {

    public Emerald() {
        this(0);
    }

    public Emerald(int meta) {
        super(0);
    }

    @Override
    public String getName() {
        return "Emerald Block";
    }

    @Override
    public int getId() {
        return EMERALD_BLOCK;
    }

    @Override
    public int getToolType() {
        return Tool.TYPE_PICKAXE;
    }

    @Override
    public int[][] getDrops(Item item) {
        if (item.isPickaxe()) {
            return new int[][]{
                    {Item.EMERALD_BLOCK, 0, 1}
            };
        } else {
            return new int[][]{};
        }
    }
}
