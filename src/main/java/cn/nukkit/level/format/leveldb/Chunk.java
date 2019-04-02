package cn.nukkit.level.format.leveldb;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.format.leveldb.key.EntitiesKey;
import cn.nukkit.level.format.leveldb.key.ExtraDataKey;
import cn.nukkit.level.format.leveldb.key.TilesKey;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.stream.NBTInputStream;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.utils.Binary;
import cn.nukkit.utils.BinaryStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Chunk extends BaseFullChunk {

    public static final int DATA_LENGTH = 16384 * (2 + 1 + 1 + 1) + 256 + 1024;

    protected boolean isLightPopulated = false;
    protected boolean isPopulated = false;
    protected boolean isGenerated = false;

    public Chunk(LevelProvider level, int chunkX, int chunkZ, byte[] terrain) {
        this(level, chunkX, chunkZ, terrain, null);
    }

    public Chunk(Class<? extends LevelProvider> providerClass, int chunkX, int chunkZ, byte[] terrain) {
        this(null, chunkX, chunkZ, terrain, null);
        this.providerClass = providerClass;
    }

    public Chunk(LevelProvider level, int chunkX, int chunkZ, byte[] terrain, List<CompoundTag> entityData) {
        this(level, chunkX, chunkZ, terrain, entityData, null);
    }

    public Chunk(LevelProvider level, int chunkX, int chunkZ, byte[] terrain, List<CompoundTag> entityData, List<CompoundTag> tileData) {
        this(level, chunkX, chunkZ, terrain, entityData, tileData, null);
    }

    public Chunk(LevelProvider level, int chunkX, int chunkZ, byte[] terrain, List<CompoundTag> entityData, List<CompoundTag> tileData, Map<Integer, Integer> extraData) {
        ByteBuffer buffer = ByteBuffer.wrap(terrain).order(ByteOrder.BIG_ENDIAN);

        byte[] blocks = new byte[32768];
        buffer.get(blocks);

        byte[] data = new byte[16384];
        buffer.get(data);

        byte[] skyLight = new byte[16384];
        buffer.get(skyLight);

        byte[] blockLight = new byte[16384];
        buffer.get(blockLight);

        int[] heightMap = new int[256];
        for (int i = 0; i < 256; i++) {
            heightMap[i] = buffer.get() & 0xff;
        }

        int[] biomeColors = new int[256];
        for (int i = 0; i < 256; i++) {
            biomeColors[i] = buffer.getInt();
        }

        this.provider = level;
        if (level != null) {
            this.providerClass = level.getClass();
        }

        this.setPosition(chunkX, chunkZ);

        this.blocks = blocks;
        this.data = data;
        this.skyLight = skyLight;
        this.blockLight = blockLight;

        if (biomeColors.length == 256) {
            this.biomeColors = biomeColors;
        } else {
            this.biomeColors = new int[256];
        }

        if (heightMap.length == 256) {
            this.heightMap = heightMap;
        } else {
            int[] ints = new int[256];
            Arrays.fill(ints, 127);
            this.heightMap = ints;
        }

        this.NBTentities = entityData == null ? new ArrayList<>() : entityData;
        this.NBTtiles = tileData == null ? new ArrayList<>() : tileData;
        this.extraData = extraData == null ? new ConcurrentHashMap<>(8, 0.9f, 1) : extraData;
    }

    @Override
    public boolean isLightPopulated() {
        return this.isLightPopulated;
    }

    @Override
    public void setLightPopulated() {
        this.setLightPopulated(true);
    }

    @Override
    public void setLightPopulated(boolean value) {
        this.isLightPopulated = value;
    }

    @Override
    public boolean isPopulated() {
        return this.isPopulated;
    }

    @Override
    public void setPopulated() {
        this.setPopulated(true);
    }

    @Override
    public void setPopulated(boolean value) {
        this.isPopulated = true;
    }

    @Override
    public boolean isGenerated() {
        return this.isGenerated;
    }

    @Override
    public void setGenerated() {
        this.setGenerated(true);
    }

    @Override
    public void setGenerated(boolean value) {
        this.isGenerated = true;
    }

    public static Chunk fromBinary(byte[] data) {
        return fromBinary(data, null);
    }

    public static Chunk fromBinary(byte[] data, LevelProvider provider) {
        try {

            int chunkX = Binary.readLInt(new byte[]{data[0], data[1], data[2], data[3]});
            int chunkZ = Binary.readLInt(new byte[]{data[4], data[5], data[6], data[7]});
            byte[] chunkData = Binary.subBytes(data, 8, data.length - 1);

            int flags = data[data.length - 1];

            List<CompoundTag> entities = new ArrayList<>();
            List<CompoundTag> tiles = new ArrayList<>();

            Map<Integer, Integer> extraDataMap = new ConcurrentHashMap<>(8, 0.9f, 1);

            if (provider instanceof LevelDB) {
                byte[] entityData = ((LevelDB) provider).getDatabase().get(EntitiesKey.create(chunkX, chunkZ).toArray());
                if (entityData != null && entityData.length > 0) {
                    try (NBTInputStream nbtInputStream = new NBTInputStream(new ByteArrayInputStream(entityData), ByteOrder.LITTLE_ENDIAN)) {
                        while (nbtInputStream.available() > 0) {
                            Tag tag = Tag.readNamedTag(nbtInputStream);
                            if (!(tag instanceof CompoundTag)) {
                                throw new IOException("Root tag must be a named compound tag");
                            }
                            entities.add((CompoundTag) tag);
                        }
                    }
                }

                byte[] tileData = ((LevelDB) provider).getDatabase().get(TilesKey.create(chunkX, chunkZ).toArray());
                if (tileData != null && tileData.length > 0) {
                    try (NBTInputStream nbtInputStream = new NBTInputStream(new ByteArrayInputStream(tileData), ByteOrder.LITTLE_ENDIAN)) {
                        while (nbtInputStream.available() > 0) {
                            Tag tag = Tag.readNamedTag(nbtInputStream);
                            if (!(tag instanceof CompoundTag)) {
                                throw new IOException("Root tag must be a named compound tag");
                            }
                            tiles.add((CompoundTag) tag);
                        }
                    }
                }

                byte[] extraData = ((LevelDB) provider).getDatabase().get(ExtraDataKey.create(chunkX, chunkZ).toArray());
                if (extraData != null && extraData.length > 0) {
                    BinaryStream stream = new BinaryStream(tileData);
                    int count = stream.getInt();
                    for (int i = 0; i < count; ++i) {
                        int key = stream.getInt();
                        int value = stream.getShort();
                        extraDataMap.put(key, value);
                    }
                }

                /*if (!entities.isEmpty() || !blockEntities.isEmpty()) {
                    CompoundTag ct = new CompoundTag();
                    ListTag<CompoundTag> entityList = new ListTag<>("entities");
                    ListTag<CompoundTag> tileList = new ListTag<>("blockEntities");

                    entityList.list = entities;
                    tileList.list = blockEntities;
                    ct.putList(entityList);
                    ct.putList(tileList);
                    NBTIO.write(ct, new File(Nukkit.DATA_PATH + chunkX + "_" + chunkZ + ".dat"));
                }*/


                Chunk chunk = new Chunk(provider, chunkX, chunkZ, chunkData, entities, tiles, extraDataMap);

                if ((flags & 0x01) > 0) {
                    chunk.setGenerated();
                }

                if ((flags & 0x02) > 0) {
                    chunk.setPopulated();
                }

                if ((flags & 0x04) > 0) {
                    chunk.setLightPopulated();
                }
                return chunk;
            }
        } catch (Exception e) {
            Server.getInstance().getLogger().logException(e);
        }
        return null;
    }

    public static Chunk fromFastBinary(byte[] data) {
        return fromFastBinary(data, null);
    }

    public static Chunk fromFastBinary(byte[] data, LevelProvider provider) {
        return fromBinary(data, provider);
    }

    @Override
    public byte[] toFastBinary() {
        return this.toBinary(false);
    }

    @Override
    public byte[] toBinary() {
        return this.toBinary(false);
    }

    public byte[] toBinary(boolean saveExtra) {
        try {
            LevelProvider provider = this.getProvider();

            if (saveExtra && provider instanceof LevelDB) {

                List<CompoundTag> entities = new ArrayList<>();

                for (Entity entity : this.getEntities().values()) {
                    if (!(entity instanceof Player) && !entity.closed) {
                        entity.saveNBT();
                        entities.add(entity.namedTag);
                    }
                }

                EntitiesKey entitiesKey = EntitiesKey.create(this.getX(), this.getZ());
                if (!entities.isEmpty()) {
                    ((LevelDB) provider).getDatabase().put(entitiesKey.toArray(), NBTIO.write(entities));
                } else {
                    ((LevelDB) provider).getDatabase().delete(entitiesKey.toArray());
                }

                List<CompoundTag> tiles = new ArrayList<>();

                for (BlockEntity blockEntity : this.getBlockEntities().values()) {
                    if (!blockEntity.closed) {
                        blockEntity.saveNBT();
                        entities.add(blockEntity.namedTag);
                    }
                }

                TilesKey tilesKey = TilesKey.create(this.getX(), this.getZ());
                if (!tiles.isEmpty()) {
                    ((LevelDB) provider).getDatabase().put(tilesKey.toArray(), NBTIO.write(tiles));
                } else {
                    ((LevelDB) provider).getDatabase().delete(tilesKey.toArray());
                }

                ExtraDataKey extraDataKey = ExtraDataKey.create(this.getX(), this.getZ());
                if (!this.getBlockExtraDataArray().isEmpty()) {
                    BinaryStream extraData = new BinaryStream();
                    Map<Integer, Integer> extraDataArray = this.getBlockExtraDataArray();
                    extraData.putInt(extraDataArray.size());
                    for (Integer key : extraDataArray.keySet()) {
                        extraData.putInt(key);
                        extraData.putShort(extraDataArray.get(key));
                    }
                    ((LevelDB) provider).getDatabase().put(extraDataKey.toArray(), extraData.getBuffer());
                } else {
                    ((LevelDB) provider).getDatabase().delete(extraDataKey.toArray());
                }

            }

            byte[] heightMap = new byte[this.getHeightMapArray().length];
            for (int i = 0; i < this.getHeightMapArray().length; i++) {
                heightMap[i] = (byte) this.getHeightMapArray()[i];
            }

            byte[] biomeColors = new byte[this.getBiomeColorArray().length * 4];
            for (int i = 0; i < this.getBiomeColorArray().length; i++) {
                byte[] bytes = Binary.writeInt(this.getBiomeColorArray()[i]);
                biomeColors[i * 4] = bytes[0];
                biomeColors[i * 4 + 1] = bytes[1];
                biomeColors[i * 4 + 2] = bytes[2];
                biomeColors[i * 4 + 3] = bytes[3];
            }

            return Binary.appendBytes(
                    Binary.writeLInt(this.getX()),
                    Binary.writeLInt(this.getZ()),
                    this.getBlockIdArray(),
                    this.getBlockDataArray(),
                    this.getBlockSkyLightArray(),
                    this.getBlockLightArray(),
                    heightMap,
                    biomeColors,
                    new byte[]{(byte) (((this.isLightPopulated ? 0x04 : 0) | (this.isPopulated() ? 0x02 : 0) | (this.isGenerated() ? 0x01 : 0)) & 0xff)}
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public static Chunk getEmptyChunk(int chunkX, int chunkZ) {
        return getEmptyChunk(chunkX, chunkZ, null);
    }

    public static Chunk getEmptyChunk(int chunkX, int chunkZ, LevelProvider provider) {
        try {
            Chunk chunk;
            if (provider != null) {
                chunk = new Chunk(provider, chunkX, chunkZ, new byte[DATA_LENGTH]);
            } else {
                chunk = new Chunk(LevelDB.class, chunkX, chunkZ, new byte[DATA_LENGTH]);
            }

            byte[] skyLight = new byte[16384];
            Arrays.fill(skyLight, (byte) 0xff);
            chunk.skyLight = skyLight;
            return chunk;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
