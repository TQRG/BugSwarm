package cn.nukkit.block;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class Sponge extends Solid {


    public Sponge() {
        this(0);
    }

    public Sponge(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return SPONGE;
    }

    @Override
    public double getHardness() {
        return 0.6;
    }

    @Override
    public String getName() {
        return "Sponge";
    }
}
