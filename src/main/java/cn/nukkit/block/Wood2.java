package cn.nukkit.block;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Wood2 extends Wood {
    public static final int ACACIA = 1;
    public static final int DARK_OAK = 2;

    public Wood2() {
        super();
    }

    public Wood2(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        String[] names = new String[]{
                "Acacia Wood",
                "Dark Oak Wood",
                ""
        };

        return names[this.meta & 0x03];
    }

}
