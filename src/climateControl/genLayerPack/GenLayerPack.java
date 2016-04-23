
package climateControl.genLayerPack;
/*
 * This seemingly odd system of duplicating the GenLayer classes allows me to
 * also run Climate Control in an altered form of Amidst by just changing imports
 * I can also fiddle with the parent layer, which is sometimes useful.
 */
import climateControl.utils.Receiver;
import climateControl.utils.StringWriter;
import java.io.File;
import java.util.concurrent.Callable;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.util.ReportedException;
import net.minecraft.world.WorldType;

import net.minecraftforge.common.*;
import net.minecraftforge.event.terraingen.*;

public abstract class GenLayerPack extends GenLayer
{
    public static final int undefined = -2;
    /**
     * seed from World#getWorldSeed that is used in the LCG prng
     */
    private long worldGenSeed;
    /**
     * parent GenLayer that was provided via the constructor
     */
    protected GenLayer parent;
    public GenLayer getParent() {return parent;}
    public void setParent(GenLayer newParent) {parent = newParent;}
    /**
     * final part of the LCG prng that uses the chunk X, Z coords along with the other two seeds to generate
     * pseudorandom numbers
     */
    private long chunkSeed;
    /**
     * base seed to the LCG prng provided via the constructor
     */
    protected long baseSeed;
    private static final String __OBFID = "CL_00000559";

    /**
     * the first array item is a linked list of the bioms, the second is the zoom function, the third is the same as the
     * first.
     */
    

    public GenLayerPack(long par1)
    {
        super(par1);
        this.baseSeed = par1;
        this.baseSeed *= this.baseSeed * 6364136223846793005L + 1442695040888963407L;
        this.baseSeed += par1;
        this.baseSeed *= this.baseSeed * 6364136223846793005L + 1442695040888963407L;
        this.baseSeed += par1;
        this.baseSeed *= this.baseSeed * 6364136223846793005L + 1442695040888963407L;
        this.baseSeed += par1;
    }
    /**
     * Initialize layer's local worldGenSeed based on its own baseSeed and the world's global seed (passed in as an
     * argument).
     */
    @Override
    public void initWorldGenSeed(long par1)
    {
        super.initWorldGenSeed(par1);
        this.worldGenSeed = par1;

        if (this.parent != null)
        {
            this.parent.initWorldGenSeed(par1);
        }

        this.worldGenSeed *= this.worldGenSeed * 6364136223846793005L + 1442695040888963407L;
        this.worldGenSeed += this.baseSeed;
        this.worldGenSeed *= this.worldGenSeed * 6364136223846793005L + 1442695040888963407L;
        this.worldGenSeed += this.baseSeed;
        this.worldGenSeed *= this.worldGenSeed * 6364136223846793005L + 1442695040888963407L;
        this.worldGenSeed += this.baseSeed;
    }

    /**
     * Initialize layer's current chunkSeed based on the local worldGenSeed and the (x,z) chunk coordinates.
     */
    public void initChunkSeed(long par1, long par3)
    {
        this.chunkSeed = this.worldGenSeed;
        this.chunkSeed *= this.chunkSeed * 6364136223846793005L + 1442695040888963407L;
        this.chunkSeed += par1;
        this.chunkSeed *= this.chunkSeed * 6364136223846793005L + 1442695040888963407L;
        this.chunkSeed += par3;
        this.chunkSeed *= this.chunkSeed * 6364136223846793005L + 1442695040888963407L;
        this.chunkSeed += par1;
        this.chunkSeed *= this.chunkSeed * 6364136223846793005L + 1442695040888963407L;
        this.chunkSeed += par3;
    }

    public long savedChunkSeed() {return this.chunkSeed;}
    public void restoreChunkSeed(long restored) {this.chunkSeed = restored;}
    /**
     * returns a LCG pseudo random number from [0, x). Args: int x
     */
    protected int nextInt(int par1)
    {
        int j = (int)((this.chunkSeed >> 24) % (long)par1);

        if (j < 0)
        {
            j += par1;
        }

        this.chunkSeed *= this.chunkSeed * 6364136223846793005L + 1442695040888963407L;
        this.chunkSeed += this.worldGenSeed;
        return j;
    }

    /**
     * Returns a list of integer values generated by this layer. These may be interpreted as temperatures, rainfall
     * amounts, or biomeList[] indices based on the particular GenLayer subclass.
     */
    public abstract int[] getInts(int var1, int var2, int var3, int var4);

