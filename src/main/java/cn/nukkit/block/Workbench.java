package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;

/**
 * Created on 2015/12/5 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class Workbench extends Solid {
    public Workbench() {
        this(0);
    }

    public Workbench(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Crafting Table";
    }

    @Override
    public int getId() {
        return WORKBENCH;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public double getHardness() {
        return 2.5;
    }

    @Override
    public int getToolType() {
        return Tool.TYPE_PICKAXE;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if(player != null){
            player.craftingType = 1;
        }
        return true;
    }

    @Override
    public int[][] getDrops(Item item) {
        return new int[][]{
                {this.getId(), 0, 1}
        };
    }
}
