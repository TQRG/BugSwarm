package cn.nukkit.entity;

import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.particle.EnchantParticle;
import cn.nukkit.level.particle.Particle;
import cn.nukkit.level.particle.SpellParticle;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * Created on 2015/12/27 by xtypr.
 * Package cn.nukkit.entity in project Nukkit .
 */
public class ThrownPotion extends Projectile{

    public static final int NETWORK_ID = 86;

    private int potionType = 0;

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 0.25f;
    }

    @Override
    public float getLength() {
        return 0.25f;
    }

    @Override
    public float getHeight() {
        return 0.25f;
    }

    @Override
    protected float getGravity() {
        return 0.1f;
    }

    @Override
    protected float getDrag() {
        return 0.01f;
    }

    public ThrownPotion(FullChunk chunk, CompoundTag nbt) {
        this(chunk, nbt, null);
    }

    public ThrownPotion(FullChunk chunk, CompoundTag nbt, Entity shootingEntity) {
        super(chunk, nbt, shootingEntity);
    }

    public int getPotionType() {
        return potionType;
    }

    public void setPotionType(int potionType) {
        this.potionType = potionType;
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if(this.closed){
            return false;
        }

        //this.timings.startTiming();

        int tickDiff = currentTick - this.lastUpdate;
        boolean hasUpdate = super.onUpdate(currentTick);

        if(this.age > 1200){
            this.kill();
            hasUpdate = true;
        }

        if (this.isCollided) {
            this.kill();
            Particle particle1 = new EnchantParticle(this);
            this.getLevel().addParticle(particle1);
            //todo 颜色根据药水的不同而不同 Color is different according to potion type
            Particle particle2 = new SpellParticle(this, 0, 0, 255);
            this.getLevel().addParticle(particle2);
            hasUpdate = true;

            Entity[] entities = this.getLevel().getNearbyEntities(this.getBoundingBox().grow(8.25, 4.24, 8.25));
            for (Entity anEntity : entities) {
                //todo 应用药水效果
            }
        }

        //this.timings.stopTiming();

        return hasUpdate;
    }
}