    /**
     * returns true if the biomeIDs are equal, or returns the result of the comparison as per BiomeGenBase.isEqualTo
     */
    protected static boolean compareBiomesById(final int p_151616_0_, final int p_151616_1_)
    {
        if (p_151616_0_ == p_151616_1_)
        {
            return true;
        }
        else if (p_151616_0_ != BiomeGenBase.mesaPlateau_F.biomeID && p_151616_0_ != BiomeGenBase.mesaPlateau.biomeID)
        {
            try
            {
                return BiomeGenBase.getBiome(p_151616_0_) != null && BiomeGenBase.getBiome(p_151616_1_) != null ? BiomeGenBase.getBiome(p_151616_0_).equals(BiomeGenBase.getBiome(p_151616_1_)) : false;
            }
            catch (Throwable throwable)
            {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Comparing biomes");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("Biomes being compared");
                crashreportcategory.addCrashSection("Biome A ID", Integer.valueOf(p_151616_0_));
                crashreportcategory.addCrashSection("Biome B ID", Integer.valueOf(p_151616_1_));
                crashreportcategory.addCrashSectionCallable("Biome A", new Callable()
                {
                    private static final String __OBFID = "CL_00000560";
                    public String call()
                    {
                        return String.valueOf(BiomeGenBase.getBiome(p_151616_0_));
                    }
                });
                crashreportcategory.addCrashSectionCallable("Biome B", new Callable()
                {
                    private static final String __OBFID = "CL_00000561";
                    public String call()
                    {
                        return String.valueOf(BiomeGenBase.getBiome(p_151616_1_));
                    }
                });
                throw new ReportedException(crashreport);
            }
        }
        else
        {
            return p_151616_1_ == BiomeGenBase.mesaPlateau_F.biomeID || p_151616_1_ == BiomeGenBase.mesaPlateau.biomeID;
        }
    }

    /**
     * returns true if the biomeId is one of the various ocean biomes.
     */
    protected static boolean isBiomeOceanic(int id)
    {
        if (id>255) return false;// oddly the below returns true for all id>255
        return id == BiomeGenBase.ocean.biomeID || id == BiomeGenBase.deepOcean.biomeID || id == BiomeGenBase.frozenOcean.biomeID;
    }
    protected static boolean isOceanic(int id)
    {
        if (id==BiomeGenBase.frozenOcean.biomeID) return true;
        if (id>255) return false;// oddly the below returns true for all id>255
        return id == BiomeGenBase.ocean.biomeID || id == BiomeGenBase.deepOcean.biomeID || id == BiomeGenBase.frozenOcean.biomeID;
        //throw new RuntimeException();
    }

    /**
     * selects a random integer from a set of provided integers
     */
    protected int selectRandom(int ... p_151619_1_)
    {
        return p_151619_1_[this.nextInt(p_151619_1_.length)];
    }

    /**
     * returns the most frequently occurring number of the set, or a random number from those provided
     */
    protected int selectModeOrRandom(int p_151617_1_, int p_151617_2_, int p_151617_3_, int p_151617_4_)
    {
        return p_151617_2_ == p_151617_3_ && p_151617_3_ == p_151617_4_ ? p_151617_2_ : (p_151617_1_ == p_151617_2_ && p_151617_1_ == p_151617_3_ ? p_151617_1_ : (p_151617_1_ == p_151617_2_ && p_151617_1_ == p_151617_4_ ? p_151617_1_ : (p_151617_1_ == p_151617_3_ && p_151617_1_ == p_151617_4_ ? p_151617_1_ : (p_151617_1_ == p_151617_2_ && p_151617_3_ != p_151617_4_ ? p_151617_1_ : (p_151617_1_ == p_151617_3_ && p_151617_2_ != p_151617_4_ ? p_151617_1_ : (p_151617_1_ == p_151617_4_ && p_151617_2_ != p_151617_3_ ? p_151617_1_ : (p_151617_2_ == p_151617_3_ && p_151617_1_ != p_151617_4_ ? p_151617_2_ : (p_151617_2_ == p_151617_4_ && p_151617_1_ != p_151617_3_ ? p_151617_2_ : (p_151617_3_ == p_151617_4_ && p_151617_1_ != p_151617_2_ ? p_151617_3_ : this.selectRandom(new int[] {p_151617_1_, p_151617_2_, p_151617_3_, p_151617_4_}))))))))));
    }

    public static byte getModdedBiomeSize(WorldType worldType, byte original)
    {
        WorldTypeEvent.BiomeSize event = new WorldTypeEvent.BiomeSize(worldType, original);
        MinecraftForge.TERRAIN_GEN_BUS.post(event);
        return event.newSize;
    }

    public void report(File file, int [] toReport, int length, int width) {
        Receiver<String> reportee = StringWriter.from(file);
        for (int i = 0; i < width;i++) {
            String report = "";
            for (int j = 0; j < length;j++) {
                int value = toReport[i*(length)+j];
                if (value == 24) value =0;// deep ocean
                report += value+ " ";
            }
            reportee.accept(report);
        }
        reportee.accept("");
        for (int i = 0; i < width;i++) {
            String report = "";
            for (int j = 0; j < length;j++) {
                int value = i;//toReport[i*(2*distanceFromOrigin+1)+j];
                report += value+ " ";
            }
            reportee.accept(report);
        }
        reportee.accept("");
        for (int i = 0; i < width;i++) {
            String report = "";
            for (int j = 0; j < length;j++) {
                int value = j;//toReport[i*(2*distanceFromOrigin+1)+j];
                report += value+ " ";
            }
            reportee.accept(report);
        }
        reportee.done();
    }

    // debugging

    public void poison(int [] toPoison, int length) {
        for (int i = 0 ; i<length;i++) {
            toPoison[i]=-2;
        }
    }

    public void taste(int [] toPoison, int length) {
        for (int i = 0 ; i<length;i++) {
            if (toPoison[i]==-2) throw new RuntimeException(""+i);
        }
    }
}