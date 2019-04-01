package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Water extends Liquid {


    public Water() {
        this(0);
    }

    public Water(int meta) {
        super(WATER, meta);
    }

    @Override
    public String getName() {
        return "Water";
    }

    @Override
    public void onEntityCollide(Entity entity) {
        entity.resetFallDistance();
        if (entity.fireTicks > 0) {
            entity.extinguish();
        }

        entity.resetFallDistance();
    }

    @Override
    public boolean place(Item item, Block block, Block target, int face, double fx, double fy, double fz) {
        return this.place(item, block, target, face, fx, fy, fz, null);
    }

    @Override
    public boolean place(Item item, Block block, Block target, int face, double fx, double fy, double fz, Player player) {
        boolean ret = this.getLevel().setBlock(this, this, true, false);
        this.getLevel().scheduleUpdate(this, this.tickRate());

        return ret;
    }
}
